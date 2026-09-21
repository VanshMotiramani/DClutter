# DClutter

> A Spring Boot--based REST API for a pre-loved / second-hand e-commerce
> marketplace, with JWT authentication, role-based authorization,
> product and category management, shopping carts, addresses, order
> placement, payment metadata, inventory management, image uploads,
> search, sorting, and pagination.

## Overview

**DClutter** is a backend-focused e-commerce application designed around
the lifecycle of buying and selling pre-loved products.

The application exposes a RESTful API for:

-   User registration and authentication
-   JWT-based stateless security
-   Product catalog management
-   Product search, filtering by category, sorting, and pagination
-   Product condition and availability tracking
-   Product image upload and replacement
-   User shopping carts
-   Order placement and order history
-   Administrative access to orders and carts
-   Centralized exception and validation handling

The codebase follows a layered Spring architecture:

``` text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Database
```

DTOs are used at the API boundary to keep request/response models
separate from persistence entities.

------------------------------------------------------------------------

## Key Features

### Authentication & Authorization

-   User signup and signin
-   BCrypt password hashing
-   JWT authentication
-   JWT stored in an HTTP-only cookie
-   Bearer-token fallback through the `Authorization` header
-   Stateless Spring Security configuration
-   `ROLE_USER` and `ROLE_ADMIN`
-   Method-level authorization using `@PreAuthorize`
-   Signout endpoint that clears the JWT cookie

### Product Management

-   Create, update, and delete products
-   Assign products to categories
-   Automatic calculation of discounted price
-   Product condition tracking:
    -   `NEW`
    -   `LIKE_NEW`
    -   `GOOD`
    -   `FAIR`
    -   `POOR`
-   Product availability tracking:
    -   `AVAILABLE`
    -   `RESERVED`
    -   `SOLD`
-   Inventory quantity tracking
-   Product image upload
-   Automatic synchronization of product price/discount/stock changes
    with carts

### Catalog Discovery

-   Paginated product listing
-   Category-specific product listing
-   Case-insensitive partial product-name search
-   Configurable sorting
-   Paginated category listing
-   Pagination metadata in API responses

Default pagination configuration:

  Setting                            Default
  ---------------------------- -------------
  Page number                            `0`
  Page size                             `10`
  Default product sort           `productId`
  Default sort order                   `asc`
  Maximum page size constant           `100`

> Note: `MAX_PAGE_SIZE` is defined in `AppConstants`, but the current
> controllers/services do not enforce it directly.

### Shopping Cart

-   One cart per user
-   Add products with a requested quantity
-   Increase/decrease quantity
-   Remove individual products
-   Automatic cart creation
-   Stock validation before adding items
-   Prevention of quantities exceeding available inventory
-   Automatic subtotal calculation
-   Automatic cart total calculation
-   Product price/discount synchronization
-   Removal of unavailable/deleted products from carts

### Checkout & Orders

-   Checkout from the authenticated user's cart
-   Address ownership validation
-   Stock validation before order creation
-   Payment metadata persistence
-   Order creation
-   Inventory deduction after order placement
-   Automatic transition to `SOLD` when stock reaches zero
-   Automatic cart clearing after successful order creation
-   Authenticated user's order history
-   Individual order lookup with ownership validation
-   Administrative access to all orders

### Address Management

-   Create addresses
-   List the authenticated user's addresses
-   Retrieve an address by ID
-   Update addresses
-   Delete addresses
-   Associate addresses with users
-   Validate ownership of the selected checkout address

### Error Handling

The application has a centralized `GlobalExceptionHandler` for:

-   Resource-not-found errors
-   Application/business-rule errors
-   Bean validation errors
-   Unexpected server errors

Validation errors return field-level details.

------------------------------------------------------------------------

## Technology Stack

  Layer                   Technology
  ----------------------- ----------------------------------------
  Language                Java
  Backend Framework       Spring Boot
  REST API                Spring Web / Spring MVC
  Security                Spring Security
  Authentication          JWT
  Password Hashing        BCrypt
  Persistence             Spring Data JPA / Hibernate
  Validation              Jakarta Bean Validation
  DTO Mapping             ModelMapper
  Boilerplate Reduction   Lombok
  JSON                    Jackson
  JWT Library             JJWT
  File Handling           Java NIO / MultipartFile
  Database                JPA-compatible relational database
  API Documentation       Swagger / OpenAPI endpoints configured

## Architecture

DClutter uses a conventional layered architecture.

``` text
                    ┌──────────────────────┐
                    │      REST Client     │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Controllers      │
                    │ Auth / Product /     │
                    │ Cart / Order / etc.  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       Services       │
                    │ Business Logic       │
                    │ Transactions         │
                    │ Validation           │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Repositories     │
                    │ Spring Data JPA       │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      Relational      │
                    │       Database       │
                    └──────────────────────┘
```


------------------------------------------------------------------------

## Project Structure

``` text
dclutter/
├── DClutterApplication.java
│
├── config/
│   ├── AppConfig.java
│   ├── AppConstants.java
│   ├── DataInitializer.java
│   └── SecurityConfig.java
│
├── controller/
│   ├── AddressController.java
│   ├── AuthController.java
│   ├── CartController.java
│   ├── CategoryController.java
│   ├── OrderController.java
│   └── ProductController.java
│
├── dto/
│   ├── APIResponse.java
│   ├── AddressDTO.java
│   ├── CartDTO.java
│   ├── CartItemDTO.java
│   ├── CategoryDTO.java
│   ├── CategoryResponse.java
│   ├── ErrorResponse.java
│   ├── JwtResponse.java
│   ├── LoginRequest.java
│   ├── MessageResponse.java
│   ├── OrderDTO.java
│   ├── OrderItemDTO.java
│   ├── OrderRequestDTO.java
│   ├── PaymentDTO.java
│   ├── ProductDTO.java
│   ├── ProductResponse.java
│   ├── SignupRequest.java
│   ├── UserInfoResponse.java
│   └── ValidationErrorResponse.java
│
├── exception/
│   ├── APIException.java
│   ├── CategoryNotFoundException.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── model/
│   ├── Address.java
│   ├── AppRole.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   ├── Product.java
│   ├── ProductCondition.java
│   ├── ProductStatus.java
│   ├── Role.java
│   └── User.java
│
├── repository/
│   ├── AddressRepository.java
│   ├── CartItemRepository.java
│   ├── CartRepository.java
│   ├── CategoryRepository.java
│   ├── OrderItemRepository.java
│   ├── OrderRepository.java
│   ├── PaymentRepository.java
│   ├── ProductRepository.java
│   ├── RoleRepository.java
│   └── UserRepository.java
│
├── security/
│   ├── jwt/
│   │   ├── AuthEntryPointJwt.java
│   │   ├── AuthTokenFilter.java
│   │   └── JwtUtils.java
│   │
│   └── services/
│       ├── UserDetailsImpl.java
│       └── UserDetailsServiceImpl.java
│
├── service/
│   ├── AddressService.java
│   ├── AddressServiceImpl.java
│   ├── CartService.java
│   ├── CartServiceImpl.java
│   ├── CategoryService.java
│   ├── CategoryServiceImpl.java
│   ├── FileService.java
│   ├── FileServiceImpl.java
│   ├── OrderService.java
│   ├── OrderServiceImpl.java
│   ├── ProductService.java
│   └── ProductServiceImpl.java
│
└── util/
    └── AuthUtil.java
```

------------------------------------------------------------------------

## Domain Model

The main entities and their relationships are:

``` text
User
 ├── Many-to-Many ──► Role
 ├── Many-to-Many ──► Address
 ├── One-to-One ────► Cart
 └── One-to-Many ────► Product (seller)

Category
 └── One-to-Many ────► Product

Cart
 └── One-to-Many ────► CartItem
                          │
                          └── Many-to-One ──► Product

Order
 ├── One-to-Many ────► OrderItem
 │                       │
 │                       └── Many-to-One ──► Product
 ├── One-to-One ────► Payment
 └── Many-to-One ───► Address
```

------------------------------------------------------------------------

# API Reference

Base URL:

``` text
http://localhost:8080
```

All endpoints below are relative to the base URL.

## Authentication

### Sign Up

``` http
POST /api/auth/signup
```

Request:

``` json
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

Optional role field:

``` json
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123",
  "roles": ["user"]
}
```

The default role is `ROLE_USER`.

Response:

``` json
{
  "message": "User registered successfully!"
}
```

### Sign In

``` http
POST /api/auth/signin
```

Request:

``` json
{
  "username": "john",
  "password": "password123"
}
```

Successful authentication returns user information and sets an HTTP-only
JWT cookie.

Example response:

``` json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "roles": [
    "ROLE_USER"
  ]
}
```

### Get Current User

``` http
GET /api/auth/user
```

Requires authentication.

### Get Current Username

``` http
GET /api/auth/username
```

Requires authentication.

### Sign Out

``` http
POST /api/auth/signout
```

Clears the JWT cookie and clears the current security context.

------------------------------------------------------------------------

# Public Product APIs

## Get Products

``` http
GET /api/public/products
```

Optional parameters:

``` text
pageNumber
pageSize
sortBy
sortOrder
```

Example:

``` http
GET /api/public/products?pageNumber=0&pageSize=10&sortBy=price&sortOrder=asc
```

Response structure:

``` json
{
  "content": [],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 25,
  "totalPages": 3,
  "lastPage": false
}
```

## Get Products by Category

``` http
GET /api/public/categories/{categoryId}/products
```

Example:

``` http
GET /api/public/categories/2/products?pageNumber=0&pageSize=10&sortBy=price&sortOrder=asc
```

## Search Products

``` http
GET /api/public/products/search?keyword=laptop
```

Optional:

``` text
pageNumber
pageSize
sortBy
sortOrder
```

Search is case-insensitive and uses partial product-name matching.

------------------------------------------------------------------------

# Public Category APIs

## Get Categories

``` http
GET /api/public/categories
```

Parameters:

``` text
pageNumber
pageSize
sortBy
sortOrder
```

------------------------------------------------------------------------

# Product Administration

All `/api/admin/**` endpoints require `ROLE_ADMIN`.

## Create Product

``` http
POST /api/admin/categories/{categoryId}/products
```

Example request:

``` json
{
  "productName": "Used Mechanical Keyboard",
  "description": "Well-maintained mechanical keyboard in excellent condition.",
  "price": 2500,
  "discount": 10,
  "quantity": 1,
  "condition": "GOOD",
  "status": "AVAILABLE",
  "categoryId": 1
}
```

The server calculates:

``` text
specialPrice = price - (price × discount / 100)
```

For the example:

``` text
specialPrice = 2500 - (2500 × 10 / 100)
             = 2250
```

## Update Product

``` http
PUT /api/admin/products/{productId}
```

## Update Product Image

``` http
PUT /api/admin/products/{productId}/image
Content-Type: multipart/form-data
```

Multipart field:

``` text
image=<file>
```

The existing image is deleted before the new image is stored.

## Delete Product

``` http
DELETE /api/admin/products/{productId}
```

Deleting a product also removes it from all carts before deleting the
product record.

------------------------------------------------------------------------

# Category Administration

## Create Category

``` http
POST /api/admin/categories
```

Request:

``` json
{
  "categoryName": "Electronics",
  "description": "Pre-loved electronic devices and accessories."
}
```

## Update Category

``` http
PUT /api/admin/categories/{categoryId}
```

## Delete Category

``` http
DELETE /api/admin/categories/{categoryId}
```

------------------------------------------------------------------------

# Cart APIs

Cart endpoints require authentication.

## Add Product

``` http
POST /api/carts/products/{productId}/quantity/{quantity}
```

Example:

``` http
POST /api/carts/products/15/quantity/2
```

The service checks:

1.  Quantity is greater than zero
2.  Product exists
3.  Product is `AVAILABLE`
4.  Product is in stock
5.  Requested quantity does not exceed available stock

## Get Current User Cart

``` http
GET /api/carts/users/cart
```

Example response:

``` json
{
  "cartId": 4,
  "totalPrice": 4500.0,
  "items": [
    {
      "cartItemId": 7,
      "product": {},
      "quantity": 2,
      "discount": 10.0,
      "productPrice": 2250.0,
      "subTotal": 4500.0
    }
  ]
}
```

## Increase / Decrease Quantity

``` http
PUT /api/carts/products/{productId}/quantity/{operation}
```

Supported operations:

``` text
increase
decrease
delete
```

`delete` behaves like a quantity decrement; when quantity reaches zero,
the cart item is removed.

## Remove Product from Cart

``` http
DELETE /api/carts/{cartId}/product/{productId}
```

## Get All Carts

``` http
GET /api/carts
```

Requires `ROLE_ADMIN`.

------------------------------------------------------------------------

# Address APIs

All address endpoints require authentication.

## Create Address

``` http
POST /api/addresses
```

Example:

``` json
{
  "street": "Main Street",
  "buildingName": "Sunrise Apartments",
  "city": "Jaipur",
  "state": "Rajasthan",
  "country": "India",
  "pincode": "302001"
}
```

## Get User Addresses

``` http
GET /api/users/addresses
```

## Get Address by ID

``` http
GET /api/addresses/{addressId}
```

## Update Address

``` http
PUT /api/addresses/{addressId}
```

## Delete Address

``` http
DELETE /api/addresses/{addressId}
```

## Get All Addresses

``` http
GET /api/addresses
```

Requires `ROLE_ADMIN`.

------------------------------------------------------------------------

# Order APIs

## Place Order

``` http
POST /api/order/users/payments/{paymentMethod}
```

Request:

``` json
{
  "addressId": 3,
  "paymentMethod": "COD",
  "pgName": "ExampleGateway",
  "pgPaymentId": "PAY-12345",
  "pgStatus": "SUCCESS",
  "pgResponseMessage": "Payment successful"
}
```

The order workflow is:

``` text
Authenticated User
       │
       ▼
Fetch Cart
       │
       ▼
Validate Address Ownership
       │
       ▼
Validate Product Availability
       │
       ▼
Validate Stock
       │
       ▼
Create Payment Record
       │
       ▼
Create Order
       │
       ▼
Create Order Items
       │
       ▼
Reduce Product Stock
       │
       ▼
Mark Product SOLD if Stock = 0
       │
       ▼
Clear Cart
       │
       ▼
Return Order
```

This workflow is executed inside a transactional service method.

## Get My Orders

``` http
GET /api/orders/users
```

Returns the authenticated user's orders sorted by order date descending.

## Get Order by ID

``` http
GET /api/orders/{orderId}
```

The service verifies that the requested order belongs to the
authenticated user's email.

## Get All Orders

``` http
GET /api/admin/orders
```

Requires `ROLE_ADMIN`.

------------------------------------------------------------------------

# Authentication Flow

DClutter supports JWT retrieval through both an HTTP-only cookie and a
Bearer token.

``` text
                   POST /api/auth/signin
                            │
                            ▼
                 AuthenticationManager
                            │
                            ▼
                 UserDetailsService
                            │
                            ▼
                    BCrypt password
                       validation
                            │
                            ▼
                      Generate JWT
                            │
                            ▼
                  Set HTTP-only cookie
                            │
                            ▼
                     Client request
                            │
                 ┌──────────┴──────────┐
                 │                     │
             JWT Cookie          Authorization
                                  Bearer token
                 │                     │
                 └──────────┬──────────┘
                            ▼
                    AuthTokenFilter
                            │
                            ▼
                     Validate JWT
                            │
                            ▼
                  Load UserDetails
                            │
                            ▼
                   SecurityContext
                            │
                            ▼
                    Protected API
```

JWT configuration is read from application properties:

``` properties
app.jwt.secret=<base64-secret>
app.jwt.expiration-ms=<expiration-in-ms>
app.jwt.cookie-name=<cookie-name>
```

------------------------------------------------------------------------

# Running the Project Locally

Because the supplied archive does not include the Maven/Gradle build
file or application configuration, the exact build command and database
configuration cannot be verified from the archive.

A typical Spring Boot setup would be:

## 1. Clone the repository

``` bash
git clone https://github.com/<your-username>/DClutter.git
cd DClutter
```

## 2. Configure the database

Create a relational database and configure Spring datasource properties.

Example:

``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/dclutter
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Use the database driver corresponding to your actual project
configuration.

## 3. Configure JWT

Add:

``` properties
app.jwt.secret=<BASE64_ENCODED_SECRET>
app.jwt.expiration-ms=86400000
app.jwt.cookie-name=dclutter-jwt
```

Do not commit the real JWT secret to GitHub.

## 4. Configure image storage

Add:

``` properties
project.image=images/
```

The directory will be created by the file service when required.

## 5. Build

For Maven:

``` bash
./mvnw clean install
```

or:

``` bash
mvn clean install
```

For Gradle:

``` bash
./gradlew build
```

Use the build system actually present in your repository.

## 6. Run

``` bash
./mvnw spring-boot:run
```

or:

``` bash
mvn spring-boot:run
```

The application should then be available at:

``` text
http://localhost:8080
```
------------------------------------------------------------------------

# Example API Workflow

Customer:

``` text
1. Sign up
       │
       ▼
2. Sign in
       │
       ▼
3. Browse categories
       │
       ▼
4. Browse/search products
       │
       ▼
5. Add product to cart
       │
       ▼
6. Create delivery address
       │
       ▼
7. Review cart
       │
       ▼
8. Place order
       │
       ▼
9. Stock is reduced
       │
       ▼
10. Cart is cleared
       │
       ▼
11. View order history
```

Administrator:

``` text
Admin login
    │
    ├── Manage categories
    │
    ├── Create products
    │
    ├── Update product details
    │
    ├── Upload product images
    │
    ├── Delete products
    │
    ├── Inspect carts
    │
    ├── Inspect addresses
    │
    └── Inspect orders
```

------------------------------------------------------------------------
# Author

**Vansh Motiramani**