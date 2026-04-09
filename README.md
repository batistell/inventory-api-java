[🇺🇸 English](#english) | [🇧🇷 Português](#português)

---

<br/>

<h1 id="english">🇺🇸 English</h1>

# 📦 Inventory API - High-Performance Microservices Architecture


This project is the distributed **Inventory API** built with Java 21 and Spring Boot 3. It serves as a proof-of-concept and showcase for building robust, scalable backend systems utilizing modern distributed architecture patterns, designed specifically to operate alongside the `catalog-api-java` ecosystem.

Instead of a basic CRUD application, this API is designed to manage critical synchronization, database versioning, synchronous/asynchronous cross-microservice communication, and complex relational aggregations.

---

## 🛠️ Tech Stack & Infrastructure

- **Language & Framework:** Java 21, Spring Boot 3.2.4
- **Database:** PostgreSQL (Relational Database)
- **Database Versioning:** Flyway
- **Cross-Service Communication (Sync):** Spring Cloud OpenFeign
- **Message Broker (Async):** Apache Kafka (Event-Driven Architecture)
- **Infrastructure:** Docker & Docker Compose

To spin up the local isolated PostgreSQL database:
```bash
docker-compose up -d
```
*(Ensure the core `catalog-api` infrastructure like Kafka is also running)*

---

## 🧠 Architectural Choices & Demonstrations

This project is heavily engineered to demonstrate advanced backend patterns representing a true distributed microservice. Below are the specific choices used inside the codebase:

### 1. Database Versioning & Migrations (Flyway)
*Found in `src/main/resources/db/migration/V1__init.sql`*
- **Implementation:** Leverages Flyway Core mapped to a native PostgreSQL database.
- **Result:** Deprecates unreliable Hibernate ORM `ddl-auto=update` techniques. The database schema evolves deterministically via strictly controlled SQL scripts before the web server even boots.

### 2. Synchronous REST Communication (Spring Cloud OpenFeign)
*Found in `CatalogClient.java`*
- **Implementation:** Leverages declarative REST HTTP client mapping using `@FeignClient`.
- **Result:** Bypasses heavy-weight REST templates. The Inventory Service can explicitly poll the Catalog API for full product metadata to provide aggregated responses transparently across the network.

### 3. Event-Driven Asynchronous Consumers (Kafka)
*Found in `KafkaConsumerService.java`*
- **Implementation:** Uses robust Kafka listeners to subscribe to the `catalog.product.lifecycle` topics.
- **Result:** Provides Eventually Consistent data mapping. Whenever a product is created in the Catalog, the Inventory service automatically and silently provisions an initial stock row matching that product ID, completely disconnected from the original HTTP thread.

### 4. Advanced Native Relational Analytics
*Found in `InventoryRepository.java`*
- **Implementation:** Drops abstracted ORM paradigms for complex calculations by utilizing Spring Data `@Query(nativeQuery = true)`.
- **Result:** Executes deep database-side `GROUP BY` aggregations and mathematical operations natively inside Postgres without loading thousands of Java Objects into heap memory.

---

## 🚀 Running & Testing

1. **Launch Core Infrastructure (Kafka):**
   *(In the `catalog-api-java` folder)*
   ```bash
   docker-compose up -d
   ```
2. **Launch Inventory Database:**
   *(In the `inventory-api-java` folder)*
   ```bash
   docker-compose up -d
   ```
3. **Launch Application:**
   Run the project via your IDE (ensure Java 21 is selected). The API binds to port `8091`.
4. **Swagger UI:**
   Navigate to HTTP Swagger documentation: `http://localhost:8091/swagger-ui.html`

### Featured Endpoints to Test:
- `GET /api/inventory/analytics/stock-by-status` → Triggers the heavy **Native PostgreSQL** aggregation group-by query.
- `GET /api/inventory/{productId}/details` → Triggers the **Feign Client** cross-microservice REST relay, merging data from PostgreSQL with Catalog API's MongoDB.
- `GET /api/inventory/batch-async` → Uses **Virtual Threads** to concurrently fetch massive batches of inventory arrays concurrently.

<br/><br/>

<hr/>

<br/>

<h1 id="português">🇧🇷 Português</h1>

# 📦 Inventory API - Arquitetura de Microsserviços de Alta Performance

Este projeto é a **Inventory API** distribuída, construída com Java 21 e Spring Boot 3. Ele serve como uma prova de conceito e portfólio para a construção de sistemas essenciais robustos utilizando padrões modernos de arquitetura, desenhado especificamente para operar em conjunto com o ecossistema do `catalog-api-java`.

Em vez de uma aplicação CRUD básica, esta API foi arquitetada para gerenciar sincronização crítica, versionamento de banco de dados, comunicação síncrona/assíncrona entre microsserviços e agregações relacionais complexas.

---

## 🛠️ Stack Tecnológico e Infraestrutura

- **Linguagem e Framework:** Java 21, Spring Boot 3.2.4
- **Banco de Dados:** PostgreSQL (Banco de Dados Relacional)
- **Versionamento de Banco:** Flyway
- **Comunicação entre Serviços (Síncrono):** Spring Cloud OpenFeign
- **Mensageria (Assíncrono):** Apache Kafka (Arquitetura Orientada a Eventos)
- **Infraestrutura:** Docker e Docker Compose

Para iniciar o banco PostgreSQL isolado localmente:
```bash
docker-compose up -d
```
*(Garante que a infraestrutura core do `catalog-api`, como o Kafka, também esteja rodando)*

---

## 🧠 Decisões Arquiteturais e Demonstrações

Este projeto foi intensamente engenhado para demonstrar padrões avançados representando um microsserviço distribuído real. Abaixo estão as implementações específicas utilizadas:

### 1. Versionamento de Banco de Dados e Migrations (Flyway)
*Encontrado em `src/main/resources/db/migration/V1__init.sql`*
- **Implementação:** Utiliza Flyway associado nativamente ao banco PostgreSQL.
- **Resultado:** Substitui do ORM a criação automática via `ddl-auto=update`. O esquema de banco evolui de forma determinística via scripts SQL rigorosamente controlados antes mesmo do servidor web subir.

### 2. Comunicação Síncrona via REST (Spring Cloud OpenFeign)
*Encontrado em `CatalogClient.java`*
- **Implementação:** Utiliza clientes HTTP REST declarativos em interface com `@FeignClient`.
- **Resultado:** A API de Estoque consegue interrogar nativamente a API de Catálogo pelas informações daquele produto mesclando respostas síncronas de ponta a ponta na rede.

### 3. Consumidores Assíncronos Baseados em Eventos (Kafka)
*Encontrado em `KafkaConsumerService.java`*
- **Implementação:** Utiliza consumidores robustos ouvindo tópicos Kafka de ciclo de vida (`catalog.product.lifecycle`).
- **Resultado:** Proporciona "Consistência Eventual". Assim que o catálogo cria um produto, o serviço de Inventário provisiona silenciosa e automaticamente uma linha de estoque correspondente nas sombras, isolando completamente a thread HTTP.

### 4. Análise Nativa Relacional Avançada
*Encontrado em `InventoryRepository.java`*
- **Implementação:** Abandona os paradigmas fixos de ORM em prol de queries profundas via `@Query(nativeQuery = true)`.
- **Resultado:** Executa cálculos de agregação, contagens e somas através do comando condicional `GROUP BY` nativamente no motor do PostgreSQL, evitando carregar milhares de objetos diretamente na Heap Memory do Java.

---

## 🚀 Como Executar e Testar

1. **Iniciando a Infraestrutura Core (Kafka):**
   *(Na pasta `catalog-api-java`)*
   ```bash
   docker-compose up -d
   ```
2. **Iniciando o Banco de Inventário:**
   *(Na pasta `inventory-api-java`)*
   ```bash
   docker-compose up -d
   ```
3. **Iniciando a Aplicação:**
   Rode o projeto pela sua IDE (Garanta que o Java 21 esteja configurado). A API se ligará à porta `8091` por padrão.
4. **Swagger UI:**
   Acesse a documentação da API em: `http://localhost:8091/swagger-ui.html`

### Principais Endpoints para Testar:
- `GET /api/inventory/analytics/stock-by-status` → Ativa as queries analíticas pesadas **Nativas do PostgreSQL**.
- `GET /api/inventory/{productId}/details` → Aciona os conectores **Feign Client** mesclando objetos de domínio do PostgreSQL emparelhados ao MongoDB do serviço Catalog via HTTP.
- `GET /api/inventory/batch-async` → Utiliza **Virtual Threads** buscando grandes chaves arrays de registros simultaneamente.