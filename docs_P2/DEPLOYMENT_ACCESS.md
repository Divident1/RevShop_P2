# RevShop Deployment Access Details

This file records the currently verified public access details for the deployed RevShop application.

## Public Access

- Frontend URL: `http://13.233.84.134/`
- EC2 public DNS: `http://ec2-13-233-84-134.ap-south-1.compute.amazonaws.com/`
- API base URL through Nginx: `http://13.233.84.134/api`
- Direct backend port: `http://13.233.84.134:8080`

## AWS Host Details

- AWS region: `ap-south-1`
- EC2 instance name: `RevShop-Prod`
- EC2 instance ID: `i-0f586a151e60d565d`
- Public IP: `13.233.84.134`
- Public DNS: `ec2-13-233-84-134.ap-south-1.compute.amazonaws.com`
- Security group: `revshop-ec2-sg`

## Seeded Application Login Credentials

These users are created by the backend seeder when the database starts empty.

### Seller Accounts

- `tech@revshop.com` / `password123`
- `fashion@revshop.com` / `password123`
- `home@revshop.com` / `password123`

### Buyer Accounts

- `pavan@example.com` / `pavan123`
- `john@example.com` / `john123`

## Docker / Database Details

- MySQL database name: `revshop_p2`
- MySQL root username: `root`
- MySQL root password: `root`
- Backend internal DB URL: `jdbc:mysql://db:3306/revshop_p2?allowPublicKeyRetrieval=true&useSSL=false`

## Run Locally

You can run this project locally in either of these two ways.

### Option 1: Run with Docker Compose

From the project root:

```bash
docker compose up --build
```

Local URLs:

- Frontend: `http://localhost/`
- Backend API: `http://localhost:8080/api`
- MySQL: `localhost:3306`

Stop the containers:

```bash
docker compose down
```

### Option 2: Run Backend and Frontend Separately

Prerequisites:

- Java `17+`
- Maven
- Node.js `18+`
- npm
- MySQL `8+`

1. Start MySQL and create the database:

```sql
CREATE DATABASE revshop_p2;
```

2. Start the backend:

```bash
cd backend
export DB_URL='jdbc:mysql://localhost:3306/revshop_p2?allowPublicKeyRetrieval=true&useSSL=false'
export DB_USERNAME='root'
export DB_PASSWORD='root'
mvn spring-boot:run
```

Backend runs at:

- `http://localhost:8080`
- API base: `http://localhost:8080/api`

3. Start the frontend in a new terminal:

```bash
cd frontend
npm install
npm start
```

Frontend runs at:

- `http://localhost:4200`

### Local Login Notes

- On localhost, the Angular app automatically uses `http://localhost:8080/api`.
- Seeded login users are inserted only if the database is empty when the backend starts.

## Notes

- The Angular frontend uses `/api` when running on a deployed host, so users should open the frontend URL, not `:8080`, for normal app access.
- The backend seeder only inserts the sample users above when the `users` table is empty.
- These details were verified against the project code and the running AWS EC2 instance on `2026-03-17`.
