BEGIN;

-- Add creator relation and challenge visibility flags.
ALTER TABLE challenges
    ADD COLUMN IF NOT EXISTS creator_id BIGINT,
    ADD COLUMN IF NOT EXISTS visibility VARCHAR(20),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN;

UPDATE challenges c
SET creator_id = u.id
FROM users u
WHERE c.creator_id IS NULL
  AND LOWER(c.creator_username) = LOWER(u.username);

UPDATE challenges
SET visibility = 'PUBLIC'
WHERE visibility IS NULL;

UPDATE challenges
SET is_active = TRUE
WHERE is_active IS NULL;

ALTER TABLE challenges
    ALTER COLUMN visibility SET DEFAULT 'PUBLIC',
    ALTER COLUMN is_active SET DEFAULT TRUE;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'challenges'::regclass
          AND conname = 'fk_challenges_creator'
    ) THEN
        ALTER TABLE challenges
            ADD CONSTRAINT fk_challenges_creator
            FOREIGN KEY (creator_id)
            REFERENCES users(id)
            ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_challenges_creator_id
    ON challenges(creator_id);

CREATE INDEX IF NOT EXISTS idx_challenges_visibility_active_created_at
    ON challenges(visibility, is_active, created_at DESC);

-- Upgrade challenge_steps so it can be mapped as a real entity with an id.
CREATE SEQUENCE IF NOT EXISTS challenge_steps_id_seq;

ALTER TABLE challenge_steps
    ADD COLUMN IF NOT EXISTS id BIGINT,
    ADD COLUMN IF NOT EXISTS question_text TEXT,
    ADD COLUMN IF NOT EXISTS points INTEGER;

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
    ALTER COLUMN id SET NOT NULL,
    ALTER COLUMN question_text SET NOT NULL,
    ALTER COLUMN points SET NOT NULL;

DO $$
DECLARE
    primary_key_name TEXT;
BEGIN
    SELECT conname
    INTO primary_key_name
    FROM pg_constraint
    WHERE conrelid = 'challenge_steps'::regclass
      AND contype = 'p'
    LIMIT 1;

    IF primary_key_name IS NOT NULL THEN
        EXECUTE format(
            'ALTER TABLE challenge_steps DROP CONSTRAINT %I',
            primary_key_name
        );
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'challenge_steps'::regclass
          AND conname = 'challenge_steps_pkey'
    ) THEN
        ALTER TABLE challenge_steps
            ADD CONSTRAINT challenge_steps_pkey PRIMARY KEY (id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'challenge_steps'::regclass
          AND conname = 'uq_challenge_steps_challenge_order'
    ) THEN
        ALTER TABLE challenge_steps
            ADD CONSTRAINT uq_challenge_steps_challenge_order
            UNIQUE (challenge_id, step_order);
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

CREATE INDEX IF NOT EXISTS idx_challenge_steps_challenge_id
    ON challenge_steps(challenge_id);

COMMIT;
