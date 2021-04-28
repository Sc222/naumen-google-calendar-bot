package ru.dudes.google_calendar_helper.telegram.controllers.core;

import org.telegram.telegrambots.meta.api.objects.Update;

public class ControllersSingleton {
    private static final BotApiMethodContainer container = BotApiMethodContainer.getInstance();

    public static BotApiMethodController getController(Update update) {
        String path;
        BotApiMethodController controller = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            path = update.getMessage().getText().split(" ")[0].trim();
            controller = container.getBotApiMethodController(path);
        } else if (update.hasCallbackQuery()) {
            path = update.getCallbackQuery().getData().split("/")[1].trim();
            controller = container.getBotApiMethodController(path);
        }
        return controller != null ? controller : container.getBotApiMethodController("");
    }
}