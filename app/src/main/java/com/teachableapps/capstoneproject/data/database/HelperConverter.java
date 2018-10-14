package com.teachableapps.capstoneproject.data.database;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.min;

public class HelperConverter {

    String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};

    public static boolean[] toBool7ArrayFromInt(int intValue8bit) {
        if (intValue8bit<0) return null;
        String binStr = Integer.toBinaryString(intValue8bit);
        int outArraySize = 7;
        boolean[] outArray = new boolean[outArraySize];
        Arrays.fill(outArray, false); // fill everything with false
        for (int i=0; i<min(outArraySize,binStr.length()); i++){
            String teststr = binStr.substring(binStr.length()-i-1,binStr.length()-i);
            outArray[outArraySize-i-1] = (teststr.charAt(0)=='1');
        }
        return outArray;
    }

    public static int toIntFromBool7Array(boolean[] inBoolArray) {
        if(inBoolArray.length<1) return -1;
        int b = 0;
        for (int j = 0; j<=min(7, inBoolArray.length-1); j++) {
            b = (b << 1) | (inBoolArray[j] ? 1 : 0);
        }
        return b;
    }

    public static String toTimeFromInt(int t_mins) {
        if (t_mins<0) return "invalid time";
        int hours = t_mins / 60;
        int minutes = t_mins - (hours*60);
        if (minutes < 10)
            return String.valueOf(hours) + ":0" + String.valueOf(minutes);
        else
            return String.valueOf(hours) + ":" + String.valueOf(minutes);
    }

    public static int toMinsFromString(String s) {
        if(s.length()==0) return -1;
        int poscolon = s.indexOf(':');
        int hours = Integer.parseInt(s.substring(0,poscolon));
        int minutes = Integer.parseInt(s.substring(poscolon+1,s.length()));
        return (hours*60) + minutes;
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}

