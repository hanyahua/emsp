# Electric Mobility Service Provider (eMSP) REST API Documentation
**Version:** 1.0.0
**Description:** This is the REST API documentation for managing Electric Mobility Service Provider resources.
**Contact:**
- **Support Team Name:** eMSP Support Team
- **Email:** hanyahua@outlook.com

### Server Information:
- **Base URL:** `http://127.0.0.1:8080`

## Table of Contents
1. [Account Management](#account-management)
2. [Card Management](#card-management)
3. [Health Check](#health-check)
4. [Models & Entities](#models--entities)

## Account Management
### 1. **Create Account**
**Endpoint:** `POST /api/accounts`
**Summary:** Create a new account.
**Request Body:**
``` json
{
  "email": "user@example.com"
}
```
**Fields:**

| Field | Type | Description | Constraints |
| --- | --- | --- | --- |
| email | string | Email address for the account | Required, max length: 255 characters |
**Responses:**
- **201 Created:** Successfully created the account with its details.
- **400 Bad Request:** Invalid account data (e.g., incorrect email format).
- **409 Conflict:** An account already exists with the provided email.

### 2. **Retrieve Accounts**
**Endpoint:** `GET /api/accounts`
**Summary:** Retrieve a paginated list of accounts with an optional time range filter.
**Query Parameters:**

| Parameter | Type | Description | Example Values |
| --- | --- | --- | --- |
| lastUpdatedFrom | string | Start datetime for filtering (ISO 8601). | `2025-05-01T10:15:30Z` |
| lastUpdatedTo | string | End datetime for filtering (ISO 8601). | `2025-12-31T10:15:30Z` |
| pageNumber | integer | Zero-based page index. | `0` |
| pageSize | integer | Number of records per page. | `10` |
**Responses:**
- **200 OK:** List of paginated account records.
- **400 Bad Request:** If invalid pagination parameters were supplied (e.g., negative page indices).

### 3. **Retrieve an Account by ID**
**Endpoint:** `GET /api/accounts/{id}`
**Summary:** Retrieve account details using its unique ID.
**Path Parameters:**

| Parameter | Type | Description | Example Value |
| --- | --- | --- | --- |
| id | integer | Unique Account ID | `1001` |
**Responses:**
- **200 OK:** Account details.
- **404 Not Found:** No account was found with the given ID.

### 4. **Update Account Status**
**Endpoint:** `PATCH /api/accounts/{id}/status`
**Summary:** Update the status of an account (e.g., activate or deactivate it).
**Path Parameters:**

| Parameter | Type | Description | Example Value |
| --- | --- | --- | --- |
| id | integer | Unique Account ID | `1001` |
**Request Body:**
``` json
{
  "targetStatus": "ACTIVATED"
}
```
**Fields:**

| Field | Type | Description | Example Value |
| --- | --- | --- | --- |
| targetStatus | string | New status for the account | `ACTIVATED/DEACTIVATED` |
**Responses:**
- **204 No Content:** Status updated successfully.
- **404 Not Found:** Account not found.
- **400 Bad Request:** Invalid status transition.
- **409 Conflict:** Forbidden status change.

## Card Management
### 1. **Create Card**
**Endpoint:** `POST /api/cards`
**Summary:** Create a new card with RFID and visible number.
**Request Body:**
``` json
{
  "rfidUid": "rfid123",
  "visibleNumber": "card123"
}
```
**Fields:**

| Field | Type | Description | Constraints |
| --- | --- | --- | --- |
| rfidUid | string | RFID Unique Identifier for the card | Required, max 100 chars |
| visibleNumber | string | Visible card number | Required, max 100 chars |
**Responses:**
- **201 Created:** Successfully created card details.
- **400 Bad Request:** Invalid request body.
- **409 Conflict:** RFID UID is already in use.

### 2. **Retrieve Cards**
**Endpoint:** `GET /api/cards`
**Summary:** Retrieve a paginated list of all cards with optional filters (e.g., time range).
**Query Parameters:**

| Parameter | Type | Description | Example Value |
| --- | --- | --- | --- |
| lastUpdatedFrom | string | Start datetime (ISO 8601). | `2025-01-01T00:00:00Z` |
| lastUpdatedTo | string | End datetime (ISO 8601). | `2025-12-31T00:00:00Z` |
| pageNumber | integer | Zero-based pagination starting from `0`. | `1` |
| pageSize | integer | Records per page | `5` |
**Responses:**
- **200 OK:** Paginated card list returned.
- **400 Bad Request:** Invalid pagination or filters provided.

### 3. **Retrieve a Card by ID**
**Endpoint:** `GET /api/cards/{id}`
**Summary:** Retrieve a specific card's details using its unique card ID.
**Path Parameters:**

| Parameter | Type | Description | Example Value |
| --- | --- | --- | --- |
| id | integer | Unique Card ID | `12` |
**Responses:**
- **200 OK:** Card details.
- **404 Not Found:** No card exists for the given ID.

### 4. **Update Card Status**
**Endpoint:** `PATCH /api/cards/{id}/status`
**Summary:** Update the status of a card (activate, deactivate, assign).
**Path Parameters:**

| Parameter | Type | Description | Example Value |
| --- | --- | --- | --- |
| id | integer | Unique Card ID | `12` |
**Request Body:**
``` json
{
  "targetStatus": "ASSIGNED",
  "assignToAccount": 1001
}
```
**Fields:**

| Field | Type | Description | Constraints |
| --- | --- | --- | --- |
| targetStatus | string | Target status to transition to. | `ASSIGNED/ACTIVATED/DEACTIVATED` |
| assignToAccount | integer | Account ID to assign the card to (if needed). | Only for "ASSIGNED". |
**Responses:**
- **204 No Content:** Status updated successfully.
- **400 Bad Request:** Invalid status or transition.
- **404 Not Found:** Card/account not found.
- **409 Conflict:** Invalid operation (e.g., data collision).

## Health Check
**Endpoint:** `GET /health`
**Description:** Verifies the availability of the service.
**Responses:**
- **200 OK:** Service is up and running.

## Models & Entities
### 1. **AccountDTO**
**Description:** Data Transfer Object for Account.

| Field | Type | Description | Format |
| --- | --- | --- | --- |
| accountId | integer | Unique account ID. | N/A |
| email | string | Account email address. | N/A |
| emaid | string | EMAID tied to the account. | N/A |
| status | string | Status of the account. | `CREATED/ACTIVATED/DEACTIVATED` |
| lastUpdated | datetime | Last updated timestamp. | ISO 8601 (e.g., `2025-06-24T10:15:30Z`) |
### 2. **CardDTO**
**Description:** Data Transfer Object for Card.

| Field | Type | Description | Format |
| --- | --- | --- | --- |
| cardId | integer | Unique card ID. | N/A |
| rfidUid | string | RFID UID associated with the card. | N/A |
| visibleNumber | string | Visible card number. | N/A |
| status | string | Current card status. | `CREATED/ASSIGNED/ACTIVATED/DEACTIVATED` |
| createdAt | datetime | Card creation time. | ISO 8601 (e.g., `2025-05-01T12:34:56Z`) |
| lastUpdated | datetime | Last updated timestamp. | ISO 8601 (e.g., `2025-06-24T10:15:30Z`) |
### 3. **ErrorResponse**
**Description:** Standard error response returned for API errors.

| Field | Type | Description | Format                                      |
| --- | --- | --- |---------------------------------------------|
| status | integer | HTTP status code. | N/A                                         |
| title | string | Error summary or title. | N/A                                         |
| details | array | List of specific error messages or details. | N/A                                         |
| path | string | API path where the error occurred. | N/A                                         |
| timestamp | datetime | Time of the error occurrence. | ISO 8601 (e.g., `2025-06-24T10:15:30.000Z`) |
