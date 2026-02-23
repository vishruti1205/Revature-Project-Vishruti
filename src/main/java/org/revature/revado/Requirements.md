# RevaDo: The Next-Gen Todo Platform

### Core Features:

* **Spring Boot + SQLite** backend 


## Application Features

### Core Components

* **Users**
* Authentication & Authorization (Login/Register)
* Profile management


* **Todo Items**
* Adding Todo items and marking them complete
* Editing Todo titles and descriptions
* Deleting Todo items


* **Subtask Items**
* Adding subtasks to specific Todo items
* Marking subtasks as complete
* Editing and deleting subtask items


### Server-Side

* Use **Spring Boot** to build the RESTful backend.
* Implement a layered architecture (Controller, Service, Repository).

### Persistence

* An **SQLite** database should be used for persistence to ensure lightweight and portable data storage.



## Entities

* **Users**
* `id` (UUID)
* `username` (String)
* `password` (String)


* **Todo Items**
* `id` (UUID)
* `title` (String)
* `description` (String)
* `isCompleted` (Boolean)
* `user_id` (Foreign Key referencing Users.id)


* **Subtask Items**
* `id` (UUID)
* `description` (String)
* `isCompleted` (Boolean)
* `todo_item_id` (Foreign Key referencing TodoItems.id)

***
***
## Application Startup Flow
*Application Starts*
↓

*RevaDoApplication (main method)*
↓

*Spring Boot Bootstraps Application*
↓

*Component Scan (org.revature.revado)*
↓

*Spring Creates Beans:*
- Controllers
- Services
- Repositories
- Entities (JPA mapping)
↓

*Embedded Server Starts (Tomcat)*
↓

*Application Ready to Accept HTTP Requests*


***
***

## Application Flow
*Client Sends HTTP Request*
↓

*Controller (receives DTO)*
↓

*Service (converts DTO → Entity)*
↓

*Repository (saves Entity)*
↓

*Database*
↓

*Repository returns Entity*
↓

*Service converts Entity → DTO*
↓

*Controller returns DTO*
↓

*Client Receives Response*
