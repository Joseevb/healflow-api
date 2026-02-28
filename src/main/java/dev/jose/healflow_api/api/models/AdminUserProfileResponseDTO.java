package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "AdminUserProfileResponse")
public record AdminUserProfileResponseDTO(List<AdminUserProfileDTO> users, Integer totalPages) {}
