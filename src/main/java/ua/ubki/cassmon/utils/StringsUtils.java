package ua.ubki.cassmon.utils;

import org.apache.commons.lang3.StringUtils;

public class StringsUtils {
    public static String truncateSpace(String value) {
        if (value == null) {
            value = "";
        }
        return (value
                .replaceAll("\u00a0", " "))
                .replaceAll(" +", " ")
                .trim();
    }

    public static boolean isEmptyOrNull(String value) {
        return StringUtils.isEmpty(value) || "null".equalsIgnoreCase(value);
    }
}
