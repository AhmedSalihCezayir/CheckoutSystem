## Project Requirements ##
Please check Requirements.pdf to learn more about item and cart constraints.

## Getting Started

To run the application, you can use the following commands:

```
# Step 1
docker build -t ty-checkout-case .

# Step 2
docker-compose up

// If in step 1 it gives an error and your machine is Windows, convert mvnw file from CRLF to LF. 
// In windows machines, git may convert line ending of files to CRLF
```

## Endpoints
```
POST http://localhost:8080/api/v1/cart/items -> Add Item
```
```
POST http://localhost:8080/api/v1/cart/vas_items -> Add VasItem to Item
```
```
DELETE http://localhost:8080/api/v1/cart/items/{itemId} -> Remove Item
```
```
DELETE http://localhost:8080/api/v1/cart/items -> Reset Cart
```
```
GET http://localhost:8080/api/v1/cart -> Display Cart
```

## Some of The Important Assumptions & Design Choices

- All itemId and vasItemId are unique in the system
- When an already existing item is added to the system, it increases its quantity with the newly added ones instead of overriding it
- When discount amount is bigger than the cart total, discount becomes 0 and promotionId null
