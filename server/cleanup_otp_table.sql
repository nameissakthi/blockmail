-- Cleanup script: Drop the email_otps table (no longer needed - using in-memory storage)

-- Drop the table if it exists
DROP TABLE IF EXISTS email_otps CASCADE;

-- Verify table is dropped
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name = 'email_otps';
