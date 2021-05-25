package ru.dudes.google_calendar_helper.db.entities;

import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/*@Data
public class User {

    private String chatId;
    private String summary;
    private String description;
}*/

@Entity
public class User {

    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String oAuthRegistrationId;

    @Getter
    private String oAuthName;

    @Getter
    private String chatId;

    @Getter
    private String token;

    @Getter
    private String userName;


    /*@Getter
    private String imageUrl;

    @Getter
    private String firstName;

    @Getter
    private String lastName;*/

    protected User() {
    }

    public User(String oAuthRegistrationId,String oAuthName ,String chatId, String token, String userName) {
        this.oAuthRegistrationId = oAuthRegistrationId;
        this.chatId = chatId;
        this.token = token;
        this.userName = userName;
        this.oAuthName = oAuthName;
    }

    public void updateWithValues(String chatId, String token, String userName) {
        this.chatId = chatId;
        this.token = token;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, chatId='%s', token='%s',userName='%s']",
                id, chatId, token, userName);
    }

    public Long getId() {
        return id;
    }
}