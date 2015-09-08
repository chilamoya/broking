
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

 
public class ReceiptsBean implements Serializable {

    private static final long serialVersonUID = 1L;
    private String name;
    private String insured;
    private Double tendered;
    private Date transactionDate ;
    private String user ;
    private String reportName ;
    private String tenderString;
    private String narration;

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
    
    

    public String getTenderString() {
        return tenderString;
    }

    public void setTenderString(String tenderString) {
        this.tenderString = tenderString;
    }
    
    

    
    
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsured() {
        return insured;
    }

    public void setInsured(String insured) {
        this.insured = insured;
    }

    public Double getTendered() {
        return tendered;
    }

    public void setTendered(Double tendered) {
        this.tendered = tendered;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

   
    
    
    public static List createBeanCollection() {
        List<ReceiptsBean> list = new ArrayList<ReceiptsBean>();
        ReceiptsBean receipt = new ReceiptsBean();
        receipt.setName("Tafadzwa"); 
        receipt.setInsured("Maputi angu arikusango uko");
        receipt.setTendered(Double.NaN);
        receipt.setTransactionDate(new Date());
        receipt.setUser("Tafadzwa");
        receipt.setReportName ("Innate Solutions");
        receipt.setNarration("Innate Solutions");
        receipt.setTenderString("USD 200");
        
        
        list.add(receipt);
        return list;
    }
}
