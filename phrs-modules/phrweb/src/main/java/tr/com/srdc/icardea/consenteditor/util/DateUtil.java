package tr.com.srdc.icardea.consenteditor.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	public DateUtil(String currentTime) {
		String[] datetime=currentTime.split(" ");
		String[] date=datetime[0].split("-");
		String[] time=datetime[1].split(":");
		year=Integer.parseInt(date[0]);
		month=Integer.parseInt(date[1]);
		day=Integer.parseInt(date[2]);
		hour=Integer.parseInt(time[0]);
		minute=Integer.parseInt(time[1]);
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
}
