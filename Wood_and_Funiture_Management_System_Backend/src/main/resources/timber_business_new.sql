-- =====================================================================
-- FULL DATABASE SCHEMA
-- Includes all original tables plus additional Foreign Key constraints
-- =====================================================================

CREATE TABLE `user` (
                        `user_id` int NOT NULL AUTO_INCREMENT,
                        `user_name` varchar(100) NOT NULL,
                        `account_locked` bit(1) DEFAULT NULL,
                        `email` varchar(255) DEFAULT NULL,
                        `failed_login_attempts` int DEFAULT NULL,
                        `lock_time` datetime(6) DEFAULT NULL,
                        `phone_number` varchar(15) DEFAULT NULL,
                        `password` varchar(255) NOT NULL,
                        `role` enum('ADMIN','SUPPLIER','EMPLOYEE') NOT NULL,
                        `user_details` text,
                        `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                        `last_login` timestamp NULL DEFAULT NULL,
                        `is_active` tinyint(1) DEFAULT '1',
                        `last_activity` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`user_id`),
                        UNIQUE KEY `uk_user_name` (`user_name`),
                        UNIQUE KEY `UK_bxs8c2q8tbrkh2hdweuly6psa` (`user_name`),
                        UNIQUE KEY `uk_email` (`email`),
                        UNIQUE KEY `UK_e6gkqunxajvyxl5uctpl2vl2p` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `customer` (
                            `Cus_id` int NOT NULL AUTO_INCREMENT,
                            `Cus_name` varchar(200) NOT NULL,
                            `Mobile` varchar(15) NOT NULL,
                            `Address` text,
                            `Email` varchar(100) DEFAULT NULL,
                            `NIC` varchar(20) DEFAULT NULL,
                            `Created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `Is_active` tinyint(1) DEFAULT '1',
                            PRIMARY KEY (`Cus_id`),
                            KEY `idx_customer_mobile` (`Mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `designation_salary` (
                                      `basic_salary` decimal(15,2) NOT NULL,
                                      `id` int NOT NULL AUTO_INCREMENT,
                                      `is_active` bit(1) DEFAULT NULL,
                                      `designation_name` varchar(100) NOT NULL,
                                      `salary_type` enum('DAILY','MONTHLY','HOURLY','PER_UNIT') NOT NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `UK_qc1xr47685ppe5l4diaxqrdy0` (`designation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee` (
                            `Id` int NOT NULL AUTO_INCREMENT,
                            `Full_Name` varchar(200) NOT NULL,
                            `Designation` varchar(100) DEFAULT NULL,
                            `Address` text,
                            `NIC` varchar(20) DEFAULT NULL,
                            `Mobile_Number` varchar(15) DEFAULT NULL,
                            `Email` varchar(100) DEFAULT NULL,
                            `Date_Joined` date DEFAULT NULL,
                            `Is_active` tinyint(1) DEFAULT '1',
                            PRIMARY KEY (`Id`),
                            UNIQUE KEY `NIC` (`NIC`),
                            UNIQUE KEY `UK_liuj5brwwd2vb7nexm9kg5avl` (`NIC`),
                            KEY `idx_employee_nic` (`NIC`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_salary_rate` (
                                        `Amount` decimal(10,2) NOT NULL,
                                        `Effective_From` date NOT NULL,
                                        `Is_active` bit(1) DEFAULT NULL,
                                        `Rate_id` int NOT NULL AUTO_INCREMENT,
                                        `Rate_Name` varchar(100) NOT NULL,
                                        `Rate_Type` enum('DAILY','MONTHLY','HOURLY','PER_UNIT') DEFAULT NULL,
                                        PRIMARY KEY (`Rate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `expence_type` (
                                `expence_type_id` int NOT NULL AUTO_INCREMENT,
                                `type_name` varchar(100) NOT NULL,
                                `description` varchar(200) NOT NULL,
                                PRIMARY KEY (`expence_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_category` (
                                    `product_cat_id` int NOT NULL AUTO_INCREMENT,
                                    `unit_price` decimal(10,2) DEFAULT NULL,
                                    `material_category` varchar(100) DEFAULT NULL,
                                    `description` text,
                                    `unit_of_measurement` enum('SQUARE_FEET','LENGTH_FEET') DEFAULT NULL,
                                    PRIMARY KEY (`product_cat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `row_material_item` (
                                     `RM_id` int NOT NULL AUTO_INCREMENT,
                                     `RM_name` varchar(200) NOT NULL,
                                     `Price_Per_CFT` decimal(10,2) DEFAULT NULL,
                                     `Description` text,
                                     PRIMARY KEY (`RM_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supplier` (
                            `Sup_id` int NOT NULL AUTO_INCREMENT,
                            `Sup_name` varchar(200) NOT NULL,
                            `Sup_Cat` varchar(100) DEFAULT NULL,
                            `Mobile` varchar(15) NOT NULL,
                            `Address` text,
                            `Email` varchar(100) DEFAULT NULL,
                            `Created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `Is_active` tinyint(1) DEFAULT '1',
                            `nic` varchar(20) DEFAULT NULL,
                            PRIMARY KEY (`Sup_id`),
                            KEY `idx_supplier_mobile` (`Mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `receipt_details_seq` (
                                       `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `paysheet` (
                            `Net_Total` decimal(15,2) DEFAULT NULL,
                            `Paysheet_id` int NOT NULL AUTO_INCREMENT,
                            `Total_Deductions` decimal(15,2) DEFAULT NULL,
                            `Total_Employeees` decimal(38,2) NOT NULL,
                            `Total_Salary` decimal(15,2) DEFAULT NULL,
                            `Paysheet_Number` varchar(50) NOT NULL,
                            `Generated_Date` varchar(255) NOT NULL,
                            `Month` varchar(255) NOT NULL,
                            `Year` varchar(255) NOT NULL,
                            `Status` enum('DRAFT','FINALIZED','APPROVED','PAID') DEFAULT NULL,
                            PRIMARY KEY (`Paysheet_id`),
                            UNIQUE KEY `UK_d93vif91yebr4oak665h5by43` (`Paysheet_Number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `asset_account` (
                                 `Asset_id` int NOT NULL AUTO_INCREMENT,
                                 `Created_by` int DEFAULT NULL,
                                 `Current_Value` decimal(15,2) DEFAULT NULL,
                                 `Depreciation_Rate` decimal(5,2) DEFAULT NULL,
                                 `Purchase_Date` date DEFAULT NULL,
                                 `Purchase_Value` decimal(15,2) NOT NULL,
                                 `Asset_Name` varchar(200) NOT NULL,
                                 `Description` text,
                                 `Asset_Type` enum('Cash','Raw_Material_Stock','Finished_Timber','Machinery','Vehicle','Office_Equipment','Other') NOT NULL,
                                 `Status` enum('Active','Disposed','Under_Repair') DEFAULT NULL,
                                 PRIMARY KEY (`Asset_id`),
                                 KEY `FK1jlicie94bdsac8us7qggicik` (`Created_by`),
                                 CONSTRAINT `FK1jlicie94bdsac8us7qggicik` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `customer_order` (
                                  `Order_Id` bigint NOT NULL AUTO_INCREMENT,
                                  `Order_Number` varchar(20) DEFAULT NULL,
                                  `Customer_Id` int NOT NULL,
                                  `Quotation_Number` varchar(50) DEFAULT NULL,
                                  `Total_Amount` decimal(15,2) DEFAULT '0.00',
                                  `Paid_Amount` decimal(15,2) DEFAULT '0.00',
                                  `Balance_Amount` decimal(15,2) GENERATED ALWAYS AS ((`Total_Amount` - `Paid_Amount`)) STORED,
                                  `Status` varchar(255) DEFAULT NULL,
                                  `Order_Date` date NOT NULL,
                                  `Created_by` int DEFAULT NULL,
                                  PRIMARY KEY (`Order_Id`),
                                  KEY `Customer_Id` (`Customer_Id`),
                                  KEY `Created_by` (`Created_by`),
                                  KEY `idx_order_date` (`Order_Date`),
                                  CONSTRAINT `customer_order_ibfk_1` FOREIGN KEY (`Customer_Id`) REFERENCES `customer` (`Cus_id`),
                                  CONSTRAINT `customer_order_ibfk_2` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`),
                                  CONSTRAINT `FK5f45gvk1g6tqi1oty43q04k6u` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`),
                                  CONSTRAINT `FKg9pduf7ilroiwk2h752kcqxsj` FOREIGN KEY (`Customer_Id`) REFERENCES `customer` (`Cus_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_attendance` (
                                       `Attend_id` int NOT NULL AUTO_INCREMENT,
                                       `Check_In` time(6) DEFAULT NULL,
                                       `Check_Out` time(6) DEFAULT NULL,
                                       `Date` date NOT NULL,
                                       `Employee_id` int NOT NULL,
                                       `Remarks` text,
                                       `Status` varchar(255) DEFAULT NULL,
                                       PRIMARY KEY (`Attend_id`),
                                       UNIQUE KEY `UKtlfunxnwmk0lios9yogoo13px` (`Employee_id`,`Date`),
                                       CONSTRAINT `FKg1m6wtbire79ie87ba66ato6q` FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_loan` (
                                 `Balance` decimal(15,2) DEFAULT NULL,
                                 `Created_by` int DEFAULT NULL,
                                 `Employee_id` int NOT NULL,
                                 `Issued_Date` date NOT NULL,
                                 `Loan_Amount` decimal(15,2) NOT NULL,
                                 `Loan_ID` int NOT NULL AUTO_INCREMENT,
                                 `Total_Deducted` decimal(15,2) DEFAULT NULL,
                                 `Reason` varchar(255) DEFAULT NULL,
                                 `Remarks` text,
                                 `Status` enum('ACTIVE','PARTIALLY_PAID','COMPLETED','SETTLED','CANCELLED') DEFAULT NULL,
                                 PRIMARY KEY (`Loan_ID`),
                                 KEY `FKl5xhejapxohm303ixmm8wywx5` (`Created_by`),
                                 KEY `FKcmwbx6b8bh9pah25s9m39ohsh` (`Employee_id`),
                                 CONSTRAINT `FKcmwbx6b8bh9pah25s9m39ohsh` FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`Id`),
                                 CONSTRAINT `FKl5xhejapxohm303ixmm8wywx5` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_paysheet` (
                                     `Base_Salary` decimal(15,2) DEFAULT NULL,
                                     `Employee_id` int DEFAULT NULL,
                                     `Loan_Deduction` decimal(15,2) DEFAULT NULL,
                                     `Month` int DEFAULT NULL,
                                     `Net_Salary` decimal(15,2) DEFAULT NULL,
                                     `Other_Deduction` decimal(15,2) DEFAULT NULL,
                                     `Overtime_Amount` decimal(15,2) DEFAULT NULL,
                                     `Paysheet_id` int NOT NULL AUTO_INCREMENT,
                                     `Present_Days` int DEFAULT NULL,
                                     `Total_Earnings` decimal(15,2) DEFAULT NULL,
                                     `Year` int DEFAULT NULL,
                                     `Generated_Date` datetime(6) DEFAULT NULL,
                                     `File_Path` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`Paysheet_id`),
                                     KEY `FK2frxa8k40cs6ebfh31s85t39s` (`Employee_id`),
                                     CONSTRAINT `FK2frxa8k40cs6ebfh31s85t39s` FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_salary_details` (
                                           `Balance_Amount` decimal(15,2) DEFAULT NULL,
                                           `Employee_id` int NOT NULL,
                                           `Is_Active` bit(1) DEFAULT NULL,
                                           `Month` int NOT NULL,
                                           `Paid_Amount` decimal(15,2) DEFAULT NULL,
                                           `Salary_details_id` int NOT NULL AUTO_INCREMENT,
                                           `Total_Amount` decimal(15,2) DEFAULT NULL,
                                           `Year` int NOT NULL,
                                           `Status` enum('PENDING','PARTIALLY_PAID','PAID') DEFAULT NULL,
                                           PRIMARY KEY (`Salary_details_id`),
                                           UNIQUE KEY `UKe62ea7g10kj6rdb0aw7alc8yx` (`Employee_id`,`Month`,`Year`),
                                           CONSTRAINT `FK7lyk07lsf36rfrogaf824qsc5` FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `equity_account` (
                                  `Amount` decimal(38,2) NOT NULL,
                                  `Created_by` int DEFAULT NULL,
                                  `Date` date NOT NULL,
                                  `Equity_id` int NOT NULL AUTO_INCREMENT,
                                  `Description` text,
                                  `Type` enum('Capital','Drawing','Retained_Earnings','Other') NOT NULL,
                                  PRIMARY KEY (`Equity_id`),
                                  KEY `FK30cjogtfa1hh7h43yt9r8jyjs` (`Created_by`),
                                  CONSTRAINT `FK30cjogtfa1hh7h43yt9r8jyjs` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_permission` (
                                   `can_access` bit(1) NOT NULL,
                                   `permission_id` int NOT NULL AUTO_INCREMENT,
                                   `user_id` int NOT NULL,
                                   `function_name` varchar(100) NOT NULL,
                                   PRIMARY KEY (`permission_id`),
                                   KEY `FKkexiot5vlac8hlpfpilxyat11` (`user_id`),
                                   CONSTRAINT `FKkexiot5vlac8hlpfpilxyat11` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supply_raw_material` (
                                       `Created_by` int DEFAULT NULL,
                                       `Cutting_Fee` decimal(10,2) DEFAULT NULL,
                                       `Cutting_Fee_id` int DEFAULT NULL,
                                       `RM_id` int NOT NULL,
                                       `Supplier_id` int NOT NULL,
                                       `Supply_Date` date NOT NULL,
                                       `Supply_id` int NOT NULL AUTO_INCREMENT,
                                       `Total_Amount` decimal(15,2) DEFAULT NULL,
                                       `Transport` decimal(10,2) DEFAULT NULL,
                                       `Invoice_Number` varchar(50) DEFAULT NULL,
                                       PRIMARY KEY (`Supply_id`),
                                       KEY `FK152vfplr8pei5jl2mv19vihht` (`Created_by`),
                                       KEY `FKnbc25k6mxia7kpadnyt3slf1e` (`RM_id`),
                                       KEY `FKfmd2dahejfm9rlmoxqunflqu7` (`Supplier_id`),
                                       CONSTRAINT `FK152vfplr8pei5jl2mv19vihht` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`),
                                       CONSTRAINT `FKfmd2dahejfm9rlmoxqunflqu7` FOREIGN KEY (`Supplier_id`) REFERENCES `supplier` (`Sup_id`),
                                       CONSTRAINT `FKnbc25k6mxia7kpadnyt3slf1e` FOREIGN KEY (`RM_id`) REFERENCES `row_material_item` (`RM_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supply_raw_material_request` (
                                               `Created_By` int DEFAULT NULL,
                                               `Request_id` int NOT NULL AUTO_INCREMENT,
                                               `Supplier_id` int NOT NULL,
                                               `Transport_By_Supplier` bit(1) DEFAULT NULL,
                                               `Approved_Date` datetime(6) DEFAULT NULL,
                                               `Request_Date` datetime(6) NOT NULL,
                                               `Remarks` text,
                                               `Transport_Notes` text,
                                               `Status` enum('Pending','Approved','Partially_Approved','Rejected','Converted') DEFAULT NULL,
                                               PRIMARY KEY (`Request_id`),
                                               KEY `FKeuih37mpxdsipe86req33vyta` (`Created_By`),
                                               KEY `FKke6dlnqrpl4nibp7smcpqof5` (`Supplier_id`),
                                               CONSTRAINT `FKeuih37mpxdsipe86req33vyta` FOREIGN KEY (`Created_By`) REFERENCES `user` (`user_id`),
                                               CONSTRAINT `FKke6dlnqrpl4nibp7smcpqof5` FOREIGN KEY (`Supplier_id`) REFERENCES `supplier` (`Sup_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `quotation` (
                             `Created_by` int DEFAULT NULL,
                             `Customer_Id` int NOT NULL,
                             `Quotation_Date` date NOT NULL,
                             `Quotation_Id` int NOT NULL AUTO_INCREMENT,
                             `Total_Amount` decimal(38,2) DEFAULT NULL,
                             `Valid_Until` date DEFAULT NULL,
                             `Quotation_Number` varchar(20) DEFAULT NULL,
                             `Remarks` varchar(255) DEFAULT NULL,
                             `Status` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`Quotation_Id`),
                             KEY `FK1xgvp5ijkk79add2o0c03954s` (`Created_by`),
                             KEY `FK37w5eff22jgfhrt46q5vnhuuj` (`Customer_Id`),
                             CONSTRAINT `FK1xgvp5ijkk79add2o0c03954s` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`),
                             CONSTRAINT `FK37w5eff22jgfhrt46q5vnhuuj` FOREIGN KEY (`Customer_Id`) REFERENCES `customer` (`Cus_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `receipt` (
                           `Card_Last_Digits` varchar(4) DEFAULT NULL,
                           `Created_by` int DEFAULT NULL,
                           `Customer_Id` int NOT NULL,
                           `Date` date NOT NULL,
                           `Total_Amount` decimal(15,2) NOT NULL,
                           `Receipt_Id` bigint NOT NULL AUTO_INCREMENT,
                           `Cheque_Number` varchar(50) DEFAULT NULL,
                           `Receipt_Number` varchar(50) NOT NULL,
                           `Bank_Name` varchar(100) DEFAULT NULL,
                           `Card_Type` varchar(255) DEFAULT NULL,
                           `Payment_Method` varchar(255) DEFAULT NULL,
                           `Remarks` text,
                           PRIMARY KEY (`Receipt_Id`),
                           UNIQUE KEY `UK_ex0usx7o6mwrgy8hsneyuktxs` (`Receipt_Number`),
                           KEY `FKcicrlb98gu48uh877r7db3o5v` (`Created_by`),
                           KEY `FKrm8u4xqd2nct3fl6vg8k3swi4` (`Customer_Id`),
                           CONSTRAINT `FKcicrlb98gu48uh877r7db3o5v` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`),
                           CONSTRAINT `FKrm8u4xqd2nct3fl6vg8k3swi4` FOREIGN KEY (`Customer_Id`) REFERENCES `customer` (`Cus_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_stock` (
                                 `product_category_id` int NOT NULL,
                                 `quantity` decimal(10,2) NOT NULL,
                                 `stock_id` int NOT NULL AUTO_INCREMENT,
                                 PRIMARY KEY (`stock_id`),
                                 UNIQUE KEY `UK_1x786xa0cyo9erryrbcc8qs43` (`product_category_id`),
                                 CONSTRAINT `FKn7rjmh3rclo18aa88p1bwjrab` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`product_cat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `customer_order_details` (
                                          `Line_Total` decimal(15,2) GENERATED ALWAYS AS ((`Quantity` * `Price`)) STORED,
                                          `Paid_Amount` decimal(15,2) NOT NULL,
                                          `Price` decimal(10,2) NOT NULL,
                                          `Product_Cat_id` int NOT NULL,
                                          `Quantity` decimal(10,2) NOT NULL,
                                          `Details_id` bigint NOT NULL AUTO_INCREMENT,
                                          `Order_Id` bigint NOT NULL,
                                          `Name` varchar(200) DEFAULT NULL,
                                          PRIMARY KEY (`Details_id`),
                                          KEY `FKobais88jylx31xk0km46w8qrv` (`Product_Cat_id`),
                                          KEY `customer_order_details_ibfk_1` (`Order_Id`),
                                          CONSTRAINT `customer_order_details_ibfk_1` FOREIGN KEY (`Order_Id`) REFERENCES `customer_order` (`Order_Id`),
                                          CONSTRAINT `FKobais88jylx31xk0km46w8qrv` FOREIGN KEY (`Product_Cat_id`) REFERENCES `product_category` (`product_cat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_loan_details` (
                                         `Amount` decimal(15,2) NOT NULL,
                                         `Date` date NOT NULL,
                                         `Details_id` int NOT NULL AUTO_INCREMENT,
                                         `Loan_id` int NOT NULL,
                                         `Salary_details_id` int DEFAULT NULL,
                                         `Remarks` text,
                                         PRIMARY KEY (`Details_id`),
                                         KEY `FKfabmklaxelomob5np2bucbqvm` (`Loan_id`),
                                         CONSTRAINT `FKfabmklaxelomob5np2bucbqvm` FOREIGN KEY (`Loan_id`) REFERENCES `employee_loan` (`Loan_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_salary_payment` (
                                           `Amount` decimal(15,2) NOT NULL,
                                           `Date` date NOT NULL,
                                           `Emp_Salary_Payment_Id` int NOT NULL AUTO_INCREMENT,
                                           `Paid_by` int DEFAULT NULL,
                                           `Salary_details_id` int NOT NULL,
                                           `Created_date` datetime(6) DEFAULT NULL,
                                           `Remarks` text,
                                           `Payment_Method` enum('CASH','CARD','BANK_TRANSFER','CHEQUE') DEFAULT NULL,
                                           PRIMARY KEY (`Emp_Salary_Payment_Id`),
                                           KEY `FKgrpnav1kv5ood6603b0sioawg` (`Paid_by`),
                                           KEY `FKna4r1ri6345uuji3i717097c5` (`Salary_details_id`),
                                           CONSTRAINT `FKgrpnav1kv5ood6603b0sioawg` FOREIGN KEY (`Paid_by`) REFERENCES `user` (`user_id`),
                                           CONSTRAINT `FKna4r1ri6345uuji3i717097c5` FOREIGN KEY (`Salary_details_id`) REFERENCES `employee_salary_details` (`Salary_details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `loan_deduction_rule` (
                                       `Created_by` int DEFAULT NULL,
                                       `Deduction_Amount` decimal(15,2) NOT NULL,
                                       `End_Month` int DEFAULT NULL,
                                       `End_Year` int DEFAULT NULL,
                                       `Is_active` bit(1) DEFAULT NULL,
                                       `Loan_id` int NOT NULL,
                                       `Rule_id` int NOT NULL AUTO_INCREMENT,
                                       `Start_Month` int NOT NULL,
                                       `Start_Year` int NOT NULL,
                                       `Remarks` text,
                                       PRIMARY KEY (`Rule_id`),
                                       KEY `FK6x6yrb9fdfm1buvup63g8p6mc` (`Created_by`),
                                       KEY `FK40cyslg469j55bg6aerqpj8x7` (`Loan_id`),
                                       CONSTRAINT `FK40cyslg469j55bg6aerqpj8x7` FOREIGN KEY (`Loan_id`) REFERENCES `employee_loan` (`Loan_ID`),
                                       CONSTRAINT `FK6x6yrb9fdfm1buvup63g8p6mc` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supply_raw_material_details` (
                                               `Girth_ft` decimal(10,2) NOT NULL,
                                               `Length_ft` decimal(10,2) NOT NULL,
                                               `Line_Total` decimal(15,2) GENERATED ALWAYS AS (((((`Length_ft` * `Girth_ft`) * `Girth_ft`) / 2304) * `Price`)) STORED,
                                               `Log_Number` int DEFAULT NULL,
                                               `Price` decimal(10,2) NOT NULL,
                                               `RM_id` int NOT NULL,
                                               `Supply_RAW_Material_Details_ID` int NOT NULL AUTO_INCREMENT,
                                               `Supply_id` int NOT NULL,
                                               `Total_Quantity_CFT` decimal(10,3) GENERATED ALWAYS AS ((((`Length_ft` * `Girth_ft`) * `Girth_ft`) / 2304)) STORED,
                                               `cut_day` date DEFAULT NULL,
                                               `status` enum('PENDING','CUT') NOT NULL,
                                               PRIMARY KEY (`Supply_RAW_Material_Details_ID`),
                                               KEY `FKi0o7bbsng0o5p6kbu6ahyukrc` (`RM_id`),
                                               KEY `FKk6e45wa32xx3rg9b1vqhm8l1n` (`Supply_id`),
                                               CONSTRAINT `FKi0o7bbsng0o5p6kbu6ahyukrc` FOREIGN KEY (`RM_id`) REFERENCES `row_material_item` (`RM_id`),
                                               CONSTRAINT `FKk6e45wa32xx3rg9b1vqhm8l1n` FOREIGN KEY (`Supply_id`) REFERENCES `supply_raw_material` (`Supply_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `supply_raw_material_request_details` (
                                                       `Admin_Requested_CFT` decimal(10,3) NOT NULL,
                                                       `RM_id` int NOT NULL,
                                                       `Request_Detail_id` int NOT NULL AUTO_INCREMENT,
                                                       `Request_id` int NOT NULL,
                                                       `Supplier_Approved_CFT` decimal(10,3) DEFAULT NULL,
                                                       `Unit_Price` decimal(10,2) DEFAULT NULL,
                                                       `Remarks` text,
                                                       PRIMARY KEY (`Request_Detail_id`),
                                                       KEY `FKtngljq6ggkvv6o7ei11t4bn6n` (`RM_id`),
                                                       KEY `FKqn4iru3kxk1gkd2ls3agn93km` (`Request_id`),
                                                       CONSTRAINT `FKqn4iru3kxk1gkd2ls3agn93km` FOREIGN KEY (`Request_id`) REFERENCES `supply_raw_material_request` (`Request_id`),
                                                       CONSTRAINT `FKtngljq6ggkvv6o7ei11t4bn6n` FOREIGN KEY (`RM_id`) REFERENCES `row_material_item` (`RM_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `quotation_details` (
                                     `Details_id` int NOT NULL AUTO_INCREMENT,
                                     `Line_Total` decimal(38,2) DEFAULT NULL,
                                     `Price` decimal(38,2) NOT NULL,
                                     `Product_Cat_id` int NOT NULL,
                                     `Quantity` decimal(38,2) NOT NULL,
                                     `Quotation_Id` int NOT NULL,
                                     `Name` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`Details_id`),
                                     KEY `FKm4jllx2o6l3euhu1qdrc9sbh4` (`Product_Cat_id`),
                                     KEY `FKhrhrkycss15s4vi5e1oau2qpd` (`Quotation_Id`),
                                     CONSTRAINT `FKhrhrkycss15s4vi5e1oau2qpd` FOREIGN KEY (`Quotation_Id`) REFERENCES `quotation` (`Quotation_Id`),
                                     CONSTRAINT `FKm4jllx2o6l3euhu1qdrc9sbh4` FOREIGN KEY (`Product_Cat_id`) REFERENCES `product_category` (`product_cat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `raw_material_cutting_fee` (
                                            `Date` date NOT NULL,
                                            `Employee_id` int NOT NULL,
                                            `Fee` decimal(10,2) NOT NULL,
                                            `Id` int NOT NULL AUTO_INCREMENT,
                                            `Supply_id` int NOT NULL,
                                            `Remarks` text,
                                            PRIMARY KEY (`Id`),
                                            KEY `FKbylog4niien1ahejfhatbhljj` (`Employee_id`),
                                            KEY `FK8l37ce834o5wj5wj8du7too1e` (`Supply_id`),
                                            CONSTRAINT `FK8l37ce834o5wj5wj8du7too1e` FOREIGN KEY (`Supply_id`) REFERENCES `supply_raw_material` (`Supply_id`),
                                            CONSTRAINT `FKbylog4niien1ahejfhatbhljj` FOREIGN KEY (`Employee_id`) REFERENCES `employee` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `income_account` (
                                  `Amount` decimal(15,2) NOT NULL,
                                  `Created_by` int DEFAULT NULL,
                                  `Date` date NOT NULL,
                                  `Income_Id` int NOT NULL AUTO_INCREMENT,
                                  `Receipt_Id` bigint DEFAULT NULL,
                                  `Description` text,
                                  PRIMARY KEY (`Income_Id`),
                                  KEY `FK6vxf032nrgo7jf2efbhowi0gn` (`Created_by`),
                                  KEY `income_account_ibfk_1` (`Receipt_Id`),
                                  CONSTRAINT `FK6byhe6p24c0911qx8s2pvn0dm` FOREIGN KEY (`Receipt_Id`) REFERENCES `receipt` (`Receipt_Id`),
                                  CONSTRAINT `FK6vxf032nrgo7jf2efbhowi0gn` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`),
                                  CONSTRAINT `income_account_ibfk_1` FOREIGN KEY (`Receipt_Id`) REFERENCES `receipt` (`Receipt_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `grn` (
                       `Amount` decimal(15,2) NOT NULL,
                       `Created_by` int DEFAULT NULL,
                       `Date` date NOT NULL,
                       `GRN_id` int NOT NULL AUTO_INCREMENT,
                       `expense_id` int DEFAULT NULL,
                       `supplier_id` int DEFAULT NULL,
                       `supply_order_id` int DEFAULT NULL,
                       `total_amount` decimal(15,2) DEFAULT NULL,
                       `created_at` datetime(6) DEFAULT NULL,
                       `updated_at` datetime(6) DEFAULT NULL,
                       `GRN_Number` varchar(50) NOT NULL,
                       `invoice_number` varchar(50) DEFAULT NULL,
                       `Remarks` text,
                       PRIMARY KEY (`GRN_id`),
                       UNIQUE KEY `UK_cr2ew1yq1ibxl9854b26pstci` (`GRN_Number`),
                       KEY `FKsj6njl4ux8lw02rev0bhs9og8` (`Created_by`),
                       KEY `FK7eh0pnbm8caac3dymru8desry` (`expense_id`),
                       KEY `FK830p4ne18edwq54cigei16ubx` (`supplier_id`),
                       KEY `FKsfvupk0ggpil2hw4ltc1dy3k1` (`supply_order_id`),
                       CONSTRAINT `FK830p4ne18edwq54cigei16ubx` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`Sup_id`),
                       CONSTRAINT `FKsfvupk0ggpil2hw4ltc1dy3k1` FOREIGN KEY (`supply_order_id`) REFERENCES `supply_raw_material` (`Supply_id`),
                       CONSTRAINT `FKsj6njl4ux8lw02rev0bhs9og8` FOREIGN KEY (`Created_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `receipt_details` (
                                   `Amount` decimal(15,2) NOT NULL,
                                   `Receipt_Details_Id` int NOT NULL,
                                   `Customer_Order_Details_Id` bigint NOT NULL,
                                   `Receipt_Id` bigint NOT NULL,
                                   PRIMARY KEY (`Receipt_Details_Id`),
                                   KEY `receipt_details_ibfk_2` (`Customer_Order_Details_Id`),
                                   KEY `receipt_details_ibfk_1` (`Receipt_Id`),
                                   CONSTRAINT `FK5g5hdhf1dqj4h33m9pb0aqayh` FOREIGN KEY (`Receipt_Id`) REFERENCES `receipt` (`Receipt_Id`),
                                   CONSTRAINT `FKt12l25bhqw0925g5fmkfou90a` FOREIGN KEY (`Customer_Order_Details_Id`) REFERENCES `customer_order_details` (`Details_id`),
                                   CONSTRAINT `receipt_details_ibfk_1` FOREIGN KEY (`Receipt_Id`) REFERENCES `receipt` (`Receipt_Id`),
                                   CONSTRAINT `receipt_details_ibfk_2` FOREIGN KEY (`Customer_Order_Details_Id`) REFERENCES `customer_order_details` (`Details_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `expence_account` (
                                   `amount` decimal(15,2) NOT NULL,
                                   `date` date NOT NULL,
                                   `expense_id` int NOT NULL AUTO_INCREMENT,
                                   `expense_type_id` int NOT NULL,
                                   `grn_id` int DEFAULT NULL,
                                   `user_id` int NOT NULL,
                                   `description` text,
                                   `paid_to` varchar(255) DEFAULT NULL,
                                   `remarks` varchar(255) DEFAULT NULL,
                                   PRIMARY KEY (`expense_id`),
                                   KEY `FKkbh6gdbvfl7sgyqeu8j4j345m` (`expense_type_id`),
                                   KEY `FKfosxlah78kxg16fft2vq97k5w` (`grn_id`),
                                   KEY `FKtcjkohlvd3sxxdonu1d65edoh` (`user_id`),
                                   CONSTRAINT `FKfosxlah78kxg16fft2vq97k5w` FOREIGN KEY (`grn_id`) REFERENCES `grn` (`GRN_id`),
                                   CONSTRAINT `FKkbh6gdbvfl7sgyqeu8j4j345m` FOREIGN KEY (`expense_type_id`) REFERENCES `expence_type` (`expence_type_id`),
                                   CONSTRAINT `FKtcjkohlvd3sxxdonu1d65edoh` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `grn_details` (
                               `Amount` decimal(15,2) NOT NULL,
                               `Date` date NOT NULL,
                               `GRN_ID` int NOT NULL,
                               `GRN_details_ID` int NOT NULL AUTO_INCREMENT,
                               `Supply_RAW_Material_Details_ID` int NOT NULL,
                               `GRN_Number` varchar(50) DEFAULT NULL,
                               PRIMARY KEY (`GRN_details_ID`),
                               KEY `FKial35patbsyqwh3yq1kw0ohch` (`GRN_ID`),
                               KEY `FKm7bmlle3b2kfrbhok30mdb0gx` (`Supply_RAW_Material_Details_ID`),
                               CONSTRAINT `FKial35patbsyqwh3yq1kw0ohch` FOREIGN KEY (`GRN_ID`) REFERENCES `grn` (`GRN_id`),
                               CONSTRAINT `FKm7bmlle3b2kfrbhok30mdb0gx` FOREIGN KEY (`Supply_RAW_Material_Details_ID`) REFERENCES `supply_raw_material_details` (`Supply_RAW_Material_Details_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- ORIGINAL DEFERRED CONSTRAINT (already present in your dump)
-- =====================================================================
ALTER TABLE `grn` ADD CONSTRAINT `FK7eh0pnbm8caac3dymru8desry` FOREIGN KEY (`expense_id`) REFERENCES `expence_account` (`expense_id`);

-- =====================================================================
-- NEW FOREIGN KEY CONSTRAINTS (added)
-- Run the orphan-check SELECTs first if this schema already has data,
-- since ALTER TABLE will fail on rows that violate the constraint.
-- =====================================================================

-- 1. Link employee's designation to the defined designation/salary structure
ALTER TABLE `employee`
    ADD CONSTRAINT `fk_employee_designation`
        FOREIGN KEY (`Designation`)
            REFERENCES `designation_salary` (`designation_name`);

-- 2. Link a raw material supply to its cutting-fee record
ALTER TABLE `supply_raw_material`
    ADD CONSTRAINT `fk_supplyraw_cuttingfee`
        FOREIGN KEY (`Cutting_Fee_id`)
            REFERENCES `raw_material_cutting_fee` (`Id`);

-- 3. Link a loan repayment record to the salary period it was deducted from
ALTER TABLE `employee_loan_details`
    ADD CONSTRAINT `fk_loandetails_salarydetails`
        FOREIGN KEY (`Salary_details_id`)
            REFERENCES `employee_salary_details` (`Salary_details_id`);


INSERT INTO `expence_type` (`type_name`, `description`) VALUES
                                                            ('Raw Material Purchase', 'Cost of purchasing raw wood/timber materials from suppliers'),
                                                            ('Employee Salary', 'Monthly or periodic salary payments to employees'),
                                                            ('Cutting Fee', 'Fees paid for cutting raw materials into usable pieces'),
                                                            ('Utilities', 'Electricity, water, and other utility bills'),
                                                            ('Maintenance', 'Equipment and facility maintenance costs'),
                                                            ('Office Expenses', 'General office supplies and administrative costs'),
                                                            ('Other', 'Miscellaneous expenses not covered by other categories');

INSERT INTO `user` (
    `user_name`, `email`, `password`, `role`, `phone_number`
    `is_active`, `account_locked`, `failed_login_attempts`
) VALUES (
             'admin',
             'admin@woodfurniture.com',
             '$2a$10$REPLACE_WITH_REAL_BCRYPT_HASH',
             'ADMIN',
             '0779134741',
             1,
             b'0',
             0
         );