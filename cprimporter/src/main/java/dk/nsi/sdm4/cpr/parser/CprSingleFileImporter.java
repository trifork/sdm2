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


package dk.nsi.sdm4.cpr.parser;

import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.cpr.parser.models.*;
import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dk.nsi.sdm4.core.util.Dates.yyyyMMddHHmm;
import static dk.nsi.sdm4.core.util.Dates.yyyy_MM_dd;

/**
 * This class parses a single file from a cpr dump.
 * It acts as a utility class for the CPRParser. It does not implements the Parser interface.
 * The class was called CPRParser in SDM3
 */
public class CprSingleFileImporter
{
	private static final String FILE_ENCODING = "ISO-8859-1";

	private static final Logger logger = Logger.getLogger(CprSingleFileImporter.class);

	private static final int END_RECORD = 999;
	private static final String EMPTY_DATE_STRING = "000000000000";

	static boolean haltOnDateErrors = true;

	private static final Pattern datePattern = Pattern.compile("([\\d]{4})-([\\d]{2})-([\\d]{2})");
	private static final Pattern timestampPattern = Pattern.compile("([\\d]{4})([\\d]{2})([\\d]{2})([\\d]{2})([\\d]{2})");

	public CPRDataset parse(File f) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), FILE_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open file " + f.getAbsolutePath(), e);
		}

		try
		{
			return parseFileContents(reader);
		} catch (IOException e) {
			throw new RuntimeException("Could not read from file " + f.getAbsolutePath(), e);
		} finally
		{
			try {
				reader.close();
			} catch (IOException e) {
				logger.warn("Could not close reader for file " + f.getAbsolutePath(), e);
			}
		}
	}


	private CPRDataset parseFileContents(BufferedReader reader) throws IOException, ParserException
	{
		boolean endRecordReached = false;
		CPRDataset cpr = new CPRDataset();

		while (reader.ready())
		{
			String line = reader.readLine();
			if (line.length() > 0)
			{
				int recordType = getRecordType(line);
				if (recordType == END_RECORD)
				{
					endRecordReached = true;
				}
				else if (endRecordReached)
				{
					throw new ParserException("Slut-record midt i cpr-filen");
				}
				else
				{
					parseLine(recordType, line, cpr);
				}
			}
		}

		if (!endRecordReached)
		{
			throw new ParserException("Slut-record mangler i cpr-filen");
		}

		return cpr;
	}


	void parseLine(int recordType, String line, CPRDataset cpr)
	{
		// TODO: Make constants for these magic numbers.

		switch (recordType)
		{
		case 0:
			cpr.setValidFrom(getValidFrom(line));
			break;
		case 1:
			cpr.addEntity(personoplysninger(line));
			break;
		case 3:
			cpr.addEntity(klarskriftadresse(line));
			break;
		case 4:
			String beskyttelseskode = cut(line, 13, 17);
			if (beskyttelseskode.equals("0001"))
			{
				cpr.addEntity(navneBeskyttelse(line));
			}
			break;
		case 8:
			cpr.addEntity(navneoplysninger(line));
			break;
		case 14:
			cpr.addEntity(barnRelation(line));
			break;
		case 16:
			cpr.addEntity(foraeldreMyndighedRelation(line));
			break;
		case 17:
			cpr.addEntity(umyndiggoerelseVaergeRelation(line));
			break;
		}
	}


	protected UmyndiggoerelseVaergeRelation umyndiggoerelseVaergeRelation(String line)
	{
		UmyndiggoerelseVaergeRelation u = new UmyndiggoerelseVaergeRelation();
		u.setCpr(cut(line, 3, 13));
		u.setUmyndigStartDato(parseDate(yyyy_MM_dd, line, 13, 23));
		u.setUmyndigStartDatoMarkering(cut(line, 23, 24));
		u.setUmyndigSletteDato(parseDate(yyyy_MM_dd, line, 24, 34));
		u.setType(cut(line, 34, 38));
		u.setRelationCpr(cut(line, 38, 48));
		u.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		u.setVaergesNavn(cut(line, 58, 92).trim());
		u.setVaergesNavnStartDato(parseDate(yyyy_MM_dd, line, 92, 102));
		u.setRelationsTekst1(cut(line, 102, 136).trim());
		u.setRelationsTekst2(cut(line, 136, 170).trim());
		u.setRelationsTekst3(cut(line, 170, 204).trim());
		u.setRelationsTekst4(cut(line, 204, 238).trim());
		u.setRelationsTekst5(cut(line, 238, 272).trim());
		return u;
	}


	protected ForaeldreMyndighedRelation foraeldreMyndighedRelation(String line)
	{
		ForaeldreMyndighedRelation f = new ForaeldreMyndighedRelation();
		f.setCpr(cut(line, 3, 13));
		f.setType(cut(line, 13, 17));
		f.setForaeldreMyndighedStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		f.setForaeldreMyndighedMarkering(cut(line, 27, 28));
		f.setForaeldreMyndighedSlettedato(parseDate(yyyy_MM_dd, line, 28, 38));
		f.setRelationCpr(cut(line, 38, 48));
		f.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		return f;
	}


	protected BarnRelation barnRelation(String line)
	{
		BarnRelation b = new BarnRelation();
		b.setCpr(cut(line, 3, 13));
		b.setBarnCpr(cut(line, 13, 23));
		return b;
	}


	protected Navneoplysninger navneoplysninger(String line)
	{
		Navneoplysninger n = new Navneoplysninger();
		n.setCpr(cut(line, 3, 13));
		n.setFornavn(cut(line, 13, 63).trim());
		n.setFornavnMarkering(cut(line, 63, 64));
		n.setMellemnavn(cut(line, 64, 104).trim());
		n.setMellemnavnMarkering(cut(line, 104, 105));
		n.setEfternavn(cut(line, 105, 145).trim());
		n.setEfternavnMarkering(cut(line, 145, 146));
		n.setStartDato(parseDate(yyyyMMddHHmm, line, 146, 158));
		n.setStartDatoMarkering(cut(line, 158, 159));
		n.setAdresseringsNavn(cut(line, 159, 193).trim());
		return n;
	}


	protected NavneBeskyttelse navneBeskyttelse(String line)
	{
		NavneBeskyttelse n = new NavneBeskyttelse();
		n.setCpr(cut(line, 3, 13));
		n.setNavneBeskyttelseStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		n.setNavneBeskyttelseSletteDato(parseDate(yyyy_MM_dd, line, 27, 37));
		return n;
	}


	protected Klarskriftadresse klarskriftadresse(String line)
	{
		Klarskriftadresse k = new Klarskriftadresse();

		k.setCpr(cut(line, 3, 13));
		k.setNavnTilAdressering(cut(line, 13, 47).trim());
		k.setCoNavn(cut(line, 47, 81).trim());
		k.setLokalitet(cut(line, 81, 115).trim());
		k.setByNavn(cut(line, 149, 183).trim());
		k.setPostNummer(parseLong(line, 183, 187));
		k.setPostDistrikt(cut(line, 187, 207).trim());
		k.setKommuneKode(parseLong(line, 207, 211));
		k.setVejKode(parseLong(line, 211, 215));
		k.setHusNummer(removeLeadingZeros(cut(line, 215, 219).trim()));
		k.setEtage(removeLeadingZeros(cut(line, 219, 221).trim()));
		k.setSideDoerNummer(cut(line, 221, 225).trim());
		k.setBygningsNummer(cut(line, 225, 229).trim());
		
		// This is a duplicate of the address field.
		
		k.setVejnavnTilAdressering(cut(line, 229, 249).trim()); 
		
		// FIXME: This is not actually the complete street name,
		// rather it is a shortened version. The complete address is not included
		// in this record type.
		
		k.setVejNavn(cut(line, 229, 249).trim());

		return k;
	}


	protected Personoplysninger personoplysninger(String line)
	{
		Personoplysninger p = new Personoplysninger();

		p.setCpr(cut(line, 3, 13));
		p.setGaeldendeCpr(cut(line, 13, 23).trim());
		p.setStatus(cut(line, 23, 25));
		p.setStatusDato(parseDate(yyyyMMddHHmm, line, 25, 37));
		p.setStatusMakering(cut(line, 37, 38));
		p.setKoen(cut(line, 38, 39));
		p.setFoedselsdato(parseDate(yyyy_MM_dd, line, 39, 49));
		p.setFoedselsdatoMarkering("*".equals(cut(line, 49, 50)));
		p.setStartDato(parseDate(yyyy_MM_dd, line, 50, 60));
		p.setStartDatoMarkering(cut(line, 60, 61));
		p.setSlutdato(parseDate(yyyy_MM_dd, line, 61, 71));
		p.setSlutDatoMarkering(cut(line, 71, 72));
		p.setStilling(cut(line, 72, 106).trim());

		return p;
	}


	/**
	 * Gets the record type of a line in the CPR file.
	 */
	private static int getRecordType(String line)
	{
		return readInt(line, 0, 3);
	}


	private static String cut(String line, int beginIndex, int endIndex)
	{
		String res = "";

		if (line.length() > beginIndex)
		{
			int end = (line.length() < endIndex) ? line.length() : endIndex;
			res = line.substring(beginIndex, end);
		}

		return res;
	}


	private static int readInt(String line, int from, int to)
	{
		try
		{
			return Integer.parseInt(cut(line, from, to));
		}
		catch (NumberFormatException e)
		{
			throw new ParserException("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, e);
		}
	}


	private static Long parseLong(String line, int from, int to)
	{
		try
		{
			return Long.parseLong(cut(line, from, to));
		}
		catch (Exception e)
		{
			throw new ParserException("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, e);
		}
	}


	private static Date parseDate(DateTimeFormatter format, String line, int from, int to)
	{
		String dateString = cut(line, from, to);
		if (dateString != null && dateString.trim().length() == to - from && !dateString.equals(EMPTY_DATE_STRING))
		{
			return parseDateAndCheckValidity(dateString, format, line);
		}
		return null;
	}


	private static Date getValidFrom(String line)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateFromLine = cut(line, 19, 27);
		try {
			return sdf.parse(dateFromLine);
		} catch (ParseException e) {
			throw new RuntimeException("Could not parse date part " + dateFromLine + " from line " + dateFromLine, e);
		}
	}


	private static String removeLeadingZeros(String str)
	{
		if (str == null) return null;

		for (int index = 0; index < str.length(); index++)
		{
			if (str.charAt(index) != '0') return str.substring(index);
		}

		return "";
	}


	static String fixWeirdDate(String date)
	{
		Matcher dateMatcher = datePattern.matcher(date);
		String fixedDate;

		if (dateMatcher.matches())
		{
			fixedDate = fixDate(dateMatcher);
		}
		else
		{
			Matcher timeMatcher = timestampPattern.matcher(date);
			if (timeMatcher.matches())
			{
				fixedDate = fixTime(timeMatcher);
			}
			else
			{
				logger.error("Unexpected date format=" + date);
				return date;
			}
		}

		if (logger.isTraceEnabled() && !fixedDate.equals(date)) {
			logger.trace("Fixing CPR date from="+date+" to=" + fixedDate);
		}

		return fixedDate;
	}


	private static String fixTime(Matcher timeMatcher)
	{
		int year, month, day, hours, minutes;
		year = Integer.parseInt(timeMatcher.group(1));
		month = Integer.parseInt(timeMatcher.group(2));
		day = Integer.parseInt(timeMatcher.group(3));
		hours = Integer.parseInt(timeMatcher.group(4));
		minutes = Integer.parseInt(timeMatcher.group(5));

		if (month == 0)
		{
			month = 1;
		}
		if (day == 0)
		{
			day = 1;
		}
		if (hours >= 24)
		{
			hours = 0;
		}
		if (minutes >= 60)
		{
			minutes = 0;
		}

		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		formatter.format("%04d%02d%02d%02d%02d", year, month, day, hours, minutes);
		return result.toString();
	}


	private static String fixDate(Matcher dateMatcher)
	{
		int year, month, day;
		year = Integer.parseInt(dateMatcher.group(1));
		month = Integer.parseInt(dateMatcher.group(2));
		day = Integer.parseInt(dateMatcher.group(3));
		if (month == 0)
		{
			month = 1;
		}
		if (day == 0)
		{
			day = 1;
		}
		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		formatter.format("%04d-%02d-%02d", year, month, day);
		return result.toString();
	}


	private static Date parseDateAndCheckValidity(String dateString, DateTimeFormatter format, String line)
	{
		dateString = fixWeirdDate(dateString);
		LocalDateTime date = format.parseLocalDateTime(dateString);
		String formattedDate = format.print(date);

		if (!formattedDate.equals(dateString))
		{
			String errorMessage = "Ugyldig dato: " + dateString + " fra linjen [" + line + "]";

			if (haltOnDateErrors)
			{
				throw new ParserException(errorMessage);
			}
			else
			{
				logger.error(errorMessage);
			}
		}

		return date.toDate();
	}
}
