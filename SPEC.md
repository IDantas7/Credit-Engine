# SPEC.md - Credit Engine

## 1. Objetivo e Escopo

Atender à mesa de operações no processamento de lote de recebíveis através do fluxo:

**Recebível → Precificação → Arredondamento → Conversão cambial (quando necessária) → Liquidação → Registro auditável e imutável.**

### Dentro do Escopo

- **Entrada e Taxas:** Receber tipo de recebível, valor de face, prazo e moeda de pagamento; aplicar Base Rate, Spread por tipo de ativo para precificação e aplicar FX à taxa cambial quando necessária.

- **Precificação e Câmbio:** Calcular e apresentar PV, deságio e realizar conversão cambial do PV quando a moeda de pagamento exigir (como USD).

- **Liquidação e Auditoria:** Registrar a liquidação das operações de forma auditável, idempotente e imutável, garantindo idempotência e integridade transacional.

### Fora do Escopo

- Gestão/cadastro de usuários, autenticação e autorização de acesso.
- Cálculo dinâmico de risco dos recebíveis.
- Integração real com provedores externos de câmbio.

---

## 2. Premissas

- **Precificação:** o Valor Presente (PV) será calculado pela fórmula:

  `PV = Valor de Face / (1 + Base Rate + Spread)^Prazo`

  O Spread será de 1,5% a.m. para Duplicata e 2,5% a.m. para Cheque. O deságio será calculado pela diferença entre o Valor de Face e o PV em BRL (`Deságio = Valor de Face - PV`).

- **Prazo:** serão considerados meses inteiros, com base na periodicidade mensal das taxas como Base Rate e Spread, também com base nos Golden Cases fornecidos no desafio. Também será importante colocar na entrada os meses inteiros (ex.: 3, 4) e não números quebrados (ex.: 3,5, 4,5). Se vier assim, o sistema rejeita.

- **Base Rate:** será adotado o valor de 1% a.m., conforme os Golden Cases. A taxa será tratada como parâmetro de configuração da aplicação, e não fixa no código, permitindo alteração sem modificar a lógica de precificação.

- **Taxa de câmbio:** será utilizada na liquidação a mesma taxa de câmbio aplicada no momento da simulação, preservando o valor apresentado ao operador e garantindo previsibilidade e rastreabilidade da operação. Vamos manter o valor da simulação mesmo que o operador volte em alguns dias.

- **Arredondamento:** os valores serão arredondados para duas casas decimais utilizando Half-Even, reduzindo o viés de arredondamento. O arredondamento ocorrerá somente no resultado final da precificação. Em operações com conversão cambial, o FX será aplicado sobre o PV em BRL já arredondado; o resultado também será arredondado para duas casas decimais utilizando o Half-Even.

- **Origem/atualização da taxa de câmbio:** a origem do câmbio será feita de forma manual, no momento sem integração com algum provedor externo. Preferi abordar de forma manual, pois atende o que é proposto pelo desafio e evita complexidade desnecessária no momento. Cada cotação manterá seu respectivo valor na data/hora da simulação.

- **Idempotência:** será utilizado um identificador para reconhecer requisições que representam a mesma solicitação de liquidação. Caso uma solicitação já processada seja repetida, o sistema retornará a liquidação anteriormente registrada, sem criar uma nova operação.

- **Moedas suportadas:** serão suportadas apenas BRL e USD, atendendo ao escopo proposto pelo desafio. Solicitações com moedas diferentes das suportadas serão rejeitadas.

---

## 3. Perguntas ao negócio

1. Caso um ou mais recebíveis de um lote apresentem erro, os recebíveis válidos devem continuar sendo processados individualmente ou qualquer falha deve invalidar o processamento de todo o lote?

2. A cotação apresentada durante a simulação deve ser garantida até a liquidação ou deve ser recalculada utilizando a FX mais recente? Existe um prazo de validade para a cotação caso o operador retome a operação posteriormente?

3. A Base Rate deve ser única para todas as operações ou pode variar conforme período, cliente, risco ou outro critério de negócio?

4. Caso o risco influencie a Base Rate, ele também deve influenciar o Spread? Se ambos forem afetados pelo risco, como deve ser feita essa composição para evitar dupla contabilização?

5. A liquidação deve ser considerada atômica por recebível individual ou pelo lote completo?

---

## 4. Precisão numérica

No banco de dados será utilizado NUMERIC, com escalas diferentes: para valores monetários, utilizaria escala `(19, 2)`, e para taxas, utilizaria uma escala `(10, 6)`. Na aplicação será utilizado BigDecimal para valores monetários, Base Rate, Spread e FX, evitando erros de representação de ponto flutuante. Valores monetários serão arredondados para duas casas decimais utilizando Half-Even, reduzindo o viés de arredondamento. Não haverá arredondamento durante as etapas intermediárias da fórmula. O PV será arredondado ao final da precificação, antes de ser apresentado ao operador e antes de eventual conversão cambial.

---

## 5. Critérios de aceite

- **Usabilidade:** o operador deve conseguir identificar os campos obrigatórios para realizar a simulação e, após o processamento, visualizar claramente o PV, o deságio e, quando aplicável, o valor convertido e a FX utilizada. Caso o operador coloque uma entrada inválida, ele recebe uma resposta personalizada para o campo que está com a entrada inválida.

- **Segurança:** entradas com valor de face menor ou igual a zero ou prazo inválido devem ser rejeitadas. Liquidações registradas devem permanecer imutáveis. Requisições repetidas que representem a mesma liquidação não devem gerar novos registros, retornando a operação previamente criada.

- **Desempenho:** utilizaremos lotes de 20 recebíveis que devem ser processados em até 2 segundos para poder ser testado e apresentado em ambiente local, para validação do desafio.

- **Precisão do Sistema:** os Golden Cases do desafio devem produzir os resultados esperados, sendo validados por testes automatizados.
