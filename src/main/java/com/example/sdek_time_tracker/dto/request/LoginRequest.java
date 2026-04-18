package com.example.sdek_time_tracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request for user login")
public record LoginRequest(

        @Schema(description = "Username", example = "admin")
        @NotBlank(message = "Username must not be blank")
        String username,

        @Schema(description = "Password", example = "admin")
        @NotBlank(message = "Password must not be blank")
        String password
) {
}
