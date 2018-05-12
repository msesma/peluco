#include <Arduino.h>
#include "Parser.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>
//https://arduinojson.org/doc/
//https://github.com/bblanchon/ArduinoJson
#include <ArduinoJson.h>


class Adafruit_PCD8544;

String logData = "";

Parser::Parser(Adafruit_PCD8544 *display)
{
    displayPtr = display;
}

long Parser::onReceive(String json)
{
    if (json.length() > 0) 
    {
       StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.parseObject(json);
        unsigned long time = root["time"];
        logData = json;
        return time;
    }
    return 0;
}

void Parser::log()
{
    displayPtr->setTextSize(1);
    displayPtr->setCursor(0,0);
    displayPtr->println(logData);
}

