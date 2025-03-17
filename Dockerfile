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

# Cria um script de inicialização melhorado
RUN echo '#!/bin/bash\n\
echo "Esperando o banco de dados inicializar..."\n\
\n\
# Ajustar formato da URL e extrair credenciais\n\
JDBC_URL=$DATABASE_URL\n\
DB_HOST="localhost"\n\
DB_PORT="5432"\n\
DB_NAME="postgres"\n\
DB_USER=$DATABASE_USERNAME\n\
DB_PASS=$DATABASE_PASSWORD\n\
\n\
# Converter formato postgresql:// para jdbc:postgresql://\n\
if [[ $DATABASE_URL == postgresql://* ]]; then\n\
  # Extrair credenciais da URL no formato postgresql://user:pass@host:port/dbname\n\
  if [[ $DATABASE_URL =~ postgresql://([^:]+):([^@]+)@([^:/@]+)(:([0-9]+))?/(.+) ]]; then\n\
    DB_USER="${BASH_REMATCH[1]}"\n\
    DB_PASS="${BASH_REMATCH[2]}"\n\
    DB_HOST="${BASH_REMATCH[3]}"\n\
    DB_PORT="${BASH_REMATCH[5]:-5
    DB_NAME="${BASH_REMATCH[6]}"\n\
    # Construir URL JDBC\n\
    JDBC_URL="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME"\n\
    echo "URL convertida para formato JDBC: $JDBC_URL"\n\
  else\n\
    echo "Não foi possível extrair dados da URL postgresql://"\n\
    exit 1\n\
  fi\n\
# Extrair detalhes da URL já no formato jdbc:postgresql://\n\
elif [[ $DATABASE_URL =~ jdbc:postgresql://([^:/@]+)(:([0-9]+))?/(.+) ]]; then\n\
  DB_HOST="${BASH_REMATCH[1]}"\n\
  DB_PORT="${BASH_REMATCH[3]:-5432}"\n\
  DB_NAME="${BASH_REMATCH[4]}"\n\
else\n\
  echo "Formato de DATABASE_URL não reconhecido: $DATABASE_URL"\n\
  exit 1\n\
fi\n\
\n\
echo "Conectando ao host: $DB_HOST porta: $DB_PORT banco: $DB_NAME usuário: $DB_USER"\n\
\n\
# Aguardar até que o banco de dados esteja disponível\n\
until PGPASSWORD=$DB_PASS psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "\\q"; do\n\
  >&2 echo "Banco de dados indisponível - esperando..."\n\
  sleep 3\n\
done\n\
\n\
echo "Banco conectado com sucesso!"\n\
\n\
# Definir variáveis para a aplicação\n\
export DATABASE_URL=$JDBC_URL\n\
export DATABASE_USERNAME=$DB_USER\n\
export DATABASE_PASSWORD=$DB_PASS\n\
\n\
echo "Executando migrações..."\n\
make db-migrate\n\
\n\
echo "Iniciando a aplicação..."\n\
java -jar app.jar\n\
' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

# Comando para iniciar a aplicação usando o script
ENTRYPOINT ["/app/entrypoint.sh"]