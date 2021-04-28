package ru.dudes.google_calendar_helper.telegram.controllers.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BotApiMethodController {

    private static final Logger logger = LoggerFactory.getLogger(BotApiMethodContainer.class);

    private Object bean;
    private Method method;
    private Process processUpdate;

    public BotApiMethodController(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        processUpdate = typeListReturnDetect() ? this::processList : this::processSingle;
    }

    public boolean successUpdatePredicate(Update update){
        return true;
    }

    public List<BotApiMethod> process(Update update) {
        if(!successUpdatePredicate(update)) return null;
        try {
            return processUpdate.accept(update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("bad invoke method", e);
        }
        return null;
    }

    boolean typeListReturnDetect() {
        return List.class.equals(method.getReturnType());
    }

    private List<BotApiMethod> processSingle(Update update) throws InvocationTargetException, IllegalAccessException {
        BotApiMethod botApiMethod = (BotApiMethod) method.invoke(bean, update);
        return botApiMethod != null ? Collections.singletonList(botApiMethod) : new ArrayList<>(0);
    }

    private List<BotApiMethod> processList(Update update) throws InvocationTargetException, IllegalAccessException {
        List<BotApiMethod> botApiMethods = (List<BotApiMethod>) method.invoke(bean, update);
        return botApiMethods != null ? botApiMethods : new ArrayList<>(0);
    }

    private interface Process{
        List<BotApiMethod> accept(Update update) throws InvocationTargetException, IllegalAccessException;
    }
}