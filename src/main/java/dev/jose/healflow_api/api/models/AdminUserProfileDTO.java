package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "AdminUserProfile")
public record AdminUserProfileDTO(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String phone,
    Instant dateOfBirth,
    Boolean isActive,
    UUID authId,
    Boolean isSubscribed,
    Instant createdAt,
    Instant updatedAt) {}
