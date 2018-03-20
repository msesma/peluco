
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>
#include "Clock.h"
#include "Energy.h"
#include "Parser.h"

//Peluco Beetle BLE
// pin D2 - Serial clock out (SCLK)
// pin D3 - Serial data out (DIN)
// pin D4 - Data/Command select (D/C)
// pin D5 - LCD chip select (CS)
// pin A0 - LCD reset (RST)

Adafruit_PCD8544 display = Adafruit_PCD8544(2, 3, 4, 5, 14);

Clock clock = Clock(&display);
Parser parser = Parser(&display);
Energy energy = Energy();

void setup()
{
  initDisplay();
  energy.setInterrupts();
  clock.adjustClock(1521573389); //TODO Remove
  Serial.begin(9600);
}

void loop() 
{
  display.clearDisplay();
  if (Serial.available())
  {  
    String json = Serial.readString();
    //Serial.print(data);
    long time = parser.onReceive(json);
    if (time !=0 )
      clock.adjustClock(time);
  }
  parser.log();
  clock.clockToScreen();
  display.display();
  energy.sleepNow();
}

ISR(TIMER1_COMPA_vect) // timer compare interrupt service routine
{
  clock.updateClock();
}

void initDisplay()
{
  display.begin(50); //initialize display with a contrast of 50
  delay(500);
  display.setTextColor(BLACK);
}
