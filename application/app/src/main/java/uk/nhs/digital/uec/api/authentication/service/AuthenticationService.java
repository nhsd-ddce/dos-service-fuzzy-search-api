package uk.nhs.digital.uec.api.authentication.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.authentication.cognito.CognitoIdpService;
import uk.nhs.digital.uec.api.authentication.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.authentication.model.AuthToken;
import uk.nhs.digital.uec.api.authentication.model.Credential;

@Service
@Slf4j
public class AuthenticationService implements AuthenticationServiceInterface {

  @Autowired private CognitoIdpService cognitoIdpService;

  @Override
  public AuthToken getAccessToken(Credential credentials) throws UnauthorisedException {
    return cognitoIdpService.authenticate(credentials);
  }
}
