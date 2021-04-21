package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.BotApiMethodContainer;
import ru.dudes.google_calendar_helper.telegram.BotApiMethodController;

public class Controllers {
    private static final BotApiMethodContainer container = BotApiMethodContainer.getInstance();

    public static BotApiMethodController getController(Update update) {
        String path;
        BotApiMethodController controller = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            path = update.getMessage().getText().split(" ")[0].trim();
            controller = container.getBotApiMethodController(path);

        } else if (update.hasCallbackQuery()) {
            //todo ??? why ? and split by /
            path = update.getCallbackQuery().getData().split("/")[1].trim();
            controller = container.getBotApiMethodController(path);
        }
        return controller != null ? controller : container.getBotApiMethodController("");
    }
}
