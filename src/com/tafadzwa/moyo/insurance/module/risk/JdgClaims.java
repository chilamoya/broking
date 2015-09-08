/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.risk;

import com.innate.cresterp.insurance.risk.entities.Claim;
import com.innate.cresterp.insurance.risk.entities.ClaimConfiguration;
import com.innate.cresterp.insurance.risk.entities.Risk;
import com.innate.cresterp.insurance.risk.persistence.ClaimConfigurationJpaController;
import com.innate.cresterp.insurance.risk.persistence.ClaimJpaController;
import com.innate.cresterp.medical.hospital.entities.PatientRecord;
import com.innate.erp.broking.claims.Configuration;
import com.tafadzwa.moyo.insurance.module.risk.table.models.ClaimsTableModel;
import com.innate.erp.broking.claims.JdgQuickView;
import com.tafadzwa.moyo.insurance.module.reports.beans.DebitNoteBean;
import com.tafadzwa.moyo.insurance.module.reports.beans.WReportBean;
import datechooser.model.exeptions.IncompatibleDataExeption;
import datechooser.model.multiple.Period;
import datechooser.model.multiple.PeriodSet;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.Query;
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
public class JdgClaims extends javax.swing.JDialog {

    private final ClaimConfigurationJpaController claimConfigManager = new ClaimConfigurationJpaController();
    private final ClaimJpaController claimsManager = new ClaimJpaController();
    private List<ClaimConfiguration> configurations = new ArrayList<ClaimConfiguration>();
    private List<Claim> claims = new ArrayList<Claim>();
    private final Risk currentRisk;
    private Claim claim;
    private boolean isUpdate;
    private ClaimsTableModel claimModel = new ClaimsTableModel();

    /**
     * Creates new form JdgClaims
     *
     * @param parent
     * @param modal
     * @param currentRisk
     * @param isView
     * @param isUpdate
     * @param claim
     */
    public JdgClaims(java.awt.Frame parent, boolean modal, Risk currentRisk, boolean isView, boolean isUpdate, Claim claim) {
        super(parent, modal);
        initComponents();
        this.currentRisk = currentRisk;
        this.claim = claim;
        this.isUpdate = isUpdate;
        jTable1.setModel(claimModel);

        loadConfigurations();
        if (isView) {
            jTabbedPane1.setSelectedIndex(2);
        } else {
            jTabbedPane1.setSelectedIndex(1);
        }
        if (isUpdate) {
            setupFields();
        }
        loadClaims(currentRisk.getClient());
    }

    private void loadClaims(PatientRecord client) {

        Query q = claimsManager.getEntityManager().createQuery("Select c from Claim c where c.claimPolicy.client = ?1");
        q.setParameter(1, client);
        claims = q.getResultList();

        List<Claim> required = new ArrayList<Claim>();

        for (Claim c : claims) {
            if (!c.getClaimStatus().isCloseClaim()) {
                required.add(c);
            }
        }

        claims.clear();
        claims = required;

        Object[][] data = new Object[claims.size()][];
        int i = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        for (Claim line : claims) {

            if (!line.getClaimStatus().isCloseClaim()) {
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
        claimModel.setData(data);
        claimModel.fireTableDataChanged();

    }

    private void setupFields() {
        jtxtBetterment.setText(claim.getBetterment().toString());
        claim.setClaimPolicy(currentRisk);

        int index = 0;
        //Find claim status index as same as the one in question
        for (ClaimConfiguration cs : claimStatuses) {

            if (cs.getValue().trim().equals(claim.getClaimStatus().getValue().trim())) {
                jcboClaimStatus.setSelectedIndex(index);
            }
            index += 1;
        }

        index = 0;
        for (ClaimConfiguration cs : claimTypes) {
            if (cs.getValue().trim().equals(claim.getClaimType().getValue().trim())) {
                jcboClaimType.setSelectedIndex(index);
            }
            index += 1;

        }

        index = 0;
        for (ClaimConfiguration cs : premiumStatuses) {
            if (cs.getValue().trim().equals(claim.getPremiumStatus().getValue().trim())) {
                jcboPremiumStatus.setSelectedIndex(index);
            }
            index += 1;
        }

        jtxtClaimant.setText(claim.getClaimant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(claim.getDateOfLoss());
        Period period = new Period(calendar);
        PeriodSet ps = new PeriodSet(period);

        try {
            jtxtDateOfLoss.setDefaultPeriods(ps);

            calendar.setTime(claim.getDatesubmittedNotification());
            period = new Period(calendar);
            ps = new PeriodSet(period);
            jtxtSubmittedDate.setDefaultPeriods(ps);

            calendar.setTime(claim.getFollowUpDate());
            period = new Period(calendar);
            ps = new PeriodSet(period);
            jtxtFollowupDate.setDefaultPeriods(ps);

            calendar.setTime(claim.getRegistrationDate());
            period = new Period(calendar);
            ps = new PeriodSet(period);
            jtxtRegistrationDate.setDefaultPeriods(ps);

        } catch (IncompatibleDataExeption ex) {
            Exceptions.printStackTrace(ex);
        }

        jtxtExcess.setText(claim.getExcess().toString());
        jtxtGrossClaim.setText(claim.getGrossClaim().toString());
        jtxtNetClaim.setText(claim.getNetClaim().toString());
        jtxtRemarks.setText(claim.getRemarks());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcboSettingsPS = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jcboSettingsCS = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jcboSettingsCT = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton4 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton5 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jtxtClaimant = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jcboClaimType = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jtxtRegistrationDate = new datechooser.beans.DateChooserCombo();
        jtxtSubmittedDate = new datechooser.beans.DateChooserCombo();
        jLabel7 = new javax.swing.JLabel();
        jtxtDateOfLoss = new datechooser.beans.DateChooserCombo();
        jLabel8 = new javax.swing.JLabel();
        jcboClaimStatus = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxtRemarks = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jtxtGrossClaim = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jtxtExcess = new javax.swing.JTextField();
        jtxtBetterment = new javax.swing.JTextField();
        jtxtNetClaim = new javax.swing.JTextField();
        jcboPremiumStatus = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jtxtFollowupDate = new datechooser.beans.DateChooserCombo();
        jLabel16 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        jButton6 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jPanel3.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel1.text")); // NOI18N

        jcboSettingsPS.setEditable(true);
        jcboSettingsPS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel2.text")); // NOI18N

        jcboSettingsCS.setEditable(true);
        jcboSettingsCS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel3.text")); // NOI18N

        jcboSettingsCT.setEditable(true);
        jcboSettingsCT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcboSettingsCT, 0, 428, Short.MAX_VALUE)
                    .addComponent(jcboSettingsPS, 0, 428, Short.MAX_VALUE)
                    .addComponent(jcboSettingsCS, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jcboSettingsPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jcboSettingsCS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jcboSettingsCT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(386, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setAutoscrolls(true);

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);
        jToolBar1.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton5, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel4.setAutoscrolls(true);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel4.text")); // NOI18N

        jtxtClaimant.setText(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jtxtClaimant.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel5.text")); // NOI18N

        jcboClaimType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel8.text")); // NOI18N

        jcboClaimStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel10.text")); // NOI18N

        jtxtRemarks.setColumns(20);
        jtxtRemarks.setRows(5);
        jScrollPane1.setViewportView(jtxtRemarks);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel11.text")); // NOI18N

        jtxtGrossClaim.setText(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jtxtGrossClaim.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel12.text")); // NOI18N

        jtxtExcess.setText(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jtxtExcess.text")); // NOI18N

        jtxtBetterment.setText(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jtxtBetterment.text")); // NOI18N

        jtxtNetClaim.setText(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jtxtNetClaim.text")); // NOI18N
        jtxtNetClaim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtNetClaimFocusLost(evt);
            }
        });

        jcboPremiumStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel13.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel14.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel15.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jLabel16.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(65, 65, 65)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcboClaimType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtClaimant)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtNetClaim, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jtxtBetterment, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtSubmittedDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jtxtRegistrationDate, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                                    .addComponent(jtxtDateOfLoss, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(197, 197, 197))
                            .addComponent(jScrollPane1)
                            .addComponent(jtxtGrossClaim)
                            .addComponent(jtxtExcess, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jcboPremiumStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jcboClaimStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jtxtFollowupDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(197, 197, 197)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtClaimant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jcboClaimType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jtxtRegistrationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtSubmittedDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDateOfLoss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtFollowupDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jcboClaimStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel11))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtGrossClaim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtExcess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBetterment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtNetClaim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboPremiumStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

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
        jScrollPane2.setViewportView(jTable1);

        jToolBar2.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton6, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton6.text")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton6);
        jToolBar2.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(jButton7, org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jButton7.text")); // NOI18N
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton7);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(JdgClaims.class, "JdgClaims.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

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
        //Save the Premium status in this field use PREMIUM STATUS

        ClaimConfiguration cc = new ClaimConfiguration();
        cc.setConfig(Configuration.PREMIUM_STATUS.toString());
        cc.setValue(jcboSettingsPS.getSelectedItem().toString());
        claimConfigManager.create(cc);

        NotifyDescriptor.Message message = new NotifyDescriptor.Message("Premium Status  saved successfully",
                NotifyDescriptor.INFORMATION_MESSAGE);

        Object result = DialogDisplayer.getDefault().notify(message);
        loadConfigurations();
    }//GEN-LAST:event_jButton1ActionPerformed

    private final List<ClaimConfiguration> claimStatuses = new ArrayList<ClaimConfiguration>();
    private final List<ClaimConfiguration> premiumStatuses = new ArrayList<ClaimConfiguration>();
    private final List<ClaimConfiguration> claimTypes = new ArrayList<ClaimConfiguration>();

    private void loadConfigurations() {
        jcboSettingsCS.removeAllItems();
        jcboSettingsCT.removeAllItems();
        jcboSettingsPS.removeAllItems();
        jcboClaimStatus.removeAllItems();
        jcboClaimType.removeAllItems();
        jcboPremiumStatus.removeAllItems();
        claimStatuses.clear();
        claimTypes.clear();
        premiumStatuses.clear();

        configurations = claimConfigManager.findClaimConfigurationEntities();

        for (ClaimConfiguration cc : configurations) {
            if (cc.getConfig().trim().equals(Configuration.CLAIM_TYPE.toString().trim())) {

                jcboSettingsCT.addItem(cc.getValue());
                jcboClaimType.addItem(cc.getValue());
                claimTypes.add(cc);

            }

            if (cc.getConfig().trim().equals(Configuration.CLAIM_STATUS.toString().trim())) {

                jcboSettingsCS.addItem(cc.getValue());
                jcboClaimStatus.addItem(cc.getValue());
                claimStatuses.add(cc);

            }

            if (cc.getConfig().trim().equals(Configuration.PREMIUM_STATUS.toString().trim())) {

                jcboSettingsPS.addItem(cc.getValue());
                jcboPremiumStatus.addItem(cc.getValue());
                premiumStatuses.add(cc);
            }

        }

    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ClaimConfiguration cc = new ClaimConfiguration();
        cc.setConfig(Configuration.CLAIM_STATUS.toString());
        cc.setValue(jcboSettingsCS.getSelectedItem().toString());
        claimConfigManager.create(cc);

        NotifyDescriptor.Message message = new NotifyDescriptor.Message("Claim Status  saved successfully",
                NotifyDescriptor.INFORMATION_MESSAGE);

        Object result = DialogDisplayer.getDefault().notify(message);
        loadConfigurations();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        ClaimConfiguration cc = new ClaimConfiguration();
        cc.setConfig(Configuration.CLAIM_TYPE.toString());
        cc.setValue(jcboSettingsCT.getSelectedItem().toString());
        claimConfigManager.create(cc);

        NotifyDescriptor.Message message = new NotifyDescriptor.Message("Claim Type saved successfully",
                NotifyDescriptor.INFORMATION_MESSAGE);

        Object result = DialogDisplayer.getDefault().notify(message);
        loadConfigurations();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        // TODO add your handling code here:
        JdgQuickView quickView = new JdgQuickView(null, true, claim);
        quickView.setVisible(true);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try {
            Claim claim = new Claim();
            claim.setBetterment(new Double(jtxtBetterment.getText()));
            claim.setClaimPolicy(currentRisk);
            claim.setClaimStatus(claimStatuses.get(jcboClaimStatus.getSelectedIndex()));
            claim.setClaimType(claimTypes.get(jcboClaimType.getSelectedIndex()));
            claim.setClaimant(jtxtClaimant.getText());
            claim.setDateOfLoss(jtxtDateOfLoss.getCurrent().getTime());
            claim.setDatesubmittedNotification(jtxtSubmittedDate.getCurrent().getTime());
            claim.setExcess(new Double(jtxtExcess.getText()));
            claim.setFollowUpDate(jtxtFollowupDate.getCurrent().getTime());
            claim.setGrossClaim(new Double(jtxtGrossClaim.getText()));
            claim.setNetClaim(new Double(jtxtNetClaim.getText()));
            claim.setPremiumStatus(premiumStatuses.get(jcboPremiumStatus.getSelectedIndex()));
            claim.setRegistrationDate(jtxtRegistrationDate.getCurrent().getTime());
            claim.setRemarks(jtxtRemarks.getText());
            if (isUpdate) {
                claim.setId(this.claim.getId());
                claimsManager.edit(claim);
                showClaimsFaceSheet(claim);
            } else {
                claimsManager.create(claim);
                showClaimsFaceSheet(claim);
            }
            NotifyDescriptor.Message message = new NotifyDescriptor.Message("Claim  saved successfully",
                    NotifyDescriptor.INFORMATION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(message);
            this.dispose();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    public void showClaimsFaceSheet(Claim claim) throws JRException {

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/newclaimReport.jrxml",
                null,
                false);

        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        List<WReportBean> list = new ArrayList<WReportBean>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        WReportBean bean = new WReportBean();
        StringBuilder sb = new StringBuilder();
        sb.append("Claimant: ").append(claim.getClaimant()).append("\n");
        String name = claim.getClaimPolicy().getClient().getName() + " "
                + claim.getClaimPolicy().getClient().getSurname();
        sb.append("Insured: ").append(name).append("\n");
        sb.append("Claim Type: ").append(claim.getClaimType().getValue());
        sb.append("Date of Registration: ").append(sdf.format(claim.getRegistrationDate())).append("\n");
        sb.append("Date Submitted: ").append(sdf.format(claim.getDatesubmittedNotification())).append("\n");
        sb.append("Date of Loss: ").append(sdf.format(claim.getDateOfLoss())).append("\n");
        sb.append("Follow Up Date: ").append(sdf.format(claim.getFollowUpDate())).append("\n");
        sb.append("Process Status: ").append(claim.getPremiumStatus().getValue()).append("\n");
        sb.append("Remarks: ").append(claim.getRemarks()).append("\n");
        sb.append("Gross Claim: USD").append(claim.getGrossClaim()).append("\n");
        sb.append("Excess: USD").append(claim.getExcess()).append("\n");
        sb.append("Betterment: USD").append(claim.getBetterment()).append("\n");
        sb.append("Net Claim: USD").append(claim.getNetClaim()).append("\n");
        sb.append("Claim Status: ").append(claim.getClaimStatus().getValue()).append("\n");

        bean.setsField1(sb.toString());
        bean.setTitle("Claims Face Sheet");

        list.add(bean);

////        JasperPrint jp = null;
////       
////            JasperReport jr = JasperCompileManager.compileReport(reportName);
////            jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));
////            
////            
////    private  static JasperPrint creatReport (String filename, List list) throws JRException{
////	     
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

        JasperPrintManager.printReport(jasperPrint, true);
    }


    private void jtxtNetClaimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNetClaimFocusLost
        // TODO add your handling code here:
        try {
            Double gc = new Double(jtxtGrossClaim.getText());
            Double ex = new Double(jtxtExcess.getText());
            Double bt = new Double(jtxtBetterment.getText());

            Double net = gc - ex - bt;
            jtxtNetClaim.setText(net.toString());
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_jtxtNetClaimFocusLost

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        isUpdate = true;
        claim = claims.get(jTable1.getSelectedRow());
        setupFields();
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        claim = claims.get(jTable1.getSelectedRow());
        JdgQuickView quickView = new JdgQuickView(null, true, claim);
        quickView.setVisible(true);

    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
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
            java.util.logging.Logger.getLogger(JdgClaims.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JdgClaims.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JdgClaims.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JdgClaims.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JdgClaims dialog = new JdgClaims(new javax.swing.JFrame(), true, new Risk(), true, true, new Claim());
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
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JComboBox jcboClaimStatus;
    private javax.swing.JComboBox jcboClaimType;
    private javax.swing.JComboBox jcboPremiumStatus;
    private javax.swing.JComboBox jcboSettingsCS;
    private javax.swing.JComboBox jcboSettingsCT;
    private javax.swing.JComboBox jcboSettingsPS;
    private javax.swing.JTextField jtxtBetterment;
    private javax.swing.JTextField jtxtClaimant;
    private datechooser.beans.DateChooserCombo jtxtDateOfLoss;
    private javax.swing.JTextField jtxtExcess;
    private datechooser.beans.DateChooserCombo jtxtFollowupDate;
    private javax.swing.JTextField jtxtGrossClaim;
    private javax.swing.JTextField jtxtNetClaim;
    private datechooser.beans.DateChooserCombo jtxtRegistrationDate;
    private javax.swing.JTextArea jtxtRemarks;
    private datechooser.beans.DateChooserCombo jtxtSubmittedDate;
    // End of variables declaration//GEN-END:variables
}
