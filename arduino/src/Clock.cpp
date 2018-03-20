#include <Arduino.h>
#include <time.h>
#include "Clock.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;

//https://en.wikipedia.org/wiki/C_date_and_time_functions
time_t     now;
struct tm  timeDate;


char indicator = ':';
const int daysOnMonth[12]  ={31,28,31,30,31,30,31,31,30,31,30,31};
const String monthShortNames[12]  = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
const String dayShortNames[7]  = {"Sun","Mon","Tue","Wed","Thr","Fri","Sat"};

Clock::Clock(Adafruit_PCD8544 *display)
{
    displayPtr = display;
    timeDate.tm_year = 2018;
}

void Clock::clockToScreen()
{
  //displayPtr->setTextSize(1);
  //displayPtr->setCursor(0,0);
  //displayPtr->print(asctime(&timeDate));
  //time(&now);
  //timeDate = *gmtime(&now);

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

String Clock::formatDigits(int num){
    char buffer[2];
    sprintf(buffer, "%02d", num); 
    return buffer;
}

void Clock::updateClock()
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
          uint8_t monthDays = daysOnMonth[timeDate.tm_mon];
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

void Clock::adjustClock(long epoch)
{
  now = epoch;
  timeDate = *gmtime(&now);
  //set_system_time(epoch);
}

String Clock::monthShortStr(uint8_t month)
{
    return monthShortNames[month]; 
}

String Clock::dayShortStr(uint8_t day) 
{
   return dayShortNames[day];
}