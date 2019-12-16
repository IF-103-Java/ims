package com.ita.if103java.ims.util;

import com.ita.if103java.ims.entity.User;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TokenUtil {

    public static boolean isValidToken(User user) {
        ZonedDateTime updatedDateTime = user.getUpdatedDate();
        ZonedDateTime currrentDateTime = ZonedDateTime.now(ZoneId.systemDefault());

        Duration duration = Duration.between(updatedDateTime, currrentDateTime);
        long diffHours = Math.abs(duration.toHours());
        return diffHours <= 24;
    }
}
