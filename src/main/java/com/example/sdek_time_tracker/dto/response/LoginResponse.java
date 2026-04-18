package com.example.sdek_time_tracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response with JWT token")
public record LoginResponse(

        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}