DROP TABLE IF EXISTS transactions_log CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TYPE currency;

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  full_name VARCHAR(256) NOT NULL,
  birth_day DATE NOT NULL,
  email VARCHAR(256) NOT NULL UNIQUE,
  username VARCHAR(256) NOT NULL UNIQUE,
  password VARCHAR(1000) NOT NULL,
  roles VARCHAR(256) NOT NULL,
  enabled boolean DEFAULT true,
  created_at timestamp DEFAULT NOW(),
  updated_at timestamp
);

CREATE TYPE currency AS ENUM ('RUB','USD','CNY');

CREATE TABLE IF NOT EXISTS accounts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  number VARCHAR(256) NOT NULL,
  balance NUMERIC(14,2) NOT NULL CHECK(balance>=0),
  currency_string_code_iso4217 currency NOT NULL,
  user_id UUID NOT NULL REFERENCES users(id),
  created_at timestamp DEFAULT NOW(),
  updated_at timestamp NOT NULL,
  is_active boolean DEFAULT true
);

CREATE TABLE IF NOT EXISTS transactions_log (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at timestamp NOT NULL DEFAULT NOW(),
  transaction_type VARCHAR(10) NOT NULL,
  from_account UUID NOT NULL REFERENCES accounts(id),
  to_account UUID NOT NULL REFERENCES accounts(id),
  amount_from NUMERIC(14,2) NOT NULL,
  amount_to NUMERIC(14,2) NOT NULL,
  is_succeed boolean NOT NULL
);