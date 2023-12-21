CREATE TABLE IF NOT EXISTS user_in_group_table (
    username VARCHAR(50) NOT NULL ,
    group_number VARCHAR(50) NOT NULL,
    user_in_date DATE,
    PRIMARY KEY (username, group_number)
);