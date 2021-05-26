package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class ResponseHelper {
    public static final String NOT_LOGGED_IN = "You are not logged in.\nPlease type /login to login or service will not work";

    public static SendMessage generateNotLoggedInResponse(String chatID) {
        var response = new SendMessage();
        response.setChatId(chatID);
        response.setText(NOT_LOGGED_IN);
        return response;
    }

    public static InlineKeyboardButton createKeyboardButton(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public static InlineKeyboardButton createKeyboardButton(String text, String callbackData, String url) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        button.setUrl(url);
        return button;
    }
}
