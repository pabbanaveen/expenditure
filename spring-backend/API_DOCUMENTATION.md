# Chitti Manager API Documentation

## Overview
The Chitti Manager API is a comprehensive Spring Boot application for managing chitti fund operations. It provides RESTful endpoints for managing chitties, members, and monthly payment slips.

## Base URL
```
http://localhost:8080
```

## Swagger Documentation
Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui/index.html
```

## Core Features

### 1. Chitty Management
- Create, read, update, delete chitties
- Dynamic chitty amounts (5L, 2L, 1L)
- Dynamic members and months
- Calculate regular and lifted payment amounts

### 2. Member Management
- Add/remove members from chitties
- Track lift status
- Manage member information

### 3. Monthly Slip Management
- Generate monthly payment slips
- Mark members as lifted
- Track payment status
- Payment date tracking

## API Endpoints

### Chitty Management

#### Get All Active Chitties
```
GET /api/chitties
```
Returns list of all active chitties with member information.

**Response Example:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": "chitty-uuid",
      "name": "5 Lakh Chitty",
      "amount": 500000.0,
      "totalMembers": 20,
      "totalMonths": 20,
      "regularPayment": 25000.0,
      "liftedPayment": 31250.0,
      "startDate": "2025-06-07T16:27:27.332",
      "memberIds": ["member-uuid-1", "member-uuid-2"],
      "active": true
    }
  ]
}
```

#### Get Chitty by ID
```
GET /api/chitties/{id}
```

#### Create New Chitty
```
POST /api/chitties
Content-Type: application/json

{
  "name": "5 Lakh Chitty",
  "amount": 500000,
  "totalMembers": 20,
  "totalMonths": 20,
  "memberNames": ["Rajesh Kumar", "Priya Sharma", "Amit Singh"]
}
```

#### Update Chitty
```
PUT /api/chitties/{id}
```

#### Delete Chitty (Soft Delete)
```
DELETE /api/chitties/{id}
```

#### Add Member to Chitty
```
POST /api/chitties/{id}/members?memberName=John Doe
```

#### Remove Member from Chitty
```
DELETE /api/chitties/{id}/members/{memberId}
```

#### Get Chitty Members
```
GET /api/chitties/{id}/members
```

#### Search Chitties
```
GET /api/chitties/search?query=5 Lakh
```

### Member Management

#### Get Member by ID
```
GET /api/members/{id}
```

#### Update Member
```
PUT /api/members/{id}
Content-Type: application/json

{
  "name": "Updated Name"
}
```

#### Mark Member as Lifted
```
POST /api/members/{id}/lift?month=1
```

#### Get Lifted Members
```
GET /api/members/chitty/{chittiId}/lifted
```

#### Get Non-Lifted Members
```
GET /api/members/chitty/{chittiId}/non-lifted
```

#### Delete Member
```
DELETE /api/members/{id}
```

### Monthly Slip Management

#### Get Slips for Chitty
```
GET /api/monthly-slips/chitty/{chittiId}
```

#### Get Slip for Specific Month
```
GET /api/monthly-slips/chitty/{chittiId}/month/{month}
```

#### Generate Monthly Slip
```
POST /api/monthly-slips/generate?chittiId={chittiId}&month=1
```

**Response Example:**
```json
{
  "success": true,
  "message": "Monthly slip generated successfully",
  "data": {
    "id": "slip-uuid",
    "chittiId": "chitty-uuid",
    "month": 1,
    "slipDate": "2025-06-07T16:27:56.457",
    "liftedMemberId": null,
    "paymentRecords": [
      {
        "memberId": "member-uuid",
        "memberName": "Rajesh Kumar",
        "amount": 25000.0,
        "paid": false,
        "lifted": false,
        "paymentDate": null
      }
    ]
  }
}
```

#### Mark Member as Lifted in Slip
```
POST /api/monthly-slips/{slipId}/lift?memberId={memberId}
```

#### Mark Payment Status
```
POST /api/monthly-slips/{slipId}/payment?memberId={memberId}&isPaid=true
```

#### Delete Monthly Slip
```
DELETE /api/monthly-slips/{id}
```

## Business Logic

### Payment Calculation
- **Regular Payment**: `amount / totalMonths`
- **Lifted Payment**: `regularPayment + (regularPayment * 0.25)` (25% extra)

### Example Payment Structure:
- **5 Lakh Chitty (20 months)**:
  - Regular payment: ₹25,000
  - Lifted payment: ₹31,250

- **2 Lakh Chitty (20 months)**:
  - Regular payment: ₹10,000
  - Lifted payment: ₹12,500

- **1 Lakh Chitty (20 months)**:
  - Regular payment: ₹5,000
  - Lifted payment: ₹6,250

### Lift Rules
- Only one person can lift per chitty per month
- Once lifted, member pays higher amount for remaining months
- Payment tracking includes dates and status

## Data Models

### Chitty
- id (UUID)
- name
- amount
- totalMembers
- totalMonths
- regularPayment (calculated)
- liftedPayment (calculated)
- startDate
- memberIds (array)
- active status
- timestamps

### Member
- id (UUID)
- name
- chittiId
- hasLifted (boolean)
- liftedDate
- liftedMonth
- timestamps

### MonthlySlip
- id (UUID)
- chittiId
- month
- slipDate
- liftedMemberId
- paymentRecords (array)
- timestamps

### PaymentRecord
- memberId
- memberName
- amount
- paid (boolean)
- lifted (boolean)
- paymentDate

## Error Handling
All endpoints return standardized error responses:
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

## Mock Data
The application initializes with:
- 1 × 5 Lakh Chitty
- 20 members with Indian names
- Ready for monthly slip generation

## Technology Stack
- **Backend**: Java 17, Spring Boot 2.7.18
- **Database**: MongoDB
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven

## Running the Application
```bash
mvn clean package -DskipTests
java -jar target/chitti-manager-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080 with MongoDB connection to localhost:27017.