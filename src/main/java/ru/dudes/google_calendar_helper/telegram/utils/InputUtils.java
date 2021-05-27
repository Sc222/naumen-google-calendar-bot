package ru.dudes.google_calendar_helper.telegram.utils;

public class InputUtils {

    public static Integer getArgumentsOneInteger(String text) {
        var arguments = text.split(" ");
        if (arguments.length == 2 && arguments[1] != null && arguments[1].matches("^\\d+$"))
            return Integer.parseInt(arguments[1]);
        return null;
    }
}
