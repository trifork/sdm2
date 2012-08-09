/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package dk.nsi.sdm4.core.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;


public class Dates
{
	public static final DateTime THE_END_OF_TIME = new DateTime().withYear(2999).withMonthOfYear(12).withDayOfMonth(31);

	public static final DateTimeFormatter yyyyMMddHHmm = DateTimeFormat.forPattern("yyyyMMddHHmm");
	public static final DateTimeFormatter yyyy_MM_dd = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	public static String toDateStringISO8601(Date date)
    {
	    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        return formatter.print(date.getTime());
    }

	public static String toFilenameDatetime(Date date)
	{
	    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH-mm-ss");
		return formatter.print(date.getTime());
	}

	public static Date toDate(int year, int month, int date)
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, date);
		return cal.getTime();
	}

	public static Date toDate(int year, int month, int date, int hours, int minutes, int secs)
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, date, hours, minutes, secs);
		return cal.getTime();
	}

    @Deprecated
	public static Date toCalendar(java.sql.Date date)
	{
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(date.getTime());
		return cal.getTime();
	}

    @Deprecated
	public static String toSqlDate(Date date)
	{
		Preconditions.checkNotNull(date);
	    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		return df.print(date.getTime());
	}
}
