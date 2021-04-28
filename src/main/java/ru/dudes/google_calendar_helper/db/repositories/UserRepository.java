package ru.dudes.google_calendar_helper.db.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.dudes.google_calendar_helper.db.entities.User;


public interface UserRepository extends CrudRepository<User, Long> {
    //List<User> findByUserName(String userName);
    User findById(long id);
    User findByChatId(String chatId);
}