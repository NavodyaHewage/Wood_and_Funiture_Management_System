-- ==============================================================================
-- DATABASE MIGRATION: Raw Material Supply Request Tables (Fixed)
-- ==============================================================================

USE timber_business;

-- Drop existing tables if they were partially created incorrectly
DROP TABLE IF EXISTS supply_raw_material_request_details;
DROP TABLE IF EXISTS supply_raw_material_request;

CREATE TABLE supply_raw_material_request (
    Request_id INT AUTO_INCREMENT PRIMARY KEY,
    Supplier_id INT NOT NULL,
    Request_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Status ENUM('Pending', 'Approved', 'Partially_Approved', 'Rejected', 'Converted') DEFAULT 'Pending',
    Transport_By_Supplier BOOLEAN DEFAULT FALSE,
    Transport_Notes TEXT,
    Remarks TEXT,
    Created_By INT,
    Approved_Date DATETIME,
    FOREIGN KEY (Supplier_id) REFERENCES Supplier(Sup_id),
    FOREIGN KEY (Created_By) REFERENCES user(user_id)
);

CREATE TABLE supply_raw_material_request_details (
    Request_Detail_id INT AUTO_INCREMENT PRIMARY KEY,
    Request_id INT NOT NULL,
    RM_id INT NOT NULL,
    Admin_Requested_CFT DECIMAL(10, 3) NOT NULL,
    Supplier_Approved_CFT DECIMAL(10, 3),
    Unit_Price DECIMAL(10, 2),
    Remarks TEXT,
    FOREIGN KEY (Request_id) REFERENCES supply_raw_material_request(Request_id) ON DELETE CASCADE,
    FOREIGN KEY (RM_id) REFERENCES Row_Material_Item(RM_id)
);
