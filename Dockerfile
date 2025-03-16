# Estágio de build usando Maven
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo POM primeiro para aproveitar o cache
COPY pom.xml .

# Baixa as dependências (separadamente para melhorar o cache)
RUN mvn dependency:go-offline -B

# Copia o código-fonte e faz o build
COPY src ./src
COPY Makefile ./
RUN mvn clean package -DskipTests

# Estágio final com JRE 21
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copia o JAR gerado do estágio de build
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/Makefile ./

# Instala ferramentas necessárias
RUN apt-get update && apt-get install -y \
    curl \
    make \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Expõe a porta usada pela aplicação
EXPOSE 8080

# Define as variáveis de ambiente necessárias
ENV DATABASE_URL=jdbc:postgresql://postgres:5432/tasksmanager
ENV DATABASE_USERNAME=admin
ENV DATABASE_PASSWORD=admin
ENV JWT_SECRET=your_jwt_secret_key
ENV ALLOWED_ORIGINS=https://gerenciador-frontend-five.vercel.app

# Cria um script de inicialização com expressão corrigida
RUN echo '#!/bin/bash\n\
echo "Esperando o banco de dados inicializar..."\n\
\n\
# Extrair o host e porta do DATABASE_URL\n\
if [[ "$DATABASE_URL" =~ jdbc:postgresql://([^:/]+)(:([0-9]+))?/ ]]; then\n\
  DB_HOST="${BASH_REMATCH[1]}"\n\
  DB_PORT="${BASH_REMATCH[3]:-5432}"\n\
  echo "Conectando ao banco de dados no host: $DB_HOST porta: $DB_PORT"\n\
else\n\
  echo "DATABASE_URL inválida: $DATABASE_URL"\n\
  exit 1\n\
fi\n\
\n\
# Aguardar até que o banco de dados esteja disponível\n\
until PGPASSWORD=$DATABASE_PASSWORD psql -h "$DB_HOST" -p "$DB_PORT" -U "$DATABASE_USERNAME" -c "\\q"; do\n\
  >&2 echo "Banco de dados indisponível - esperando..."\n\
  sleep 2\n\
done\n\
\n\
echo "Executando migrações do banco de dados..."\n\
make db-migrate\n\
\n\
echo "Iniciando a aplicação..."\n\
java -jar app.jar\n\
' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

# Healthcheck para verificar se a aplicação está rodando
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para iniciar a aplicação usando o script
ENTRYPOINT ["/app/entrypoint.sh"]