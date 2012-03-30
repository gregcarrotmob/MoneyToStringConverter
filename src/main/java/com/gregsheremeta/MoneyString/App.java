package com.gregsheremeta.MoneyString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Command-line program that reads in a decimal money amount and converts it to the string representation.
 * Example: Convert 2523.04 to "Two thousand five hundred twenty-three and 04/100 dollars"
 * 
 * @author gds
 */
public class App {
	
	protected final static Pattern moneyPattern = Pattern.compile("^\\d*(\\.\\d{2})?$");
	private final static int MAX_INPUT_LENGTH   = 20;
	
    public static void main(String[] args)
    {
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	String money = null;
    	while (true) {
	    	try {
	    		System.out.println("Enter a dollar amount (formatted like $123.45) and I will convert it to a string representation. Type 'quit' to quit.");
	    		money = br.readLine();
	    		money = money.trim();
	    		if (money.equals("quit")) {
	    			System.out.println("Thanks for playing. Goodbye.");
	    			System.exit(0);
	    		}
	    		else {
	    			String cleanedMoney = sanitizeInput(money);
	    			if (StringUtils.isBlank(cleanedMoney)) {
	    				invalidWarn();
	    			}
	    			else if (cleanedMoney.length() > MAX_INPUT_LENGTH) {
	    				invalidWarn();
	    			}
	    			else {
	    				BigDecimal moneyDecimal = new BigDecimal(cleanedMoney);
	    				String moneyString = MoneyToStringConverter.convertMoneyToString(moneyDecimal);
	    				if (StringUtils.isBlank(moneyString)) {
		    				invalidWarn();
		    			}
	    				else {
		    				// this is the good case :)
	    					System.out.println(moneyString);
	    				}
	    			}
	    		}
	    	}
	    	catch (IOException ioe) {
	    		invalidWarn();
	    		System.exit(-1);
	    	}
    	}
    }
    
    private static void invalidWarn() {
    	System.out.println("Sorry, please enter a valid money format, less than $1 quadrillion.");
    }
    
    private static String sanitizeInput(String input) {
    	input = input.replaceAll("[,$]", ""); // handle stripping dollar signs, commas, anything else?
    	
    	if (moneyPattern.matcher(input).matches()) {
    		return input;
    	}
    	return null;
    }
}
