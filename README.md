# Expedia Demo Environment

A Spring Boot application simulating Expedia backend services for flight search, booking, and payment. This demo is designed to showcase test coverage improvement using Devin AI.

## Features

- **Flight Search**: Search for flights using Amadeus API
- **Flight Details**: Get detailed pricing and information for specific flights
- **Booking**: Create flight bookings with consistent user ID generation
- **Payment**: Process payments for bookings
- **Frontend**: Simple HTML interface for testing all services

## Prerequisites

- Java 21 (already installed)
- Maven 3.6+

## Project Structure

```
expedia_demo/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/expedia/demo/
│   │   │   ├── ExpediaDemoApplication.java
│   │   │   ├── config/
│   │   │   │   └── AmadeusConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── SearchController.java
│   │   │   │   ├── FlightDetailsController.java
│   │   │   │   ├── BookController.java
│   │   │   │   └── PayController.java
│   │   │   ├── service/
│   │   │   │   ├── SearchService.java
│   │   │   │   ├── FlightDetailsService.java
│   │   │   │   ├── BookService.java
│   │   │   │   └── PayService.java
│   │   │   ├── model/
│   │   │   │   ├── Flight.java
│   │   │   │   ├── Booking.java
│   │   │   │   ├── BookRequest.java
│   │   │   │   ├── BookResponse.java
│   │   │   │   ├── PaymentRequest.java
│   │   │   │   └── PaymentResponse.java
│   │   │   └── storage/
│   │   │       └── BookingStorage.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   └── index.html
│   │       └── bookings.json (created at runtime)
│   └── test/
│       └── java/com/expedia/demo/
│           └── service/
│               ├── SearchServiceTest.java
│               ├── FlightDetailsServiceTest.java
│               ├── BookServiceTest.java
│               └── PayServiceTest.java
└── README.md
```

## Setup

1. **Clone/Navigate to the project directory**

2. **API Configuration**
   - Amadeus API credentials are already configured in `src/main/resources/application.properties`
   - API Key: `8ToZWSxK1fi3Ds2SXfUXI8x4qTCAR879`
   - API Secret: `gx4AqaOwlUg0zc5L`

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the frontend**
   - Open your browser and navigate to: `http://localhost:8080`
   - The application will be available on port 8080 by default

## API Endpoints

### Search Flights
- **GET** `/api/search?origin={origin}&destination={destination}&date={date}`
- **Example**: `GET /api/search?origin=NYC&destination=LAX&date=2025-12-01`
- **Response**: Array of flight objects with `flight_id`, `origin`, `destination`, `departure_date`, `price`, `airline`

### Get Flight Details
- **GET** `/api/flight-details/{flightId}`
- **Example**: `GET /api/flight-details/abc123`
- **Response**: Detailed flight information including pricing, departure/arrival times

### Book Flight
- **POST** `/api/book`
- **Request Body**:
  ```json
  {
    "flight_id": "abc123",
    "passenger_name": "John Doe",
    "passenger_email": "john@example.com"
  }
  ```
- **Response**:
  ```json
  {
    "user_id": "generated-user-id",
    "flight_id": "abc123",
    "status": "PENDING",
    "message": "completed!"
  }
  ```

### Process Payment
- **POST** `/api/pay`
- **Request Body**:
  ```json
  {
    "user_id": "generated-user-id",
    "flight_id": "abc123",
    "payment_method": "credit_card",
    "amount": "500.00"
  }
  ```
- **Response**:
  ```json
  {
    "user_id": "generated-user-id",
    "flight_id": "abc123",
    "status": "CONFIRMED",
    "message": "completed!"
  }
  ```

## Testing

### Run Tests
```bash
mvn test
```

### Generate Code Coverage Report
```bash
mvn jacoco:report
```

### View Coverage Report
After running `mvn jacoco:report`, open:
```
target/site/jacoco/index.html
```

The initial test coverage is designed to be **<50%** as it only includes happy path tests. Error handling, validation, and edge cases are intentionally untested to demonstrate coverage improvement.

## Demo Flow

1. **Show Project Structure**: Explain the 4 services (Search, FlightDetails, Book, Pay)

2. **Demonstrate Frontend**:
   - Open `http://localhost:8080`
   - Search for flights (e.g., NYC to LAX)
   - View flight details for a selected flight
   - Create a booking
   - Process payment

3. **Show Test Coverage**:
   - Run `mvn clean test`
   - Run `mvn jacoco:report`
   - Open coverage report and show it's <50%
   - Point out untested paths:
     - Error handling (API failures, ResponseException)
     - Input validation (null checks, format validation)
     - Edge cases (empty results, duplicate bookings)
     - JSON file I/O errors

4. **Use Devin to Improve Coverage**:
   - Instruct Devin to add tests for error handling
   - Instruct Devin to add validation tests
   - Instruct Devin to add edge case tests
   - Show improved coverage after Devin's work

## Technical Details

### ID Generation
- **user_id**: Generated using SHA-256 hash of (passenger_name + passenger_email), ensuring consistency
- **flight_id**: Uses the `id` field from Amadeus `FlightOfferSearch` response

### Booking Storage
- Bookings are stored in `src/main/resources/bookings.json`
- Each booking is uniquely identified by the composite key (user_id, flight_id)
- Bookings are stored as JSON array with snake_case field names

### Naming Convention
- JSON fields use **snake_case** (e.g., `user_id`, `flight_id`)
- Java code uses **camelCase** (standard Java convention)

## Dependencies

- Spring Boot 3.2.0
- Amadeus Java SDK 10.0.0
- JUnit 5
- Mockito
- JaCoCo Maven Plugin 0.8.11

## Notes

- The application uses real Amadeus API calls for flight search and details
- Booking and payment are simulated (no actual charges)
- The `bookings.json` file is created automatically on first booking
- All API responses use snake_case for JSON field names

