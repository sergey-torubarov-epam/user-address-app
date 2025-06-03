# User Address Management System (UAMS)

## Overview

The User Address Management System is a comprehensive web application designed to manage user profiles, their associated addresses, orders, and products. It provides an intuitive interface for creating, updating, retrieving, and deleting user, address, order, and product information with a focus on data integrity and usability.

## Features

- **User Management**
  - View list of all users
  - Add new users
  - Edit existing users
  - Delete users
  
- **Address Management**
  - View list of all addresses
  - Add new addresses
  - Edit existing addresses
  - Delete addresses
  
- **User-Address Relationship Management**
  - Assign addresses to users
  - Remove addresses from users
  - View all addresses for a specific user
  - Search functionality to find users and addresses

- **Order Management**
  - Create new orders
  - View order details
  - Update order status
  - Cancel orders
  - View order history for users

- **Product Management**
  - Add new products
  - Update product information
  - Delete products
  - View product inventory
  - Search for products

## Technology Stack

- **Backend**: Spring Boot 2.7.0, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5
- **Database**: MySQL
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **API Documentation**: Swagger/OpenAPI

## Database Schema

The application uses the following database tables:

1. **users**
   - user_id (BIGINT, Primary Key)
   - email (VARCHAR(255))
   - first_name (VARCHAR(255))
   - last_name (VARCHAR(255))
   - mobile_number (VARCHAR(255))
   - password (VARCHAR(255))

2. **addresses**
   - address_id (BIGINT, Primary Key)
   - building_name (VARCHAR(255))
   - street (VARCHAR(255))
   - city (VARCHAR(255))
   - state (VARCHAR(255))
   - pincode (VARCHAR(255))

3. **user_address** (Junction Table)
   - user_id (BIGINT, Foreign Key)
   - address_id (BIGINT, Foreign Key)

4. **orders**
   - order_id (BIGINT, Primary Key)
   - user_id (BIGINT, Foreign Key)
   - order_date (DATETIME)
   - total_amount (DECIMAL(10,2))
   - status (VARCHAR(50))

5. **products**
   - product_id (BIGINT, Primary Key)
   - name (VARCHAR(255))
   - description (TEXT)
   - price (DECIMAL(10,2))
   - stock_quantity (INT)

6. **order_items** (Junction Table)
   - order_id (BIGINT, Foreign Key)
   - product_id (BIGINT, Foreign Key)
   - quantity (INT)
   - price (DECIMAL(10,2))

## Project Structure

```
user-address-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── uams/
│   │   │           ├── controller/
│   │   │           │   ├── AddressController.java
│   │   │           │   ├── HomeController.java
│   │   │           │   ├── UserController.java
│   │   │           │   ├── OrderController.java
│   │   │           │   └── ProductController.java
│   │   │           ├── model/
│   │   │           │   ├── Address.java
│   │   │           │   ├── User.java
│   │   │           │   ├── Order.java
│   │   │           │   └── Product.java
│   │   │           ├── repository/
│   │   │           │   ├── AddressRepository.java
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── OrderRepository.java
│   │   │           │   └── ProductRepository.java
│   │   │           ├── service/
│   │   │           │   ├── AddressService.java
│   │   │           │   ├── AddressServiceImpl.java
│   │   │           │   ├── UserService.java
│   │   │           │   ├── UserServiceImpl.java
│   │   │           │   ├── OrderService.java
│   │   │           │   ├── OrderServiceImpl.java
│   │   │           │   ├── ProductService.java
│   │   │           │   └── ProductServiceImpl.java
│   │   │           └── UserAddressManagementSystemApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── address/
│   │       │   │   ├── form.html
│   │       │   │   └── list.html
│   │       │   ├── layout/
│   │       │   │   └── main.html
│   │       │   ├── user/
│   │       │   │   ├── addresses.html
│   │       │   │   ├── form.html
│   │       │   │   └── list.html
│   │       │   ├── order/
│   │       │   │   ├── form.html
│   │       │   │   ├── list.html
│   │       │   │   └── details.html
│   │       │   └── product/
│   │       │       ├── form.html
│   │       │       └── list.html
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── uams/
│                   ├── controller/
│                   │   ├── AddressControllerTest.java
│                   │   ├── UserControllerTest.java
│                   │   ├── OrderControllerTest.java
│                   │   └── ProductControllerTest.java
│                   └── service/
│                       ├── AddressServiceImplTest.java
│                       ├── UserServiceImplTest.java
│                       ├── OrderServiceImplTest.java
│                       └── ProductServiceImplTest.java
└── pom.xml
```

## Getting Started

### Prerequisites

- JDK 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Database Setup

1. Create a MySQL database named `uams` (or update the database name in `application.properties`)
2. The application will automatically create the required tables on startup

### Configuration

Update the `src/main/resources/application.properties` file with your MySQL connection details:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/uams?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Building and Running the Application

1. Clone the repository:
   ```
   git clone https://gitbud.epam.com/Sergey_Torubarov/test.git
   cd test
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. Access the application at `http://localhost:8080`
   Swagger documentation: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### User Endpoints

- `GET /users` - List all users
- `GET /users/new` - Show form to create a new user
- `POST /users` - Create a new user
- `GET /users/{id}/edit` - Show form to edit a user
- `POST /users/{id}` - Update a user
- `GET /users/{id}/delete` - Delete a user
- `GET /users/{id}/addresses` - View addresses for a user
- `POST /users/{userId}/addresses/add` - Add an address to a user
- `GET /users/{userId}/addresses/{addressId}/remove` - Remove an address from a user

### Address Endpoints

- `GET /addresses` - List all addresses
- `GET /addresses/new` - Show form to create a new address
- `POST /addresses` - Create a new address
- `GET /addresses/{id}/edit` - Show form to edit an address
- `POST /addresses/{id}` - Update an address
- `GET /addresses/{id}/delete` - Delete an address

### Order Endpoints

- `GET /orders` - List all orders
- `GET /orders/new` - Show form to create a new order
- `POST /orders` - Create a new order
- `GET /orders/{id}` - View order details
- `GET /orders/{id}/edit` - Show form to edit an order
- `POST /orders/{id}` - Update an order
- `GET /orders/{id}/cancel` - Cancel an order
- `GET /users/{id}/orders` - View order history for a user

### Product Endpoints

- `GET /products` - List all products
- `GET /products/new` - Show form to create a new product
- `POST /products` - Create a new product
- `GET /products/{id}` - View product details
- `GET /products/{id}/edit` - Show form to edit a product
- `POST /products/{id}` - Update a product
- `GET /products/{id}/delete` - Delete a product
- `GET /products/search` - Search for products

## Testing

Run the tests using Maven:
```
mvn test
```

For integration tests:
```
mvn verify
```

## Deployment

The application can be deployed using Docker:

1. Build the Docker image:
   ```
   docker build -t user-address-management-system .
   ```

2. Run the container:
   ```
   docker run -p 8080:8080 user-address-management-system
   ```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Create a new Pull Request

## Project Status

This project is actively developed and maintained. For feature requests or bug reports, please open an issue on the repository.

## Support

For support, contact the development team or open an issue on the repository.

## License

This project is licensed under the proprietary license. See the LICENSE file for more details.

## Authors and Acknowledgment

- Development Team: EPAM Systems
- Project Lead: Sergey Torubarov