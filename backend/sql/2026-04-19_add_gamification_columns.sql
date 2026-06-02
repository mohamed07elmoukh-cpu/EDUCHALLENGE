BEGIN;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS streak_count INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_activity_date DATE;

ALTER TABLE users
    ALTER COLUMN streak_count SET DEFAULT 0;

UPDATE users
SET streak_count = 0
WHERE streak_count IS NULL;

ALTER TABLE badges
    ADD COLUMN IF NOT EXISTS slug VARCHAR(80),
    ADD COLUMN IF NOT EXISTS condition_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS condition_value INTEGER,
    ADD COLUMN IF NOT EXISTS tone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS icon_name VARCHAR(60);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'badges'::regclass
          AND conname = 'uq_badges_slug'
    ) THEN
        ALTER TABLE badges
            ADD CONSTRAINT uq_badges_slug UNIQUE (slug);
    END IF;
END $$;

ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS notification_type VARCHAR(50);

COMMENT ON COLUMN users.streak_count IS 'Current consecutive daily activity streak.';
COMMENT ON COLUMN users.last_activity_date IS 'Last day where the user completed a challenge attempt.';
COMMENT ON COLUMN badges.condition_type IS 'Badge unlocking strategy such as POINTS_REACHED or CHALLENGES_COMPLETED.';
COMMENT ON COLUMN badges.condition_value IS 'Numeric threshold used to evaluate badge unlocking.';
COMMENT ON COLUMN notifications.notification_type IS 'Gamified event type such as CHALLENGE_COMPLETED or BADGE_UNLOCKED.';

COMMIT;
