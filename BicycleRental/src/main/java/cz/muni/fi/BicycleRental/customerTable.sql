CREATE TABLE customer (id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        fullname VARCHAR(50) NOT NULL,
                        address VARCHAR(200) NOT NULL,
                        phone VARCHAR (30) NOT NULL);

CREATE TABLE bicycle (id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                     available SMALLINT NOT NULL,
                     price INTEGER NOT NULL);

CREATE TABLE rental (id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                            bicycleID BIGINT,
                            customerID BIGINT,
                            rentedFrom DATE NOT NULL,
                            rentedTo DATE NOT NULL,
                            FOREIGN KEY (bicycleID) REFERENCES bicycle (id),
                            FOREIGN KEY (customerID) REFERENCES customer (id));
