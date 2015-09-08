/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.communications;

import com.innate.cresterp.insurance.risk.entities.Claim;
import com.innate.cresterp.insurance.risk.entities.ClaimConfiguration;
import com.innate.cresterp.insurance.risk.entities.Risk;
import com.innate.cresterp.insurance.risk.persistence.ClaimConfigurationJpaController;
import com.innate.cresterp.insurance.risk.persistence.ClaimJpaController;
import com.innate.cresterp.insurance.risk.persistence.RiskJpaController;
import com.innate.cresterp.medical.hospital.entities.PatientRecord;
import com.innate.cresterp.medical.hospital.persistence.PatientRecordJpaController;
import com.innate.cresterp.security.SecuritySettings;
import com.innate.cresterp.security.communication.SendSMS;
import com.innate.cresterp.security.entities.Company;
import com.innate.cresterp.security.persistence.CompanyJpaController;
import com.innate.erp.broking.claims.Configuration;
import com.tafadzwa.moyo.insurance.module.reports.beans.ReceiptsBean;
import com.tafadzwa.moyo.insurance.module.risk.JdgClaims;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;
import javax.swing.DefaultListModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author Tafadzwa
 */
public class JdgCommunication extends javax.swing.JDialog {

    final RiskJpaController riskJpaController = new RiskJpaController();
    final PatientRecordJpaController clientManager = new PatientRecordJpaController();
    private List<Risk> risks = new ArrayList<Risk>();
    private RiskTableModel riskModel = new RiskTableModel();
    private ClaimsTableModel claimsTableModel = new ClaimsTableModel();

    private DefaultListModel listModel = new DefaultListModel();
    private boolean sendSingleUser;
    private final ClaimConfigurationJpaController claimConfigManager = new ClaimConfigurationJpaController();
    private final ClaimJpaController claimsManager = new ClaimJpaController();
    private List<ClaimConfiguration> configurations = new ArrayList<ClaimConfiguration>();
    private List<Claim> claims = new ArrayList<Claim>();

    /**
     * Creates new form JdgCommunication
     */
    public JdgCommunication(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        postInitComponents();
    }

    Company company;

    private void postInitComponents() {
        jTable1.setModel(riskModel);
        risks = retrieveRiskWithFilter();
        displayPremiums();
        jList1.setModel(listModel);
        jTable2.setModel(claimsTableModel);
        displayConfigurations();
        displayAllClaimsDue();
        company = new CompanyJpaController(null).findCompanyEntities().get(0);

    }

    private void displayConfigurations() {
        jcboClaimStatus.removeAllItems();
        Query q = riskJpaController.getEntityManager().createQuery(
                "Select c From ClaimConfiguration c Where c.config = ?1 ");
        q.setParameter(1, Configuration.CLAIM_STATUS.toString());
        configurations = q.getResultList();

        for (ClaimConfiguration cc : configurations) {
            jcboClaimStatus.addItem(cc.getValue());
        }

    }

    private void displayClaimsDue() {

        try {
            Query q = riskJpaController.getEntityManager().createQuery(
                    "Select c From Claim c Where c.claimStatus.configValue = ?1 and c.followUpDate <= ?2"
                            + "  AND c.claimType.closeClaim = 0");
            q.setParameter(1, configurations.get(jcboClaimStatus.getSelectedIndex()).getValue());
            q.setParameter(2, new Date());

            claims = q.getResultList();
            showClaims();
        } catch (Exception ex) {

        }

    }

    private void displayAllClaimsDue() {

     
  
        try {
            Query q = riskJpaController.getEntityManager().createQuery(
                    //     "Select c From Claim c Where c.claimStatus.configValue = ?1 and c.followUpDate <= ?2");
                    "Select c From Claim c Where  c.followUpDate <= ?2 AND c.claimType.closeClaim = 0");
            //  q.setParameter(1, configurations.get(jcboClaimStatus.getSelectedIndex()).getValue());
            q.setParameter(2, new Date());

            claims = q.getResultList();
            showClaims();
        } catch (Exception ex) {

        }

    }

    private void displayPremiums() {

        try {

            Object[][] data = new Object[risks.size()][];
            int i = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

            for (Risk line : risks) {

                Calendar effetive = Calendar.getInstance();
                effetive.setTime(sdf.parse(sdf.format(line.getPolicyPeriod().getEffectiveDate())));

                Calendar inception = Calendar.getInstance();
                inception.setTime(sdf.parse(sdf.format(line.getPolicyPeriod().getInception())));

                Calendar renewalDate = Calendar.getInstance();
                renewalDate.setTime(sdf.parse(sdf.format(line.getPolicyPeriod().getRenewal())));

                //SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd"); 
                data[i++] = new Object[]{
                    line.getClient().getName() + " " + line.getClient().getSurname(),
                    line.getClient().getCell(),
                    line.getPolicyType().getPolicyType(),
                    line.getDescription(),
                    sdf.format(line.getPolicyPeriod().getRenewal()),};

            }
            riskModel.setData(data);
            riskModel.fireTableDataChanged();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void showClaims() {

        try {

            List<Claim> required = new ArrayList<Claim>();
            
            for (Claim c: claims){
                if (!c.getClaimStatus().isCloseClaim()){
                    required.add(c);
                }
            }
            
            claims.clear();
            claims = required;
            
            Object[][] data = new Object[required.size()][];
            int i = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

            for (Claim line : required) {
         //SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd"); 

                if (!line.getClaimStatus().isCloseClaim()){
                data[i++] = new Object[]{
                    line.getClaimPolicy().getInsurer().getName(),
                    line.getClaimant(),
                    line.getClaimPolicy().getClient().getName() + " "
                    + line.getClaimPolicy().getClient().getSurname(),
                    line.getRemarks(),
                    sdf.format(line.getRegistrationDate()),
                    sdf.format(line.getFollowUpDate()),
                    line.getPremiumStatus().getValue(),
                    line.getClaimStatus().getValue(),};
                }
            }
            claimsTableModel.setData(data);
            claimsTableModel.fireTableDataChanged();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public
            void processRisksFilter(String range) {
        if (range.equals("Due For Renewal")) {

            risks = retrieveRiskWithFilter();
            displayPremiums();

        }
        if (range.equals("Expired")) {
            risks = retrieveRiskForExpired();
            displayPremiums();
        }
        if (range.equals("Lapsed")) {
            risks = retrieveRiskForLapsed();
            displayPremiums();
        }
    }

    private List<Risk> retrieveRiskWithFilter() {

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, 21);

        Query q = riskJpaController.getEntityManager().createQuery(
                "Select r From Risk r Where r.policyPeriod.renewal <= ?1 and r.policyPeriod.renewal >= ?2");
        q.setParameter(1, cal.getTime());
        q.setParameter(2, currentDate);

        return q.getResultList();

    }

    private List<Risk> retrieveRiskForExpired() {

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, -365);

        Query q = riskJpaController.getEntityManager().createQuery(
                "Select r From Risk r Where r.policyPeriod.renewal >= ?1 and r.policyPeriod.renewal <= ?2");
        q.setParameter(1, cal.getTime());
        q.setParameter(2, currentDate);

        //  return null;
        return q.getResultList();

    }

    private List<Risk> retrieveRiskForLapsed() {

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, -365);

        Query q = riskJpaController.getEntityManager().createQuery(
                "Select r From Risk r Where r.policyPeriod.renewal >= ?1");
        q.setParameter(1, cal.getTime());

        return q.getResultList();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private final String USER_AGENT = "Mozilla/5.0";

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jcboClaimStatus = new javax.swing.JComboBox();
        jButton8 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jLabel1.text")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Due For Renewal", "Expired", "Lapsed", " " }));

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton4.text")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton5, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton5.text")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addGap(12, 12, 12))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jLabel2.text")); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Renewal Template", "Expiry Template", "Elapsed Template", "Standard Communication" }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextArea1CaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(jTextArea1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton6, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton6.text")); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(43, 43, 43)
                        .addComponent(jComboBox2, 0, 676, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6)
                    .addComponent(jLabel3))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jList1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jList1.border.title"))); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton9, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton9.text")); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton10, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton10.text")); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10))
                .addContainerGap())
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTable2);

        org.openide.awt.Mnemonics.setLocalizedText(jButton7, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton7.text")); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jLabel4.text")); // NOI18N

        jcboClaimStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcboClaimStatus.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcboClaimStatusItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton8, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton8.text")); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton11, org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jButton11.text")); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 807, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton7))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(59, 59, 59)
                .addComponent(jcboClaimStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jcboClaimStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8)
                    .addComponent(jButton11))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(JdgCommunication.class, "JdgCommunication.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        processRisksFilter(jComboBox1.getSelectedItem().toString());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        JdgEditMobile editMobile = new JdgEditMobile(null, rootPaneCheckingEnabled, risks.get(jTable1.getSelectedRow()).getClient(), clientManager);
        editMobile.setLocationByPlatform(true);
        editMobile.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        processRisksFilter(jComboBox1.getSelectedItem().toString());

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        // TODO add your handling code here:
        if (jComboBox2.getSelectedIndex() == 0) {
            jTextArea1.setText(
                    " Dear Client please be advised that your <policytype> policy is due for renewal on <renewal>"
            );
        }

        if (jComboBox2.getSelectedIndex() == 1) {
            jTextArea1.setText(
                    " Dear Client please be advised that your <policytype> policy expired on <expired>"
            );
        }

        if (jComboBox2.getSelectedIndex() == 2) {
            jTextArea1.setText(
                    " Dear Client please be advised that your <policytype> policy has lapsed"
            );
        }

        if (jComboBox2.getSelectedIndex() == 3) {
            jTextArea1.setText(
                    "...this a sample message. Please type a standard message of not more than 160 characters"
            );
        }
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jTextArea1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextArea1CaretUpdate
        // TODO add your handling code here:
        jLabel3.setText("characters found: " + jTextArea1.getText().length());
    }//GEN-LAST:event_jTextArea1CaretUpdate

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        prepareSending();
        sendSingleUser = true;
    }//GEN-LAST:event_jButton3ActionPerformed

    private void prepareSending() {
        jTabbedPane1.setSelectedIndex(1);
        jComboBox2.setSelectedIndex(jComboBox1.getSelectedIndex());
        jComboBox2ItemStateChanged(null);
        listModel.clear();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        sendSingleUser = false;
        prepareSending();
    }//GEN-LAST:event_jButton2ActionPerformed

    List<ReceiptsBean> numberList = new ArrayList<ReceiptsBean>();

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        jLabel3.setText("Processing...Please wait...");
        listModel.clear();
        numberList.clear();
        readMessageFromFile();

        if (sendSingleUser) {

            Risk risk = risks.get(jTable1.getSelectedRow());
            if (risk.getClient().getCell().length() > 13) {

                ReceiptsBean b = new ReceiptsBean();
                b.setName(risk.getClient().getName() + " " + risk.getClient().getSurname());
                b.setReportName(risk.getClient().getCell());
                numberList.add(b);

            } else {
                sendTextMessage(risk.getClient().getCell(), processMessage(jTextArea1.getText().trim(), risk), company);
            }
        } else {

            for (Risk risk : risks) {

                //if message length is not for number 263733863515
                if (risk.getClient().getCell().length() > 13) {

                    ReceiptsBean b = new ReceiptsBean();
                    b.setName(risk.getClient().getName() + " " + risk.getClient().getSurname());
                    b.setReportName(risk.getClient().getCell());
                    numberList.add(b);

                } else {
                    sendTextMessage(risk.getClient().getCell(), processMessage(jTextArea1.getText().trim(), risk), company);
                }
            }

        }
        NotifyDescriptor.Message message = new NotifyDescriptor.Message("Message(s) has been sent successfully",
                NotifyDescriptor.INFORMATION_MESSAGE);
        Object result = DialogDisplayer.getDefault().notify(message);
        jLabel3.setText(messages.size() + "/" + risks.size() + " messages sent successfully");


    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here: Open Claims window and pass the selected claim to it

        JdgClaims jdgClaims = new JdgClaims(null, true,
                claims.get(jTable2.getSelectedRow()).getClaimPolicy(),
                false, true, claims.get(jTable2.getSelectedRow()));
        jdgClaims.setVisible(true);

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jcboClaimStatusItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcboClaimStatusItemStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_jcboClaimStatusItemStateChanged

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here: 
        displayClaimsDue();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        clearMessagesFile();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        try {
            // TODO add your handling code here:
            if (numberList.isEmpty()) {
                List<PatientRecord> customers = new ArrayList<PatientRecord>();
                customers = new PatientRecordJpaController().findPatientRecordEntities();
                for (PatientRecord c : customers) {
                    //263733863515
                    if (c.getCell().length() < 12) {
                        ReceiptsBean b = new ReceiptsBean();
                        b.setName(c.getName() + " " + c.getSurname());
                        b.setReportName(c.getCell());
                        numberList.add(b);
                    }
                }
                showDebitNOte(numberList);
            } else {
                showDebitNOte(numberList);
            }
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        displayAllClaimsDue();
        showClaims();
        
    }//GEN-LAST:event_jButton11ActionPerformed

    private String processMessage(String message, Risk risk) {

        message = message.replaceAll("Client", risk.getClient().getName().trim());
        message = message.replaceAll("<policytype>", risk.getDescription().trim());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        message = message.replaceAll("<expired>", sdf.format(risk.getPolicyPeriod().getExpiry()));

        message = message.replaceAll("<renewal>", sdf.format(risk.getPolicyPeriod().getRenewal()));

        return message;

    }

    public static String replaceAll(String text, String searchString, String replacementString) {
        StringBuffer sBuffer = new StringBuffer();
        int pos = 0;
        while ((pos = text.indexOf(searchString)) != -1) {
            sBuffer.append(text.substring(0, pos) + replacementString);
            text = text.substring(pos + searchString.length());
        }
        sBuffer.append(text);
        return sBuffer.toString();
    }

    public void sendTextMessage1(String buildURL, String parameters) {

        try {

            URL url;
            URLConnection urlConn;
            DataOutputStream dos;
            DataInputStream dis;

            url = new URL(buildURL);
            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            dos = new DataOutputStream(urlConn.getOutputStream());
            String message = "NEW_ITEM=" + URLEncoder.encode(parameters);
            dos.writeBytes(parameters);
            dos.flush();
            dos.close();

            // the server responds by saying 
            // "SUCCESS" or "FAILURE"
            dis = new DataInputStream(urlConn.getInputStream());
            String s = dis.readLine();
            dis.close();

            if (s.equals("SUCCESS")) {

                //   JOptionPane.showMessageDialog(rootPane, "Messages su");
            } else {

            }

        } // end of "try"
        catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    Vector messages = new Vector();

    private void displayMessage(final String message) {
        new Thread(new Runnable() {
            public void run() {
                listModel.addElement(message);
            }
        }).start();

    }

    private void clearMessagesFile() {
        FileWriter fileWritter = null;
        try {
            fileWritter = new FileWriter(createFileName().getCanonicalPath());
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write("");
            bufferWritter.close();
            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
            // Exceptions.printStackTrace(ex);
        } finally {
            try {
                fileWritter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                //    Exceptions.printStackTrace(ex);
            }
        }
    }

    private void appendMessageToFile(String message) {
        FileWriter fileWritter = null;
        try {
            fileWritter = new FileWriter(createFileName().getCanonicalPath(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write("\n" + message);
            bufferWritter.close();
            System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
            //  Exceptions.printStackTrace(ex);
        } finally {
            try {
                fileWritter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                //  Exceptions.printStackTrace(ex);
            }
        }
    }

    private void readMessageFromFile() {
        messages.clear();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(createFileName()));
            String line = null;
            while ((line = br.readLine()) != null) {
                messages.add(line);
                System.out.println(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
//            Exceptions.printStackTrace(ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }

    }

    private File createFileName() {
        File file = null;
        try {
            //use . to get current directory
            File dir = new File(".");
            file = new File(dir.getCanonicalPath() + File.separator + "messages.txt");
            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            //   Exceptions.printStackTrace(ex);
        }

        return file;
    }

    public void sendTextMessage(final String mobile, final String message, final Company company) {

        // in the thread now...
        // do something.... 
        try {

            String display = "Message to: " + mobile + " SENT";

            if (!messages.contains(display)) {
                String[] result = new SecuritySettings().retrieveLicenseDetails();

                SendSMS test = new SendSMS(result[0], result[1], company);
                test.sendMessage(message, mobile);

                messages.add(display);
                displayMessage(display);

                //Add to the file of messages here 
                appendMessageToFile(display);

            } else {
                displayMessage("Message already sent to: " + mobile);
            }
        } catch (IOException ex) {
            listModel.addElement("Failed to send message to: " + mobile);
            ex.printStackTrace();
        } catch (Exception ex) {
            // Exceptions.printStackTrace(ex);
        }

    }

    public void showDebitNOte(List<ReceiptsBean> list) throws JRException {

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/mobileNumberExceptions.jrxml",
                null,
                false);

        String reportName = reportFile.getPath();
        JasperPrint jasperPrint = new JasperPrint();
        JasperReport jasperReport;

        try {

            jasperReport = JasperCompileManager
                    .compileReport(reportName);

            jasperPrint = JasperFillManager
                    .fillReport(jasperReport, null, new JRBeanCollectionDataSource(list));
        } catch (JRException e) {

            e.printStackTrace();
        }

        JasperPrintManager.printReport(jasperPrint, false);
    }

    /**
     *
     * @param args the command line arguments
     *
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JdgCommunication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JdgCommunication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JdgCommunication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JdgCommunication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JdgCommunication dialog = new JdgCommunication(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JComboBox jcboClaimStatus;
    // End of variables declaration//GEN-END:variables
}
