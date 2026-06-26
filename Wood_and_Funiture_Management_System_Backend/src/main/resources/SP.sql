CREATE DEFINER=`root`@`localhost` PROCEDURE `Generate_Invoice_Number`()
BEGIN
    DECLARE last_seq INT DEFAULT 0;
    DECLARE current_year CHAR(2);
    DECLARE invoice_number VARCHAR(20);

    SET current_year = RIGHT(YEAR(CURDATE()), 2);

    -- Get MAX Supply_id for current year (always unique, always increments)
SELECT COALESCE(MAX(Supply_id), 0)
INTO last_seq
FROM timber_business.supply_raw_material
WHERE YEAR(Supply_Date) = YEAR(CURDATE());

SET last_seq = last_seq + 1;

    -- INV + 2-digit year + 6-digit sequence = INV26000001
    SET invoice_number = CONCAT('INV', current_year, LPAD(last_seq, 6, '0'));

SELECT invoice_number AS Invoice_Number;

END




===============================================================================





CREATE DEFINER=`root`@`localhost` PROCEDURE `Generate_Order_Number`()
BEGIN
    DECLARE last_seq INT DEFAULT 0;
    DECLARE current_year CHAR(2);
    DECLARE order_number VARCHAR(20);

    SET current_year = RIGHT(YEAR(CURDATE()), 2);

    -- Get MAX Order_Id for current year (always unique, always increments)
SELECT COALESCE(MAX(Order_Id), 0)
INTO last_seq
FROM timber_business.customer_order
WHERE YEAR(Order_Date) = YEAR(CURDATE());

SET last_seq = last_seq + 1;

    SET order_number = CONCAT('ORD', current_year, LPAD(last_seq, 6, '0'));

SELECT order_number AS Order_Number;

END







=====================================================================================





CREATE DEFINER=`root`@`localhost` PROCEDURE `Generate_Quotation_Number`()
BEGIN
    DECLARE last_seq INT DEFAULT 0;
    DECLARE current_year CHAR(2);
    DECLARE quotation_number VARCHAR(20);

    SET current_year = RIGHT(YEAR(CURDATE()), 2);

SELECT COALESCE(MAX(Quotation_Id), 0)
INTO last_seq
FROM timber_business.quotation
WHERE YEAR(Quotation_Date) = YEAR(CURDATE());

SET last_seq = last_seq + 1;

    SET quotation_number = CONCAT('QUO', current_year, LPAD(last_seq, 5, '0'));

SELECT quotation_number AS Quotation_Number;

END


