CREATE DATABASE timber_business;
USE timber_business;

CREATE TABLE user (
                      user_id INT NOT NULL AUTO_INCREMENT,
                      user_name VARCHAR(100) NOT NULL,
                      account_locked BIT(1) DEFAULT NULL,
                      email VARCHAR(255) DEFAULT NULL,
                      failed_login_attempts INT DEFAULT NULL,
                      lock_time DATETIME(6) DEFAULT NULL,
                      phone_number VARCHAR(15) DEFAULT NULL,
                      password VARCHAR(255) NOT NULL,
                      role ENUM('ADMIN','SUPPLIER','MANAGER') NOT NULL,
                      user_details TEXT DEFAULT NULL,
                      created_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
                      last_login TIMESTAMP NULL DEFAULT NULL,
                      is_active TINYINT(1) DEFAULT 1,
                      last_activity DATETIME(6) DEFAULT NULL,
                      PRIMARY KEY (user_id),
                      UNIQUE KEY uk_user_name (user_name),
                      UNIQUE KEY uk_email (email)
);

-- ============================================
-- CUSTOMER MANAGEMENT (Timber Suppliers)
-- ============================================

CREATE TABLE Customer (
    Cus_id INT PRIMARY KEY AUTO_INCREMENT,
    Cus_name VARCHAR(200) NOT NULL,
    Mobile VARCHAR(15) NOT NULL,
    Address TEXT,
    Email VARCHAR(100),
    NIC VARCHAR(20),
    Created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Is_active BOOLEAN DEFAULT TRUE
);

-- ============================================
-- SUPPLIER MANAGEMENT (Product Suppliers)
-- ============================================

CREATE TABLE Supplier (
    Sup_id INT PRIMARY KEY AUTO_INCREMENT,
    Sup_name VARCHAR(200) NOT NULL,
    Sup_Cat VARCHAR(100),
    Mobile VARCHAR(15) NOT NULL,
    Address TEXT,
    Email VARCHAR(100),
    NIC VARCHAR(20),
    Created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Is_active BOOLEAN DEFAULT TRUE
);

-- ============================================
-- EMPLOYEE MANAGEMENT
-- ============================================

CREATE TABLE Employee (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Full_Name VARCHAR(200) NOT NULL,
    Designation VARCHAR(100),
    Address TEXT,
    NIC VARCHAR(20) UNIQUE,
    Mobile_Number VARCHAR(15),
    Email VARCHAR(100),
    Date_Joined DATE,
    Is_active BOOLEAN DEFAULT TRUE
);
SELECT Id , Full_Name , NIC FROM Employee;
-- ============================================
-- PRODUCT CATEGORY
-- ============================================

CREATE TABLE Prouct_Category (
    Product_Cat_id INT PRIMARY KEY AUTO_INCREMENT,
    Product_Cat_name VARCHAR(150) NOT NULL,
    Material_Category VARCHAR(100),
    Unit_of_Measurement ENUM('Cubic Feet', 'Square Feet', 'Pieces', 'Kg') DEFAULT 'Square Feet',
    Description TEXT
);

-- ============================================
-- QUOTATION MANAGEMENT
-- ============================================

CREATE TABLE Quotation (
    Quotation_Id INT PRIMARY KEY AUTO_INCREMENT,
    Customer_Id INT NOT NULL,
    Total_Amount DECIMAL(15,2) DEFAULT 0.00,
    Status ENUM('Pending', 'Approved', 'Rejected', 'Converted') DEFAULT 'Pending',
    Quotation_Date DATE NOT NULL,
    Valid_Until DATE,
    Created_by INT,
    Remarks TEXT,
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Cus_id),
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

CREATE TABLE Quotation_Details (
    Details_id INT PRIMARY KEY AUTO_INCREMENT,
    Quotation_Id INT NOT NULL,
    Order_Id INT,
    Product_Cat_id INT NOT NULL,
    Name VARCHAR(200),
    Quantity DECIMAL(10,2) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Line_Total DECIMAL(15,2) GENERATED ALWAYS AS (Quantity * Price) STORED,
    FOREIGN KEY (Quotation_Id) REFERENCES Quotation(Quotation_Id) ON DELETE CASCADE,
    FOREIGN KEY (Product_Cat_id) REFERENCES Prouct_Category(Product_Cat_id)
);

-- ============================================
-- CUSTOMER ORDER (SALES)
-- ============================================

CREATE TABLE Customer_Order (
    Order_Id INT PRIMARY KEY AUTO_INCREMENT,
    Customer_Id INT NOT NULL,
    Receipt_Number VARCHAR(50),
    Total_Amount DECIMAL(15,2) DEFAULT 0.00,
    Paid_Amount DECIMAL(15,2) DEFAULT 0.00,
    Balance_Amount DECIMAL(15,2) GENERATED ALWAYS AS (Total_Amount - Paid_Amount) STORED,
    Status ENUM('Pending', 'Processing', 'Completed', 'Cancelled') DEFAULT 'Pending',
    Order_Date DATE NOT NULL,
    Created_by INT,
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Cus_id),
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

CREATE TABLE Customer_Order_Details (
    Details_id INT PRIMARY KEY AUTO_INCREMENT,
    Order_Id INT NOT NULL,
    Product_Cat_id INT NOT NULL,
    Name VARCHAR(200),
    Quantity DECIMAL(10,2) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Line_Total DECIMAL(15,2) GENERATED ALWAYS AS (Quantity * Price) STORED,
    FOREIGN KEY (Order_Id) REFERENCES Customer_Order(Order_Id) ON DELETE CASCADE,
    FOREIGN KEY (Product_Cat_id) REFERENCES Prouct_Category(Product_Cat_id)
);

-- ============================================
-- RECEIPT MANAGEMENT
-- ============================================

CREATE TABLE Receipt (
    Receipt_Id INT PRIMARY KEY AUTO_INCREMENT,
    Receipt_Number VARCHAR(50) NOT NULL UNIQUE,
    Date DATE NOT NULL,
    Payment_Method ENUM('Cash', 'Card', 'Bank Transfer', 'Cheque') DEFAULT 'Cash',
    Customer_Id INT NOT NULL,
    Total_Amount DECIMAL(15,2) NOT NULL,
    Created_by INT,
    Remarks TEXT,
    FOREIGN KEY (Customer_Id) REFERENCES Customer(Cus_id),
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

CREATE TABLE Receipt_Details (
    Receipt_Details_Id INT PRIMARY KEY AUTO_INCREMENT,
    Receipt_Id INT NOT NULL,
    Customer_Order_Details_Id INT NOT NULL,
    Amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (Receipt_Id) REFERENCES Receipt(Receipt_Id) ON DELETE CASCADE,
    FOREIGN KEY (Customer_Order_Details_Id) REFERENCES Customer_Order_Details(Details_id)
);

-- ============================================
-- RAW MATERIAL ITEMS
-- ============================================

CREATE TABLE Row_Material_Item (
    RM_id INT PRIMARY KEY AUTO_INCREMENT,
    RM_name VARCHAR(200) NOT NULL,
    Price_Per_CFT DECIMAL(10,2),
    Description TEXT
);

-- ============================================
-- SUPPLY RAW MATERIAL (Timber Purchase from Customers)
-- ============================================

CREATE TABLE Supply_RAW_Material (
    Supply_id INT PRIMARY KEY AUTO_INCREMENT,
    Supplier_id INT NOT NULL,
    RM_id INT NOT NULL,
    Invoice_Number VARCHAR(50),
    Total_Amount DECIMAL(15,2) DEFAULT 0.00,
    Transport DECIMAL(10,2) DEFAULT 0.00,
    Cutting_Fee_id INT,
    Supply_Date DATE NOT NULL,
    Created_by INT,
    FOREIGN KEY (Supplier_id) REFERENCES Customer(Cus_id),
    FOREIGN KEY (RM_id) REFERENCES Row_Material_Item(RM_id),
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

CREATE TABLE Supply_RAW_Material_Details (
    Supply_RAW_Material_Details_ID INT PRIMARY KEY AUTO_INCREMENT,
    Supply_id INT NOT NULL,
    RM_id INT NOT NULL,
    Log_Number INT,
    Length_ft DECIMAL(10,2) NOT NULL,
    Girth_ft DECIMAL(10,2) NOT NULL,
    Total_Quantity_CFT DECIMAL(10,3) GENERATED ALWAYS AS ((Length_ft * Girth_ft * Girth_ft) / 12) STORED,
    Price DECIMAL(10,2) NOT NULL,
    Line_Total DECIMAL(15,2) GENERATED ALWAYS AS ((Length_ft * Girth_ft * Girth_ft / 12) * Price) STORED,
    FOREIGN KEY (Supply_id) REFERENCES Supply_RAW_Material(Supply_id) ON DELETE CASCADE,
    FOREIGN KEY (RM_id) REFERENCES Row_Material_Item(RM_id)
);

-- ============================================
-- RAW MATERIAL CUTTING FEE
-- ============================================

CREATE TABLE RAW_Material_Cutting_Fee (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Supply_id INT NOT NULL,
    Employee_id INT NOT NULL,
    Fee DECIMAL(10,2) NOT NULL,
    Date DATE NOT NULL,
    Remarks TEXT,
    FOREIGN KEY (Supply_id) REFERENCES Supply_RAW_Material(Supply_id),
    FOREIGN KEY (Employee_id) REFERENCES Employee(Id)
);

-- ============================================
-- GRN (GOODS RECEIVED NOTE)
-- ============================================

CREATE TABLE GRN (
    GRN_id INT PRIMARY KEY AUTO_INCREMENT,
    GRN_Number VARCHAR(50) NOT NULL UNIQUE,
    Date DATE NOT NULL,
    Amount DECIMAL(15,2) NOT NULL,
    Created_by INT,
    Remarks TEXT,
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

CREATE TABLE GRN_Details (
    GRN_details_ID INT PRIMARY KEY AUTO_INCREMENT,
    GRN_ID INT NOT NULL,
    Supply_RAW_Material_Details_ID INT NOT NULL,
    GRN_Number VARCHAR(50),
    Date DATE NOT NULL,
    Amount DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (GRN_ID) REFERENCES GRN(GRN_id) ON DELETE CASCADE,
    FOREIGN KEY (Supply_RAW_Material_Details_ID) REFERENCES Supply_RAW_Material_Details(Supply_RAW_Material_Details_ID)
);

-- ============================================
-- INCOME ACCOUNT
-- ============================================

CREATE TABLE Income_Account (
    Income_Id INT PRIMARY KEY AUTO_INCREMENT,
    Date DATE NOT NULL,
    Receipt_Id INT,
    Amount DECIMAL(15,2) NOT NULL,
    Description TEXT,
    Created_by INT,
    FOREIGN KEY (Receipt_Id) REFERENCES Receipt(Receipt_Id),
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

-- ============================================
-- EXPENSE TYPE
-- ============================================

CREATE TABLE Expence_Type (
    Expence_Type_Id INT PRIMARY KEY AUTO_INCREMENT,
    Description VARCHAR(200) NOT NULL
);

-- ============================================
-- EXPENSE ACCOUNT
-- ============================================

CREATE TABLE Expence_Account (
    Expence_Id INT PRIMARY KEY AUTO_INCREMENT,
    Expence_Type_Id INT NOT NULL,
    Date DATE NOT NULL,
    GRN_ID INT,
    Amount DECIMAL(15,2) NOT NULL,
    Description TEXT,
    Created_by INT,
    FOREIGN KEY (Expence_Type_Id) REFERENCES Expence_Type(Expence_Type_Id),
    FOREIGN KEY (GRN_ID) REFERENCES GRN(GRN_id),
    FOREIGN KEY (Created_by) REFERENCES User(user_id)
);

-- ============================================
-- EMPLOYEE SALARY DETAILS
-- ============================================

CREATE TABLE Employee_Salary_details (
    Salary_details_id INT PRIMARY KEY AUTO_INCREMENT,
    Employee_id INT NOT NULL,
    Month INT NOT NULL CHECK (Month BETWEEN 1 AND 12),
    Year INT NOT NULL,
    Total_Amount DECIMAL(15,2) DEFAULT 0.00,
    Paid_Amount DECIMAL(15,2) DEFAULT 0.00,
    Balance_Amount DECIMAL(15,2) GENERATED ALWAYS AS (Total_Amount - Paid_Amount) STORED,
    Status ENUM('Pending', 'Partially Paid', 'Paid') DEFAULT 'Pending',
    FOREIGN KEY (Employee_id) REFERENCES Employee(Id),
    UNIQUE KEY unique_employee_month (Employee_id, Month, Year)
);

-- ============================================
-- EMPLOYEE ATTENDANCE
-- ============================================

CREATE TABLE Employee_Attendance (
    Attend_id INT PRIMARY KEY AUTO_INCREMENT,
    Employee_id INT NOT NULL,
    Date DATE NOT NULL,
    Status ENUM('Present', 'Absent', 'Half Day', 'Leave') DEFAULT 'Present',
    Check_In TIME,
    Check_Out TIME,
    Remarks TEXT,
    FOREIGN KEY (Employee_id) REFERENCES Employee(Id),
    UNIQUE KEY unique_attendance (Employee_id, Date)
);

-- ============================================
-- EMPLOYEE SALARY RATE
-- ============================================

CREATE TABLE Employee_Salary_Rate (
    Rate_id INT PRIMARY KEY AUTO_INCREMENT,
    Rate_Name VARCHAR(100) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    Rate_Type ENUM('Daily', 'Monthly', 'Hourly', 'Per Unit') DEFAULT 'Monthly',
    Effective_From DATE NOT NULL,
    Is_active BOOLEAN DEFAULT TRUE
);

-- ============================================
-- EMPLOYEE SALARY PAYMENT
-- ============================================

CREATE TABLE Employee_Salary_payment (
    Emp_Salary_Payment_Id INT PRIMARY KEY AUTO_INCREMENT,
    Salary_details_id INT NOT NULL,
    Date DATE NOT NULL,
    Amount DECIMAL(15,2) NOT NULL,
    Payment_Method ENUM('Cash', 'Bank Transfer', 'Cheque') DEFAULT 'Cash',
    Paid_by INT,
    Remarks TEXT,
    FOREIGN KEY (Salary_details_id) REFERENCES Employee_Salary_details(Salary_details_id),
    FOREIGN KEY (Paid_by) REFERENCES User(user_id)
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX idx_customer_mobile ON Customer(Mobile);
CREATE INDEX idx_supplier_mobile ON Supplier(Mobile);
CREATE INDEX idx_employee_nic ON Employee(NIC);
CREATE INDEX idx_quotation_date ON Quotation(Quotation_Date);
CREATE INDEX idx_order_date ON Customer_Order(Order_Date);
CREATE INDEX idx_receipt_date ON Receipt(Date);
CREATE INDEX idx_supply_date ON Supply_RAW_Material(Supply_Date);
CREATE INDEX idx_grn_date ON GRN(Date);
CREATE INDEX idx_income_date ON Income_Account(Date);
CREATE INDEX idx_expense_date ON Expence_Account(Date);
CREATE INDEX idx_attendance_date ON Employee_Attendance(Date);

-- ============================================
-- SAMPLE DATA FOR EXPENSE TYPES
-- ============================================

INSERT INTO Expence_Type (Description) VALUES
('Raw Material Purchase'),
('Employee Salary'),
('Cutting Fee'),
('Utilities'),
('Maintenance'),
('Office Expenses'),
('Other');
