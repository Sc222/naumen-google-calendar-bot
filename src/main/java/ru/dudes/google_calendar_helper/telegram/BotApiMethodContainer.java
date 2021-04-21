package ru.dudes.google_calendar_helper.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BotApiMethodContainer {
    private static final Logger logger = LoggerFactory.getLogger(BotApiMethodContainer.class);

    private Map<String, BotApiMethodController> controllerMap;

    public static BotApiMethodContainer getInstance() {
        return Holder.INSTANCE;
    }

    public void addBotController(String path, BotApiMethodController controller) {
        if(controllerMap.containsKey(path))
            logger.error(String.format("path %s is already added", path));
        logger.trace("add telegram bot controller for path: " +  path);
        controllerMap.put(path, controller);
    }

    public BotApiMethodController getBotApiMethodController(String path) {
        return controllerMap.get(path);
    }

    private BotApiMethodContainer() {
        controllerMap = new HashMap<>();
    }

    private static class Holder{
        final static BotApiMethodContainer INSTANCE = new BotApiMethodContainer();
    }
}
