# Mobile Place Order System

This is a Spring Boot REST API project for a simple mobile place order system. The API supports product listing and order cart use cases.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Testing](#testing)

## Introduction

This project provides a RESTful API for managing products and order transactions. It includes functionalities to list products, add products to a cart, and place orders.

## Features

- CRUD operations for products
- Adding products to a cart
- Placing orders
- Validation for request payloads

## Prerequisites

- Java 17 or higher
- Maven 3.8.4 or higher

## Setup

1. **Clone the repository:**
    ```sh
    git clone https://github.com/bhimsur/place-order-system.git
    cd place-order-system
    ```

2. **Build the project:**
    ```sh
    mvn clean install
    ```

3. **Run the application:**
    ```sh
    mvn spring-boot:run
    ```

The application will start on `http://localhost:8080`.

### Testing

To run the tests, use the following command:
```sh
mvn test