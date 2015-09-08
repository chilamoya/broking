
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.reports.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

 
public class IncomeReportBean implements Serializable {

    private static final long serialVersonUID = 1L;
     
    private String quarter;
    private double gpRecievable ;
    private double gpPayable ;
    private double commission ;
     private String reportName ;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    
  
    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public double getGpRecievable() {
        return gpRecievable;
    }

    public void setGpRecievable(double gpRecievable) {
        this.gpRecievable = gpRecievable;
    }

    public double getGpPayable() {
        return gpPayable;
    }

    public void setGpPayable(double gpPayable) {
        this.gpPayable = gpPayable;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }
    
    

   
      
    public static List createBeanCollection() {
        List<IncomeReportBean> list = new ArrayList<IncomeReportBean>();
        IncomeReportBean claim = new IncomeReportBean();
        
        claim.setQuarter("Third Quarter");
        claim.setCommission(new Double("1000"));
        claim.setGpPayable(new Double("1000"));
        claim.setGpRecievable(new Double("1000"));
        claim.setReportName("Income");
        
        
        list.add(claim);
        return list;
    }
}
