package uk.nhs.digital.uec.api.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Amazon Cognito configuration
 */
@Configuration
public class CognitoConfig {

    @Bean
    public AWSCognitoIdentityProvider amazonCognitoIdentityClient() {
        return AWSCognitoIdentityProviderClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();
    }
}
