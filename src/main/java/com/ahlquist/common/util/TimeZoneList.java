
package com.ahlquist.common.util;

import java.util.Hashtable;
import java.lang.Number.*;

/**
* 
*/
public class TimeZoneList {
	Hashtable hash = new Hashtable();

	public TimeZoneList() {
		hash.put("-12", "GMT -12:00 Dateline: Eniwetok, Kwajalein");
		hash.put("-11", "GMT -11:00 Samoa: Midway Island, Samoa");
		hash.put("-10", "GMT -10:00 Hawaiian: Hawaii");
		hash.put("-9", "GMT -09:00 Alaskan: Alaska");
		hash.put("-8", "GMT -08:00 Pacific: Pacific Time(U.S. & Canada)");
		hash.put("-7", "GMT -07:00 Mountain: Mountain Time(U.S. & Canada)");
		hash.put("-6", "GMT -06:00 Central: Central Time(U.S. & Canada)");
		hash.put("-5", "GMT -05:00 Eastern: Eastern Time(U.S. & Canada)");
		hash.put("-4", "GMT -04:00 Atlantic: Atlantic Time (Canada)");
		hash.put("-3", "GMT -03:00 E. South America: Brasilia");
		hash.put("-2", "GMT -02:00 Mid-Atlantic: Mid-Atlantic");
		hash.put("-1", "GMT -01:00 Azores: Azores, Cape Verde Is.");
		hash.put("0", "GMT Greenwich Mean Time: Dublin, Lisbon, London");
		hash.put("1", "GMT +01:00 W. Europe: Amsterdam, Berlin");
		hash.put("2", "GMT +02:00 E. Europe: E. Europe");
		hash.put("3", "GMT +03:00 Russian: Moscow, St. Petersburg");
		hash.put("4", "GMT +04:00 Arabian: Abu Dhabi, Muscat");
		hash.put("5", "GMT +05:30 India: Bombay, New Delhi");
		hash.put("6", "GMT +06:00 loCentral Asia: Almaty, Dhakacation");
		hash.put("7", "GMT +07:00 Bangkok: Bangkok");
		hash.put("8", "GMT +08:00 China: Beijing, Hong Kong, Urumqi");
		hash.put("9", "GMT +09:00 Korea: Seoul");
		hash.put("10", "GMT +10:00 E. Australia: Canberra, Sydney");
		hash.put("11", "GMT +11:00 Central Pacific: Magadan, Sol. Is.");
		hash.put("12", "GMT +12:00 New Zealand: Auckland, Wellington");
	}

	public String getDescription(int index) {
		return (String) hash.get(new Integer(index).toString());
	}

}