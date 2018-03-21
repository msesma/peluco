package eu.sesma.peluco.ui;

public class EpochConverter {

    private static class Tm {
        int tm_sec;         /* seconds,  range 0 to 59          */
        int tm_min;         /* minutes, range 0 to 59           */
        int tm_hour;        /* hours, range 0 to 23             */
        int tm_mday;        /* day of the month, range 1 to 31  */
        int tm_mon;         /* month, range 0 to 11             */
        int tm_year;        /* The number of years since 1900   */
        int tm_wday;        /* day of the week, range 0 to 6    */
        int tm_yday;        /* day in the year, range 0 to 365  */
        int tm_isdst;       /* daylight saving time             */
    }

    private final static int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private Tm tm = new Tm();
    private final static int DAY_SECONDS = 24 * 60 * 60;
    private final static int JAN_1_1972 = 365 * 2 * DAY_SECONDS;
    private final static int JAN_1_1972_WDAY = 6;
    private final static int LEAP_CYCLE_DAYS = 365 * 4 + 1;

    public void convert(long epoch) {
        if (epoch < JAN_1_1972) {
            return;
        }
        //move to the first leap year cycle after 1970
        epoch -= JAN_1_1972;

        long daysRemaining = setTime(epoch, tm);

        daysRemaining = setYear(daysRemaining, tm);

        setDate(daysRemaining, tm);

        setWeekDay(epoch, tm);

        setDst(tm);
    }

    private long setTime(long epoch, Tm tm) {
        int timeSeconds = (int) (epoch % DAY_SECONDS);
        tm.tm_hour = timeSeconds / 3600;
        tm.tm_min = (timeSeconds % 3600) / 60;
        tm.tm_sec = (timeSeconds % 3600) % 60;

        return epoch / DAY_SECONDS;
    }

    private long setYear(long daysRemaining, Tm tm) {
        // Set the year to 1971 plus the 4 year leap cycles since
        int leapCycles = (int) (daysRemaining / LEAP_CYCLE_DAYS);
        tm.tm_year = 1971 + leapCycles * 4;
        daysRemaining = daysRemaining % LEAP_CYCLE_DAYS;

        // Add the completed years of the current leap cycle
        if (daysRemaining > 366) {
            tm.tm_year++;
            daysRemaining -= 366;
            tm.tm_year += daysRemaining / 365;
            daysRemaining = daysRemaining % 365;
        }
        tm.tm_yday = (int) daysRemaining;
        return daysRemaining;
    }

    private void setDate(long daysRemaining, Tm tm) {
        tm.tm_mon = 1;
        for (int monthDays : DAYS_IN_MONTH) {
            // February
            if (monthDays == 28 && tm.tm_year % 4 == 0) {
                monthDays++;
            }

            if (daysRemaining > monthDays) {
                tm.tm_mon++;
                daysRemaining -= monthDays;
            } else {
                break;
            }
        }
        tm.tm_mday = (int) daysRemaining;
    }

    private void setWeekDay(long epoch, Tm tm) {
        tm.tm_wday = (int) (epoch + JAN_1_1972_WDAY) % 7;
    }

    private void setDst(Tm tm) {
        //January, february, november and december are out.
        if (tm.tm_mon < 3 || tm.tm_mon > 10) {
            tm.tm_isdst = 0;
            return;
        }
        //April to september are in
        if (tm.tm_mon > 3 && tm.tm_mon < 11) {
            tm.tm_isdst = 1;
            return;
        }
        int previousSunday = tm.tm_mday - tm.tm_wday;
        //In march, we are DST if our previous sunday was on or after the 25th.
        if (tm.tm_mon == 3) {
            tm.tm_isdst = previousSunday >= 25 ? 1 : 0;
            return;
        }
        //In october, we aren´ DST if our previous sunday was on or after the 25th.
        tm.tm_isdst = previousSunday >= 25 ? 0 : 1;
    }
}
