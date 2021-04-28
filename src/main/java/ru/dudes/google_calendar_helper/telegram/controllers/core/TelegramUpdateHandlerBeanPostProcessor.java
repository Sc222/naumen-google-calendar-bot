package ru.dudes.google_calendar_helper.telegram.controllers.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class TelegramUpdateHandlerBeanPostProcessor implements BeanPostProcessor, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(BotApiMethodContainer.class);

    private BotApiMethodContainer container = BotApiMethodContainer.getInstance();
    private Map<String, Class> botControllerMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(BotController.class))
            botControllerMap.put(beanName, beanClass);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!botControllerMap.containsKey(beanName)) return bean;
        Class<?> original =  botControllerMap.get(beanName);
        Arrays.stream(original.getMethods())
                .filter(method -> method.isAnnotationPresent(BotRequestMapping.class))
                .forEach((Method method) -> generateController(bean, method));
        return bean;
    }

    private void generateController(Object bean, Method method) {
        logger.info("GENERATE CONTROLLER");
        var botController = bean.getClass().getAnnotation(BotController.class);
        var botRequestMapping = method.getAnnotation(BotRequestMapping.class);
        var path = (botController.value().length != 0 ? botController.value()[0] : "")
                + (botRequestMapping.value().length != 0 ? botRequestMapping.value()[0] : "");
        BotApiMethodController controller = null;
        switch (botRequestMapping.method()[0]){
            case MSG:
                controller = createControllerUpdate2ApiMethod(bean, method);
                break;
            case EDIT:
                controller = createProcessListForController(bean, method);
                break;
            default:
                break;
        }
        if (controller != null) {
            container.addBotController(path, controller);
        }
    }

    private BotApiMethodController createControllerUpdate2ApiMethod(Object bean, Method method){
        return new BotApiMethodController(bean, method) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update!=null && update.hasMessage() && update.getMessage().hasText();
            }
        };
    }

    private BotApiMethodController createProcessListForController(Object bean, Method method){
        return new BotApiMethodController(bean, method) {
            @Override
            public boolean successUpdatePredicate(Update update) {
                return update!=null && update.hasCallbackQuery() && update.getCallbackQuery().getData() != null;
            }
        };
    }

    //todo what's the point in getOrder?
    @Override
    public int getOrder() {
        return 100;
    }
}
