# Assumptions
The application works with the AWS S3 mock working at the following URL: **http://localhost:4566**

It is assumed that the S3 bucket **upload1** already exists.  
If it does not exist then it can be created using the following command:
> aws --endpoint-url=http://localhost:4566 s3 mb s3://upload1

# Running the application

> mvn spring-boot:run

# Running tests

> mvn test

# Executing a sample HTTP request

> http://localhost:8080/burgers/63