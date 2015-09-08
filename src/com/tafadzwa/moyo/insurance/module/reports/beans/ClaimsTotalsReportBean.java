
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.reports.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

 
public class ClaimsTotalsReportBean implements Serializable {

    private static final long serialVersonUID = 1L;
    private String insurer;
    private Long totalClaims;
    private double totalValue;
    private Long totalSettled;
    private double valueSetteled;
    private Long totalUnsetteled;
    private double valueUnsettled;
    private String quarter;
     private String reportName ;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public Long getTotalClaims() {
        return totalClaims;
    }

    public void setTotalClaims(Long totalClaims) {
        this.totalClaims = totalClaims;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public Long getTotalSettled() {
        return totalSettled;
    }

    public void setTotalSettled(Long totalSettled) {
        this.totalSettled = totalSettled;
    }

    public double getValueSetteled() {
        return valueSetteled;
    }

    public void setValueSetteled(double valueSetteled) {
        this.valueSetteled = valueSetteled;
    }

    public Long getTotalUnsetteled() {
        return totalUnsetteled;
    }

    public void setTotalUnsetteled(Long totalUnsetteled) {
        this.totalUnsetteled = totalUnsetteled;
    }

    public double getValueUnsettled() {
        return valueUnsettled;
    }

    public void setValueUnsettled(double valueUnsettled) {
        this.valueUnsettled = valueUnsettled;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

   
      
    public static List createBeanCollection() {
        List<ClaimsTotalsReportBean> list = new ArrayList<ClaimsTotalsReportBean>();
        ClaimsTotalsReportBean claim = new ClaimsTotalsReportBean();
        claim.setInsurer("Testing");
        claim.setQuarter("Third");
        claim.setTotalClaims(new Double("899").longValue());
        claim.setTotalSettled(new Double("899").longValue());
        claim.setTotalUnsetteled(new Double("899").longValue());
        claim.setTotalValue(new Double("899"));
        claim.setValueSetteled(new Double("899"));
        claim.setValueUnsettled(new Double("899"));
        claim.setReportName("Claims");
        
        
        
        list.add(claim);
        return list;
    }
}
