#include <Arduino.h>
#include "Parser.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>
//https://arduinojson.org/doc/
#include <ArduinoJson.h>

class Adafruit_PCD8544;

String logData = "";

Parser::Parser(Adafruit_PCD8544 *display)
{
    displayPtr = display;
}

String Parser::onReceive(String data)
{
    if (data.length() > 0) 
    {
       logData = data;
    }
    return data;
}

void Parser::log()
{
    displayPtr->setTextSize(1);
    displayPtr->setCursor(0,0);
    displayPtr->println(logData);
}