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

/**
 *
 * @author Edmund
 */
public class BodStatementBean implements Serializable {

    private static final long serialVersonUID = 1L;
    private Date monthOf;
    private String name;
    private String surname;
    private String riskInsurer;
    private String policyType;
    private Double grossPremium;
    private Double levies;
    private Double stampDuty;
    private Double brokerageRate ;
    private Double brokerageAmount;
    private Double netPremium;
    private String policyNumber ;
    private Date transactionDate;
    private Double totalInsNet;
    private String dateRange;
    private String coverNote;
    private String reportName ;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    
    

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }
    
    

    public Date getMonthOf() {
        return monthOf;
    }

    public void setMonthOf(Date monthOf) {
        this.monthOf = monthOf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBrokerageAmount() {
        return brokerageAmount;
    }

    public void setBrokerageAmount(Double brokerageAmount) {
        this.brokerageAmount = brokerageAmount;
    }

    public Double getBrokerageRate() {
        return brokerageRate;
    }

    public void setBrokerageRate(Double brokerageRate) {
        this.brokerageRate = brokerageRate;
    }

    public Double getGrossPremium() {
        return grossPremium;
    }

    public void setGrossPremium(Double grossPremium) {
        this.grossPremium = grossPremium;
    }

    public Double getLevies() {
        return levies;
    }

    public void setLevies(Double levies) {
        this.levies = levies;
    }

    public Double getNetPremium() {
        return netPremium;
    }

    public void setNetPremium(Double netPremium) {
        this.netPremium = netPremium;
    }

    public Double getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(Double stampDuty) {
        this.stampDuty = stampDuty;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCoverNote() {
        return coverNote;
    }

    public void setCoverNote(String coverNote) {
        this.coverNote = coverNote;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getTotalInsNet() {
        return totalInsNet;
    }

    public void setTotalInsNet(Double totalInsNet) {
        this.totalInsNet = totalInsNet;
    }
    
        


    
    
    public static List createBeanCollection() {
        List<BodStatementBean> allBodStatements = new ArrayList<BodStatementBean>();
        BodStatementBean bodStatement = new BodStatementBean();
        bodStatement.setReportName("Bod statement");
        bodStatement.setName("nn");
        bodStatement.setSurname("nn");
        bodStatement.setMonthOf(new Date());
        bodStatement.setBrokerageAmount(new Double("10.484"));
        bodStatement.setBrokerageRate(new Double("10.484"));
        bodStatement.setGrossPremium(new Double("10.484"));
        bodStatement.setLevies(new Double("10.484"));
        bodStatement.setNetPremium(new Double("10.484"));
        bodStatement.setPolicyType("Motor");
        bodStatement.setRiskInsurer("CBZ");
        bodStatement.setStampDuty(new Double("10.484"));
        bodStatement.setTransactionDate(new Date());
        bodStatement.setPolicyNumber("TestPN");
        bodStatement.setTotalInsNet(new Double ("0.0"));
        allBodStatements.add(bodStatement);
        return allBodStatements;
    }
}
