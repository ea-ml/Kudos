# Kudos Application

A Spring Boot + Tailwind CSS application for managing employee kudos, teams, and rankings.

## Prerequisites
- **Java 17+** (or compatible with your Spring Boot version)
- **Node.js & npm** (for frontend assets)
- **A SQL database** (e.g., MySQL, PostgreSQL)
- **Git**

## Setup Instructions

### 1. Clone the Repository
```sh
git clone <your-repo-url>
cd Kudos
```

### 2. Set Up the Database
- Create a new database (e.g., `kudosdb`) in your preferred SQL database.
- Note the username, password, and connection URL.

### 3. Configure Database Credentials
- Open `src/main/resources/application.properties`.
- Update the following properties to match your database:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/kudos_db
  spring.datasource.username=your_db_user
  spring.datasource.password=your_db_password
  # (Adjust the URL for your DB type, e.g., PostgreSQL)
  ```

### 4. Install Node.js Dependencies
```sh
npm install
```

### 5. Run the Application
In one terminal, start the frontend asset watcher:
```sh
npm run watch
```

In another terminal, start the Spring Boot backend:
```sh
./mvnw spring-boot:run
```
*On Windows, use `mvnw.cmd spring-boot:run` if `./mvnw` doesn't work.*

The app will be available at [http://localhost:8080](http://localhost:8080).

---

## Troubleshooting
- Ensure your database is running and accessible.
- If you change the DB type, update the JDBC URL and driver in `application.properties`.
- For frontend issues, ensure Node.js and npm are installed and up to date.

---

## License
MIT (or your license here) 