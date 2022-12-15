package illyena.gilding.core.util;

import com.ibm.icu.util.EasterHoliday;
import com.ibm.icu.util.SimpleHoliday;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

public class GildingCalendar {
    static Calendar calendar = Calendar.getInstance();
    static Birthdays birthday;

    public static Holidays checkHolidays() {
        if (isNewYears()) {
            return Holidays.NEW_YEARS;
        }
        if (isEaster()) {
            return Holidays.EASTER;
        }
        if (isIndependenceDay()) {
            return Holidays.INDEPENDENCE_DAY;
        }
        if (isHalloween()) {
            return Holidays.HALLOWEEN;
        }
        if (isThanksGiving()) {
            return Holidays.THANKSGIVING;
        }
        if (isChristmas()) {
            return Holidays.CHRISTMAS;
        }
        if (isBirthday()) {
            return Holidays.BIRTHDAY;
        }
        return Holidays.NONE;

    }

    public static boolean isNewYears() {
        calendar.setLenient(true);
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 31 && calendar.get(Calendar.DATE) <= 32;
    }

    public static boolean isEaster() {
        return calendar.after(EasterHoliday.GOOD_FRIDAY) && calendar.before(EasterHoliday.EASTER_MONDAY);
    }

    public static boolean isIndependenceDay() {
        return calendar.get(Calendar.MONTH) == Calendar.JULY && calendar.get(Calendar.DATE) >=2 && calendar.get(Calendar.DATE) <= 6;
    }

    public static boolean isHalloween() {
        return calendar.get(Calendar.MONTH) == Calendar.OCTOBER || SimpleHoliday.ALL_SAINTS_DAY.isOn(calendar.getTime());
    }

    public static boolean isThanksGiving() {
        boolean offset = calendar.get(Calendar.MONTH) == Calendar.NOVEMBER && TemporalAdjusters.firstDayOfMonth() == DayOfWeek.FRIDAY || TemporalAdjusters.firstDayOfMonth() == DayOfWeek.SATURDAY;
        return calendar.get(Calendar.MONTH) == Calendar.NOVEMBER && (calendar.get(Calendar.WEEK_OF_MONTH) == 4 + (offset ? 1 : 0) && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY);
    }

    public static boolean isChristmas() {
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER || (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DATE) <= 3);
    }

    public static boolean isBirthday() {
        //MOM January 4
        if (calendar.get(Calendar.MONTH) == Calendar.JANUARY  && calendar.get(Calendar.DATE) == 4) {
            setBirthday(Birthdays.MOM);
        }
        //LIAM August 9
        if (calendar.get(Calendar.MONTH) == Calendar.AUGUST  && calendar.get(Calendar.DATE) == 9) {
            setBirthday(Birthdays.LIAM);
        }
        //DAD August 26
        if (calendar.get(Calendar.MONTH) == Calendar.AUGUST  && calendar.get(Calendar.DATE) == 26) {
            setBirthday(Birthdays.DAD);
        }
        //JOHN September 30
        if (calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER  && calendar.get(Calendar.DATE) == 30) {
            setBirthday(Birthdays.JOHN);
        }
        return birthday != Birthdays.NONE;
    }

    private static void setBirthday(Birthdays birthday) { GildingCalendar.birthday = birthday; }

    public Birthdays getBirthday() { return birthday; }

    public enum Holidays {
        NONE,
        NEW_YEARS,
        EASTER,
        INDEPENDENCE_DAY,
        HALLOWEEN,
        THANKSGIVING,
        CHRISTMAS,
        BIRTHDAY;

        Holidays() { }

    }

    public enum Birthdays {
        NONE,
        MOM,
        LIAM,
        DAD,
        JOHN;

        Birthdays() { }
    }
}
