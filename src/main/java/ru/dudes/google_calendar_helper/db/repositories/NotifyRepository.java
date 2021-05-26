package ru.dudes.google_calendar_helper.db.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.dudes.google_calendar_helper.db.entities.Notification;

import java.util.List;


public interface NotifyRepository extends CrudRepository<Notification, Long> {

    public List<Notification> getAllBy();
}
