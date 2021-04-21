package ru.dudes.google_calendar_helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dudes.google_calendar_helper.telegram.BotApiMethodController;
import ru.dudes.google_calendar_helper.telegram.controllers.Controllers;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class GoogleCalendarBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarBot.class);

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethodController controller = Controllers.getController(update);
        List<BotApiMethod> methods = controller.process(update);
        methods.forEach(method -> {
            try {
                execute(method);
                logger.info("Sent message to {}", update.getMessage().getChatId());
            } catch (TelegramApiException e) {
                e.printStackTrace();
                logger.error("Failed to send message to {} due to error: {}", update.getMessage().getChatId(), e.getMessage());

            }
        });
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

}