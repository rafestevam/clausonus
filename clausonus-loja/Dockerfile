FROM registry.access.redhat.com/ubi8/openjdk-17:1.14 AS build

USER root
WORKDIR /app

# Copiar arquivos de dependências
COPY pom.xml ./
COPY src ./src

# Construir a aplicação
RUN mvn package -DskipTests

# Segunda etapa - imagem de execução
FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.14

WORKDIR /deployments

# Copiar artefatos da fase de build
COPY --from=build /app/target/quarkus-app/lib/ ./lib/
COPY --from=build /app/target/quarkus-app/*.jar ./
COPY --from=build /app/target/quarkus-app/app/ ./app/
COPY --from=build /app/target/quarkus-app/quarkus/ ./quarkus/

# Configurar diretório de logs
RUN mkdir -p /deployments/logs && \
    chmod -R 775 /deployments/logs

# Variáveis de ambiente
ENV QUARKUS_PROFILE=prod

# Expor a porta
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "quarkus-run.jar"]