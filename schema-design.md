# Smart Clinic System – Database Schema Design

## 1. MySQL Database Design

Below each table, the columns include:

- **Name**
- **Data type**
- **Constraints** (PK = primary key, FK = foreign key)

---

### 1.1. `patients` Table

Basic information about patients.

- `patient_id` – INT, PK, auto-increment, NOT NULL  
- `first_name` – VARCHAR(50), NOT NULL  
- `last_name` – VARCHAR(50), NOT NULL  
- `date_of_birth` – DATE, NOT NULL  
- `gender` – ENUM('M', 'F', 'O'), NOT NULL  
- `phone_number` – VARCHAR(20), NOT NULL, UNIQUE  
- `email` – VARCHAR(100), NOT NULL, UNIQUE  
- `created_at` – DATETIME, NOT NULL, default current timestamp  

**Why this design is simple:**  
We keep only essential identity/contact fields and one primary key (`patient_id`) to link to other tables.

---

### 1.2. `doctors` Table

Basic information about doctors.

- `doctor_id` – INT, PK, auto-increment, NOT NULL  
- `first_name` – VARCHAR(50), NOT NULL  
- `last_name` – VARCHAR(50), NOT NULL  
- `specialization` – VARCHAR(100), NOT NULL  
- `email` – VARCHAR(100), NOT NULL, UNIQUE  
- `phone_number` – VARCHAR(20), NULL  
- `created_at` – DATETIME, NOT NULL, default current timestamp  

**Why this design is simple:**  
We store only the data we need to identify a doctor and contact them, plus a single primary key (`doctor_id`).

---

### 1.3. `admins` Table

System users who can manage data (e.g., receptionists, managers).

- `admin_id` – INT, PK, auto-increment, NOT NULL  
- `username` – VARCHAR(50), NOT NULL, UNIQUE  
- `password_hash` – VARCHAR(255), NOT NULL  
- `role` – ENUM('SUPER_ADMIN', 'STAFF'), NOT NULL, default 'STAFF'  
- `created_at` – DATETIME, NOT NULL, default current timestamp  

**Why this design is simple:**  
We only store what is needed for login and basic role-based access.

---

### 1.4. `appointments` Table

Connects a patient with a doctor at a specific time.

- `appointment_id` – INT, PK, auto-increment, NOT NULL  
- `patient_id` – INT, NOT NULL, FK → `patients.patient_id`  
- `doctor_id` – INT, NOT NULL, FK → `doctors.doctor_id`  
- `scheduled_at` – DATETIME, NOT NULL  
- `status` – ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED'), NOT NULL, default 'SCHEDULED'  
- `created_by_admin_id` – INT, NOT NULL, FK → `admins.admin_id`  

## 2. MongoDB Collection Design

### 2.1. `prescriptions` Collection

A flexible document for prescriptions. It uses nested objects and an array.

#### Example document

{
  "_id": "675f0c823d5c4f00125e4b91",

  "patient": {
    "id": 1,                 // matches patients.patient_id in MySQL
    "fullName": "John Doe"
  },

  "doctor": {
    "id": 2,                 // matches doctors.doctor_id in MySQL
    "fullName": "Dr. Ada Love"
  },

  "appointmentId": 10,        // matches appointments.appointment_id in MySQL

  "issuedAt": "2025-12-23T09:00:00Z",

  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500 mg",
      "frequency": "3 times a day",
      "durationDays": 5
    },
    {
      "name": "Ibuprofen",
      "dosage": "200 mg",
      "frequency": "2 times a day",
      "durationDays": 3
    }
  ],

  "notes": "Take with food and plenty of water.",

  "status": "ACTIVE"          // e.g., ACTIVE or CANCELLED
}
