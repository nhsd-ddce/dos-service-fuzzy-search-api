package uk.nhs.digital.uec.api.service.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import uk.nhs.digital.uec.api.model.authentication.LoginResult;

import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
public class AuthenticationService implements AuthenticationServiceInterface{

  @Autowired
  private AWSCognitoIdentityProvider identityClient;

  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

  public LoginResult authenticate(String email, String password, String userPoolClientId, String userPoolClientSecret) throws Exception {

    log.info("Calling authentication: "  + email + ": " + password);

    Map<String, String> authenticationParameters = new HashMap<>();
      authenticationParameters.put("USERNAME", email);
      authenticationParameters.put("PASSWORD", password);
      authenticationParameters.put("SECRET_HASH", createSecretHash(email, userPoolClientSecret, userPoolClientId));

      log.info("created secret hash");

    InitiateAuthRequest authenticationRequest = new InitiateAuthRequest()
        .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
        .withClientId(userPoolClientId)
        .withAuthParameters(authenticationParameters);

        log.info("created authreq");
    InitiateAuthResult authenticationResult = identityClient.initiateAuth(authenticationRequest);
    log.info("called cognito: " + authenticationResult);
    return new LoginResult(authenticationResult.getAuthenticationResult().getAccessToken(),
                          authenticationResult.getAuthenticationResult().getRefreshToken());
  }

  public String createSecretHash(String email, String userPoolClientSecret, String userPoolClientId) throws Exception{

    log.info("creating secret hash: "  + email);

    SecretKeySpec signingKey = new SecretKeySpec(userPoolClientSecret.getBytes(UTF_8), HMAC_SHA256_ALGORITHM);
    log.info("got signing key");

    try {
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        mac.update(email.getBytes(UTF_8));
        log.info("got mac stuff");
        byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(UTF_8));
        log.info("got byte and returning");
        return Base64.getEncoder().encodeToString(rawHmac);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      log.info("exception: " + e.getMessage());
        throw new Exception();
    }

  }
}
