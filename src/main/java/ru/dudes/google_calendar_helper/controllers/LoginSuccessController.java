package ru.dudes.google_calendar_helper.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.dudes.google_calendar_helper.auth2.ClientInfoHelper;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotApiMethodContainer;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class LoginSuccessController {

    private static final Logger logger = LoggerFactory.getLogger(BotApiMethodContainer.class);
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public LoginSuccessController(OAuth2AuthorizedClientService authorizedClientService, GoogleService googleService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(HttpSession session, OAuth2AuthenticationToken authentication) throws Exception {
        var client = ClientInfoHelper.loadClientInfo(authentication, authorizedClientService);
        var userInfo = getUserInfo(client);

        //TODO STORE USER DETAILS IN DATABASE

        //client.getRefreshToken()
        Object a = authentication.getDetails();

        String tgChatId = (String) session.getAttribute("chatId");
        logger.info("GET FROM SESSION: " + tgChatId);


        //OPTIONAL SAVE USER DETAILS SOMEWHERE HERE
        //EXAMPLE VALUES:
        //USER_NAME: userInfo.get("name");
        //USER_EMAIL: userInfo.get("email");
        //TOKEN_VALUE: client.getAccessToken().getTokenValue();
        logger.info("ACCESS_TOKEN VALUE: " + client.getAccessToken().getTokenValue());
        //System.out.println("REFRESH_TOKEN VALUE: " + client.getRefreshToken().getTokenValue());
        logger.info(userInfo.toString());
        return "Successfully logged in with user: " + userInfo.get("name");
    }


    private Map getUserInfo(OAuth2AuthorizedClient client) throws Exception {
        var userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();
        try {
            var restTemplate = new RestTemplate();
            var headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
            var entity = new HttpEntity("", headers);
            var response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            var userAttributes = response.getBody();
            return userAttributes;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
