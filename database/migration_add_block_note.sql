-- Migration script to add block_note column to users table
-- This adds support for administrator notes when blocking users

-- Add block_note column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS block_note VARCHAR(500);

-- Optional: Add comment to document the column
COMMENT ON COLUMN users.block_note IS 'Administrator note explaining why the user was blocked';

-- Note: The is_blocked column should already exist in the users table
-- If it doesn't exist, uncomment the following line:
-- ALTER TABLE users ADD COLUMN IF NOT EXISTS is_blocked BOOLEAN DEFAULT FALSE;
