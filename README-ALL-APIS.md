# Expense Claim Management Backend - Implemented API Modules

Base URL: `http://localhost:8080`

All JSON endpoints return `ResponseEntity<Response>`. Global errors return `ApiErrorResponse`. Receipt binary download returns a streamed `ResponseEntity<Resource>` so the file is not base64-expanded in memory.

## Authentication (`/api/v1/auth`)
- POST `/register` employee signup
- POST `/login` both roles
- POST `/refresh` rotate refresh token
- POST `/logout` revoke refresh token
- GET `/me` current principal

## Connection (`/api/v1/system`)
- GET `/ping`
- GET `/status`

## Employee claims (`/api/v1/claims`, EMPLOYEE)
- POST `/` create pending claim; body `{title,amount,categoryId,expenseDate,description}`
- GET `/` and GET `/my-claims` with `page,size,status,search`
- GET `/{claimId}`
- PUT `/{claimId}` edit only while PENDING
- DELETE `/{claimId}` delete only while PENDING
- PATCH `/{claimId}/submit`
- GET `/{claimId}/timeline`

## Manager claims (`/api/v1/manager/claims`, MANAGER)
- GET `/` filters `status,employeeId,fromDate,toDate,search,page,size`
- GET `/pending`
- PATCH `/{claimId}/approve` optional `{comment}`
- PATCH `/{claimId}/reject` mandatory `{comment}`

## Receipts (`/api/v1/claims/{claimId}/receipts`)
- POST `/` multipart field `file`, JPG/PNG/WEBP/PDF, max 5MB
- GET `/` metadata, employee owner or manager
- GET `/{receiptId}` download stream, employee owner or manager
- DELETE `/` employee owner, only while claim PENDING

## Categories (`/api/v1/categories`)
- GET `/`, GET `/{id}` active category lookup/list
- POST `/`, PUT `/{id}`, DELETE `/{id}` manager only; DELETE soft-deactivates

## Dashboards
- GET `/api/v1/employee/dashboard` EMPLOYEE
- GET `/api/v1/manager/dashboard` MANAGER

## Manager user management (`/api/v1/manager/users`)
- GET `/` paginated employees
- GET `/{userId}`
- PATCH `/{userId}/status` body `{ "active": false }`

## Important implementation notes
- New registrations always receive EMPLOYEE role. Manager is seeded from environment variables.
- Employee claim access is ownership-checked in the service, not just the controller.
- Claim status uses the existing model: PENDING, APPROVED, REJECTED. Creation starts PENDING, matching the supplied task requirement that edits/deletes are allowed while Pending.
- One receipt per claim is supported by the supplied `@OneToOne` entity design.
- Audit events are persisted for create/update/delete/review/receipt changes.
- Receipt files are stored under `./uploads/receipts` by default. Back up this directory with the database.
- For receipt downloads, binary streaming is intentionally used instead of wrapping bytes in `Response`.

Import `postman/Expense-Claim-Management-Complete.postman_collection.json` into Postman. Set collection variables for employee/manager tokens and UUIDs after creating records. For receipt upload, choose Body > form-data and set key `file` to File.
