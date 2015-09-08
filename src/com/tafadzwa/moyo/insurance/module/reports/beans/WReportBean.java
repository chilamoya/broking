
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.reports.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WReportBean implements Serializable {

    private static final long serialVersonUID = 1L;
    private String sField1;
    private String sField2;
    private String sField3;
    private String sField4;
    private String sField5;
    private String sField6;
    private String sField7;
    private String sField8;
    private String sField9;
    private String sField10;

    private int iField1;
    private int iField2;
    private int iField3;
    private int iField4;
    private int iField5;

    private Double dField1;
    private Double dField2;
    private Double dField3;
    private Double dField4;
    private Double dField5;

    private Date dateField1;
    private Date dateField2;
    private Date dateField3;
    private Date dateField4;
    private Date dateField5;

    private String reportName;
    private String title;
    private String address;

    public String getsField1() {
        return sField1;
    }

    public void setsField1(String sField1) {
        this.sField1 = sField1;
    }

    public String getsField2() {
        return sField2;
    }

    public void setsField2(String sField2) {
        this.sField2 = sField2;
    }

    public String getsField3() {
        return sField3;
    }

    public void setsField3(String sField3) {
        this.sField3 = sField3;
    }

    public String getsField4() {
        return sField4;
    }

    public void setsField4(String sField4) {
        this.sField4 = sField4;
    }

    public String getsField5() {
        return sField5;
    }

    public void setsField5(String sField5) {
        this.sField5 = sField5;
    }

    public String getsField6() {
        return sField6;
    }

    public void setsField6(String sField6) {
        this.sField6 = sField6;
    }

    public String getsField7() {
        return sField7;
    }

    public void setsField7(String sField7) {
        this.sField7 = sField7;
    }

    public String getsField8() {
        return sField8;
    }

    public void setsField8(String sField8) {
        this.sField8 = sField8;
    }

    public String getsField9() {
        return sField9;
    }

    public void setsField9(String sField9) {
        this.sField9 = sField9;
    }

    public String getsField10() {
        return sField10;
    }

    public void setsField10(String sField10) {
        this.sField10 = sField10;
    }

    public int getiField1() {
        return iField1;
    }

    public void setiField1(int iField1) {
        this.iField1 = iField1;
    }

    public int getiField2() {
        return iField2;
    }

    public void setiField2(int iField2) {
        this.iField2 = iField2;
    }

    public int getiField3() {
        return iField3;
    }

    public void setiField3(int iField3) {
        this.iField3 = iField3;
    }

    public int getiField4() {
        return iField4;
    }

    public void setiField4(int iField4) {
        this.iField4 = iField4;
    }

    public int getiField5() {
        return iField5;
    }

    public void setiField5(int iField5) {
        this.iField5 = iField5;
    }

    public Double getdField1() {
        return dField1;
    }

    public void setdField1(Double dField1) {
        this.dField1 = dField1;
    }

    public Double getdField2() {
        return dField2;
    }

    public void setdField2(Double dField2) {
        this.dField2 = dField2;
    }

    public Double getdField3() {
        return dField3;
    }

    public void setdField3(Double dField3) {
        this.dField3 = dField3;
    }

    public Double getdField4() {
        return dField4;
    }

    public void setdField4(Double dField4) {
        this.dField4 = dField4;
    }

    public Double getdField5() {
        return dField5;
    }

    public void setdField5(Double dField5) {
        this.dField5 = dField5;
    }

    public Date getDateField1() {
        return dateField1;
    }

    public void setDateField1(Date dateField1) {
        this.dateField1 = dateField1;
    }

    public Date getDateField2() {
        return dateField2;
    }

    public void setDateField2(Date dateField2) {
        this.dateField2 = dateField2;
    }

    public Date getDateField3() {
        return dateField3;
    }

    public void setDateField3(Date dateField3) {
        this.dateField3 = dateField3;
    }

    public Date getDateField4() {
        return dateField4;
    }

    public void setDateField4(Date dateField4) {
        this.dateField4 = dateField4;
    }

    public Date getDateField5() {
        return dateField5;
    }

    public void setDateField5(Date dateField5) {
        this.dateField5 = dateField5;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static List createBeanCollection() {
        List<WReportBean> list = new ArrayList<WReportBean>();
        WReportBean report = new WReportBean();
        report.setAddress("Yangu");
        report.setDateField1(new Date());
        report.setDateField2(new Date());
        report.setDateField3(new Date());
        report.setDateField4(new Date());
        report.setDateField5(new Date());

        report.setReportName("Ini");
        report.setTitle("Eheka");
        report.setdField1(Double.NaN);
        report.setdField2(Double.NaN);
        report.setdField3(Double.NaN);
        report.setdField4(Double.NaN);
        report.setdField5(Double.NaN);

        report.setiField1(1);
        report.setiField2(1);
        report.setiField3(1);
        report.setiField4(1);
        report.setiField5(1);

        report.setsField1("Test");
        report.setsField2("Test");
        report.setsField3("Test");
        report.setsField4("Test");
        report.setsField5("Test");
        report.setsField6("Test");
        report.setsField7("Test");
        report.setsField8("Test");
        report.setsField9("Test");
        report.setsField10("Test");
        
        list.add(report);
        return list;
    }
}
