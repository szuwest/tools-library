package org.szuwest.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.szuwest.library.R;

import android.content.Context;

public class CalendarUtil { 
	
    public static Calendar getTimeAfterInSecs(int secs) { 
        Calendar cal = Calendar.getInstance(); 
        cal.add(Calendar.SECOND,secs); 
        return cal; 
    } 
    
    public static Calendar getCurrentTime(){ 
        Calendar cal = Calendar.getInstance(); 
        return cal; 
    } 
    
    public static Calendar getTodayAt(int hours){ 
        Calendar today = Calendar.getInstance(); 
        Calendar cal = Calendar.getInstance(); 
        cal.clear(); 
         
        int year = today.get(Calendar.YEAR); 
        int month = today.get(Calendar.MONTH); 
        //represents the day of the month 
        int day = today.get(Calendar.DATE); 
        cal.set(year,month,day,hours,0,0); 
        return cal; 
    } 
    
    public static String getDateTimeString(Calendar cal){ 
    	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss"); 
    	df.setLenient(false); 
    	String s = df.format(cal.getTime()); 
    	return s; 
    } 
    
    public static String getDateString(Calendar cal){ 
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
        df.setLenient(false); 
        String s = df.format(cal.getTime()); 
        return s; 
    }

    public static String getHourMinuteString(Calendar cal){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setLenient(false);
        String s = df.format(cal.getTime());
        return s;
    }

    public static String getNowDateString(Context context){ 
    	Calendar cal  = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();
		sb.append(context.getResources().getString(R.string.today_is));
		sb.append(cal.get(Calendar.YEAR));
		sb.append(context.getResources().getString(R.string.year));
		sb.append(cal.get(Calendar.MONTH)+1);
		sb.append(context.getResources().getString(R.string.month));
		sb.append(cal.get(Calendar.DATE));
		sb.append(context.getResources().getString(R.string.day_t));
		sb.append(getDayofWeekString(cal.get(Calendar.DAY_OF_WEEK), context));
		return sb.toString();
    }
    
    public static String getDayofWeekString(int dayOfWeek, Context context){
    	String str = null;
    	switch(dayOfWeek){
    	case Calendar.MONDAY:
    		str  = context.getString(R.string.monday);
    		break;
    	case Calendar.TUESDAY:
    		str  = context.getString(R.string.tuesday);
    		break;
    	case Calendar.WEDNESDAY:
    		str  = context.getString(R.string.wendesday);
    		break;
    	case Calendar.THURSDAY:
    		str  = context.getString(R.string.thursday);
    		break;
    	case Calendar.FRIDAY:
    		str  = context.getString(R.string.friday);
    		break;
    	case Calendar.SATURDAY:
    		str  = context.getString(R.string.saturday);
    		break;
    	case Calendar.SUNDAY:
    		str  = context.getString(R.string.sunday);
    		break;
    	}
    	return str;
    }
    
    public static int getDayofWeek(int dayOfWeek){
    	int field = 0;
    	switch(dayOfWeek){
    	case 1:
    		field  = Calendar.MONDAY;
    		break;
    	case 2:
    		field  = Calendar.TUESDAY;
    		break;
    	case 3:
    		field  = Calendar.WEDNESDAY;
    		break;
    	case 4:
    		field  = Calendar.THURSDAY;
    		break;
    	case 5:
    		field  = Calendar.FRIDAY;
    		break;
    	case 6:
    		field  = Calendar.SATURDAY;
    		break;
    	case 7:
    		field  = Calendar.SUNDAY;
    		break;
    	}
    	return field;
    }
} 