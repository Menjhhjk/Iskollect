CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    course VARCHAR(100),
    year_level INTEGER NOT NULL DEFAULT 0,
    age INTEGER,
    profile_photo VARCHAR(500),
    total_points DOUBLE PRECISION NOT NULL DEFAULT 0,
    raw_bottle_count INTEGER NOT NULL DEFAULT 0,
    weekly_bottles INTEGER NOT NULL DEFAULT 0,
    streak INTEGER NOT NULL DEFAULT 0,
    last_submit_date DATE,
    account_status VARCHAR(50) NOT NULL DEFAULT 'active',
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    session_token VARCHAR(255),
    last_activity TIMESTAMP,
    verification_code VARCHAR(100),
    verification_expiry TIMESTAMP,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS transactions (
    trans_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    bottles INTEGER NOT NULL,
    base_points DOUBLE PRECISION NOT NULL,
    streak_bonus DOUBLE PRECISION NOT NULL DEFAULT 0,
    badge_bonus DOUBLE PRECISION NOT NULL DEFAULT 0,
    points DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS rewards (
    reward_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    points_required DOUBLE PRECISION NOT NULL,
    description TEXT,
    coupon_type VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS redeemed_rewards (
    redeem_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
    reward_id INTEGER NOT NULL REFERENCES rewards(reward_id) ON DELETE RESTRICT,
    redeem_date DATE NOT NULL,
    coupon_code VARCHAR(100) UNIQUE NOT NULL,
    is_fulfilled BOOLEAN NOT NULL DEFAULT FALSE,
    points_deducted DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS inout_logs (
    log_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT,
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

INSERT INTO rewards (name, points_required, description, coupon_type)
VALUES
    ('Supplies Coupon', 10, 'Ballpen, bond paper (5 pcs.), pencil, eraser, correction tape', 'SUPPLIES'),
    ('Snack V1 Coupon', 30, 'Biscuits, breads, chips/chichirya, light snacks', 'SNACK_V1'),
    ('Snack V2 Coupon', 50, 'Street food: fishball, kikiam, kwek-kwek (tusok-tusok)', 'SNACK_V2'),
    ('Lunch Coupon', 100, 'Full meal with rice (ulam) at campus food partner', 'LUNCH')
ON CONFLICT DO NOTHING;
