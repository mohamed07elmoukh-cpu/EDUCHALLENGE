BEGIN;

-- EduChallenge - QCM schema extension
-- Adds missing tables for multiple-choice challenges and upgrades challenge_steps.

-- ---------------------------------------------------------------------------
-- challenge_steps upgrade
-- Keeps existing structure compatible with the current backend while adding
-- a technical id and QCM-oriented columns.
-- ---------------------------------------------------------------------------

CREATE SEQUENCE IF NOT EXISTS challenge_steps_id_seq;

ALTER TABLE challenge_steps
    ADD COLUMN IF NOT EXISTS id BIGINT,
    ADD COLUMN IF NOT EXISTS question_text TEXT,
    ADD COLUMN IF NOT EXISTS points INTEGER DEFAULT 1;

ALTER TABLE challenge_steps
    ALTER COLUMN id SET DEFAULT nextval('challenge_steps_id_seq'),
    ALTER COLUMN points SET DEFAULT 1;

ALTER SEQUENCE challenge_steps_id_seq OWNED BY challenge_steps.id;

UPDATE challenge_steps
SET id = nextval('challenge_steps_id_seq')
WHERE id IS NULL;

UPDATE challenge_steps
SET question_text = step_text
WHERE question_text IS NULL;

UPDATE challenge_steps
SET points = 1
WHERE points IS NULL;

ALTER TABLE challenge_steps
    ALTER COLUMN id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'challenge_steps'::regclass
          AND conname = 'uq_challenge_steps_id'
    ) THEN
        ALTER TABLE challenge_steps
            ADD CONSTRAINT uq_challenge_steps_id UNIQUE (id);
    END IF;
END $$;

DO $$
DECLARE
    existing_fk_name TEXT;
BEGIN
    SELECT c.conname
    INTO existing_fk_name
    FROM pg_constraint c
    JOIN pg_attribute a
      ON a.attrelid = c.conrelid
     AND a.attnum = ANY (c.conkey)
    WHERE c.conrelid = 'challenge_steps'::regclass
      AND c.contype = 'f'
      AND a.attname = 'challenge_id'
    LIMIT 1;

    IF existing_fk_name IS NOT NULL THEN
        EXECUTE format(
            'ALTER TABLE challenge_steps DROP CONSTRAINT %I',
            existing_fk_name
        );
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'challenge_steps'::regclass
          AND conname = 'fk_challenge_steps_challenge'
    ) THEN
        ALTER TABLE challenge_steps
            ADD CONSTRAINT fk_challenge_steps_challenge
            FOREIGN KEY (challenge_id)
            REFERENCES challenges(id)
            ON DELETE CASCADE;
    END IF;
END $$;

COMMENT ON TABLE challenge_steps IS 'Stores challenge questions or steps with ordering and per-step scoring.';
COMMENT ON COLUMN challenge_steps.id IS 'Technical identifier used by QCM-related tables.';
COMMENT ON COLUMN challenge_steps.step_text IS 'Legacy text column used by the current backend.';
COMMENT ON COLUMN challenge_steps.question_text IS 'Question text for QCM mode.';
COMMENT ON COLUMN challenge_steps.step_order IS 'Order of the step inside the challenge.';
COMMENT ON COLUMN challenge_steps.points IS 'Points awarded for a correct answer to this step.';

-- ---------------------------------------------------------------------------
-- step_options
-- Stores answer choices for each QCM question.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS step_options (
    id BIGSERIAL PRIMARY KEY,
    step_id BIGINT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_step_options_step
        FOREIGN KEY (step_id)
        REFERENCES challenge_steps(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_step_options_step_text
        UNIQUE (step_id, option_text)
);

COMMENT ON TABLE step_options IS 'Contains answer options for each QCM question.';

-- ---------------------------------------------------------------------------
-- challenge_attempts
-- Stores one user attempt on one challenge.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS challenge_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    challenge_id BIGINT NOT NULL,
    status VARCHAR(30) DEFAULT 'IN_PROGRESS',
    score INTEGER DEFAULT 0,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_challenge_attempts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_challenge_attempts_challenge
        FOREIGN KEY (challenge_id)
        REFERENCES challenges(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_challenge_attempts_status
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED'))
);

COMMENT ON TABLE challenge_attempts IS 'Represents a user attempt on a challenge, with score and lifecycle timestamps.';

-- ---------------------------------------------------------------------------
-- attempt_answers
-- Stores each answer given inside a challenge attempt.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS attempt_answers (
    id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    step_id BIGINT NOT NULL,
    selected_option_id BIGINT NOT NULL,
    is_correct BOOLEAN,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempt_answers_attempt
        FOREIGN KEY (attempt_id)
        REFERENCES challenge_attempts(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_attempt_answers_step
        FOREIGN KEY (step_id)
        REFERENCES challenge_steps(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_attempt_answers_selected_option
        FOREIGN KEY (selected_option_id)
        REFERENCES step_options(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_attempt_answers_attempt_step
        UNIQUE (attempt_id, step_id)
);

COMMENT ON TABLE attempt_answers IS 'Records which option was selected for each step during one attempt.';

-- ---------------------------------------------------------------------------
-- badges
-- Catalog of all badges available in the platform.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    badge_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_badges_name
        UNIQUE (name)
);

COMMENT ON TABLE badges IS 'Catalog of badges that can be awarded to users.';

-- ---------------------------------------------------------------------------
-- user_badges
-- Badges earned by users.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS user_badges (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_id BIGINT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_badges_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_badges_badge
        FOREIGN KEY (badge_id)
        REFERENCES badges(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_user_badges_user_badge
        UNIQUE (user_id, badge_id)
);

COMMENT ON TABLE user_badges IS 'Associates users with the badges they have earned.';

-- ---------------------------------------------------------------------------
-- notifications
-- User activity notifications.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

COMMENT ON TABLE notifications IS 'Stores notifications related to user activity, rewards, and challenge events.';

-- ---------------------------------------------------------------------------
-- Useful indexes
-- ---------------------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_step_options_step_id
    ON step_options(step_id);

CREATE INDEX IF NOT EXISTS idx_challenge_attempts_user_id
    ON challenge_attempts(user_id);

CREATE INDEX IF NOT EXISTS idx_challenge_attempts_challenge_id
    ON challenge_attempts(challenge_id);

CREATE INDEX IF NOT EXISTS idx_challenge_attempts_status
    ON challenge_attempts(status);

CREATE INDEX IF NOT EXISTS idx_attempt_answers_attempt_id
    ON attempt_answers(attempt_id);

CREATE INDEX IF NOT EXISTS idx_attempt_answers_step_id
    ON attempt_answers(step_id);

CREATE INDEX IF NOT EXISTS idx_user_badges_user_id
    ON user_badges(user_id);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id
    ON notifications(user_id);

CREATE INDEX IF NOT EXISTS idx_notifications_user_read
    ON notifications(user_id, is_read);

COMMIT;
