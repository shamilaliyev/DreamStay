# Database Setup & Import

This project uses **PostgreSQL**. The application expects a database named `dreamstay` and a user `dreamstay_user`.

## Prerequisites
- PostgreSQL installed and running.

## 1. Create Database and User
Run the following commands in your terminal or pgAdmin query tool:

```sql
CREATE USER dreamstay_user WITH PASSWORD 'password';
CREATE DATABASE dreamstay;
GRANT ALL PRIVILEGES ON DATABASE dreamstay TO dreamstay_user;
```

## 2. Import Data
To see the sample data (Users, Properties, Messages), import the provided `database_dump.sql` file.

**Using Terminal:**
Navigate to the `backend` folder and run:
```bash
psql -U dreamstay_user -h localhost -d dreamstay -f database_dump.sql
```

**Using pgAdmin:**
1. Right-click on the `dreamstay` database.
2. Select **Restore**.
3. Select the `database_dump.sql` file.
4. Click **Restore**.

## 3. Run the Application
The application will now start with all data pre-loaded.
