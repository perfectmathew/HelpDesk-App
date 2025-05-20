# HelpDeskApp

A web application for managing help desk tickets, created for an engineering paper. It allows users with different roles (admin, manager, user, worker) to interact with tickets, manage users, departments, and more. Features include ticket management, user authentication with TOTP, notifications, and file attachments.

## Tech Stack

**Backend:**
*   Java 17
*   Spring Boot 2.7.4
    *   Spring Data JPA
    *   Spring Security
    *   Spring Web
*   PostgreSQL
*   Thymeleaf
*   Maven

**Frontend:**
*   HTML
*   Tailwind CSS
*   JavaScript
*   jQuery
*   ToastifyJS
*   Font Awesome
*   Moment.js

**Security:**
*   Spring Security
*   TOTP (Time-based One-Time Password) via `dev.samstevens.totp`

**Other:**
*   Spring Mail (for notifications)

## Prerequisites

*   JDK 17 or newer
*   Maven 3.6 or newer
*   Node.js (LTS version recommended) - for frontend asset management (Tailwind CSS)
*   PostgreSQL database server

## Getting Started

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
    (Replace `<repository-url>` with the actual URL of the repository)

2.  **Database Setup:**
    *   Ensure your PostgreSQL server is running.
    *   Create a PostgreSQL database (e.g., `helpdesk_db`).
    *   Update database connection details in `src/main/resources/application.properties`.
        You'll need to configure:
        *   `spring.datasource.url` (e.g., `jdbc:postgresql://localhost:5432/helpdesk_db`)
        *   `spring.datasource.username`
        *   `spring.datasource.password`

3.  **Backend:**
    *   Navigate to the project root directory.
    *   Build and run the Spring Boot application:
        ```bash
        mvn spring-boot:run
        ```
    *   The application should be accessible at `http://localhost:8080` (or the port configured in `application.properties`).

4.  **Frontend Assets (Tailwind CSS):**
    *   The project uses Tailwind CSS. Spring Boot Devtools should handle live reloading of static assets.
    *   If you make changes to `styles/input.css` or `tailwind.config.js` and don't see them reflected, you can manually trigger a build. First, ensure Node.js dependencies are installed:
        ```bash
        npm install
        ```
    *   Then, run the build command:
        ```bash
        npx tailwindcss -i ./styles/input.css -o ./src/main/resources/static/css/main.css
        ```
    *   For continuous watching during development (recompiles on change):
        ```bash
        npx tailwindcss -i ./styles/input.css -o ./src/main/resources/static/css/main.css --watch
        ```

## Project Structure

*   `src/main/java/com/perfect/hepdeskapp/`: Core backend Java code.
    *   `config/`: Spring Boot and security configurations.
    *   `attachment/`, `department/`, `documentation/`, `notification/`, `priority/`, `role/`, `solutions/`, `status/`, `task/`, `ticket/`, `user/`: Feature-specific modules, typically containing:
        *   JPA Entities: Java classes representing data models (often directly within the feature package or a `model` sub-package).
        *   Spring Data JPA Repositories: Interfaces for data access (often in a `repository` sub-package).
        *   Services: Classes containing business logic (often in a `service` sub-package).
        *   Spring MVC Controllers: Classes handling HTTP requests (often in a `controller` sub-package).
    *   `HepDeskAppApplication.java`: The main Spring Boot application class.
    *   `MainController.java`: A general application controller.
*   `src/main/resources/`:
    *   `static/`: Contains static assets like compiled CSS (e.g., `css/main.css`), JavaScript libraries (jQuery, ToastifyJS, etc.), and images.
    *   `templates/`: Holds Thymeleaf HTML templates, organized by user roles (admin, manager, user, worker) and features.
        *   `fragments/`: Contains reusable UI components (Thymeleaf fragments).
    *   `application.properties`: The main application configuration file (database credentials, mail server settings, server port, etc.).
*   `pom.xml`: The Maven project configuration file, defining project dependencies, build settings, and plugins.
*   `package.json`: The Node.js project configuration file, used for managing frontend dependencies like Tailwind CSS.
*   `tailwind.config.js`: The configuration file for Tailwind CSS, used to customize the framework.
*   `styles/input.css`: The main input CSS file for Tailwind CSS, where custom styles and Tailwind's `@tailwind` directives are defined.

## License

This project was created for an engineering paper. If you intend to distribute it further, consider adding a standard open-source license (e.g., MIT, Apache 2.0).
