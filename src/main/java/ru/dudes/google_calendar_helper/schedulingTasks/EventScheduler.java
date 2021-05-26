package ru.dudes.google_calendar_helper.schedulingTasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dudes.google_calendar_helper.db.entities.Notification;
import ru.dudes.google_calendar_helper.db.repositories.NotifyRepository;
import ru.dudes.google_calendar_helper.telegram.GoogleCalendarBot;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class EventScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final NotifyRepository notifyRepository;
    private final GoogleCalendarBot telegramBot;

    @Autowired
    public EventScheduler(NotifyRepository notifyRepository, GoogleCalendarBot telegramBot) {
        this.notifyRepository = notifyRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedDelay = 5000)
    public void reportCurrentTime() {
        var notifications = notifyRepository.getAllBy(); //возможно метод должен называться getAll
        for (Notification notififcation : notifications) {
            var dateTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            if (notififcation.getDateTime().compareTo(dateTime) >= 0) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(notififcation.getChatId()));
                sendMessage.setText("Notification:\n" + 5 + " minutes before event: " + notififcation.getEventId());
                try {
                    telegramBot.sendMessageFromController(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    logger.error("Can't send telegram message when notifying about event");
                }
            }
        }

    }
}