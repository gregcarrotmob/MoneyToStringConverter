package com.gregsheremeta.MoneyString;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for converting a decimal money amount to the String representation.
 * Example: Convert 2523.04 to "Two thousand five hundred twenty-three and 04/100 dollars"
 * 
 * @author gds
 *
 */
public class MoneyToStringConverter {
	
	private static final BigDecimal ONE  = new BigDecimal("1");
	private static final BigDecimal ZERO = new BigDecimal("0");
	private static final long       MAX  = 1000000000000000L;
	
	/**
	 * Convert a dollar amount (less than $1 quadrillion) in decimal format to a string representation.
	 * Example: Convert 2523.04 to "Two thousand five hundred twenty-three and 04/100 dollars"
	 * @param money decimal money amount
	 * @return string representation
	 */
	public static String convertMoneyToString(BigDecimal money) {
		
		// handle negative
		if (money.compareTo(ZERO) < 0) {
			return null;
		}
		
		// handle zero
		if (money.equals(ZERO)) {
			return "0 dollars";
		}
		
		// handle one
		if (money.equals(ONE)) {
			return "1 dollar";
		}
		
		// avoid dealing with BigDecimal math by splitting of cents and using a long for the dollars
		String cents = money.remainder(ONE).toString().replaceAll("0\\.", "");
		long dollars = money.longValue();
		
		// handle more than maximum
		if (dollars >= MAX) {
			return null;
		}
		
		StringBuilder ret = new StringBuilder();   // buffer for the result
		long place = 1L;                           // incrementing place counter
		
		// divide (literally!) and conquer.
		// loop over the number, pick off the pieces between the commas (what are those called?) right to left and stringify them one at a time.
		while (1000L < MAX && dollars > 0) {

			long part = dollars % 1000L; // get last 3 digits
			if (part > 0) {
				StringBuilder temp = new StringBuilder();
				temp.append(convertMoneyToStringSubThousand((int) part));
				temp.append(" ");
				
				// append "thousand", "million", etc.
				temp.append(" ");
				temp.append(placeString(place));
				temp.append(" ");
				ret.insert(0,  temp);
			}
			
			// prepare for next loop
			dollars = dollars / 1000L;  // throw away last three digits
			place *= 1000L;             // move from thousands to millions, or millions to billions, etc.
		}
		
		// now we have something like "two thousand one hundred ten"
		// add in the cents, if any
		
		if (StringUtils.isNotBlank(cents) && Integer.parseInt(cents) != 0) {
			if (StringUtils.isNotBlank(ret.toString())) {   // TODO cheaper way to check this?
				ret.append(" and ");
			}
			ret.append(cents);
			ret.append("/100");
		}
		
		ret.append(" dollars");

		return ret.toString().trim().replaceAll(" +", " ");  // the routine isn't perfect with spaces, so clean up a bit
		
	}
	
	/**
	 * Converts a number less than 1000 into the string representation. Examples:<br/><br/>
	 * 456 "four hundred fifty-six"<br/>
	 * 9   "nine"<br/>
	 * 12  "twelve"<br/>
	 * 
	 * @param number a number less than 1000 for which you want a string representation
	 * @return string representation
	 */
	private static String convertMoneyToStringSubThousand(int number) {
		
		if (number > 1000) {
			throw new IllegalArgumentException("number passed in must be less than 1000");
		}
		
		if (number == 0) {
			return "";
		}
		
		StringBuilder ret = new StringBuilder();
		if (number > 99) {
			// x hundred something
			int hundredPlace = (number - (number % 100)) / 100;
			ret.append(digitToString(hundredPlace));
			ret.append(" hundred ");
			number = number % 100;
		}
		
		if (number > 19) {
			// twenty, twenty-one, etc.
			int tensPlace = (number - (number % 10)) / 10;
			ret.append(tensToString(tensPlace));
			int onesPlace = number - (tensPlace * 10);
			if (onesPlace > 0) {
				ret.append("-");
				ret.append(digitToString(onesPlace));
			}
		}
		else if (number > 9) {
			// ten, eleven, etc.
			ret.append(teenToString(number));
		}
		else {
			// 0 through 9
			ret.append(digitToString(number));
		}
		
		return ret.toString();
	}

	/** 
	 * Get string representation of a digit
	 */
	private static String digitToString(int digit) {
		// could use an array, switch, whatever ... a matter of style
		// we don't verbalize zero
		if (digit == 1) {
			return "one";
		}
		else if (digit == 2) {
			return "two";
		}
		else if (digit == 3) {
			return "three";
		}
		else if (digit == 4) {
			return "four";
		}
		else if (digit == 5) {
			return "five";
		}
		else if (digit == 6) {
			return "six";
		}
		else if (digit == 7) {
			return "seven";
		}
		else if (digit == 8) {
			return "eight";
		}
		else if (digit == 9) {
			return "nine";
		}
		return "";
	}
	
	/**
	 * Get string representation of a number between 10 and 19 inclusive
	 */
	private static String teenToString(int teen) {
		// could use an array, switch, whatever ... a matter of style
		if (teen == 10) {
			return "ten";
		}
		else if (teen == 11) {
			return "eleven";
		}
		else if (teen == 12) {
			return "twelve";
		}
		else if (teen == 13) {
			return "thirteen";
		}
		else if (teen == 14) {
			return "fourteen";
		}
		else if (teen == 15) {
			return "fifteen";
		}
		else if (teen == 16) {
			return "sixteen";
		}
		else if (teen == 17) {
			return "seventeen";
		}
		else if (teen == 18) {
			return "eighteen";
		}
		else if (teen == 19) {
			return "nineteen";
		}
		return "";
	}

	/**
	 * Get string representation of the tens place of a number -- so for 2, return twenty, for 3, thirty, etc.
	 */
	private static String tensToString(int tens) {
		// could use an array, switch, whatever ... a matter of style
		if (tens == 2) {
			return "twenty";
		}
		else if (tens == 3) {
			return "thirty";
		}
		else if (tens == 4) {
			return "forty";
		}
		else if (tens == 5) {
			return "fifty";
		}
		else if (tens == 6) {
			return "sixty";
		}
		else if (tens == 7) {
			return "seventy";
		}
		else if (tens == 8) {
			return "eighty";
		}
		else if (tens == 9) {
			return "ninety";
		}
		return "";
	}
	
	
	/**
	 * I'm not sure what the math word for this is, but given a long representation of a place, get the text description
	 * you'd use for the numbers in that place. For example, 1000L returns "thousand", 1000000L returns "million", etc.
	 * Only goes up to trillion for this exercise.
	 * 
	 * @param place
	 * @return string representation
	 */
	private static String placeString(long place) {
		if (place == 1000L) {
			return "thousand";
		}
		else if (place == 1000L * 1000L) {
			return "million";
		}
		else if (place == 1000L * 1000L * 1000L) {
			return "billion";
		}
		else if (place == 1000L * 1000L * 1000L * 1000L) {
			return "trillion";
		}
		return "";
	}

}
