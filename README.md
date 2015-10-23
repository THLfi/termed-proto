# Termed

Termed is a web-based vocabulary and metadata editor.

## Development

To start the server in development mode using memory based database and index, run:
```
mvn spring-boot:run
```

### Using profile-specific properties

To use different configurations based on Spring profile, such as `dev`, add a new property
file:
```
/src/main/resources/application-dev.properties
```
with config like:
```
spring.datasource.url=jdbc:postgresql:termed
spring.datasource.username=termed
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.search.default.directory_provider=filesystem
spring.jpa.properties.hibernate.search.default.indexBase=index
```

and run:
```
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Creating a war file

A war file can be built using:
```
mvn package
```
