#include <ctime>
#include <iostream>

#include <boost/date_time/gregorian/gregorian.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


//==============================================================================
//! Convert date part of Unix timestamp (time_t) to boost date
//!
//! time_t contains the number of seconds since 00:00 hours, Jan 1, 1970 UTC
//! 
//! \param time unix timestamp to convert
//! \return boost date of the timestamp's date
//
boost::gregorian::date to_bdate(time_t time) {
  return boost::posix_time::from_time_t(time).date();
}

//==============================================================================
//! Convert boost::gregorian::date to Unix timestamp (time_t)
//!
//! Converts a boost date to a Unix timestamp pointing to 00:00 hours, 0 seconds
//! of the day given by date.
//!
//! time_t contains the number of seconds since 00:00 hours, Jan 1, 1970 UTC.
//!
//! \param date to convert
//! \return unix timestamp 
time_t to_time_t(const boost::gregorian::date& date ){
  using namespace boost::posix_time;
  static ptime epoch(boost::gregorian::date(1970, 1, 1));
  time_duration::sec_type secs = (ptime(date,seconds(0)) - epoch).total_seconds();
  return time_t(secs);
}



using namespace std;
namespace bd = boost::gregorian;
namespace pt = boost::posix_time;

/*
int main() {

  // get current time
  time_t nowtime;
  time(&nowtime);
  cout << "Unix timestamp of now:          " << nowtime << endl;

  // conversion time_t -> boost date
  bd::date today(to_bdate(nowtime));
  cout << "Timestamp converted to date:   " << today << endl;

  // conversion boost date -> time_t
  time_t thismorning = to_time_t(today);
  cout << "Date converted back to time_t:  " << thismorning << endl;

}
*/
