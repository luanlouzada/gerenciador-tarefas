# Sistema Gerenciador de Tarefas

## Visão Geral

Sistema para controle e gerenciamento de tarefas com funcionalidades de cadastro, consulta, edição e exclusão. Inclui sistema de notificações para alertar sobre prazos de vencimento.

## Entidade: Tarefa

### Atributos

- **Título**: Nome identificador da tarefa
- **Descrição**: Detalhamento das informações da tarefa
- **Data Limite**: Prazo final para execução da tarefa
- **Status**: Estado atual da tarefa (pendente, em andamento, concluído)
- **Data de Criação**: Momento em que a tarefa foi registrada no sistema

## Funcionalidades

### Gerenciamento de Tarefas

- Cadastro de novas tarefas
- Listagem de tarefas com filtros por:
  - Data limite
  - Título
- Edição de tarefas existentes
- Exclusão de tarefas

### Regras de Negócio

- Não é permitido registrar tarefas com data limite anterior à data atual
- O título da tarefa deve conter no mínimo 20 caracteres e não pode estar vazio

## Sistema de Notificações

- Verificação diária de tarefas com vencimento no dia corrente
- Implementação de timer para processamento das notificações
- Envio de alertas por email aos usuários responsáveis
