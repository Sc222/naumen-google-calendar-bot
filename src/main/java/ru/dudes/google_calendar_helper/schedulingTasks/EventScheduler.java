package ru.dudes.google_calendar_helper.schedulingTasks;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import com.google.api.client.util.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.dudes.google_calendar_helper.db.entities.Notification;
import ru.dudes.google_calendar_helper.db.repositories.NotifyRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;

@Component
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final NotifyRepository notifyRepository;
    @Autowired
    public EventScheduler(NotifyRepository notifyRepository){
        this.notifyRepository = notifyRepository;
    }
    @Scheduled(fixedDelay = 5000)
    public void reportCurrentTime() {
        var notifications = notifyRepository.getAllBy();
        for (Notification notififcation: notifications) {
            var dateTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            if(notififcation.getDateTime().compareTo(dateTime) >= 0)
                //TODO отправить сообщение, что до старта меньше 5ти минут
        }

    }
}