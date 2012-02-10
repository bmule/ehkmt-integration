package at.srfg.kmt.ehealth.phrs.presentation.utils;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Date;

public class TimeUtils {
    /**
     * CompareTo
     *  boolean isLessThan = TimeUtils.isBeginLess(Date beginDate,Date endDate)
     * @param beginDate
     * @param endDate
     * @return  negative if this is less, zero if equal, positive if greater
     */
    public static int compare(Date beginDate,Date endDate){

        DateTime first =  beginDate!=null ? new DateTime(beginDate.getTime()) : null;
        DateTime second =  endDate!=null ? new DateTime(endDate.getTime()) : null;

        LocalDate firstDate = first != null ? first.toLocalDate() : null;
        LocalDate secondDate = second != null ? second.toLocalDate() : null;

        int value= 0;
        if(firstDate!=null && secondDate !=null)
            value = firstDate.compareTo(secondDate);
        else if(firstDate==null && secondDate==null )
            value=0;
        else if(firstDate==null  )
            value= -1;
        else
            value= 1;

        //return firstDate.compareTo(secondDate);
        return value;
    }
    public static boolean isBeginLess(Date beginDate,Date endDate){
        int value=TimeUtils.compare(beginDate,endDate);
        
        return value < 0 ? true : false;
    }

}
