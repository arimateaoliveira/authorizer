package com.challenge.authorizer.util;

import com.challenge.authorizer.model.request.TransactionRequestImmutable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    public static LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
    }

    public static LocalDateTime getLocalDateTime(Date time) {
        return DateUtils.convertDateToLocalDateTime(time);
    }

}
