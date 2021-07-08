package uk.nhs.digital.uec.api.controller;

import org.springframework.beans.factory.annotation.Autowired;

import uk.nhs.digital.uec.api.model.authentication.LoginResult;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.service.authentication.AuthenticationServiceInterface;

@Slf4j
@RestController
@RequestMapping("/dosapi/dosservices/v0.0.1")
public class LoginController {

  @Autowired
  private AuthenticationServiceInterface authenticationService;

  @GetMapping("authentication/login")
  @CrossOrigin(origins = "*")
  public ResponseEntity<LoginResult> login(
    @RequestParam(name = "email", required = true) String email,
    @RequestParam(name = "password", required = true) String password,
    @RequestParam(name = "userPoolClientId", required = true) String userPoolClientId,
    @RequestParam(name = "userPoolClientSecret", required = true) String userPoolClientSecret
  )
  {

    try {
      log.info("Authenticate: " + email + ": " + password);
      LoginResult result = authenticationService.authenticate(email,password,userPoolClientId, userPoolClientSecret);
      log.info("Got login result: " + result);
      return ResponseEntity.ok(result);
    }
    catch(Exception ex)
    {
      log.info("exception: " + ex.getMessage());
      LoginResult resultb = new LoginResult("nothing","nothing");
      return ResponseEntity.ok(resultb);
    }

  }


}
