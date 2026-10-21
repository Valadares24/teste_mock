# Sistema de Cadastro de Clientes - CRUD Web com Testes Automatizados

Aplicação web desenvolvida com **Spring Boot**, **JPA/Hibernate**, banco de dados relacional **PostgreSQL**, interface com **HTML5/Bootstrap 5/JavaScript** e testes de integração end-to-end com **Selenium WebDriver**, além de testes de unidade com **JUnit 5 / Mockito**.

---

## 📋 Requisitos Atendidos

| Camada / Requisito | Detalhes da Implementação |
| :--- | :--- |
| **Interface de Usuário (Front-End)** | Tela web moderna e responsiva com 3 campos de entrada (`Nome`, `E-mail`, `Telefone`), botão para salvar/cadastrar, botão para editar e botão para excluir registros na tabela dinâmica. |
| **Camada de Aplicação (Back-End)** | **Spring Boot (Java 17)** com arquitetura em camadas (`Model`, `Repository`, `Service`, `Controller`). Implementação completa das operações de Create, Read, Update e Delete (CRUD) com validações de regras de negócio. |
| **Camada de Persistência** | **PostgreSQL** com a tabela relacional `clientes`. Mapeamento objeto-relacional realizado via **JPA / Hibernate** com anotações `@Entity`, `@Table`, `@Id`, `@GeneratedValue(strategy = IDENTITY)`. |
| **Teste 1 (Insert - Selenium)** | Preenche os dados de entrada pela interface do navegador e valida tanto a exibição na tabela quanto a persistência real no banco de dados. |
| **Teste 2 (Update - Selenium)** | Aciona a edição de um cliente pela interface, altera os dados, salva e valida a atualização tanto na tela quanto no banco de dados. |
| **Teste 3 (Delete - Selenium)** | Aciona a exclusão de um registro pela interface, confirma o alerta nativo (`window.confirm`), verifica o desaparecimento na tela e a exclusão definitiva no banco de dados. |
| **Testes de Unidade** | 8 testes com JUnit 5 e Mockito validando regras de negócio no `ClienteService` (validação de e-mail duplicado, integridade de campos, exclusão e atualização). |

---

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.3.4**
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-test`
- **PostgreSQL 18** (Driver `org.postgresql:postgresql`)
- **Hibernate / JPA**
- **Selenium WebDriver 4.25.0** (com Chrome / ChromeDriver via `WebDriverManager`)
- **Bootstrap 5** (Front-end responsivo)
- **JUnit 5 & Mockito**

---

## 🚀 Como Executar a Aplicação

### 1. Pré-requisitos
- PostgreSQL rodando localmente na porta padrão `5432` com usuário `postgres` e senha configurada em `src/main/resources/application.properties` (banco `crud_db`).

### 2. Inicialização do Servidor Web
No terminal, execute:
```powershell
.\run_mvn.ps1 spring-boot:run
```
*(ou se preferir utilizar o Maven global: `mvn spring-boot:run`)*

Acesse a interface no navegador:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🧪 Como Executar os Testes Automatizados

### Executar todos os testes (Unidade + Integração Selenium):
```powershell
.\run_mvn.ps1 test
```

### Executar apenas os testes de integração com Selenium WebDriver:
```powershell
.\run_mvn.ps1 test -Dtest=ClienteSeleniumIntegrationTest
```

### Executar apenas os testes de unidade da camada de serviço:
```powershell
.\run_mvn.ps1 test -Dtest=ClienteServiceTest
```

