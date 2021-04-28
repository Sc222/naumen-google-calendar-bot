package ru.dudes.google_calendar_helper.auth2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class Auth2InfoHelper {
    public static OAuth2AuthorizedClient loadClientInfo(OAuth2AuthenticationToken authentication, OAuth2AuthorizedClientService authorizedClientService) {
        return authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
    }

    public static Map getUserInfo(OAuth2AuthorizedClient client) {
        var userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();

        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                .getTokenValue());
        var entity = new HttpEntity<String>("", headers);
        var response = restTemplate
                .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }
}
