/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.communications;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tafadzwa
 */
public class RiskTableModel extends AbstractTableModel {

    private String[] columnNames = {
        "Client",
        "Mobile",
        "Policy Type" //1
        , "Notes", "Renewal"

    };
    private Object[][] data = {};

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

//    @Override
//    public Class getColumnClass(int c){
//
//        return getValueAt(0,c).getClass();
//
//    }
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setData(Object[][] data) {
        if (data != null) {
            this.data = data;
        }
    }

}
