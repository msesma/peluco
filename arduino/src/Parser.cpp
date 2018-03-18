#include <Arduino.h>
#include "Parser.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>

class Adafruit_PCD8544;


Parser::Parser(Adafruit_PCD8544 *display)
{
    displayPtr = display;
}

String Parser::onReceive(String data)
{
    displayPtr->setTextSize(1);
    displayPtr->setCursor(0,0);
    displayPtr->println(data);
    return data;
}