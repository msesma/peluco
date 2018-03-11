#ifndef _ENERGY_H
#define _ENERGY_H

#include <Arduino.h>
#include <avr/sleep.h>
#include <avr/power.h>

class Energy {
public:

    void setInterrupts();

    void sleepNow();

private:    

};

#endif