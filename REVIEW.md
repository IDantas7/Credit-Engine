# Code Review - Settlement Endpoint

## Objetivo

Este documento apresenta o code review do endpoint de liquidação disponibilizado no Anexo A do desafio.

Os problemas foram organizados por severidade levando em consideração o impacto nos valores financeiros, a consistência dos dados, o comportamento da API e a segurança.

Para cada problema, temos o possível impacto em produção e uma resolução do problema.

---

# Problemas Críticos

## 1. Representação incorreta das taxas

**Severidade: Crítica**

### Problema

O código utiliza os seguintes valores para representar as taxas:

- Base Rate: `1.0`
- Duplicata: `1.5`
- Cheque: `2.5`

Porém, ao utilizar porcentagens diretamente na fórmula, os valores de 1%, 1,5% e 2,5% deveriam ser representados como `0.01`, `0.015` e `0.025`.

Dessa forma, o problema não está na fórmula utilizada para o cálculo do valor presente, mas nos valores que estão sendo passados para ela.

### Impacto

Esse problema afeta diretamente o valor final calculado pelo sistema.

Como essas taxas serão utilizadas em uma operação normal, os recebíveis serão precificados com valores incorretos, podendo causar prejuízo financeiro.

### Correção proposta

Alterar os valores utilizados no cálculo para:

- Base Rate: `0.01`
- Duplicata: `0.015`
- Cheque: `0.025`

Podemos validar essa correçao, utilizando testes automatizados com aas informações dos Golden Cases fornecidos pelo desafio, assim garantindo o retorno esperado dos valores.


---

## 2. Uso de ponto flutuante para valores monetários

**Severidade: Crítica**

### Problema

Os cálculos financeiros estão sendo realizados utilizando o tipo numérico padrão do JavaScript/TypeScript.

O ponto flutuante pode apresentar problemas de precisão em cálculos decimais. Um exemplo simples desse comportamento é:

```javascript
0.1 + 0.2
// 0.30000000000000004
```

Para valores financeiros precisamos ter controle sobre a precisão dos cálculos.

### Impacto

Uma diferença de precisão pode alterar o resultado financeiro da operação, inclusive gerando diferenças de centavos.

Como o sistema realiza cálculos de precificação e liquidação, essas diferenças podem causar valores incorretos e possíveis prejuízos financeiros.

### Correção proposta

Utilizar uma solução que trabalhe com precisão decimal para realizar os cálculos financeiros.

Também deve ser respeitada a regra definida para o desafio, utilizando arredondamento `half-even` para duas casas decimais no resultado final.

Em Java, por exemplo, esse tipo de cálculo pode ser realizado utilizando `BigDecimal` e `RoundingMode.HALF_EVEN`.

---

## 3. Falta de transação e rollback

**Severidade: Crítica**

### Problema

O código executa duas alterações no banco:

```typescript
await db.query(
  `INSERT INTO settlements (...)`
);

await db.query(
  `UPDATE receivables SET status = 'SETTLED' ...`
);
```

O primeiro comando adiciona a liquidação na tabela `settlements`.

O segundo atualiza o status do recebível para `SETTLED`.

Apesar de serem dois comandos diferentes, os dois fazem parte da mesma operação de negócio e precisam ser executados em conjunto.

Caso o `INSERT` funcione e o `UPDATE` falhe, o primeiro comando já terá sido executado no banco.

### Impacto

O banco pode ficar inconsistente.

Pode existir uma liquidação salva na tabela `settlements`, enquanto o recebível continua com um status indicando que ainda não foi liquidado.

Isso pode também permitir que o sistema tente processar novamente um recebível que já possui uma liquidação registrada.

### Correção proposta

Executar as duas operações dentro de uma transação.

Caso qualquer uma delas falhe, deve ocorrer um rollback.

Dessa forma:

```text
INSERT ✅
UPDATE ✅
→ COMMIT
```

ou:

```text
INSERT ✅
UPDATE ❌
→ ROLLBACK
```

No segundo caso, o `INSERT` que já havia sido executado também seria desfeito, mantendo o banco consistente.

---

## 4. Erro ignorado e resposta 200 OK

**Severidade: Crítica**

### Problema

O código possui o seguinte tratamento:

```typescript
try {
    // operações no banco
} catch (e) {
    // se falhar aqui, o insert já rodou, então segue o jogo
}
```

O `catch` recebe a exceção, mas não faz nenhum tratamento com ela.

Depois disso, o código continua normalmente e executa:

```typescript
res.status(200).json({
    ok: true,
    amount: finalAmount.toFixed(2)
});
```

Dessa forma, uma operação pode falhar e mesmo assim o sistema retornar `200 OK` e `ok: true`.

### Impacto

O usuário pode receber uma resposta informando que a liquidação aconteceu com sucesso quando na realidade ocorreu um erro.

Além de apresentar uma informação incorreta para o usuário, isso dificulta entender o que realmente aconteceu durante a operação.

### Correção proposta

O erro não deve ser ignorado.

A aplicação deve tratar ou propagar a exceção e retornar um status HTTP de acordo com o problema ocorrido.

Por exemplo, uma falha inesperada no servidor ou no banco poderia retornar:

```text
500 Internal Server Error
```

Outros tipos de erro podem retornar outros status, dependendo da situação.

---

## 5. Risco de SQL Injection

**Severidade: Crítica**

### Problema

Alguns dados recebidos do usuário estão sendo colocados diretamente dentro das queries SQL.

Exemplo:

```typescript
`SELECT * FROM receivables WHERE id = ${receivableId}`
```

O mesmo acontece no `INSERT`:

```typescript
`VALUES (${receivableId}, ${finalAmount.toFixed(2)}, '${currency}')`
```

O problema não é o usuário informar o ID do recebível, pois o sistema precisa saber qual recebível deve ser processado.

O problema é colocar esse valor recebido diretamente dentro da string que será enviada como comando para o banco.

### Impacto

Um usuário mal-intencionado poderia tentar enviar um conteúdo que alterasse o comando executado pelo banco.

Isso cria uma vulnerabilidade de SQL Injection e pode permitir uma manipulação indevida dos dados.

### Correção proposta

Utilizar queries parametrizadas, separando o comando SQL dos valores recebidos.

Por exemplo:

```sql
SELECT *
FROM receivables
WHERE id = ?
```

O valor do ID seria passado separadamente como parâmetro.

Outra alternativa seria utilizar uma camada de persistência ou ORM, evitando construir as consultas através da concatenação direta das entradas.

---

# Problemas de Severidade Alta

## 6. Uso da última cotação de câmbio

**Severidade: Alta**

### Problema

Quando a moeda escolhida é USD, o código utiliza:

```typescript
const rate = await fxService.getLatestRate("USD");
```

Esse método busca a última cotação disponível.

O problema é que a cotação pode mudar entre o momento da simulação e o momento em que a liquidação for realizada.

Por exemplo:

```text
14:00 → FX = 5,4321
14:30 → simulação

15:00 → FX = 5,5000
16:00 → liquidação
```

Ao buscar simplesmente a última cotação, a liquidação pode acabar utilizando `5,5000`, mesmo que a operação tenha sido simulada anteriormente utilizando `5,4321`.

### Impacto

O cálculo pode mudar dependendo do momento em que a liquidação for realizada.

Isso pode causar uma diferença entre o valor apresentado anteriormente para o usuário e o valor utilizado posteriormente na operação.

Também dificulta reproduzir o cálculo para entender qual cotação foi utilizada.

### Correção proposta

Criar uma forma de o sistema identificar a cotação correspondente àquela operação, em vez de sempre buscar somente a última cotação disponível.

A liquidação deve utilizar a cotação definida pela regra de negócio para aquela operação, mantendo também uma referência da taxa utilizada.

---

# Problemas de Severidade Média

## 7. Falta de validação das entradas

**Severidade: Média**

### Problema

O código recebe diretamente:

```typescript
const { receivableId, currency } = req.body;
```

Porém, não existe uma validação antes de utilizar esses valores.

Por exemplo, não é verificado se:

- o `receivableId` foi informado;
- o recebível realmente existe;
- a moeda informada é válida;
- os valores estão no formato esperado.

Depois da consulta também são utilizados diretamente:

```typescript
receivable.type
receivable.face_value
receivable.term
```

sem uma verificação explícita de que o recebível realmente foi encontrado.

### Impacto

Uma entrada inválida pode continuar sendo processada pelo sistema até gerar um erro posteriormente.

Por exemplo, caso o recebível não exista, o sistema pode tentar acessar `receivable.type` sem possuir um recebível válido.

### Correção proposta

Validar os dados recebidos antes de iniciar o processamento.

Também deve existir um tratamento específico para situações como recebível inexistente ou moeda não suportada.

A API deve retornar um status HTTP correspondente ao problema encontrado.

---

# Problemas de Severidade Baixa

## 8. Tipo desconhecido recebe automaticamente o spread do cheque

**Severidade: Baixa**

### Problema

A escolha do spread é feita desta forma:

```typescript
const spread =
    receivable.type === "DUPLICATA"
        ? 1.5
        : 2.5;
```

O código verifica apenas se o tipo é `DUPLICATA`.

Caso não seja, automaticamente utiliza o segundo valor.

Isso significa que o comportamento seria parecido com:

```text
DUPLICATA → spread da duplicata
CHEQUE    → spread do cheque
CONTRATO  → spread do cheque
OUTRO     → spread do cheque
```

Ou seja, qualquer coisa diferente de duplicata acaba sendo tratada como cheque.

### Impacto

Caso um tipo de recebível desconhecido chegue ao sistema, ele pode receber uma regra de cálculo que não pertence àquele tipo.

Apesar de depender da entrada de um tipo não suportado, o sistema não deveria assumir automaticamente que qualquer outro tipo é um cheque.

### Correção proposta

Validar de forma específica os tipos de recebíveis suportados.

A lógica deve reconhecer:

```text
DUPLICATA → regra da duplicata
CHEQUE    → regra do cheque
OUTRO     → erro
```

Caso seja recebido um tipo não suportado, o sistema deve interromper o processamento e informar que não existe uma regra para aquele tipo.

---

# Conclusão

Os problemas foram priorizados principalmente considerando o impacto financeiro e a possibilidade de acontecerem durante um processo normal do sistema.

Os primeiros pontos estão relacionados diretamente ao cálculo financeiro e à consistência de uma liquidação.

Também foram encontrados problemas relacionados ao tratamento de erros, segurança, uso da cotação de câmbio e validação das entradas.

As correções propostas buscam fazer com que o processo de liquidação tenha cálculos corretos, mantenha os dados consistentes e trate corretamente situações de erro.