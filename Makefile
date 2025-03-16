# Variáveis
MVN_WRAPPER = ./mvnw
TARGET_DIR = target
COMPOSE_PROJECT_NAME = tasksmanager
DB_NAME = tasksmanager

# Variáveis de ambiente para banco de dados - valores padrão se não definidos
DATABASE_URL ?= jdbc:postgresql://localhost:5432/tasksmanager
DATABASE_USERNAME ?= admin
DATABASE_PASSWORD ?= admin

# Extrai host e porta do DATABASE_URL para Docker e comandos específicos
DB_HOST = $(shell echo $(DATABASE_URL) | sed -n 's/.*:\/\/\([^:]*\).*/\1/p')
DB_PORT = $(shell echo $(DATABASE_URL) | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')

# Comandos principais
.PHONY: all clean build run test install verify help docker-up docker-down docker-restart dev setup-permissions check-db db-init db-migrate db-reset

# Configura permissões
setup-permissions:
	@echo "Configurando permissões..."
	@sudo chown -R $(USER):$(USER) .
	@sudo chmod -R 755 .
	@chmod +x $(MVN_WRAPPER)
	@mkdir -p $(TARGET_DIR)
	@chmod 777 $(TARGET_DIR)

# Comando padrão
all: clean build

# Limpa os arquivos compilados e cache
clean:
	@echo "Limpando arquivos..."
	@rm -rf $(TARGET_DIR) || true
	@rm -rf ~/.m2/repository/io/tasksmanager || true
	@$(MVN_WRAPPER) clean || true

# Compila e empacota o projeto
build:
	$(MVN_WRAPPER) package

# Executa o projeto
run:
	DATABASE_URL=$(DATABASE_URL) \
	DATABASE_USERNAME=$(DATABASE_USERNAME) \
	DATABASE_PASSWORD=$(DATABASE_PASSWORD) \
	$(MVN_WRAPPER) spring-boot:run

# Executa os testes
test:
	$(MVN_WRAPPER) test

# Instala dependências
install:
	$(MVN_WRAPPER) install

# Verifica o projeto sem executar os testes
verify:
	$(MVN_WRAPPER) verify -DskipTests

# Comandos Docker
docker-up:
	COMPOSE_PROJECT_NAME=$(COMPOSE_PROJECT_NAME) docker compose up -d

docker-down:
	COMPOSE_PROJECT_NAME=$(COMPOSE_PROJECT_NAME) docker compose down

docker-restart: docker-down docker-up

# Limpa volumes do Docker
docker-clean:
	COMPOSE_PROJECT_NAME=$(COMPOSE_PROJECT_NAME) docker compose down -v
	rm -rf ./data

# Setup completo
setup: docker-clean docker-up
	sleep 5
	make clean install

# Ajuda
help:
	@echo "Comandos disponíveis:"
	@echo "  make clean         - Limpa os arquivos compilados"
	@echo "  make build         - Compila e empacota o projeto"
	@echo "  make run           - Executa o projeto"
	@echo "  make test          - Executa os testes"
	@echo "  make install       - Instala dependências"
	@echo "  make verify        - Verifica o projeto sem executar testes"
	@echo "  make all           - Limpa e reconstrói o projeto"
	@echo "  make docker-up     - Inicia os containers Docker"
	@echo "  make docker-down   - Para os containers Docker"
	@echo "  make docker-restart- Reinicia os containers Docker"
	@echo "  make docker-clean  - Remove containers e volumes"
	@echo "  make setup         - Configuração completa do ambiente"

# Verifica se o banco está rodando
check-db:
	@echo "Verificando banco de dados..."
	@docker ps | grep tasksmanager-postgres || (echo "Iniciando banco de dados..." && make docker-up && sleep 10)

# Comandos de Banco de Dados
db-init: check-db
	@echo "Inicializando banco de dados..."
	@docker exec -i tasksmanager-postgres psql -U $(DATABASE_USERNAME) -d postgres -c "DROP DATABASE IF EXISTS $(DB_NAME);"
	@docker exec -i tasksmanager-postgres psql -U $(DATABASE_USERNAME) -d postgres -c "CREATE DATABASE $(DB_NAME);"
	@echo "Banco de dados $(DB_NAME) criado com sucesso"

db-migrate:
	@echo "Executando migrações..."
	$(MVN_WRAPPER) flyway:migrate \
		-Dflyway.url=$(DATABASE_URL) \
		-Dflyway.user=$(DATABASE_USERNAME) \
		-Dflyway.password=$(DATABASE_PASSWORD) \
		-Dflyway.locations=filesystem:src/main/resources/db/migration \
		-Dflyway.baselineOnMigrate=true \
		-Dflyway.validateOnMigrate=true

db-clean:
	@echo "Limpando todas as migrações do Flyway..."
	$(MVN_WRAPPER) flyway:clean \
	-Dflyway.url=$(DATABASE_URL) \
	-Dflyway.user=$(DATABASE_USERNAME) \
	-Dflyway.password=$(DATABASE_PASSWORD)

db-repair:
	@echo "Reparando o histórico do Flyway..."
	$(MVN_WRAPPER) flyway:repair \
	-Dflyway.url=$(DATABASE_URL) \
	-Dflyway.user=$(DATABASE_USERNAME) \
	-Dflyway.password=$(DATABASE_PASSWORD)

db-reset: db-init db-migrate
	@echo "Banco de dados resetado e migrado com sucesso"

# Executa o projeto em modo desenvolvimento com hot reload e banco de dados
dev: setup-permissions check-db db-reset
	@echo "Iniciando em modo desenvolvimento com hot reload..."
	export SPRING_PROFILES_ACTIVE=dev && \
	export DATABASE_URL=$(DATABASE_URL) && \
	export DATABASE_USERNAME=$(DATABASE_USERNAME) && \
	export DATABASE_PASSWORD=$(DATABASE_PASSWORD) && \
	$(MVN_WRAPPER) spring-boot:run \
		-Dspring-boot.run.jvmArguments="-XX:TieredStopAtLevel=1 -noverify -Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true" \
		-Dspring-boot.run.addResources=true \
		-Dcheckstyle.skip=true

# Executa verificação de estilo de código
lint:
	$(MVN_WRAPPER) checkstyle:check

# Atualiza a documentação do Swagger
swagger:
	$(MVN_WRAPPER) clean compile spring-boot:run -Dspring-boot.run.profiles=dev