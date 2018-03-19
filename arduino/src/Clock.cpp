#include <Arduino.h>
#include <time.h>
#include "Clock.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;

int seconds =0;
int minutes =0;
int hours =0;
int dayOfWeek =0;
int curDay =1;
int curMonth =0;
int curYear =2018;

//https://en.wikipedia.org/wiki/C_date_and_time_functions
time_t     now;
struct tm  timeDate;

char indicator = ':';
const int daysOnMonth[12]  ={31,28,31,30,31,30,31,31,30,31,30,31};
const String monthShortNames[12]  = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
const String dayShortNames[7]  = {"Mon","Tue","Wed","Thr","Fri","Sat","Sun"};


Clock::Clock(Adafruit_PCD8544 *display)
{
    displayPtr = display;
}

void Clock::clockToScreen()
{
  timeDate* = localtime(&now);
  displayPtr->setCursor(6,16);
  displayPtr->setTextSize(2);
  displayPtr->print(formatDigits(hours));
  displayPtr->print(indicator);
  displayPtr->print(formatDigits(minutes));
  displayPtr->setTextSize(1);
  displayPtr->setCursor(72,23);
  displayPtr->println(formatDigits(seconds));
  displayPtr->print(" ");
  displayPtr->print(dayShortStr(dayOfWeek));
  displayPtr->print(" ");
  displayPtr->print(monthShortStr(curMonth));
  displayPtr->print(" ");
  displayPtr->print(curDay);
}

String Clock::formatDigits(int num){
    char buffer[2];
    sprintf(buffer, "%02d", num); 
    return buffer;
}

void Clock::updateClock()
{
  if (indicator == ':')
    indicator = ' ';
  else{
    indicator = ':';
    seconds++;
    if (seconds == 60){
      seconds = 0;
      minutes++;
      if (minutes == 60){
        minutes = 0;
        hours++;
        if (hours == 24){
          hours = 0;
          dayOfWeek++;
          if (dayOfWeek == 7) dayOfWeek = 0;
          uint8_t monthDays = daysOnMonth[curMonth];
          boolean leap = ( ((curYear)>0) && !((curYear)%4) && ( ((curYear)%100) || !((curYear)%400) ) );
          if (curMonth == 1 && leap) monthDays++;
          curDay++;  
          if (curDay > monthDays){
            curDay = 1;
            curMonth++;
            if (curMonth == 12){
              curMonth = 0;
              curYear++;   
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
}

String Clock::monthShortStr(uint8_t month)
{
    return monthShortNames[month]; 
}

String Clock::dayShortStr(uint8_t day) 
{
   return dayShortNames[day];
}