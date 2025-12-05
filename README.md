# **Nexa – Boutique Inventory Management System**

A complete end-to-end **Inventory Management System** built for fashion boutiques.
This system supports product tracking, supplier management, stock monitoring, transactions (purchase, sales, returns), authentication, and role-based access control — all with a modern React UI and Spring Boot backend.

Link for Demo of the Application: https://drive.google.com/file/d/1F2eexpjX_vH-gkbrLhdfJM9WKTEHMP0B/view?usp=sharing 

---

## 🚀 **Tech Stack**

### **Backend**

* Java 21
* Spring Boot 3
* Spring Security (JWT Authentication)
* Hibernate / JPA
* MySQL
* Maven

### **Frontend**

* React
* Axios
* CryptoJS (Token Encryption)
* React Router

---

## 📦 **Features**

### 🔐 **Authentication & Authorization**

* User registration & login
* JWT-based security
* Roles: **Admin** & **Manager**
* Role-based UI and API access

### 🛍️ **Product Management**

* Add, edit, delete products
* Upload and store product images
* Assign categories & suppliers
* Search products
* View all products with pagination

### 🏷️ **Category Management**

* Create, update, delete categories
* Auto-populate category dropdowns
* View all categories

### 🚚 **Supplier Management**

* Add suppliers
* Update supplier info
* Remove suppliers
* Supplier dropdown available in product creation

### 🔄 **Transaction Management**

* Purchase stock
* Sell products
* Return items to suppliers
* Filter transactions by month and year

### 🖼️ **Image Handling**

* Upload product images
* Saved in React’s `public/products/` directory
* Stored with unique filenames

---

## 📁 **Project Structure**

```
root/
│
├── backend/
│   ├── src/main/java/com/phegondev/InventoryMgtSystem/
│   │   ├── controllers/
│   │   ├── dto/
│   │   ├── models/
│   │   ├── repositories/
│   │   ├── security/
│   │   ├── services/
│   │   └── InventoryMgtSystemApplication.java
│   └── resources/application.properties
│
├── frontend/
│   ├── public/products/          <-- image storage
│   ├── src/
│   │   ├── component/
│   │   ├── pages/
│   │   ├── service/ApiService.js
│   │   ├── App.js
│   │   └── Sidebar.jsx
│   └── package.json
│
└── README.md
```

---

## ⚙️ **Setup Instructions**

### **1️⃣ Clone the Repository**

```bash
git clone https://github.com/<your-username>/<repo-name>.git
```

---

## **2️⃣ Backend Setup (Spring Boot)**

### **Update MySQL Credentials**

Inside `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### **Run Backend**

```bash
cd backend
mvn spring-boot:run
```

Backend runs on:

```
http://localhost:5050
```

---

## **3️⃣ Frontend Setup (React)**

```bash
cd frontend
npm install
npm start
```

Frontend runs on:

```
http://localhost:3000
```

---

## 🔑 **Default Roles**

When registering a new user:

* Automatically assigned **MANAGER**
* Admin role must be manually set in MySQL:

```sql
UPDATE users SET role='ADMIN' WHERE email='your_email';
```

---

## 🧪 **Testing the Workflow**

1. Create categories (e.g., Dresses, Tops, Accessories)
2. Add suppliers
3. Add products with images
4. Perform transactions (purchase / sell / return)
5. View dashboard, charts, and stock updates

---

## 🧩 **Troubleshooting**

### ❗ Supplier dropdown empty?

Add this in frontend:

```jsx
const suppliers = await ApiService.getAllSuppliers();
```

### ❗ Product not saving?

Ensure:

* `supplierId` is included in request
* ProductController contains:

```java
@RequestParam("supplierId") Long supplierId
```

### ❗Images not displaying?

Images must be inside:

```
frontend/public/products/
```

---

## 🌟 **Future Improvements**

* Sales analytics dashboard
* PDF export for transactions
* Low-stock automatic alerts
* Cloud storage for images
* Multi-branch inventory support

---

## 👨‍💻 **Author**

**Kris Soni & Harsh Patel**
Inventory Management System — Nexa Boutique
Built for academic and professional learning purposes.

---
