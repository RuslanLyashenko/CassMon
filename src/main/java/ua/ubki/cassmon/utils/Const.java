package ua.ubki.cassmon.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Const {
    public static final DateTimeFormatter UBKI_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter UBKI_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter UBKI_BDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public static final DateTimeFormatter UBKI_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    public static final DateTimeFormatter UBKI_BDATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final LocalDate UBKI_MIN_DATE = LocalDate.of(1900, 1, 1);


}
