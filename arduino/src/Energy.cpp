#include "Energy.h"

void Energy::setInterrupts()
{
    // initialize timer1 See http://letsmakerobots.com/node/28278
  noInterrupts();           // disable all interrupts
  TCCR1A = 0;
  TCCR1B = 0;
  TCNT1  = 0;

  //FIXME this works but theoretically it should be half this number for this frequencies and prescaler
  OCR1A = 62500;            // compare match register 16MHz/256/2Hz
  TCCR1B |= (1 << WGM12);   // CTC mode
  TCCR1B |= (1 << CS12);    // 256 prescaler 
  TIMSK1 |= (1 << OCIE1A);  // enable timer compare interrupt
  interrupts();             // enable all interrupts
}

void Energy::sleepNow()         // here we put the arduino to sleep
{
//  http://donalmorrissey.blogspot.com.es/2011/11/sleeping-arduino-part-4-wake-up-via.html
     /* The 5 different modes are:
     *     SLEEP_MODE_IDLE         -the least power savings
     *     SLEEP_MODE_ADC
     *     SLEEP_MODE_PWR_SAVE
     *     SLEEP_MODE_STANDBY
     *     SLEEP_MODE_PWR_DOWN     -the most power savings
     */
  //FIXME only SLEEP_MODE_IDLE is supported. Maybe using external interrupts driven by a autogenerated PWM...
  set_sleep_mode(SLEEP_MODE_IDLE);
  sleep_enable();
  /* Disable all of the unused peripherals. This will reduce power
   * consumption further and, more importantly, some of these
   * peripherals may generate interrupts that will wake our Arduino from
   * sleep!
   */
  power_adc_disable();
  power_spi_disable();
  power_timer0_disable();
  power_timer2_disable();
  power_twi_disable();  
  /* Now enter sleep mode. */
  sleep_mode();
  /* The program will continue from here after the timer timeout*/
  sleep_disable(); /* First thing to do is disable sleep. */
  /* Re-enable the peripherals. */
  power_all_enable();
}

