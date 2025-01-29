CREATE TABLE IF NOT EXISTS point (
    point_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(255),
    remain_amount DOUBLE,
    init_amount DOUBLE,
    expired_date DATE,
    tx_type_detail VARCHAR(50),
    is_admin BOOLEAN
);

CREATE TABLE IF NOT EXISTS POINT_TRANSACTION (
    transaction_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    point_key BIGINT,
    transaction_date DATE,
    amount DOUBLE,
    tx_type VARCHAR(50),
    tx_type_detail VARCHAR(50),
    order_key VARCHAR(255),
    FOREIGN KEY (point_key) REFERENCES POINT(point_key)
);

CREATE TABLE IF NOT EXISTS USER_POINT (
    user_point_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_key VARCHAR(255),
    MAX_POINT DOUBLE,
    IS_LOCKED BOOLEAN

);