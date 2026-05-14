-- ==============================================================================
-- DATABASE MIGRATION SCRIPT (SAFE UPDATE)
-- ==============================================================================
-- INSTRUCTIONS:
-- This script uses ALTER TABLE to safely modify existing tables without dropping them,
-- ensuring 100% data preservation. 
-- NOTE: For completely new entities (like Employee_Loan_Details) that did not exist 
-- in the base schema, CREATE TABLE IF NOT EXISTS is used since ALTER cannot 
-- create a non-existent table.
-- ==============================================================================

USE timber_business;

-- 1. USER TABLE: Add 'CUSTOMER' to Role
-- (COMMENTED OUT AS PER REQUEST - Do not modify User table due to authentication dependencies)
/*
ALTER TABLE User 
MODIFY COLUMN Role ENUM('Admin', 'Supplier', 'Manager', 'CUSTOMER') NOT NULL;
*/

-- 2. SUPPLIER TABLE: Add NIC
-- (Ignore error if column already exists)
ALTER TABLE Supplier 
ADD COLUMN NIC VARCHAR(50) DEFAULT NULL;

-- 3. EXPENSE TYPE & ACCOUNT TABLES (Rename from old camel case to lower snake case)
-- (Ignore errors if tables are already renamed)
RENAME TABLE Expence_Type TO expense_type;
RENAME TABLE Expence_Account TO expense_account;

ALTER TABLE expense_type 
RENAME COLUMN Expence_Type_id TO expense_type_id,
RENAME COLUMN Type_Name TO type_name;

ALTER TABLE expense_account 
RENAME COLUMN Expence_id TO expense_id,
RENAME COLUMN Date TO date,
RENAME COLUMN Amount TO amount,
RENAME COLUMN Description TO description;

-- 4. EMPLOYEE ATTENDANCE: Update Status to Uppercase Enum
ALTER TABLE Employee_Attendance 
MODIFY COLUMN Status ENUM('PRESENT', 'ABSENT', 'HALF_DAY', 'LEAVE') DEFAULT 'PRESENT';

-- 5. EMPLOYEE SALARY DETAILS: Add New Payroll Calculation Columns
-- (Ignore errors if columns already exist)
ALTER TABLE Employee_Salary_details
  ADD COLUMN Working_Days INT DEFAULT 0,
  ADD COLUMN Worked_Days INT DEFAULT 0,
  ADD COLUMN Basic_Salary DECIMAL(15,2) DEFAULT 0.00,
  ADD COLUMN Overtime_Amount DECIMAL(15,2) DEFAULT 0.00,
  ADD COLUMN Loan_Deduction DECIMAL(15,2) DEFAULT 0.00,
  ADD COLUMN Other_Deduction DECIMAL(15,2) DEFAULT 0.00,
  ADD COLUMN Total_Amount DECIMAL(15,2) GENERATED ALWAYS AS (Basic_Salary + Overtime_Amount - Loan_Deduction - Other_Deduction) STORED,
  ADD COLUMN Paid_Amount DECIMAL(15,2) DEFAULT 0.00,
  ADD COLUMN Balance_Amount DECIMAL(15,2) GENERATED ALWAYS AS (Total_Amount - Paid_Amount) STORED,
  ADD COLUMN Status ENUM('Pending','Partially_Paid','Paid') DEFAULT 'Pending',
  ADD COLUMN Remarks TEXT;

-- Drop any old constraint if exists, then add the new unique key
-- (Ignore error if key already exists)
ALTER TABLE Employee_Salary_details 
  ADD UNIQUE KEY unique_employee_month (Employee_id, Month, Year);

-- 6. EMPLOYEE LOAN: Safely migrate Enum states to uppercase
-- Step A: Set the new Enum values (MySQL treats 'Active' and 'ACTIVE' as the same, so no duplicate allowed)
ALTER TABLE Employee_Loan 
MODIFY COLUMN Status ENUM('ACTIVE', 'PARTIALLY_PAID', 'COMPLETED', 'CANCELLED', 'Settled') DEFAULT 'ACTIVE';

-- Step B: Migrate old 'Settled' data to 'COMPLETED'
UPDATE Employee_Loan SET Status = 'COMPLETED' WHERE Status = 'Settled';

-- Step C: Lock Enum strictly to the final values
ALTER TABLE Employee_Loan 
MODIFY COLUMN Status ENUM('ACTIVE', 'PARTIALLY_PAID', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE';



-- ==============================================================================
-- NEW TABLES (Required for the new final schema, cannot use ALTER)
-- ==============================================================================

CREATE TABLE IF NOT EXISTS Employee_Loan_Details (
    Details_id INT PRIMARY KEY AUTO_INCREMENT,
    Loan_id INT NOT NULL,
    Date DATE NOT NULL,
    Amount DECIMAL(15,2) NOT NULL,
    Salary_details_id INT NULL, 
    Remarks TEXT,
    FOREIGN KEY (Loan_id) REFERENCES Employee_Loan(Loan_id),
    FOREIGN KEY (Salary_details_id) REFERENCES Employee_Salary_details(Salary_details_id)
);

CREATE TABLE IF NOT EXISTS Equity_Account (
    Equity_id     INT PRIMARY KEY AUTO_INCREMENT,
    Date          DATE NOT NULL,
    Type          ENUM('Capital', 'Drawing', 'Retained_Earnings', 'Other') NOT NULL,
    Amount        DECIMAL(15,2) NOT NULL,
    Description   TEXT,
    Created_by    INT, 
    FOREIGN KEY (Created_by) REFERENCES User(User_id)
);

CREATE TABLE IF NOT EXISTS Asset_Account (
    Asset_id          INT PRIMARY KEY AUTO_INCREMENT,
    Asset_Name        VARCHAR(200) NOT NULL,
    Asset_Type        ENUM('Cash', 'Raw Material Stock', 'Finished Timber',
                           'Machinery', 'Vehicle', 'Office Equipment', 'Other') NOT NULL,
    Purchase_Date     DATE,
    Purchase_Value    DECIMAL(15,2) NOT NULL,
    Current_Value     DECIMAL(15,2),
    Depreciation_Rate DECIMAL(5,2) DEFAULT 0.00,
    Status            ENUM('Active', 'Disposed', 'Under Repair') DEFAULT 'Active',
    Description       TEXT,
    Created_by        INT,
    FOREIGN KEY (Created_by) REFERENCES User(User_id)
);

CREATE TABLE IF NOT EXISTS Employee_Paysheet (
    Paysheet_id INT AUTO_INCREMENT PRIMARY KEY,
    Employee_id INT NOT NULL,
    Month INT NOT NULL,
    Year INT NOT NULL,
    Base_Salary DECIMAL(15, 2) DEFAULT 0.00,
    Overtime_Amount DECIMAL(15, 2) DEFAULT 0.00,
    Loan_Deduction DECIMAL(15, 2) DEFAULT 0.00,
    Other_Deduction DECIMAL(15, 2) DEFAULT 0.00,
    Total_Earnings DECIMAL(15, 2) DEFAULT 0.00,
    Net_Salary DECIMAL(15, 2) DEFAULT 0.00,
    Present_Days INT DEFAULT 0,
    Generated_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    File_Path VARCHAR(500),
    CONSTRAINT FK_Emp_Paysheet FOREIGN KEY (Employee_id) REFERENCES Employee(Id),
    UNIQUE KEY unique_paysheet_record (Employee_id, Month, Year)
);

-- 7. QUOTATION TABLE: Add Quotation_Number and Stored Procedure
ALTER TABLE timber_business.quotation
ADD COLUMN Quotation_Number VARCHAR(20) NULL AFTER Quotation_Id;

DELIMITER $$

CREATE PROCEDURE Generate_Quotation_Number()
BEGIN
    DECLARE last_seq INT DEFAULT 0;
    DECLARE current_year CHAR(2);
    DECLARE quotation_number VARCHAR(20);
    
    SET current_year = RIGHT(YEAR(CURDATE()), 2);
    
    -- Get last sequence from Quotation_Number column for current year
    SELECT COALESCE(MAX(CAST(RIGHT(Quotation_Number, 5) AS UNSIGNED)), 0)
    INTO last_seq
    FROM timber_business.quotation
    WHERE LEFT(Quotation_Number, 5) = CONCAT('QUO', current_year);
    
    SET last_seq = last_seq + 1;
    
    SET quotation_number = CONCAT('QUO', current_year, LPAD(last_seq, 5, '0'));
    
    SELECT quotation_number AS Quotation_Number;
    
END$$

DELIMITER ;

-- 8. CUSTOMER ORDER TABLE: Add Order_Number and Stored Procedure
ALTER TABLE timber_business.customer_order
ADD COLUMN Order_Number VARCHAR(20) NULL AFTER Order_Id;

DELIMITER $$

CREATE PROCEDURE Generate_Order_Number()
BEGIN
    DECLARE last_seq INT DEFAULT 0;
    DECLARE current_year CHAR(2);
    DECLARE order_number VARCHAR(20);
    
    SET current_year = RIGHT(YEAR(CURDATE()), 2);
    
    -- Get last sequence from Order_Number column for current year
    SELECT COALESCE(MAX(CAST(RIGHT(Order_Number, 6) AS UNSIGNED)), 0)
    INTO last_seq
    FROM timber_business.customer_order
    WHERE LEFT(Order_Number, 5) = CONCAT('ORD', current_year);
    
    SET last_seq = last_seq + 1;
    
    -- ORD + 2-digit year + 6-digit sequence = ORD26000001
    SET order_number = CONCAT('ORD', current_year, LPAD(last_seq, 6, '0'));
    
    SELECT order_number AS Order_Number;
    
END$$

DELIMITER ;


