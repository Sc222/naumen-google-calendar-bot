package ru.dudes.google_calendar_helper.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.dudes.google_calendar_helper.auth2.Auth2InfoHelper;
import ru.dudes.google_calendar_helper.entities.CalendarDto;
import ru.dudes.google_calendar_helper.entities.EventDto;
import ru.dudes.google_calendar_helper.services.GoogleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotApiMethodContainer;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("calendar")
public class GoogleCalendarController {

    private static final Logger logger = LoggerFactory.getLogger(BotApiMethodContainer.class);
    private final GoogleService googleService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public GoogleCalendarController(OAuth2AuthorizedClientService authorizedClientService, GoogleService googleService
    ) {
        this.authorizedClientService = authorizedClientService;
        this.googleService = googleService;
    }

    @GetMapping("/getEvents")
    public List<EventDto> getEvents(OAuth2AuthenticationToken authentication,
                                    @RequestParam(value = "calendarId", required = false, defaultValue = "primary")
                                            String calendarId) {
        var client = Auth2InfoHelper.loadClientInfo(authentication, authorizedClientService);

        //wont work from telegram, get tokens from database using clientId as parameter

        var tokenValue = client.getAccessToken().getTokenValue();
        return googleService.getEvents(tokenValue, calendarId);
    }

    @GetMapping("/getCalendars")
    public List<CalendarDto> getCalendars(OAuth2AuthenticationToken authentication) throws IOException {
        var client = Auth2InfoHelper.loadClientInfo(authentication, authorizedClientService);

        //wont work from telegram, get tokens from database using clientId as parameter

        return googleService.getCalendars(client.getAccessToken().getTokenValue());
    }

}
