-- Migration script to add country field to addresses table
-- CR1: Add Country Field Implementation
-- Date: 2025-01-27

-- Add country column to addresses table
ALTER TABLE addresses 
ADD COLUMN country VARCHAR(100) NULL 
AFTER state;

-- Add comment to document the change
ALTER TABLE addresses 
MODIFY COLUMN country VARCHAR(100) NULL 
COMMENT 'Country field - allows letters, spaces, and hyphens only. Maximum 100 characters. Optional field.';

-- Create index on country field for better query performance (optional)
CREATE INDEX idx_addresses_country ON addresses(country);

-- Update any existing test data if needed (optional)
-- UPDATE addresses SET country = 'United States' WHERE state IN ('CA', 'NY', 'TX', 'FL');
-- UPDATE addresses SET country = 'Canada' WHERE state IN ('ON', 'BC', 'AB', 'QC');

-- Verify the change
-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'addresses' AND COLUMN_NAME = 'country';