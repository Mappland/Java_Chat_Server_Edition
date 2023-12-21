CREATE TABLE IF NOT EXISTS group_table (
    group_number VARCHAR(50) PRIMARY KEY,
    create_date DATETIME NOT NULL,
    create_user VARCHAR(50) NOT NULL
);