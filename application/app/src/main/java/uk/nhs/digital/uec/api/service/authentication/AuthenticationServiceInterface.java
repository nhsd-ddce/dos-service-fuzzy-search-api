package uk.nhs.digital.uec.api.service.authentication;

import uk.nhs.digital.uec.api.model.authentication.LoginResult;

public interface AuthenticationServiceInterface {

  public LoginResult authenticate(String email, String password, String userPoolClientId, String userPoolClientSecret) throws Exception;

  public String createSecretHash(String email, String userPoolClientSecret, String userPoolClientId) throws Exception;

}
