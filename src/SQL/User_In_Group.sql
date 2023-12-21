CREATE TABLE IF NOT EXISTS user_in_group_table (
    username VARCHAR(50) NOT NULL ,
    group_number VARCHAR(50) NOT NULL,
    user_in_date DATETIME NOT NULL,
    PRIMARY KEY (username, group_number)
);