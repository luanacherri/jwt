# JWT Validator API

[![GitHub Repository](https://img.shields.io/badge/github-repository-green.svg)](https://github.com/luana/jwt-validator)

## Repositório
O código fonte está disponível em: [https://github.com/luana/jwt-validator](https://github.com/luana/jwt-validator)

## Descrição
API REST para validação de JWTs (JSON Web Tokens) que verifica o formato e valida claims obrigatórios seguindo regras específicas de negócio. O projeto implementa uma solução completa com observabilidade, logs estruturados e monitoramento.

## 🚀 Tecnologias Utilizadas
- Java 21
- Spring Boot 3.2.2
- OpenTelemetry para observabilidade
- Logback para logging estruturado
- Prometheus + Grafana para métricas
- Jaeger para tracing distribuído
- Docker e Docker Compose para containerização

## ⚙️ Pré-requisitos
- Java 21+
- Maven 3.8+
- Docker e Docker Compose

## 🏃‍♂️ Como Executar

### Via Docker Compose (Recomendado)
1. Clone o repositório:
```bash
git clone https://github.com/luana/jwt-validator.git
cd jwt-validator
```

2. Execute:
```bash
docker-compose up -d
```

Os serviços estarão disponíveis em:
- JWT Validator: http://localhost:8081
- Swagger UI: http://localhost:8081
- Jaeger UI: http://localhost:16686
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (usuário: admin, senha: admin)

### Via Maven
1. Clone o repositório
2. Execute:
```bash
./mvnw spring-boot:run
```

## 📚 API Endpoints

### POST /api/v1/jwt/validate
Valida um JWT verificando seu formato e claims obrigatórios.

#### Request
```json
{
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
}
```

#### Response Success (200 OK)
```json
{
    "valid": true,
    "type": "https://api.validator.com/jwt/valid",
    "title": "JWT Válido"
}
```

#### Response Error (400 Bad Request)
```json
{
    "valid": false,
    "type": "https://api.validator.com/problems/invalid-jwt",
    "title": "JWT Inválido",
    "detail": "Claim 'Name' é inválido"
}
```

## 📋 Regras de Validação

### Claims Obrigatórios
O JWT deve conter exatamente 3 claims que são validados conforme as seguintes regras:

1. **Name**
   - Não pode conter números
   - Tamanho máximo de 256 caracteres
   - Case sensitive
   - Deve ser uma string não vazia

2. **Role**
   - Valores permitidos: "Admin", "Member", "External"
   - Case sensitive
   - Deve ser exatamente um dos valores permitidos

3. **Seed**
   - Deve ser um número primo
   - Deve ser um valor numérico válido
   - Não pode ser negativo

## 🔍 Observabilidade

### Logs
- Logs estruturados em formato JSON
- Rotação de logs diária com arquivamento
- Local: ./logs/jwt-validator.log
- Retenção: 30 dias
- Limite total: 3GB
- Níveis configurados por pacote
- MDC com request ID

### Tracing (Jaeger)
- UI: http://localhost:16686
- Features:
  - Propagação de contexto via B3
  - Rastreamento de requisições HTTP
  - Spans personalizados para validações
  - Anotações para operações importantes
  - Métricas de latência

### Métricas (Prometheus + Grafana)
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Métricas disponíveis:
  - JVM (heap, threads, gc)
  - HTTP (latência, códigos de resposta)
  - Sistema (CPU, memória)
  - Negócio (validações/s, erros/s)

## 📄 Documentação API
Swagger UI disponível em: http://localhost:8081

O Swagger inclui:
- Descrição detalhada dos endpoints
- Schemas dos requests/responses
- Exemplos de payloads
- Códigos de resposta possíveis
- Try-out interativo

## 🤔 Premissas Assumidas

1. **Formato e Validação do JWT**
   - JWT deve seguir o formato padrão com 3 partes separadas por "."
   - Payload deve ser um JSON válido em Base64
   - Validação fail-fast: primeira falha interrompe o processo
   - Claims são case-sensitive para garantir precisão
   - Não é necessário validar a assinatura do token

2. **Performance e Escalabilidade**
   - Validações são executadas em memória
   - Sem persistência de dados necessária
   - Cache não implementado por ser stateless
   - Pronto para containerização e orquestração

3. **Observabilidade**
   - Request ID gerado para cada requisição
   - Correlação entre logs, métricas e traces
   - Retenção de 30 dias baseada em práticas comuns
   - Granularidade de logs por pacote para debug

4. **Segurança**
   - CORS habilitado para facilitar testes
   - Rate limiting não implementado (pode ser adicionado se necessário)
   - Foco na validação estrutural do JWT

## 🏗️ Princípios SOLID Aplicados

### Single Responsibility Principle (SRP)
- `JwtController`: Responsável apenas pela exposição da API REST
- `JwtValidationService`: Coordena o processo de validação
- Classes específicas para cada tipo de validação:
  - `NameValidator`: Validação específica do claim Name
  - `RoleValidator`: Validação específica do claim Role
  - `SeedValidator`: Validação específica do claim Seed
- `TracingFilter`: Responsável apenas pelo trace das requisições

### Open/Closed Principle (OCP)
- Interface `ClaimValidator` permite adicionar novas validações sem modificar código existente
- `ValidatorChain` permite extensão das regras de validação
- `OpenTelemetryConfig` extensível para novos exporters
- Sistema de observabilidade permite adição de novas métricas sem alteração do código base

### Liskov Substitution Principle (LSP)
- Todos os validators implementam a interface `ClaimValidator`
- Possibilidade de trocar implementações sem afetar o comportamento do sistema
- Implementações de ExceptionHandler seguem contratos consistentes
- Testes unitários validam comportamento consistente entre implementações

### Interface Segregation Principle (ISP)
- Interface `ClaimValidator` enxuta e focada
- Separação clara entre interfaces de validação e interfaces de serviço
- `TracingInterceptor` com interfaces específicas para seu propósito
- DTOs específicos para cada tipo de operação

### Dependency Inversion Principle (DIP)
- Serviços dependem de abstrações (`ClaimValidator`) e não implementações
- Injeção de dependências via Spring Framework
- Configurações abstraídas em interfaces
- Uso de factories para criação de objetos complexos

Os princípios SOLID foram fundamentais na arquitetura do projeto, garantindo:
- Código mais manutenível e testável
- Facilidade para adicionar novas funcionalidades
- Redução de acoplamento entre componentes
- Melhor organização e coesão do código

## 🧪 Testes

### Executando os Testes
```bash
./mvnw test
```

### Cobertura de Testes
O projeto possui testes unitários e de integração cobrindo os principais cenários:

#### Testes Unitários
- `JwtValidationServiceTest`: Testes da lógica de validação dos claims
  - Validação do formato do JWT
  - Validação do claim Name (caracteres especiais, números, tamanho)
  - Validação do claim Role (valores permitidos)
  - Validação do claim Seed (números primos)
  - Cenários de erro e exceções

#### Testes de Integração
- `JwtControllerIntegrationTest`: Testes end-to-end da API
  - Validação de JWTs válidos
  - Validação de erros de formato
  - Validação de claims inválidos
  - Verificação de respostas HTTP
  - Headers e content types corretos

### Fixtures e Test Helpers
- Builders para criação de JWTs de teste
- Fixtures com tokens válidos e inválidos
- Utilitários para geração de tokens de teste

### Relatórios de Teste
Os relatórios de teste podem ser encontrados em:
- Relatórios Surefire: `target/surefire-reports/`
- Relatórios de Cobertura: `target/site/jacoco/`

### Integração com CI/CD
Os testes são executados automaticamente:
- Em cada commit
- Antes da geração de uma nova build
- Como parte do pipeline de CI/CD

## 🛠️ Ferramentas de Teste

### Insomnia
Uma coleção do Insomnia está disponível para testar a API facilmente.

#### Importando a Coleção
1. Abra o Insomnia
2. Clique em `Create` > `Import From` > `File`
3. Selecione o arquivo `insomnia_collection.json` do projeto
4. Clique em `Import`

#### Requests Disponíveis
1. **Validar JWT (Válido)**
   - Exemplo de JWT válido com todas as claims corretas
   - Role: "Admin", Seed: "7841" (primo), Name: "Toninho Araujo"

2. **Validar JWT (Nome Inválido)**
   - Exemplo com nome contendo números
   - Role: "External", Seed: "88037", Name: "M4ria Olivia"

3. **Validar JWT (Role Inválida)**
   - Exemplo com role não permitida
   - Role: "User" (não permitido)

4. **Validar JWT (Seed Inválido)**
   - Exemplo com número não primo
   - Seed: "100" (não é primo)

5. **Health Check**
   - Verifica o status da aplicação
   - Endpoint: GET /actuator/health

### Postman
Uma coleção do Postman também está disponível para testar a API.

#### Importando a Coleção
1. Abra o Postman
2. Clique em `Import` > `File`
3. Selecione o arquivo `postman_collection.json` do projeto
4. Clique em `Import`

#### Requests Disponíveis
1. **Validar JWT (Válido)**
   - Exemplo de JWT válido com todas as claims corretas
   - Role: "Admin", Seed: "7841" (primo), Name: "Toninho Araujo"

2. **Validar JWT (Nome Inválido)**
   - Exemplo com nome contendo números
   - Role: "External", Seed: "88037", Name: "M4ria Olivia"

3. **Validar JWT (Role Inválida)**
   - Exemplo com role não permitida
   - Role: "User" (não permitido)

4. **Validar JWT (Seed Inválido)**
   - Exemplo com número não primo
   - Seed: "100" (não é primo)

5. **Health Check**
   - Verifica o status da aplicação
   - Endpoint: GET /actuator/health

### Curl
Você também pode usar curl para testar a API. Exemplos:

```bash
# Health Check
curl http://localhost:8081/actuator/health

# Validar JWT
curl -X POST http://localhost:8081/api/v1/jwt/validate \
  -H "Content-Type: application/json" \
  -d '{"jwt": "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"}'
```

### Swagger UI
A documentação interativa da API está disponível em http://localhost:8081/

- Documentação completa dos endpoints
- Interface para testar as requisições
- Exemplos de requests e responses
- Descrição dos códigos de erro

### Monitoramento

1. **Jaeger UI (Tracing)**
   - URL: http://localhost:16686
   - Visualize traces distribuídos
   - Análise de latência
   - Debugging de requisições

2. **Prometheus (Métricas)**
   - URL: http://localhost:9090
   - Métricas da aplicação
   - Queries personalizadas
   - Alertas configuráveis

3. **Grafana (Dashboards)**
   - URL: http://localhost:3000
   - Login: admin/admin
   - Visualização de métricas
   - Dashboards personalizados

## 🏗️ Estrutura do Projeto
```
jwt-validator/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── config/              # Configurações Spring e Infra
│   │   │   │   ├── JacksonConfig.java
│   │   │   │   ├── MetricsConfig.java
│   │   │   │   ├── OpenTelemetryConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/          # Controllers REST
│   │   │   │   ├── JwtController.java
│   │   │   │   └── dto/
│   │   │   └── service/             # Lógica de Negócio
│   │   │       ├── JwtValidationService.java
│   │   │       └── validator/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test/                        # Testes
├── docker-compose.yml               # Definição dos serviços
├── Dockerfile                       # Build da aplicação
└── prometheus.yml                   # Config do Prometheus
```

