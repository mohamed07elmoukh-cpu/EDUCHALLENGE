create table if not exists challenge_likes (
    id bigserial primary key,
    challenge_id bigint not null references challenges(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    created_at timestamp not null default now(),
    constraint uq_challenge_likes_challenge_user unique (challenge_id, user_id)
);

create table if not exists saved_challenges (
    id bigserial primary key,
    challenge_id bigint not null references challenges(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    created_at timestamp not null default now(),
    constraint uq_saved_challenges_challenge_user unique (challenge_id, user_id)
);

create table if not exists challenge_shares (
    id bigserial primary key,
    challenge_id bigint not null references challenges(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    created_at timestamp not null default now()
);

create table if not exists challenge_comments (
    id bigserial primary key,
    challenge_id bigint not null references challenges(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    content varchar(800) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create index if not exists idx_challenge_likes_challenge_id on challenge_likes (challenge_id);
create index if not exists idx_saved_challenges_challenge_id on saved_challenges (challenge_id);
create index if not exists idx_challenge_shares_challenge_id on challenge_shares (challenge_id);
create index if not exists idx_challenge_comments_challenge_id on challenge_comments (challenge_id);
