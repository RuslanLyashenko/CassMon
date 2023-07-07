package ua.ubki.cassmon.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ua.ubki.cassmon.utils.Const.*;

public class DateUtils {

    public static LocalDateTime smartParse(String dateStr) {
        if (StringsUtils.isEmptyOrNull(dateStr) || StringUtils.isBlank(dateStr)) {
            return null;
        }
        LocalDateTime parsedDate = null;
        dateStr = StringUtils.substring(dateStr, 0, 19);
        try {
            if (dateStr.length() <= 10) {
                if (dateStr.contains(".")) {
                    parsedDate = LocalDate.parse(dateStr, UBKI_BDATE_FORMATTER).atStartOfDay();
                } else {
                    parsedDate = LocalDate.parse(dateStr, UBKI_DATE_FORMATTER).atStartOfDay();
                }
            } else if (dateStr.contains("_")) {
                parsedDate = LocalDateTime.parse(dateStr, UBKI_DATE_TIME_FORMAT);
            } else if (dateStr.contains("T")) {
                if (dateStr.contains("Z")) {
                    parsedDate = LocalDateTime.parse(StringUtils.substring(dateStr, 0, dateStr.length() - 1));
                } else {
                    parsedDate = LocalDateTime.parse(dateStr);
                }
            } else if (dateStr.contains(".")) {
                parsedDate = LocalDateTime.parse(dateStr, UBKI_BDATETIME_FORMATTER);
            } else if (dateStr.contains(" ")) {
                parsedDate = LocalDateTime.parse(dateStr, UBKI_DATETIME_FORMAT);
            }
        } catch (Exception ex) {
            // skipped
        }
        if (parsedDate == null) {
            String datePart = StringUtils.substring(dateStr, 0, 10);
            if (datePart.contains("0000")) {
                return UBKI_MIN_DATE.atStartOfDay();
            }
            try {
                parsedDate = LocalDate.parse(datePart, UBKI_DATE_FORMATTER).atStartOfDay();
            } catch (Exception ex) {
                // skipped
            }
        }
        return parsedDate;
    }
}
