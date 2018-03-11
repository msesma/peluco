#ifndef _MAIN_H
#define _MAIN_H

#include <Arduino.h>
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;

class Clock {
public:

    Clock(Adafruit_PCD8544 *display);

    String monthShortStr(uint8_t month);

    String dayShortStr(uint8_t day);

private:    

    Adafruit_PCD8544 *_display;

};

#endif