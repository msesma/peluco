
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>
#include "ClockManager.h"
#include "Energy.h"
#include "Parser.h"

//Peluco Beetle BLE
// pin D2 - Serial clock out (SCLK)
// pin D3 - Serial data out (DIN)
// pin D4 - Data/Command select (D/C)
// pin D5 - LCD chip select (CS)
// pin A0 - LCD reset (RST)

Adafruit_PCD8544 display = Adafruit_PCD8544(2, 3, 4, 5, 14);

ClockManager clockManager = ClockManager(&display);
Parser parser = Parser(&display);
Energy energy = Energy();

void setup()
{
  initDisplay();
  energy.setInterrupts();
  clockManager.adjustClock(1525110385);
  Serial.begin(9600);
}

void loop() 
{
  display.clearDisplay();
  if (Serial.available())
  {  
    String json = Serial.readString();
    Serial.print(json);//send what has been received for testing
    Serial.println();   //print line feed character
    unsigned long time = parser.onReceive(json);
    Serial.print("Time: " + String(time));//send parsed time for testing
    Serial.println();   //print line feed character
    if (time !=0 ) {
      String decodeTime = clockManager.adjustClock(time);
      Serial.print("Time decoded: " + decodeTime);//send decoded time for testing
      Serial.println();   //print line feed character
    }
  }
  clockManager.clockToScreen();
  //parser.log();
  //clockManager.log();
  display.display();
  energy.sleepNow();
}

ISR(TIMER1_COMPA_vect) // timer compare interrupt service routine
{
  clockManager.updateClock();
}

void initDisplay()
{
  display.begin(50); //initialize display with a contrast of 50
  delay(500);
  display.setTextColor(BLACK);
}
