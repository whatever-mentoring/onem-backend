-- 단축 URL 테이블 생성
CREATE TABLE IF NOT EXISTS shorten_urls (
    id SERIAL PRIMARY KEY,
    short_key VARCHAR(20) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expiry_time TIMESTAMP WITH TIME ZONE NOT NULL,
    
    CONSTRAINT uq_short_key UNIQUE (short_key)
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_short_key ON shorten_urls (short_key);
CREATE INDEX IF NOT EXISTS idx_expiry_time ON shorten_urls (expiry_time);
