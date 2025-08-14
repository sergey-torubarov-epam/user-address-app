-- Migration script to add Country field to Address table
-- Implements requirement from EPMCDMETST-14145 and EPMCDMETST-14149

-- Add country column to addresses table
ALTER TABLE addresses 
ADD COLUMN country VARCHAR(100) NULL DEFAULT NULL;

-- Add comment for documentation
COMMENT ON COLUMN addresses.country IS 'Country field for address - CR-001 implementation';

-- Optional: Create index for better query performance
CREATE INDEX idx_addresses_country ON addresses(country);