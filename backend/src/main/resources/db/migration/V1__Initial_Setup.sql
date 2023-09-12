CREATE TABLE customer(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    gender TEXT NOT NULL ,
    age INT NOT NULL
);

ALTER TABLE customer
    ADD CONSTRAINT customer_email_unique UNIQUE (email);
