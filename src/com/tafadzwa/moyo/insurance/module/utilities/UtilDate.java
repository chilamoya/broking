/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.utilities;

/**
 *
 * @author Tafadzwa
 */
import java.util.Date;
public class UtilDate{
    
   public int numberOfDays(String fromDate,String toDate)
   {    
       java.util.Calendar cal1 = new java.util.GregorianCalendar();
       java.util.Calendar cal2 = new java.util.GregorianCalendar();

       //split year, month and days from the date using StringBuffer.
       StringBuffer sBuffer = new StringBuffer(fromDate);
       System.out.println(sBuffer);
       String yearFrom = sBuffer.substring(6,10);
       String monFrom = sBuffer.substring(0,2);
       String ddFrom = sBuffer.substring(3,5);
       int intYearFrom = Integer.parseInt(yearFrom);
       int intMonFrom = Integer.parseInt(monFrom);
       int intDdFrom = Integer.parseInt(ddFrom);

       // set the fromDate in java.util.Calendar
       cal1.set(intYearFrom, intMonFrom, intDdFrom);

       //split year, month and days from the date using StringBuffer.
       StringBuffer sBuffer1 = new StringBuffer(toDate);
       String yearTo = sBuffer1.substring(6,10);
       String monTo = sBuffer1.substring(0,2);
       String ddTo = sBuffer1.substring(3,5);
       int intYearTo = Integer.parseInt(yearTo);
       int intMonTo = Integer.parseInt(monTo);
       int intDdTo = Integer.parseInt(ddTo);

       // set the toDate in java.util.Calendar
       cal2.set(intYearTo, intMonTo, intDdTo);

       //call method daysBetween to get the number of days between two dates
       int days = daysBetween(cal1.getTime(),cal2.getTime());
       return days;
   }

   //This method is called by the above method numberOfDays
   private int daysBetween(Date d1, Date d2)
   {
      return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
   }

    
    public UtilDate() {
  
    }
   
   
   
  
}
