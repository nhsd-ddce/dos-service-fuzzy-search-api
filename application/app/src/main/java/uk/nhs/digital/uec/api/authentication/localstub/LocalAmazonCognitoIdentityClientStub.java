package uk.nhs.digital.uec.api.authentication.localstub;

import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.COGNITO_GROUP;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.LOGIN_ACCEPTED;
import static uk.nhs.digital.uec.api.authentication.localstub.LocalConstants.USERNAME;

import com.amazonaws.services.cognitoidp.AbstractAWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.authentication.exception.InvalidCredentialsException;

@Slf4j
public class LocalAmazonCognitoIdentityClientStub extends AbstractAWSCognitoIdentityProvider {

  private final Map<String, String> identityProviderIdPasswordMap;

  public LocalAmazonCognitoIdentityClientStub() {
    identityProviderIdPasswordMap = new HashMap<>();
    identityProviderIdPasswordMap.put("admin@nhs.net", "password");
  }

  @Override
  public InitiateAuthResult initiateAuth(InitiateAuthRequest initiateAuthRequest) {
    Map<String, String> authParameters = initiateAuthRequest.getAuthParameters();
    String username = authParameters.get(USERNAME);
    String password = identityProviderIdPasswordMap.get(username);
    log.info("Login attempted using credentials : " + username + "/" + password);

    if (!identityProviderIdPasswordMap.get(username).equals(password)) {
      log.info("Attempted to login using invalid credentials");
      throw new InvalidCredentialsException("Invalid Credentials");
    }

    log.info(LOGIN_ACCEPTED);
    Set<String> groupNames = new HashSet<>(Arrays.asList(COGNITO_GROUP));
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken(
        generateAuthToken("id", "issuer", username, 360000L, groupNames));
    authenticationResult.setRefreshToken(
        generateAuthToken("rtid", "issuer", username, 86400000, new HashSet<>()));
    InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
    initiateAuthResult.setAuthenticationResult(authenticationResult);
    return initiateAuthResult;
  }

  private String generateAuthToken(
      String id, String issuer, String userName, long duration, Set<String> groupNames) {
    LocalJwtFactory testJwtFactory = new LocalJwtFactory();
    Set<String> cognitoGroupNames = new HashSet<>(Arrays.asList(COGNITO_GROUP));
    return testJwtFactory.create(id, issuer, userName, duration, cognitoGroupNames);
  }
}
