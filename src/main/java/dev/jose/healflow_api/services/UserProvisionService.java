package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.ProvisionUserRequestDTO;
import dev.jose.healflow_api.api.models.ValidateAuthUserIdsDTO;

public interface UserProvisionService {

  /**
   * Provisions a new user. This method is indepotent, so it will not throw an exception if the user
   * already exists.
   *
   * @param request the request containing the user's information
   */
  String provisionUser(ProvisionUserRequestDTO request);

  void validateUserIds(ValidateAuthUserIdsDTO userIds);
}
