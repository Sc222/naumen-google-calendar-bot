package ru.dudes.google_calendar_helper.telegram.utils;

public class InputUtils {

    //TODO: generic arguments parser

    public static Integer getArgumentsFirstInteger(String text, int minValue) {
        var arguments = text.split(" ");
        if (arguments.length >= 2 && arguments[1] != null && arguments[1].matches("^-?\\d+$")) {
            var argument = Integer.parseInt(arguments[1]);
            return Math.max(argument, minValue);
        }
        return null;
    }

    public static String getArgumentsFirstString(String text) {
        var arguments = text.split(" ");
        if (arguments.length >= 2 && arguments[1] != null)
            return arguments[1];
        return null;
    }

    public static String getArgumentsSecondString(String text) {
        var arguments = text.split(" ");
        if (arguments.length >= 3 && arguments[2] != null)
            return arguments[2];
        return null;
    }
}
