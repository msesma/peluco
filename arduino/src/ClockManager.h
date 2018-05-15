#ifndef _CLOCK_H
#define _CLOCK_H

#include <Arduino.h>
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>
#include <time.h>

class Adafruit_PCD8544;

class ClockManager 
{
public:

    ClockManager(Adafruit_PCD8544 *display);

    void clockToScreen();

    void updateClock();

    void adjustClock(unsigned long epoch);

    void log();

private:    

    Adafruit_PCD8544 *displayPtr;

    String monthShortStr(uint8_t month);

    String dayShortStr(uint8_t day);

    String formatDigits(int num);

    void convert(long epoch, struct tm *timeDate);

    long setTime(long epoch, struct tm *timeDate);

    long setYear(long daysRemaining, struct tm *timeDate);

    void setDate(long daysRemaining, struct tm *timeDate);

    void setWeekDay(long days, struct tm *timeDate);

    void setDst(struct tm *timeDate);

    void setCalendar();

};

#endif