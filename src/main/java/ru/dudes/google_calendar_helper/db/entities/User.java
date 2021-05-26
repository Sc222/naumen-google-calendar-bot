package ru.dudes.google_calendar_helper.db.entities;

import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import ru.dudes.google_calendar_helper.auth2.UserInfo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

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

    @Getter
    private String email;

    @Getter
    private String imageUrl;

    protected User() {
    }

    public User(String oAuthRegistrationId, String oAuthName, String chatId, String token, String userName, String email, String imageUrl) {
        this.oAuthRegistrationId = oAuthRegistrationId;
        this.oAuthName = oAuthName;
        this.chatId = chatId;
        this.token = token;
        this.userName = userName;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public User(String oAuthRegistrationId, String oAuthName, String chatId, String token, UserInfo userInfo) {
        this.oAuthRegistrationId = oAuthRegistrationId;
        this.oAuthName = oAuthName;
        this.chatId = chatId;
        this.token = token;
        this.userName = userInfo.getName();
        this.email = userInfo.getEmail();
        this.imageUrl = userInfo.getPicture();
    }

    public void updateWithValues(String chatId, String token, String userName) {
        this.chatId = chatId;
        this.token = token;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, chatId='%s', token='%s',userName='%s']",
                id, chatId, token, userName);
    }

    public Long getId() {
        return id;
    }
}