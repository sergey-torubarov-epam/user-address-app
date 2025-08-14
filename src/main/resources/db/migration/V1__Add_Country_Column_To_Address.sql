-- Migration script to add country column to addresses table
-- This script adds the country field as specified in CR1 requirements

-- Add country column to addresses table
ALTER TABLE addresses 
ADD COLUMN country VARCHAR(100) NULL DEFAULT NULL;

-- Add comment to document the change
COMMENT ON COLUMN addresses.country IS 'Country field added for CR1 - stores ISO 3166-1 country names, nullable for backward compatibility';

-- Create index for better query performance (optional)
CREATE INDEX idx_addresses_country ON addresses(country);

-- Update existing records to have NULL country (already default, but explicit for clarity)
-- No update needed as DEFAULT NULL handles this automatically