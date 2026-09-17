# Decisões e Simplificações

Este documento apresenta algumas decisões de escopo tomadas durante o desenvolvimento do Credit Engine.

A ideia foi priorizar o funcionamento das regras de negócio, a precisão dos cálculos e a clareza da implementação, evitando adicionar complexidade que não fosse necessária para demonstrar o funcionamento do sistema.

## 1. Cotação de câmbio manual

**Decisão:** utilizar o cadastro manual das cotações de câmbio em vez de realizar uma integração com um serviço externo.

O próprio desafio deixa em aberto algumas decisões relacionadas ao câmbio, como qual taxa deve ser considerada no momento da operação.

Pensando no objetivo de manter a solução com corretude e clareza, optei por trabalhar com cotações cadastradas manualmente através do endpoint:

```http POST /exchange-rates```

## 2. Frontend simples e funcional

**Decisão:** desenvolver um frontend React simples, focado apenas nos principais fluxos do Credit Engine.

O frontend permite:

- cadastrar uma cotação;
- realizar uma simulação;
- visualizar os resultados;
- realizar uma liquidação;
- visualizar as liquidações realizadas.

### Motivo

Para o escopo do desafio, o objetivo do frontend foi demonstrar de forma simples o funcionamento e a integração com o backend.

Por isso, optei por não adicionar recursos como gerenciamento de estado global, autenticação, rotas complexas ou bibliotecas de interface.

A prioridade permaneceu nas regras de negócio e no funcionamento do backend.

### Possível evolução

O frontend poderia futuramente receber melhorias de interface, autenticação, navegação entre páginas e um tratamento mais completo dos estados da aplicação.

---

## 3. Autenticação e usuários fora do escopo

**Decisão:** não implementar autenticação e gerenciamento de usuários.

### Motivo

Esses recursos não são necessários para demonstrar as principais regras propostas no desafio, relacionadas à precificação, câmbio e liquidação de recebíveis.

Adicionar autenticação aumentaria o escopo sem contribuir diretamente para a validação dessas regras.

### Possível evolução

Em um cenário de produção, seriam necessários autenticação, autorização e definição de permissões para controlar quais usuários poderiam realizar simulações, cadastrar cotações ou executar liquidações.

---

## 4. Priorização das regras de negócio e testes

**Decisão:** priorizar a implementação e os testes das funcionalidades principais em vez de aumentar o número de funcionalidades do projeto.

### Motivo

Durante o desenvolvimento, priorizei principalmente:

- cálculo do valor presente;
- deságio;
- Strategies de Duplicata e Cheque;
- conversão de moeda;
- Golden Cases;
- liquidação;
- idempotência;
- persistência;
- tratamento de erros.

A ideia foi entregar um fluxo principal funcional e que eu conseguisse entender, testar e explicar, em vez de adicionar funcionalidades de maior complexidade apenas para aumentar o escopo da solução.

### Possível evolução

O projeto poderia evoluir com recursos como observabilidade, métricas, maior cobertura de testes de integração e mecanismos adicionais de resiliência.