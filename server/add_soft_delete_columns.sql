-- Migration script to add soft delete columns to encrypted_emails table
-- Run this if you want to preserve existing data when adding the new columns

-- Add deleted_by_sender column (default false for existing emails)
ALTER TABLE encrypted_emails 
ADD COLUMN IF NOT EXISTS deleted_by_sender BOOLEAN NOT NULL DEFAULT false;

-- Add deleted_by_recipient column (default false for existing emails)
ALTER TABLE encrypted_emails 
ADD COLUMN IF NOT EXISTS deleted_by_recipient BOOLEAN NOT NULL DEFAULT false;

-- Add comments for documentation
COMMENT ON COLUMN encrypted_emails.deleted_by_sender IS 'Indicates if sender deleted this email from their sent folder';
COMMENT ON COLUMN encrypted_emails.deleted_by_recipient IS 'Indicates if recipient deleted this email from their inbox';

-- Verify the columns were added
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'encrypted_emails' 
AND column_name IN ('deleted_by_sender', 'deleted_by_recipient');
