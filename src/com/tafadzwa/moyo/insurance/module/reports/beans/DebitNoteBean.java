
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

 
public class DebitNoteBean implements Serializable {

    private static final long serialVersonUID = 1L;
    private String name;
    private String address;
    private String riskInsurer;
    private String policyType;
    private String renewalDate;
    private String expiryDate ;
    private String effectiveDate;
    private BigDecimal basicPremium;
    private BigDecimal levies;
    private BigDecimal stampDuty;
    private BigDecimal netPremium;
     private String reportName ;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getBasicPremium() {
        return basicPremium;
    }

    public void setBasicPremium(BigDecimal basicPremium) {
        this.basicPremium = basicPremium;
    }

      
    public BigDecimal getLevies() {
        return levies;
    }

    public void setLevies(BigDecimal levies) {
        this.levies = levies;
    }

      public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getNetPremium() {
        return netPremium;
    }

    public void setNetPremium(BigDecimal netPremium) {
        this.netPremium = netPremium;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getRiskInsurer() {
        return riskInsurer;
    }

    public void setRiskInsurer(String riskInsurer) {
        this.riskInsurer = riskInsurer;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

  
    


    
    
    public static List createBeanCollection() {
        List<DebitNoteBean> list = new ArrayList<DebitNoteBean>();
        DebitNoteBean debitNote = new DebitNoteBean();
        debitNote.setName("Tafadzwa");
        debitNote.setAddress("Harare");
        debitNote.setLevies(BigDecimal.TEN);
        debitNote.setNetPremium(BigDecimal.TEN);
        debitNote.setPolicyType("Motor");
        debitNote.setRiskInsurer("CBZ");
        debitNote.setStampDuty(BigDecimal.TEN);
        debitNote.setEffectiveDate("04/05/2015");
        debitNote.setRenewalDate("04/05/2015");
        debitNote.setExpiryDate("04/05/2015");
        debitNote.setReportName("Debit Note");
        
        list.add(debitNote);
        return list;
    }
}
