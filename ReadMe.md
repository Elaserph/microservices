Demo Microservices Project

auth-service 9898

eureka 8761

api-gateway 8080

service-a 8081

service-b 8082

auth-server h2 console:
http://localhost:9898/h2-console/

Eureka Dashboard:
http://localhost:8761/

Google SSO from auth-server:
http://localhost:9898/login/oauth2/code/google

Google SSO from API-Gateway:
http://localhost:8080/oauth2/authorization/google


localhost:8080/auth/register

{
"username": "elaserph",
"password": "password",
"role": "user"
}

localhost:8080/auth/authenticate

{
"username": "elaserph",
"password": "password",
"role": "user"
}


localhost:8080/auth/validate

localhost:8080/auth/refresh

localhost:8080/helloEureka

localhost:8080/auth/logout