-- Migration to fix Supply_RAW_Material foreign key
-- It currently points to Customer table, but it should point to Supplier table
-- This is necessary to fix the 500 Internal Server Error when saving logs from a Supplier.

USE timber_business;

-- 1. Drop existing foreign key
-- We need to find the name of the foreign key constraint. 
-- In the SQL script it was unnamed, so MySQL generated one.
-- Common generated name is Supply_RAW_Material_ibfk_1

-- Try to drop it (we might need to check the actual name if this fails)
ALTER TABLE Supply_RAW_Material DROP FOREIGN KEY Supply_RAW_Material_ibfk_1;

-- 2. Add new foreign key pointing to Supplier table
ALTER TABLE Supply_RAW_Material 
ADD CONSTRAINT fk_supply_raw_material_supplier
FOREIGN KEY (Supplier_id) REFERENCES Supplier(Sup_id);

-- 3. Also fix Cutting Fee foreign key just in case
ALTER TABLE RAW_Material_Cutting_Fee DROP FOREIGN KEY RAW_Material_Cutting_Fee_ibfk_1;
ALTER TABLE RAW_Material_Cutting_Fee 
ADD CONSTRAINT fk_cutting_fee_supply
FOREIGN KEY (Supply_id) REFERENCES Supply_RAW_Material(Supply_id);
