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

private:    

    Adafruit_PCD8544 *displayPtr;

    String monthShortStr(uint8_t month);

    String dayShortStr(uint8_t day);

    String formatDigits(int num);

    int __secs_to_tm(long long t, struct tm *tm);

};

#endif