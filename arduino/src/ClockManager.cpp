#include <Arduino.h>
#include <time.h>
#include "ClockManager.h"
#include <Adafruit_GFX.h>
#include <Adafruit_PCD8544.h>
//#include <time_impl.h>
#include <limits.h>

class Adafruit_PCD8544;

//https://en.wikipedia.org/wiki/C_date_and_time_functions
time_t     now;
struct tm  timeDate;


char indicator = ':';
const int daysOnMonth[12]  ={31,28,31,30,31,30,31,31,30,31,30,31};
const String monthShortNames[12]  = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
const String dayShortNames[7]  = {"Sun","Mon","Tue","Wed","Thr","Fri","Sat"};

ClockManager::ClockManager(Adafruit_PCD8544 *display)
{
    displayPtr = display;
    timeDate.tm_year = 2018;
}

void ClockManager::clockToScreen()
{
  //displayPtr->setTextSize(1);
  //displayPtr->setCursor(0,0);
  //displayPtr->print(asctime(&timeDate));
  //time(&now);
  //timeDate = *gmtime(&now);

  displayPtr->setCursor(6,16);
  displayPtr->setTextSize(2);
  displayPtr->print(formatDigits(timeDate.tm_hour));
  displayPtr->print(indicator);
  displayPtr->print(formatDigits(timeDate.tm_min));
  displayPtr->setTextSize(1);
  displayPtr->setCursor(72,23);
  displayPtr->println(formatDigits(timeDate.tm_sec));
  displayPtr->print(" ");
  displayPtr->print(dayShortStr(timeDate.tm_wday));
  displayPtr->print(" ");
  displayPtr->print(monthShortStr(timeDate.tm_mon));
  displayPtr->print(" ");
  displayPtr->print(timeDate.tm_mday);
}

String ClockManager::formatDigits(int num){
    char buffer[2];
    sprintf(buffer, "%02d", num); 
    return buffer;
}

void ClockManager::updateClock()
{
  if (indicator == ':')
  {
    //system_tick();
    indicator = ' ';
  }
  else{
    indicator = ':';
    timeDate.tm_sec++;
    if (timeDate.tm_sec == 60){
      timeDate.tm_sec = 0;
      timeDate.tm_min++;
      if (timeDate.tm_min == 60){
        timeDate.tm_min = 0;
        timeDate.tm_hour++;
        if (timeDate.tm_hour == 24){
          timeDate.tm_hour = 0;
          timeDate.tm_wday++;
          if (timeDate.tm_wday == 7) timeDate.tm_wday = 0;
          uint8_t monthDays = daysOnMonth[timeDate.tm_mon];
          boolean leap = ( ((timeDate.tm_year)>0) && !((timeDate.tm_year)%4) && ( ((timeDate.tm_year)%100) || !((timeDate.tm_year)%400) ) );
          if (timeDate.tm_mon == 1 && leap) monthDays++;
          timeDate.tm_mday++;  
          if (timeDate.tm_mday > monthDays){
            timeDate.tm_mday = 1;
            timeDate.tm_mon++;
            if (timeDate.tm_mon == 12){
              timeDate.tm_mon = 0;
              timeDate.tm_year++;   
            }
          }
        }
      }
    }
  }
}

void ClockManager::adjustClock(unsigned long epoch)
{
  //now = epoch;
  //timeDate = *gmtime(&now);
  //set_system_time(epoch);


  __secs_to_tm(epoch, &timeDate);
}

String ClockManager::monthShortStr(uint8_t month)
{
    return monthShortNames[month]; 
}

String ClockManager::dayShortStr(uint8_t day) 
{
   return dayShortNames[day];
}

/* 2000-03-01 (mod 400 year, immediately after feb29 */
#define LEAPOCH (946684800LL + 86400*(31+29))

#define DAYS_PER_400Y (146097) //365 * 400 + 97
#define DAYS_PER_100Y (36524)  //365 * 100 + 24
#define DAYS_PER_4Y   (14611)  //365 * 4   + 1

int ClockManager::__secs_to_tm(long long t, struct tm *tm)
{
	long long days, secs;
	int remdays, remsecs, remyears;
	int qc_cycles, c_cycles, q_cycles;
	int years, months;
	int wday, yday, leap;
	static const char days_in_month[] = {31,30,31,30,31,31,30,31,30,31,31,29};

	/* Reject time_t values whose year would overflow int */
	if (t < INT_MIN * 31622400LL || t > INT_MAX * 31622400LL)
		return -1;

	secs = t - LEAPOCH;
	days = secs / 86400;
	remsecs = secs % 86400;
	if (remsecs < 0) {
		remsecs += 86400;
		days--;
	}

	wday = (3+days)%7;
	if (wday < 0) wday += 7;

	qc_cycles = days / DAYS_PER_400Y;
	remdays = days % DAYS_PER_400Y;
	if (remdays < 0) {
		remdays += DAYS_PER_400Y;
		qc_cycles--;
	}

	c_cycles = remdays / DAYS_PER_100Y;
	if (c_cycles == 4) c_cycles--;
	remdays -= c_cycles * DAYS_PER_100Y;

	q_cycles = remdays / DAYS_PER_4Y;
	if (q_cycles == 25) q_cycles--;
	remdays -= q_cycles * DAYS_PER_4Y;

	remyears = remdays / 365;
	if (remyears == 4) remyears--;
	remdays -= remyears * 365;

	leap = !remyears && (q_cycles || !c_cycles);
	yday = remdays + 31 + 28 + leap;
	if (yday >= 365+leap) yday -= 365+leap;

	years = remyears + 4*q_cycles + 100*c_cycles + 400*qc_cycles;

	for (months=0; days_in_month[months] <= remdays; months++)
		remdays -= days_in_month[months];

	if (years+100 > INT_MAX || years+100 < INT_MIN)
		return -1;

	tm->tm_year = years + 100;
	tm->tm_mon = months + 2;
	if (tm->tm_mon >= 12) {
		tm->tm_mon -=12;
		tm->tm_year++;
	}
	tm->tm_mday = remdays + 1;
	tm->tm_wday = wday;
	tm->tm_yday = yday;

	tm->tm_hour = remsecs / 3600;
	tm->tm_min = remsecs / 60 % 60;
	tm->tm_sec = remsecs % 60;

	return 0;
}