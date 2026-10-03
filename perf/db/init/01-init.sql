-- 1) 확장을 Supabase와 같은 위치에 켠다
CREATE SCHEMA IF NOT EXISTS extensions;
CREATE EXTENSION IF NOT EXISTS postgis            WITH SCHEMA extensions;
CREATE EXTENSION IF NOT EXISTS pg_stat_statements WITH SCHEMA extensions;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp"        WITH SCHEMA extensions;
CREATE EXTENSION IF NOT EXISTS pgcrypto           WITH SCHEMA extensions;
CREATE EXTENSION IF NOT EXISTS pg_trgm            WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS vector             WITH SCHEMA public;

-- 2) Supabase 전용 요소 흉내 (회원 테이블 복원용)
CREATE ROLE anon NOLOGIN;
CREATE ROLE authenticated NOLOGIN;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE TABLE IF NOT EXISTS auth.users (id uuid PRIMARY KEY);
CREATE OR REPLACE FUNCTION auth.uid() RETURNS uuid
  LANGUAGE sql STABLE AS $$ SELECT NULL::uuid $$;

-- 3) ST_Distance 같은 함수를 스키마 이름 없이 쓰게 한다
ALTER DATABASE bts SET search_path = public, extensions;