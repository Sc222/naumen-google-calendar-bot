package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.entities.Notification;
import ru.dudes.google_calendar_helper.db.repositories.NotifyRepository;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMethod;
import ru.dudes.google_calendar_helper.telegram.utils.ResponseUtils;

import java.util.Date;


@BotController
public class NotifyController {

    private final UserRepository userRepository;
    private final NotifyRepository notifyRepository;
    private final GoogleService googleService;

    @Autowired
    public NotifyController(UserRepository userRepository, NotifyRepository notifyRepository, GoogleService googleService) {
        this.userRepository = userRepository;
        this.notifyRepository = notifyRepository;
        this.googleService = googleService;
    }

    @BotRequestMapping(value = "/callback-notify", method = BotRequestMethod.EDIT)
    public SendMessage NotifyEventButton(Update update) {
        //todo remove mock
        return ResponseUtils.createSendMessage(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "Notification has been set up");
    }

    @BotRequestMapping(value = "/notify")
    public SendMessage NotifyEvent(Update update) {
        var message = update.getMessage();
        var values = message.getText().split(" ");
        var response = new SendMessage();
        response.setChatId(String.valueOf(update.getMessage().getChatId()));

        if (values.length != 3) {
            response.setText("Wrong command format!\nPlease type /notify %calendarID %eventId\nCalendar ID's can be listed using /calendars .");
            return response;
        }
        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));
        var calendarId = values[1];
        var eventId = values[2];
        var event = googleService.getEvent(calendarId, eventId, user.getToken());
        if (event == null) {
            response.setText("There was a error getting your calendar events.\nCheck command arguments or try /login command.");
            return response;
        }

        var notification = new Notification(message.getChatId(), new Date(event.getStartDateTime().getValue()), calendarId, eventId);
        notifyRepository.save(notification);

        response.setText("Success adding notification");
        return response;
    }
}


