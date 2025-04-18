package dev.book.accountbook.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public enum Frequency {
    DAILY {
        @Override
        public LocalDateTime calcStartDate() {
            return LocalDate.now().atStartOfDay();
        }

        @Override
        public PeriodRange calcPeriod() {
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterdayStart = getYesterdayStart();
            LocalDateTime yesterdayEnd = getYesterdayEnd();

            return new PeriodRange(todayStart, now, yesterdayStart, yesterdayEnd);
        }
    },
    WEEKLY {
        @Override
        public LocalDateTime calcStartDate() {
            return getStartOfCurrentWeek();
        }

        @Override
        public PeriodRange calcPeriod() {
            LocalDateTime thisWeekStart = getStartOfCurrentWeek();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastWeekStart = thisWeekStart.minusWeeks(1);
            LocalDateTime lastWeekEnd = thisWeekStart.minusSeconds(1);

            return new PeriodRange(thisWeekStart, now, lastWeekStart, lastWeekEnd);
        }
    },
    MONTHLY {
        @Override
        public LocalDateTime calcStartDate() {
            return getStartOfCurrentMonth();
        }

        @Override
        public PeriodRange calcPeriod() {
            LocalDateTime thisMonthStart = getStartOfCurrentMonth();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            return new PeriodRange(thisMonthStart, now, lastMonthStart, lastMonthEnd);
        }
    },
    YEARLY {
        @Override
        public LocalDateTime calcStartDate() {
            return getStartOfCurrentYear();
        }

        @Override
        public PeriodRange calcPeriod() {
            LocalDateTime thisYearStart = getStartOfCurrentYear();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastYearStart = thisYearStart.minusYears(1);
            LocalDateTime lastYearEnd = thisYearStart.minusSeconds(1);

            return new PeriodRange(thisYearStart, now, lastYearStart, lastYearEnd);
        }
    };

    public abstract LocalDateTime calcStartDate();

    public abstract PeriodRange calcPeriod();


    private static LocalDateTime getStartOfCurrentWeek() {
        return LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();
    }

    private static LocalDateTime getStartOfCurrentMonth() {
        return LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
    }

    private static LocalDateTime getYesterdayStart() {
        return LocalDate.now()
                .minusDays(1)
                .atStartOfDay();
    }

    private static LocalDateTime getYesterdayEnd() {
        return LocalDate.now()
                .minusDays(1)
                .atTime(LocalTime.MAX);
    }

    private static LocalDateTime getStartOfCurrentYear() {
        return LocalDate.now()
                .withDayOfYear(1)
                .atStartOfDay();
    }
}
