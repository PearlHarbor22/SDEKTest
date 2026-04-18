package com.example.sdek_time_tracker;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class TestCheck {

    @Test
    void test() {
        PostgreSQLContainer<?> postgres =
                new PostgreSQLContainer<>("postgres:15");
    }
}