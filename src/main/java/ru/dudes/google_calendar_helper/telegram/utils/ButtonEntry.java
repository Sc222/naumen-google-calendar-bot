package ru.dudes.google_calendar_helper.telegram.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ButtonEntry {

    private String text;
    private String callbackData;

}
