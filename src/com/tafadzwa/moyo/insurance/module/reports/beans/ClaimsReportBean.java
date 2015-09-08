
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.reports.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClaimsReportBean implements Serializable {

    private static final long serialVersonUID = 1L;
    private String name;
    private String claimant;
    private String riskInsurer;
    private String policyType;
    private String claimType;
    private String premiumStatus;
    private String claimStatus;
    private Double grossClaim;
    private Double excess;
    private Double betterment;
    private Double netClaim;
    private Date submissionDate;
    private Date registrationDate;
    private Date followUpDate;
    private Date dateOfLoss;
    private double totalNetClaim;
     private String reportName ;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public double getTotalNetClaim() {
        return totalNetClaim;
    }

    public void setTotalNetClaim(double totalNetClaim) {
        this.totalNetClaim = totalNetClaim;
    }

    public Date getDateOfLoss() {
        return dateOfLoss;
    }

    public void setDateOfLoss(Date dateOfLoss) {
        this.dateOfLoss = dateOfLoss;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(Date followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        this.claimant = claimant;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getPremiumStatus() {
        return premiumStatus;
    }

    public void setPremiumStatus(String premiumStatus) {
        this.premiumStatus = premiumStatus;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Double getGrossClaim() {
        return grossClaim;
    }

    public void setGrossClaim(Double grossClaim) {
        this.grossClaim = grossClaim;
    }

    public Double getExcess() {
        return excess;
    }

    public void setExcess(Double excess) {
        this.excess = excess;
    }

    public Double getBetterment() {
        return betterment;
    }

    public void setBetterment(Double betterment) {
        this.betterment = betterment;
    }

    public Double getNetClaim() {
        return netClaim;
    }

    public void setNetClaim(Double netClaim) {
        this.netClaim = netClaim;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public static List createBeanCollection() {
        List<ClaimsReportBean> list = new ArrayList<ClaimsReportBean>();
        ClaimsReportBean claim = new ClaimsReportBean();
        claim.setName("Tafadzwa");
        claim.setBetterment(Double.NaN);
        claim.setClaimStatus("Test");
        claim.setClaimant("Test");
        claim.setExcess(Double.NaN);
        claim.setGrossClaim(Double.NaN);
        claim.setName(null);
        claim.setNetClaim(Double.NaN);
        claim.setPremiumStatus("Test");
        claim.setRiskInsurer(null);
        claim.setSubmissionDate(new Date());
        claim.setPolicyType("Motor");
        claim.setRiskInsurer("CBZ");
        claim.setReportName("Claims");

        list.add(claim);
        return list;
    }
}
