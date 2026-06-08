CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    course VARCHAR(100),
    year_level INTEGER NOT NULL DEFAULT 0,
    age INTEGER,
    profile_photo VARCHAR(255),
    total_points NUMERIC(10,2) NOT NULL DEFAULT 0,
    raw_bottle_count INTEGER NOT NULL DEFAULT 0,
    account_status VARCHAR(20) NOT NULL DEFAULT 'Active',
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    session_token VARCHAR(255),
    last_activity TIMESTAMP,
    verification_code VARCHAR(10),
    verification_expiry TIMESTAMP,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS bottle_records (
    record_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    bottles_collected INTEGER NOT NULL CHECK (bottles_collected > 0),
    collection_date DATE NOT NULL,
    week_start_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS streaks (
    streak_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    streak_days INTEGER NOT NULL,
    bonus_points NUMERIC(10,2) NOT NULL,
    date_logged TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS badges (
    badge_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    badge_name VARCHAR(50) NOT NULL UNIQUE,
    level INTEGER NOT NULL UNIQUE,
    bonus_points NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_badges (
    user_badge_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    badge_id INTEGER NOT NULL REFERENCES badges(badge_id) ON DELETE CASCADE,
    date_awarded DATE NOT NULL,
    week_start_date DATE NOT NULL,
    UNIQUE (user_id, badge_id, week_start_date)
);

CREATE TABLE IF NOT EXISTS coupons (
    coupon_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coupon_name VARCHAR(100) NOT NULL,
    points_required NUMERIC(10,2) NOT NULL,
    description TEXT,
    coupon_type VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS redemptions (
    redemption_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    coupon_id INTEGER NOT NULL REFERENCES coupons(coupon_id) ON DELETE RESTRICT,
    coupon_code VARCHAR(100) UNIQUE NOT NULL,
    redemption_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending'
);

CREATE TABLE IF NOT EXISTS points_ledger (
    ledger_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    points_change NUMERIC(10,2) NOT NULL,
    source VARCHAR(30) NOT NULL,
    ref_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS leaderboard_cache (
    cache_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rank INTEGER NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    raw_bottle_count INTEGER NOT NULL,
    bonus_points_awarded NUMERIC(10,2) NOT NULL DEFAULT 0,
    snapshot_time TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inout_logs (
    log_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    event_type VARCHAR(10) NOT NULL,
    entry_method VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    staff_note TEXT,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS system_config (
    key VARCHAR(100) PRIMARY KEY,
    value TEXT NOT NULL
);

INSERT INTO badges (badge_name, level, bonus_points)
VALUES
    ('Bronze', 1, 0),
    ('Silver', 2, 1),
    ('Emerald', 3, 3),
    ('Gold', 4, 5),
    ('Constellation', 5, 10)
ON CONFLICT DO NOTHING;

INSERT INTO coupons (coupon_name, points_required, description, coupon_type)
VALUES
    ('Supplies Coupon', 10, 'Ballpen, bond paper (5 pcs.), pencil, eraser, correction tape', 'SUPPLIES'),
    ('Snack V1 Coupon', 30, 'Biscuits, breads, chips/chichirya, light snacks', 'SNACK_V1'),
    ('Snack V2 Coupon', 50, 'Street food: fishball, kikiam, kwek-kwek (tusok-tusok)', 'SNACK_V2'),
    ('Lunch Coupon', 100, 'Full meal with rice (ulam) at campus food partner', 'LUNCH')
ON CONFLICT DO NOTHING;
