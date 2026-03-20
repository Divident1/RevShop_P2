# RevShop Viva Answers - Simple Spoken English

This file is written in simple spoken English so you can answer confidently in a viva without sounding too memorized.

## 1. Project Overview

`My project is RevShop. It is a full-stack e-commerce application. It supports two types of users: buyer and seller. Buyers can search products, add to cart, place orders, and give reviews. Sellers can manage products and stock.`

## 2. Architecture

`I used Angular for the frontend and Spring Boot for the backend. The frontend sends API requests to the backend. The backend processes the request, applies business logic, and stores data in MySQL.`

## 3. Tech Stack

`In frontend I used Angular, TypeScript, HTML, and CSS. In backend I used Java, Spring Boot, Spring Security, and JPA. For database I used MySQL. For deployment I used Docker and AWS EC2.`

## 4. User Stories

`I divided the project into buyer features and seller features. Buyer features include login, product browsing, cart, checkout, favorites, and reviews. Seller features include add product, update product, and stock management.`

## 5. Branching Strategy

`We used feature branches for development. Then we merged the work into shared branches like develop and main. This helped the team work on different modules at the same time.`

## 6. UI and Responsiveness

`I made the UI responsive using CSS Flexbox, Grid, and media queries. So the pages work better on desktop and smaller screens.`

## 7. Data Model

`The main tables are User, Product, Category, Cart, CartItem, Order, OrderItem, Favorite, Review, and Notification. These tables are connected based on the e-commerce flow.`

## 8. Security

`I used JWT for login authentication and BCrypt for password hashing. I also used route protection and token-based access in the frontend.`

## 9. Performance

`I used pagination and search filtering in the product module. I did not add advanced caching, so that can be future improvement.`

## 10. Maintainability

`I kept the code in separate layers like controller, service, repository, and model. This makes the code easier to understand and maintain.`

## 11. Testing

`I wrote unit tests in the backend using JUnit and Mockito. In frontend there are Angular spec files. Full end-to-end testing is still not added.`

## 12. AWS Deployment

`I deployed the project on AWS EC2 using Docker Compose. I used separate containers for frontend, backend, and database.`

## 13. Microservices

`This project is not microservices right now. It is a modular monolith. But in future it can be split into separate services like auth, product, order, and notification.`

## 14. Closing Answer

`Overall, this project shows frontend, backend, database, security, testing, and deployment in one complete application.`
