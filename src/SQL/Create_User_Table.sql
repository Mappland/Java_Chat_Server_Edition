CREATE TABLE IF NOT EXISTS user_table (
    username VARCHAR(50) PRIMARY KEY,
    user_password VARCHAR(200) NOT NULL,
    user_level INT NOT NULL,
    user_create_data DATETIME NOT NULL
);