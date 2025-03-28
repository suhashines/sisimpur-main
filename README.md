# **Library Management API - The project that earned the internship at PrideSys It** 📚

Welcome to the **Library Management API**! This guide will help you **clone, set up, and run** the project smoothly on your **local machine**.  

---

## **1️⃣ Prerequisites** 🔧
Before you begin, make sure you have the following installed on your system:  
- **Java 21** or later  
- **Gradle**  
- **Docker** (Required for database setup)  
- **Git**  

---

## **2️⃣ Cloning the Repository** ⬇️
First, clone the repository from GitHub:
```sh
git clone <repo-url>
cd <project-folder>
```
### Installing Docker

Follow this guide to install docker in your system: [https://github.com/WCSCourses/index/blob/main/Docker\_guide.md](https://github.com/WCSCourses/index/blob/main/Docker_guide.md)

### Setting up the Database

There is a `init.sql` file in the `db` folder. If you want to add more tables or columns or want to add predefined data, you can do it there. For incremental change in the schema flyway database can be used.

Notice that there is a `docker-compose.yaml` file present in the root directory. Run `docker compose up` command in the root directory. 

Make sure the docker desktop (if you are on windows or mac) is running. You should see a new container spawn in the docker desktop UI. 

### Running Spring Boot Project

The project is setup with the database connection configured. Run `./gradlew bootrun` in linux or `gradlew bootRun` if you're on windows , to start the spring boot server. To check if everything is working, go to any browser (or postman or any other tool), and use `localhost:8080/api/v1/health`. You should see a greetings message.  

### Testing the Endpoints

The API documentation is provided as bruno files in the `bruno` folder in root. Install Bruno from [https://www.usebruno.com/downloads](https://www.usebruno.com/downloads). From Bruno chose `collection > open collection` and chose the bruno folder in the project root. Alternatively, you can use the bruno extension from VS Code. 
