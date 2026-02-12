package dev.jose.healflow_api.api.controllers;

import dev.jose.healflow_api.api.docs.UserProvisionsApi;
import dev.jose.healflow_api.api.models.ProvisionUserRequestDTO;
import dev.jose.healflow_api.api.models.ValidateAuthUserIdsDTO;
import dev.jose.healflow_api.services.UserProvisionService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserProvisionsController implements UserProvisionsApi {

  private final UserProvisionService userProvisionService;

  @Override
  public ResponseEntity<Void> provisionUser(
      ProvisionUserRequestDTO body, UriComponentsBuilder uriBuilder) {
    String id = userProvisionService.provisionUser(body);
    URI location = uriBuilder.path("/{id}").buildAndExpand(id).toUri();
    log.info("Provisioning user with id {}", id);

    return ResponseEntity.created(location).build();
  }

  @Override
  public ResponseEntity<Void> validateUser(ValidateAuthUserIdsDTO body) {
    userProvisionService.validateUserIds(body);
    return ResponseEntity.ok().build();
  }
}
