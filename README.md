# 🚀 AI Model Sharing Platform

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-Build-blue?style=for-the-badge&logo=apachemaven)
![JUnit](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5)

A software engineering project modeling an **AI Model Sharing Platform** and managing **Shared GPU Cluster Resources**. It was developed in Java following a layered architecture pattern (MVC/DAO).

## 📝 Overview

The **AI Model Sharing Platform** system provides a structured environment to share computational resources (GPUs) for training and hosting AI models.

**Key concepts explored:**
-   **Software Architecture**: Layered design (`domain`, `dao`, `business`, `controllers`, `view`) to ensure separation of concerns.
-   **Design Patterns**: Application of standard OOP design patterns for robust, maintainable code.
-   **Security**: Password hashing via `jbcrypt`.
-   **Quality Assurance**: Unit testing using **JUnit 5** and code coverage analysis via **JaCoCo**.
-   **Documentation**: Includes UML diagrams and a LaTeX-compiled project report.

## 📄 Project Report

The repository includes a comprehensive project report written in LaTeX ([ReportLaTeX/main.pdf](ReportLaTeX/main.pdf)). The report details:
-   **Requirements and Design**: Use Case diagrams, templates, Mockups of the CLI interface, and Page Navigation diagrams.
-   **Architecture**: Package and Class diagrams illustrating the MVC/DAO layered architecture.
-   **Design Patterns**: Detailed explanations of the applied design patterns:
    -   **Observer Pattern**: Used for load balancing and monitoring GPU temperature.
    -   **Strategy Pattern**: Used to implement different GPU load balancing algorithms (Even and Eco balancing).
    -   **Singleton Pattern**: Used for the central `Timer` and the `GpuCluster` manager.
-   **Implementation Details**: Code snippets and explanations of core methods in the Domain and Business Logic layers.

## 📂 Project Structure

```text
├── ReportLaTeX/       # LaTeX source files for the project report
├── UML/               # UML diagrams (Class, Use Case, Sequence, etc.)
├── pom.xml            # Maven configuration and dependencies
└── src/
    └── main/java/it/unifi/ing/
        ├── domain/       # Core business entities and models
        ├── dao/          # Data Access Objects (Persistence layer)
        ├── business/     # Business logic and services
        ├── controllers/  # Application flow controllers
        └── view/         # User Interface (Main application entry point)
```

## 🛠️ Technologies & Dependencies

-   **Language**: Java 17
-   **Build Tool**: Apache Maven
-   **Testing**: JUnit Jupiter (5.10.2)
-   **Security**: jBCrypt (0.4)
-   **Code Coverage**: JaCoCo Maven Plugin (0.8.11)

## 🚀 Installation & Usage

### 1. Requirements

Ensure you have the following installed on your system:
-   **JDK 17** (or newer)
-   **Apache Maven**

### 2. Compilation

To compile the project and download all dependencies, use Maven:

```bash
mvn clean compile
```

### 3. Running the Application

You can execute the application directly through Maven using the `exec:java` plugin:

```bash
mvn exec:java
```
*(This command will launch `it.unifi.ing.view.Main`)*

### 4. Running Tests & Generating Coverage Reports

To run all unit tests and generate the JaCoCo coverage report:

```bash
mvn test
```

After running the tests, the coverage report will be available in the `target/site/jacoco` directory.

---
*Developed as a Software Engineering (SWE) project.*