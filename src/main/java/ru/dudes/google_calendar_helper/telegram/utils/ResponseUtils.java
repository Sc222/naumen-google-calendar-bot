package ru.dudes.google_calendar_helper.telegram.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ResponseUtils {
    public static final String NOT_LOGGED_IN = "You are not logged in.\nPlease type /login to login or service will not work";

    public static SendMessage generateNotLoggedInResponse(String chatId) {
        var response = new SendMessage();
        response.setChatId(chatId);
        response.setText(NOT_LOGGED_IN);
        return response;
    }

    public static SendMessage createSendMessage(String chatId, String text) {
        var response = new SendMessage();
        response.setChatId(chatId);
        response.setText(text);
        return response;
    }

    public static SendMessage createSendMessage(String chatId, String text, InlineKeyboardMarkup replyMarkup) {
        var response = new SendMessage();
        response.setChatId(chatId);
        response.setText(text);
        response.setReplyMarkup(replyMarkup);
        return response;
    }

    public static EditMessageText createEditMessage(String chatId, int messageId, String text, InlineKeyboardMarkup replyMarkup) {
        var response = new EditMessageText();
        response.setChatId(chatId);
        response.setMessageId(messageId);
        response.setText(text);
        response.setReplyMarkup(replyMarkup);
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

    public static InlineKeyboardMarkup createPaginationButtons(String callbackUrl, int currentPage, int pagesCount) {
        var previousButton = ResponseUtils.createKeyboardButton("Previous", callbackUrl + " " + (currentPage - 1));
        var nextButton = ResponseUtils.createKeyboardButton("Next", callbackUrl + " " + (currentPage + 1));
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        if (currentPage > 0)
            keyboardRow.add(previousButton);
        if (currentPage < pagesCount - 1)
            keyboardRow.add(nextButton);
        var keyboard = List.of(keyboardRow);
        var replyMarkup = new InlineKeyboardMarkup();
        replyMarkup.setKeyboard(keyboard);
        return replyMarkup;
    }
}
