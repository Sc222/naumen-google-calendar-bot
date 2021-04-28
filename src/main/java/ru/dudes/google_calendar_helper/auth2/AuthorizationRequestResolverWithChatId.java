package ru.dudes.google_calendar_helper.auth2;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;

public class AuthorizationRequestResolverWithChatId implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public AuthorizationRequestResolverWithChatId(ClientRegistrationRepository repo) {
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        var authorizationRequest = defaultResolver.resolve(request);
        if (authorizationRequest != null)
            authorizationRequest = customizeAuthorizationRequest(authorizationRequest, request);
        return authorizationRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        var authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        if (authorizationRequest != null)
            authorizationRequest = customizeAuthorizationRequest(authorizationRequest, request);
        return authorizationRequest;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        var chatId = request.getParameter("chatId");
        var httpSession = request.getSession();
        httpSession.setAttribute("chatId", chatId != null ? chatId : "");
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .build();
    }

}
