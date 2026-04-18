DROP TABLE IF EXISTS time_records;
DROP TABLE IF EXISTS tasks;

CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(50) NOT NULL
);

CREATE TABLE time_records (
                              id BIGSERIAL PRIMARY KEY,
                              employee_id BIGINT NOT NULL,
                              task_id BIGINT NOT NULL,
                              start_time TIMESTAMP NOT NULL,
                              end_time TIMESTAMP NOT NULL,
                              description TEXT,
                              CONSTRAINT fk_time_records_task
                                  FOREIGN KEY (task_id) REFERENCES tasks(id)
);