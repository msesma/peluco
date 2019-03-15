#include <mbed.h>
#include "N5110.h"

//    VCC,SCE,RST,D/C,MOSI,SCLK,LED
//N5110 lcd(p7,p8,p9,p10,p11,p13,p21);  // LPC1768 - pwr from GPIO
N5110 lcd(P0_8,P0_9,P0_10,P0_11,P0_13,P0_21);  // LPC1768 - powered from +3V3 - JP1 in 2/3 position
//N5110 lcd(PTC9,PTC0,PTC7,PTD2,PTD1,PTC11);  // K64F - pwr from 3V3


int main() {

  // put your setup code here, to run once:

  lcd.init();
  lcd.setContrast(0.4);
  lcd.normalMode();      // normal colour mode
  lcd.setBrightness(0.5); // put LED backlight on 50%
  lcd.clear();
  lcd.printString("Hello, World!",0,0);

  while(1) {
    // put your main code here, to run repeatedly:
  }
}