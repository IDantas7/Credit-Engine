# AI Usage

Durante o desenvolvimento do desafio utilizei IA como uma ferramenta de apoio, principalmente para dividir o desenvolvimento em tasks menores, tirar dúvidas de sintaxe e auxiliar na criação de cenários de testes.

## Como utilizei

Durante a implementação fiz consultas pontuais sobre Java e Spring, como métodos e operações com `BigDecimal`, além de dúvidas sobre organização do projeto.

Também utilizei a IA para auxiliar na criação de testes unitários. Normalmente eu implementava a regra e os primeiros testes e utilizava a IA para ajudar a pensar nos próximos cenários.

Um exemplo foi o `PricingApplicationService`. Depois de implementar os Golden Cases C1 e C2, utilizei a IA para me ajudar a criar o teste do C3 seguindo a mesma estrutura. Também pedi um cenário para validar a `ExchangeRateNotFoundException` quando uma operação em USD não possui uma taxa de câmbio disponível.

## Erro identificado

Durante a criação do teste do Golden Case C3, a IA confundiu o `presentValue` com o `convertedValue`.

Identifiquei o problema comparando a resposta com os dados fornecidos pelo próprio Golden Case. O `presentValue` deveria continuar representando o valor presente em BRL, enquanto o `convertedValue` representava esse valor após a conversão para USD.

Após identificar o problema, corrigi o teste para validar separadamente o valor presente, o deságio, a taxa de câmbio e o valor convertido.

## O que não deleguei

Não deleguei completamente para a IA as decisões relacionadas às regras de negócio e à estrutura principal do projeto.

Fiz questão de entender e decidir principalmente:

- os cálculos de precificação e suas regras;
- a divisão das responsabilidades entre API, Application, Domain e Infrastructure;
- o uso de Strategy para separar as regras de Duplicata e Cheque;
- a implementação dos Controllers, utilizando IA apenas como apoio pontual.

Utilizei a IA como apoio durante o desenvolvimento, mas validei as sugestões através dos requisitos do desafio, dos Golden Cases e dos testes da aplicação.