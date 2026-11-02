# StudyGroup

StudyGroup is a complete Spring Boot MVC university project for creating, joining, and managing collaborative study groups.

## Highlights

- Java 21, Spring Boot, Maven, Thymeleaf, MySQL
- Layered MVC architecture: controllers, services, repositories, entities, DTO/form objects, utilities
- Strong OOP demonstration: abstract base classes, inheritance, interfaces, polymorphic resources, method overloading, encapsulated domain behavior
- Multiple modern UI pages: home, login, register, dashboard, groups, group details, chat, profile, settings, notifications, search, about
- Functional CRUD for users, groups, memberships, messages, resources, notifications, and categories

## Run the desktop application

1. Create or start MySQL.
2. Optionally set environment variables:
   - `DB_URL=jdbc:mysql://localhost:3307/studygroup_app_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `DB_USERNAME=root`
   - `DB_PASSWORD=your_password`
3. Run:

```bash
mvn javafx:run
```

The main deliverable is a JavaFX desktop application window. It opens first on the login page, with register and about actions available immediately. The Spring Boot application context still powers the services, repositories, OOP architecture, and MySQL integration.

The application seeds IHEC-style demo data: Licence Sciences de Gestion, Licence Informatique de Gestion - BI, and masters in Entrepreneuriat, Analyste Financier, Big Data Analytics et E-commerce, Marketing, Comptabilite, Management, and Finance.

The configured database is only `studygroup_app_db`. If you still see an older database from a previous run and you no longer need it, remove it manually in MySQL:

```sql
DROP DATABASE studygroup_db;
```

Demo accounts after the first successful run:

- `lina@student.edu` / `1234`
- `adam@student.edu` / `1234`
- `maya@student.edu` / `1234`

## Optional web fallback

The project still contains Thymeleaf MVC pages from the first version. If needed, they can be launched with:

```bash
mvn spring-boot:run
```
