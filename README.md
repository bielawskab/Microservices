# Spring Boot Microservices Project
## Overview
This project demonstrates the implementation of a microservices architecture using Spring Boot and Spring Cloud. It consists of four key services:

1. External Service: A REST/HTTP controller that handles user requests.
2. Internal Service: Connects to an in-memory database and extracts data.
3. Service Registry: Implemented using Netflix Eureka for service registration and discovery.
4. Gateway Service: Acts as a proxy to hide the external service.
## Architecture
* The external service calls the internal service to retrieve data and return it to the user.
* Services communicate using RestTemplate and are load-balanced with the @LoadBalanced annotation.
* The internal service manages a database schema via FlywayDB or Liquibase.
* A gateway service is used to route and proxy requests to the external service.
## Features
* CRUD Operations: The first service provides a REST API for CRUD operations, including data pagination, sorting, and filtering.
* Filtering: API clients can filter data by any entity attribute.
* Service Discovery: Both services register in the service registry (Netflix Eureka).
## Technologies & Dependencies
* Service Registry: Spring Web, Eureka Server
* Gateway: Spring Cloud Gateway, Eureka Discovery
* Internal Service: Spring Web, Eureka Discovery
* External Service: Eureka Discovery, Flyway, H2 Database
