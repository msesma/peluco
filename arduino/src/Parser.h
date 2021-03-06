#ifndef _BT_H
#define _BT_H

#include <Arduino.h>
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;

class Parser {
public:

    Parser(Adafruit_PCD8544 *display);

    long onReceive(String json);

    void log();

private:

    Adafruit_PCD8544 *displayPtr;

};

#endif