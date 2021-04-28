package ru.dudes.google_calendar_helper.auth2;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class ClientInfoHelper {
    public static OAuth2AuthorizedClient loadClientInfo(OAuth2AuthenticationToken authentication, OAuth2AuthorizedClientService authorizedClientService) throws Exception {
        try {
            return authorizedClientService
                    .loadAuthorizedClient(
                            authentication.getAuthorizedClientRegistrationId(),
                            authentication.getName());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
