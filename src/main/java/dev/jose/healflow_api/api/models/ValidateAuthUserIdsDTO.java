package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(
    name = "ValidateAuthUserIds",
    description =
        "Schema to validate the existing user IDs on the Auth provider to ensure that they are in"
            + " sync")
public record ValidateAuthUserIdsDTO(List<UUID> ids) {}
