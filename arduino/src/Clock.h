#ifndef _CLOCK_H
#define _CLOCK_H

#include <Arduino.h>
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;

class Clock {
public:

    Clock(Adafruit_PCD8544 *display);

    void clockToScreen();

    void updateClock();

private:    

    Adafruit_PCD8544 *displayPtr;

    String monthShortStr(uint8_t month);

    String dayShortStr(uint8_t day);

};

#endif