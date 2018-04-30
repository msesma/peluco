#include <Arduino.h>
#include <time.h>
#include "ClockManager.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;

//https://en.wikipedia.org/wiki/C_date_and_time_functions
struct tm  timeDate;

char indicator = ':';
const int daysInMonth[12]   ={31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
const String monthShortNames[12]  = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
const String dayShortNames[7]  = {"Sun","Mon","Tue","Wed","Thr","Fri","Sat"};

#define DAY_SECONDS (86400) //24 * 60 * 60
#define JAN_1_1972 (365 * 2 * DAY_SECONDS)
#define JAN_1_1972_WDAY (6)
#define LEAP_CYCLE_DAYS (365 * 4 + 1)

String debug = "";

ClockManager::ClockManager(Adafruit_PCD8544 *display)
{
    displayPtr = display;
}

void ClockManager::clockToScreen()
{
  //DEBUG
  //displayPtr->setTextSize(1);
  //displayPtr->setCursor(0,0);
  //displayPtr->print(debug);

  displayPtr->setCursor(6,16);
  displayPtr->setTextSize(2);
  displayPtr->print(formatDigits(timeDate.tm_hour));
  displayPtr->print(indicator);
  displayPtr->print(formatDigits(timeDate.tm_min));
  displayPtr->setTextSize(1);
  displayPtr->setCursor(72,23);
  displayPtr->println(formatDigits(timeDate.tm_sec));
  displayPtr->print(" ");
  displayPtr->print(dayShortStr(timeDate.tm_wday));
  displayPtr->print(" ");
  displayPtr->print(monthShortStr(timeDate.tm_mon));
  displayPtr->print(" ");
  displayPtr->print(timeDate.tm_mday);
}

String ClockManager::formatDigits(int num){
    char buffer[2];
    sprintf(buffer, "%02d", num); 
    return buffer;
}

void ClockManager::updateClock()
{
  if (indicator == ':')
  {
    //system_tick();
    indicator = ' ';
  }
  else{
    indicator = ':';
    timeDate.tm_sec++;
    if (timeDate.tm_sec == 60){
      timeDate.tm_sec = 0;
      timeDate.tm_min++;
      if (timeDate.tm_min == 60){
        timeDate.tm_min = 0;
        timeDate.tm_hour++;
        if (timeDate.tm_hour == 24){
          timeDate.tm_hour = 0;
          timeDate.tm_wday++;
          if (timeDate.tm_wday == 7) timeDate.tm_wday = 0;
          uint8_t monthDays = daysInMonth[timeDate.tm_mon];
          boolean leap = ( ((timeDate.tm_year)>0) && !((timeDate.tm_year)%4) && ( ((timeDate.tm_year)%100) || !((timeDate.tm_year)%400) ) );
          if (timeDate.tm_mon == 1 && leap) monthDays++;
          timeDate.tm_mday++;  
          if (timeDate.tm_mday > monthDays){
            timeDate.tm_mday = 1;
            timeDate.tm_mon++;
            if (timeDate.tm_mon == 12){
              timeDate.tm_mon = 0;
              timeDate.tm_year++;   
            }
          }
        }
      }
    }
  }
}

void ClockManager::adjustClock(unsigned long epoch)
{
  //now = epoch;
  //timeDate = *gmtime(&now);
  //set_system_time(epoch);


  convert(epoch, &timeDate);
}

String ClockManager::monthShortStr(uint8_t month)
{
    return monthShortNames[month]; 
}

String ClockManager::dayShortStr(uint8_t day) 
{
   return dayShortNames[day];
}

void ClockManager::convert(long epoch, struct tm *timeDate) {
        if (epoch < JAN_1_1972) {
            return;
        }
        //move to the first leap year cycle after 1970
        epoch -= JAN_1_1972;

        long daysRemaining = setTime(epoch, timeDate);

        setWeekDay(daysRemaining, timeDate);

        daysRemaining = setYear(daysRemaining, timeDate);

        setDate(daysRemaining, timeDate);

        setDst(timeDate);
    }

    long ClockManager::setTime(long epoch, struct tm *timeDate) {
        long timeSeconds = (long) (epoch % DAY_SECONDS);
        timeDate->tm_hour = timeSeconds / 3600;
        timeDate->tm_min = (timeSeconds % 3600) / 60;
        timeDate->tm_sec = (timeSeconds % 3600) % 60;

        return epoch / DAY_SECONDS;
    }

    long ClockManager::setYear(long daysRemaining, struct tm *timeDate) {
        // Set the year to 1971 plus the 4 year leap cycles since
        int leapCycles = (int) (daysRemaining / LEAP_CYCLE_DAYS);
        timeDate->tm_year = 1971 + leapCycles * 4;
        daysRemaining = daysRemaining % LEAP_CYCLE_DAYS;

        // Add the completed years of the current leap cycle
        if (daysRemaining > 366) {
            timeDate->tm_year++;
            daysRemaining -= 366;
            timeDate->tm_year += daysRemaining / 365 + 1; // Add one for the current year
            daysRemaining = daysRemaining % 365;
        }
        timeDate->tm_yday = (int) ++daysRemaining; //increase in one for today
        return daysRemaining;
    }

    void ClockManager::setDate(long daysRemaining, struct tm *timeDate) {
        timeDate->tm_mon = 0;
        for (int monthDays : daysInMonth) {
            // February
            if (monthDays == 28 && timeDate->tm_year % 4 == 0) {
                monthDays++;
            }

            if (daysRemaining > monthDays) {
                timeDate->tm_mon++;
                daysRemaining -= monthDays;
            } else {
                break;
            }
        }
        timeDate->tm_mday = (int) daysRemaining;
    }

    void ClockManager::setWeekDay(long days, struct tm *timeDate) {
        timeDate->tm_wday = (int) (days + JAN_1_1972_WDAY) % 7;
    }

    void ClockManager::setDst(struct tm *timeDate) {
        //January, february, november and december are out.
        if (timeDate->tm_mon < 3 || timeDate->tm_mon > 10) {
            timeDate->tm_isdst = 0;
            return;
        }
        //April to september are in
        if (timeDate->tm_mon > 3 && timeDate->tm_mon < 10) {
            timeDate->tm_isdst = 1;
            return;
        }
        int previousSunday = timeDate->tm_mday - timeDate->tm_wday;
        //In march, we are DST if our previous sunday was on or after the 25th.
        if (timeDate->tm_mon == 3) {
            timeDate->tm_isdst = previousSunday >= 25 ? 1 : 0;
            return;
        }
        //In october, we aren´ DST if our previous sunday was on or after the 25th.
        timeDate->tm_isdst = previousSunday >= 25 ? 0 : 1;
    }
