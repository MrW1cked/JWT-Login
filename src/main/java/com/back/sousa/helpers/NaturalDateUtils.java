package com.back.sousa.helpers;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@UtilityClass
public class NaturalDateUtils {

    public Integer getTodayWeek() {
        var now = LocalDate.now();
        return Integer.parseInt(now.format(DateTimeFormatter.ofPattern("ww")));
    }

    public Integer getWeekFromDate(LocalDate date) {
        return Integer.parseInt(date.format(DateTimeFormatter.ofPattern("ww")));
    }

    public boolean isThisWeek(LocalDate date) {
        return Objects.equals(getWeekFromDate(date), getTodayWeek());
    }

}
