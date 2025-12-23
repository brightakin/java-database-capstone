This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

1. User accesses AdminDashboard Appointment pages or REST Apis through a web or mobile app.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer
4. The service layers holds the business logic and communicates with the Mysql and MongoDb database to store and retrieve data depending on the request of the user.
