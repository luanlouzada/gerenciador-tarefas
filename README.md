# Gerenciador de Tarefas

Este projeto é um sistema de gerenciamento de tarefas construído com Spring Boot, seguindo os princípios da Clean Architecture. Ele permite aos usuários criar, listar, atualizar e excluir tarefas, com recursos como rastreamento de status e gerenciamento de prazos.

## Arquitetura

A aplicação segue os princípios da Clean Architecture com as seguintes camadas:

- **Domain**: Contém entidades de negócio, exceções e enumerações
- **Use Cases**: Contém a lógica de negócio da aplicação
- **Application**: Implementa os casos de uso e conecta com recursos externos
- **Infrastructure**: Contém controllers, repositórios, configuração, segurança, etc.

## Funcionalidades

- Registro e autenticação de usuários com token JWT
- Operações CRUD para tarefas
- Listagem de tarefas com filtros (por data e título)
- Gerenciamento de status de tarefas (EM_ANDAMENTO, PENDENTE, RASCUNHO, CONCLUÍDO, EXPIRADO)
- Validação de propriedades de tarefas (ex: título com tamanho mínimo de 20 caracteres)
- Prevenção de criação de tarefas com datas de vencimento no passado
- Notificações para tarefas que estão próximas do vencimento

## Como Começar

### Pré-requisitos

- JDK 21
- Maven
- Docker e Docker Compose
- PostgreSQL

### Configuração e Execução

1. Clone o repositório:

   ```
   git clone <url-do-repositorio>
   cd gerenciador
   ```

2. Inicie o banco de dados PostgreSQL usando Docker:

   ```
   make docker-up
   ```

3. Rode as migrations

   ```
   make db-migrate
   ```

4. Compile a aplicação:

   ```
   make all
   ```

5. Execute a aplicação:

   ```
   make run
   ```

6. Acesse a aplicação:
   - API: http://localhost:8080/api-docs
   - Swagger UI: http://localhost:8080/swagger-ui/index.html

## Documentação da API

A documentação da API está disponível através do Swagger UI:

1. Inicie a aplicação
2. Abra http://localhost:8080/swagger-ui.html no seu navegador
3. Explore os endpoints disponíveis:
   - `/api/v1/user`: Registro e autenticação de usuários
   - `/api/v1/task`: Endpoints de gerenciamento de tarefas

## Exemplo de Uso

1. Registrar um novo usuário:

   - Endpoint: `POST /api/v1/user/registerUser`
   - Corpo:
     ```json
     {
       "email": "usuario@exemplo.com",
       "name": "João Silva",
       "password": "Senha123!"
     }
     ```

2. Login para obter um token JWT:

   - Endpoint: `POST /api/v1/user/login`
   - Corpo:
     ```json
     {
       "email": "usuario@exemplo.com",
       "password": "Senha123!"
     }
     ```

3. Criar uma nova tarefa (use o token no cabeçalho Authorization):

   - Endpoint: `POST /api/v1/task`
   - Cabeçalho: `Authorization: Bearer <seu-token-jwt>`
   - Corpo:
     ```json
     {
       "title": "Completar a documentação do projeto",
       "description": "Escrever toda a documentação necessária para o projeto",
       "dueAt": "2023-12-31T23:59:59"
     }
     ```

4. Listar todas as tarefas:
   - Endpoint: `GET /api/v1/task`
   - Cabeçalho: `Authorization: Bearer <seu-token-jwt>`

## Possíveis Melhorias

### Testes

- Adicionar testes unitários para casos de uso e lógica de domínio
- Adicionar testes de integração para endpoints da API
- Implementar relatórios de cobertura de testes
- Adicionar testes de contrato para dependências externas

### Funcionalidades

- Implementar notificações por e-mail para tarefas próximas do vencimento
- Adicionar categorias e tags para tarefas
- Implementar tarefas recorrentes
- Adicionar níveis de prioridade para tarefas
- Implementar subtarefas
- Adicionar comentários em tarefas
- Implementar atribuição de tarefas para outros usuários
- Adicionar anexos de arquivos às tarefas
- Adicionar SoftDelete em Usuário e Tarefas

### Segurança

- Implementar tokens de atualização (refresh tokens)
- Adicionar controle de acesso baseado em funções
- Implementar autenticação de múltiplos fatores
- Implementar configuração CORS ✅
- Adicionar limitação de taxa de API ✅

### Arquitetura

- Implementar arquitetura orientada a eventos para notificações
- Extrair serviço de notificação para um microsserviço separado
- Implementar fila de mensagens para processamento assíncrono
- Adicionar cache para dados acessados frequentemente (MUITO CARO, NÃO VIÁVEL)
- Implementar versionamento de API
- Adicionar endpoints de verificação de saúde (health check)
- Implementar circuit breakers para resiliência

### DevOps

- Configurar pipeline CI/CD
- Fazer deploy para um provedor de nuvem (AWS, Azure, GCP)
- Configurar monitoramento e alertas
- Implementar infraestrutura como código(CloudFormation ou Terraform)
- Adicionar estratégia de migração de banco de dados para produção

### Performance

- Otimizar consultas ao banco de dados
- Implementar paginação para grandes conjuntos de resultados
- Adicionar indexação de banco de dados para campos frequentemente consultados
- Implementar estratégias de cache

### Implementar um FrontEnd

- Desenvolver interface responsiva com React/Angular/Vue usando design system criado no Figma
- Implementar recursos de ordenação, filtragem e atualização em tempo real das tarefas
- Adicionar visualizações em lista, kanban e calendário com drag-and-drop para mudança de status
- Garantir otimização para dispositivos móveis e conformidade com padrões de acessibilidade

### Criar a documentação do System Design

- Elaborar diagramas C4, fluxo de dados e decisões arquiteturais do sistema
- Documentar padrões de integração, escalabilidade e requisitos não-funcionais

## Licença

Apache 2.0

## Contribuidores
