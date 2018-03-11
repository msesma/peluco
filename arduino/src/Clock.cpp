#include <Arduino.h>
#include "Clock.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>


//const int daysOnMonth[]  ={31,28,31,30,31,30,31,31,30,31,30,31};
const String monthShortNames[]  = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
const String dayShortNames[]  = {"Mon","Tue","Wed","Thr","Fri","Sat","Sun"};

Clock::Clock(Adafruit_PCD8544 *display)
{
    _display = display;
}

String Clock::monthShortStr(uint8_t month)
{
    return monthShortNames[month]; 
}

String Clock::dayShortStr(uint8_t day) 
{
   return dayShortNames[day];
}