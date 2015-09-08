/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.risk;

import com.innate.cresterp.insurance.risk.entities.Insurer;
import com.innate.cresterp.insurance.risk.entities.PolicyCategory;
import com.innate.cresterp.insurance.risk.entities.PolicyPeriod;
import com.innate.cresterp.insurance.risk.entities.PolicyType;
import com.innate.cresterp.insurance.risk.entities.Receipt;
import com.innate.cresterp.insurance.risk.entities.Risk;
import com.innate.cresterp.insurance.risk.entities.RiskPremium;
import com.innate.cresterp.insurance.risk.entities.SystemUser;
import com.innate.cresterp.insurance.risk.persistence.InsurerJpaController;
import com.innate.cresterp.insurance.risk.persistence.PolicyCategoryJpaController;
import com.innate.cresterp.insurance.risk.persistence.ReceiptJpaController;
import com.innate.cresterp.insurance.risk.persistence.RiskJpaController;
import com.innate.cresterp.medical.hospital.entities.PatientRecord;
import com.innate.cresterp.medical.hospital.persistence.PatientRecordJpaController;
import com.innate.cresterp.security.PrivilegeManagement;
import com.innate.cresterp.security.SecuritySettings;
import com.innate.cresterp.security.login.JdgLogin;
import com.tafadzwa.moyo.communications.SendMail;
import com.tafadzwa.moyo.communications.SendSMS;
import com.tafadzwa.moyo.insurance.module.reports.beans.DebitNoteBean;
import com.tafadzwa.moyo.insurance.module.risk.table.models.RiskTableModel;
import com.tafadzwa.moyo.insurance.module.roles.RolesEnum;
import datechooser.model.exeptions.IncompatibleDataExeption;
import datechooser.model.multiple.Period;
import datechooser.model.multiple.PeriodSet;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.NotifyDescriptor.Message;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.tafadzwa.moyo.insurance.module.risk//RiskModule//EN",
        autostore = false)
@TopComponent.Description(preferredID = "RiskModuleTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.tafadzwa.moyo.insurance.module.risk.RiskModuleTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_RiskModuleAction",
        preferredID = "RiskModuleTopComponent")

public final class RiskModuleTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider {

    private InsurerJpaController insurerManager = new InsurerJpaController();
    private PatientRecordJpaController clientManager = new PatientRecordJpaController();
    private List<PolicyCategory> categories = new ArrayList<PolicyCategory>();
    private PolicyCategoryJpaController policyCatManager = new PolicyCategoryJpaController();
    private RiskJpaController riskManager = new RiskJpaController();
    private BigDecimal totalSumInsured = new BigDecimal("0");
    private BigDecimal premiumRate = new BigDecimal("0");
    private BigDecimal brokerageRate = new BigDecimal("0");
    private BigDecimal basicPremium = new BigDecimal("0");
    private BigDecimal brokerageAmount = new BigDecimal("0");
    private BigDecimal transactionPremium = new BigDecimal("0");
    private BigDecimal annualPremium = new BigDecimal("0");
    private List<Risk> clientRisks = new ArrayList<Risk>();
    private List<Insurer> insurers = new ArrayList<Insurer>();
    private RiskTableModel riskModel = new RiskTableModel();
    private Lookup.Result result = null;
    private final SaveCookieImpl impl;
    private UndoRedo.Manager manager = new UndoRedo.Manager();
    private final InstanceContent content;
    private PatientRecord customer;
    private boolean editRisk;
    private boolean renewal;
    private boolean override;
    private Risk currentRisk;
    private DefaultListModel debitNotes = new DefaultListModel();
    private ReceiptJpaController receiptManager = new ReceiptJpaController();
    private SystemUser user = null;

    public RiskModuleTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(RiskModuleTopComponent.class, "CTL_RiskModuleTopComponent"));
        setToolTipText(NbBundle.getMessage(RiskModuleTopComponent.class, "HINT_RiskModuleTopComponent"));
        jTable1.setModel(riskModel);

        // categories = policyCatManager.findPolicyCategoryEntities();
//       jtxtClientNumber.setVisible(false);
        initializeManager();
        //Create a new instance of our SaveCookie implementation:
        impl = new SaveCookieImpl();
        //Create a new instance of our dynamic object:
        content = new InstanceContent();

        //Add the dynamic object to the TopComponent Lookup:
        associateLookup(new AbstractLookup(content));
        jTabbedPane1.setSelectedIndex(3);
        override = false;
        insurersLoad();
        categoriesLoad();
        user = new SecuritySettings().getLoggedUser();

    }

    private void categoriesLoad() {
        try {
            categories = policyCatManager.findPolicyCategoryEntities();
            jcboPolicyCategories.removeAllItems();
            jcboPoliciesTypes.removeAllItems();
            for (PolicyCategory pc : categories) {
                jcboPolicyCategories.addItem(pc.getCategory());
                jcboPoliciesTypes.addItem(pc.getCategory());
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void insurersLoad() {
        try {
            insurers = insurerManager.findInsurerEntities();
            jcboInsurer.removeAllItems();
            jcboRiskInsurer.removeAllItems();
            for (Insurer pc : insurers) {
                jcboInsurer.addItem(pc.getName());
                jcboRiskInsurer.addItem(pc.getName());

            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void initializeManager() {

        jtxtClientNumber.getDocument().addUndoableEditListener(manager);
        jtxtClientNumber.getDocument().addDocumentListener(createDocListener());

    }

    public void fire(boolean modified) {
        if (modified) {
            //If the text is modified,
            //we add SaveCookie impl to Lookup:
            content.add(impl);
        } else {
            //Otherwise, we remove the SaveCookie impl from the lookup:
            content.remove(impl);
        }
    }

//    @Override
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        reset();
        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection<PatientRecord> coll = r.allInstances();
        // System.out.println("Collection has: "+r.allItems().size());
        jTabbedPane1.setSelectedIndex(3);
        if (!coll.isEmpty()) {
            for (PatientRecord cust : coll) {

                jtxtClientNumber.setText(cust.getHospitalNumber());
                displayPremiums();

            }
        } else {
            jtxtClientNumber.setText("[no Client]");
        }
    }

    private DocumentListener createDocListener() {
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                fire(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fire(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fire(true);
            }
        };
        return dl;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jmnuAddClaim = new javax.swing.JMenuItem();
        jmnuViewClaims = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtxtTransactionDate = new datechooser.beans.DateChooserCombo();
        jtxtRenewal = new datechooser.beans.DateChooserCombo();
        jtxtExpiry = new datechooser.beans.DateChooserCombo();
        jtxtInception = new datechooser.beans.DateChooserCombo();
        jtxtEffectiveDate = new datechooser.beans.DateChooserCombo();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jtxtCoverNote = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jcboPoliciesTypes = new javax.swing.JComboBox();
        jlblPerson = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jtxtNumberOfRisks = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jtxtTotalSumInsured = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jcboRiskInsurer = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jtxtPremiumRate = new javax.swing.JTextField();
        jlblPremium = new javax.swing.JLabel();
        jtxtTransactionPremium = new javax.swing.JTextField();
        jtxtLevies = new javax.swing.JTextField();
        jtxtStampDuty = new javax.swing.JTextField();
        jtxtBrokerageRate = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jtxtBrokerageAmount = new javax.swing.JTextField();
        jtxtPremiumNet = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jtxtAnnualPremium = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtClientNumber = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jcboPolicyCategories = new javax.swing.JComboBox();
        jButton6 = new javax.swing.JButton();
        jcboInsurer = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel30 = new javax.swing.JLabel();
        jtxtReceipting = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jmnuAddClaim, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jmnuAddClaim.text")); // NOI18N
        jmnuAddClaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuAddClaimActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmnuAddClaim);

        org.openide.awt.Mnemonics.setLocalizedText(jmnuViewClaims, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jmnuViewClaims.text")); // NOI18N
        jmnuViewClaims.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnuViewClaimsActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmnuViewClaims);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel1.border.title"))); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel2.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel7.text")); // NOI18N

        jtxtRenewal.setWeekStyle(datechooser.view.WeekDaysStyle.FULL);

        jtxtExpiry.addCommitListener(new datechooser.events.CommitListener() {
            public void onCommit(datechooser.events.CommitEvent evt) {
                jtxtExpiryOnCommit(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxtEffectiveDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtExpiry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtInception, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addComponent(jtxtRenewal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtTransactionDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jtxtInception, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jtxtExpiry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jtxtRenewal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jtxtTransactionDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jtxtEffectiveDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel19.text")); // NOI18N

        jtxtCoverNote.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtCoverNote.text")); // NOI18N
        jtxtCoverNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCoverNoteActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jlblPerson, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jlblPerson.text")); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jlblPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(113, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel2))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcboPoliciesTypes, 0, 388, Short.MAX_VALUE)
                            .addComponent(jtxtCoverNote, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                        .addGap(174, 174, 174))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jlblPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboPoliciesTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jtxtCoverNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(64, 64, 64))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(636, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(408, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(36, 36, 36))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(123, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel3.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel8.text")); // NOI18N

        jtxtNumberOfRisks.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtNumberOfRisks.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel9.text")); // NOI18N

        jtxtTotalSumInsured.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtTotalSumInsured.text")); // NOI18N
        jtxtTotalSumInsured.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtTotalSumInsuredKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtTotalSumInsuredKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel17.text")); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jcboRiskInsurer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel21.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel17)
                    .addComponent(jLabel21))
                .addGap(43, 43, 43)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboRiskInsurer, 0, 377, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jtxtNumberOfRisks)
                        .addComponent(jtxtTotalSumInsured, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)))
                .addGap(159, 159, 159))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jtxtNumberOfRisks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtTotalSumInsured, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcboRiskInsurer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(36, 36, 36))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel10.text")); // NOI18N

        jtxtPremiumRate.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtPremiumRate.text")); // NOI18N
        jtxtPremiumRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPremiumRateKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtPremiumRateKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPremiumRateKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jlblPremium, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jlblPremium.text")); // NOI18N

        jtxtTransactionPremium.setEditable(false);
        jtxtTransactionPremium.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtTransactionPremium.text")); // NOI18N
        jtxtTransactionPremium.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jtxtTransactionPremiumCaretUpdate(evt);
            }
        });
        jtxtTransactionPremium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtTransactionPremiumActionPerformed(evt);
            }
        });
        jtxtTransactionPremium.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTransactionPremiumFocusGained(evt);
            }
        });
        jtxtTransactionPremium.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtTransactionPremiumKeyTyped(evt);
            }
        });

        jtxtLevies.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtLevies.text")); // NOI18N
        jtxtLevies.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtLeviesKeyTyped(evt);
            }
        });

        jtxtStampDuty.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtStampDuty.text")); // NOI18N
        jtxtStampDuty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtStampDutyKeyTyped(evt);
            }
        });

        jtxtBrokerageRate.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtBrokerageRate.text")); // NOI18N
        jtxtBrokerageRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtBrokerageRateActionPerformed(evt);
            }
        });
        jtxtBrokerageRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtBrokerageRateKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBrokerageRateKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel12.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel13.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel14.text")); // NOI18N

        jtxtBrokerageAmount.setEditable(false);
        jtxtBrokerageAmount.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtBrokerageAmount.text")); // NOI18N
        jtxtBrokerageAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtBrokerageAmountKeyReleased(evt);
            }
        });

        jtxtPremiumNet.setEditable(false);
        jtxtPremiumNet.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtPremiumNet.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel15.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel16.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton5, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton5.text")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jtxtAnnualPremium.setEditable(false);
        jtxtAnnualPremium.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtAnnualPremium.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton14, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton14.text")); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton14)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14)
                            .addComponent(jLabel13)
                            .addComponent(jlblPremium)
                            .addComponent(jLabel12)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addGap(56, 56, 56)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtPremiumRate, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                            .addComponent(jtxtAnnualPremium, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jtxtTransactionPremium, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5))
                            .addComponent(jtxtStampDuty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                            .addComponent(jtxtBrokerageRate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                            .addComponent(jtxtBrokerageAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                            .addComponent(jtxtPremiumNet, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                            .addComponent(jtxtLevies, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))))
                .addGap(22, 22, 22))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jtxtPremiumRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtAnnualPremium, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlblPremium, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtTransactionPremium, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtLevies, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtStampDuty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBrokerageRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jtxtBrokerageAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtPremiumNet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(27, 27, 27)
                .addComponent(jButton14)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(552, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(106, 106, 106))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(23, 23, 23))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jtxtClientNumber.setEditable(false);
        jtxtClientNumber.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtClientNumber.text")); // NOI18N
        jtxtClientNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtClientNumberKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton7, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton7.text")); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton8, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton8.text")); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton9, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton9.text")); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton13, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton13.text")); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton15, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton15.text")); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton18, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton18.text")); // NOI18N
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton4.text")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(63, 63, 63)
                        .addComponent(jtxtClientNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                        .addGap(63, 63, 63))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton18)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxtClientNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addComponent(jButton4)
                    .addComponent(jButton13)
                    .addComponent(jButton15)
                    .addComponent(jButton18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel8.TabConstraints.tabTitle_1"), jPanel8); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel18.text")); // NOI18N

        jcboPolicyCategories.setEditable(true);
        jcboPolicyCategories.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jButton6, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton6.text")); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jcboInsurer.setEditable(true);
        jcboInsurer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel20.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton12, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton12.text")); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jcboInsurer, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton12))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(36, 36, 36)
                        .addComponent(jcboPolicyCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)))
                .addContainerGap(238, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jcboPolicyCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jcboInsurer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12))
                .addContainerGap(374, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel10.TabConstraints.tabTitle"), jPanel10); // NOI18N

        jPanel12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPanel12FocusGained(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane4.setViewportView(jTextArea2);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel30, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jLabel30.text")); // NOI18N

        jtxtReceipting.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jtxtReceipting.setForeground(new java.awt.Color(255, 0, 0));
        jtxtReceipting.setText(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jtxtReceipting.text")); // NOI18N
        jtxtReceipting.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtReceiptingKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtReceiptingKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton16, org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jButton16.text")); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jButton16)
                                .addGap(0, 522, Short.MAX_VALUE))
                            .addComponent(jtxtReceipting))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(jtxtReceipting, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton16)
                .addGap(20, 20, 20))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(RiskModuleTopComponent.class, "RiskModuleTopComponent.jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jtxtBrokerageRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtBrokerageRateActionPerformed
// TODO add your handling code here:
    //     calculateBrokerageAmount();
}//GEN-LAST:event_jtxtBrokerageRateActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
    jTabbedPane1.setSelectedIndex(1);
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
    jTabbedPane1.setSelectedIndex(2);
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    // TODO add your handling code here:
    savePremium();
    reset();

//              if (renewal==true)
//              {
//                  Renewal renewPolicy = new Renewal();
//                  RenewalJpaController renewalManager = new RenewalJpaController();
//                  
//                  renewPolicy.setCreatedDated(new Date());
//                  PolicyPeriod pp = new PolicyPeriod();
//                  pp.setEffectiveDate(jtxtEffectiveDate.getCurrent().getTime());
//                  pp.setExpiry(jtxtExpiry.getCurrent().getTime());
//                  pp.setInception(jtxtInception.getCurrent().getTime());
//                  pp.setRenewal(jtxtRenewal.getCurrent().getTime());
//                  pp.setTransactionDate(jtxtTransactionDate.getCurrent().getTime());
//                  
//                  renewPolicy.setRisk(currentRisk);
//                
//                  
//                  renewPolicy.setPeriod(pp);
//                  
//                //find out about the policy period i they take in new premiums or use the existing 
//                  
//                  renewalManager.create(renewPolicy);
//                  Confirmation message = new NotifyDescriptor.Confirmation(" Policy successfully Renewed\" please enter correct ID to proceed",
//                    NotifyDescriptor.OK_CANCEL_OPTION,
//                    NotifyDescriptor.QUESTION_MESSAGE);
//
//            Object result = DialogDisplayer.getDefault().notify(message);
//                  
//              }

}//GEN-LAST:event_jButton3ActionPerformed

private void jtxtTotalSumInsuredKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTotalSumInsuredKeyTyped
// TODO add your handling code here:
    // calculateBasicPremium();
}//GEN-LAST:event_jtxtTotalSumInsuredKeyTyped

private void jtxtPremiumRateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPremiumRateKeyTyped
// TODO add your handling code here:


}//GEN-LAST:event_jtxtPremiumRateKeyTyped

private void jtxtPremiumRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPremiumRateKeyPressed

}//GEN-LAST:event_jtxtPremiumRateKeyPressed

private void jtxtPremiumRateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPremiumRateKeyReleased
// TODO add your handling code here:

  //  calculateBasicPremium();
}//GEN-LAST:event_jtxtPremiumRateKeyReleased

private void jtxtTotalSumInsuredKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTotalSumInsuredKeyReleased
// TODO add your handling code here:
//    calculateBasicPremium(); 
}//GEN-LAST:event_jtxtTotalSumInsuredKeyReleased

private void jtxtBrokerageAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrokerageAmountKeyReleased
// TODO add your handling code here:

}//GEN-LAST:event_jtxtBrokerageAmountKeyReleased

private void jtxtBrokerageRateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrokerageRateKeyReleased
// TODO add your handling code here:

}//GEN-LAST:event_jtxtBrokerageRateKeyReleased

private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// TODO add your handling code here:
    //   jtxtBasicPremium.setEditable(true);
    override = true;
    JdgOverridePremium overridePremium = new JdgOverridePremium(null, true, jtxtTransactionPremium, this);
    overridePremium.setVisible(true);
    overridePremium.setLocationRelativeTo(this);


}//GEN-LAST:event_jButton5ActionPerformed

    private void displayPremiums() {

        try {
            clientRisks = riskManager.findRiskByClient(clientManager.getpatient(jtxtClientNumber.getText()).get(0));

            Object[][] data = new Object[clientRisks.size()][];
            int i = 0;
            debitNotes.clear();
            for (Risk line : clientRisks) {

                Calendar effetive = Calendar.getInstance();
                effetive.setTime(line.getPolicyPeriod().getEffectiveDate());

                Calendar inception = Calendar.getInstance();
                inception.setTime(line.getPolicyPeriod().getInception());

                debitNotes.addElement(line.getCoverNoteNumber() + " " + line.getPolicyType().getPolicyType());

                Calendar renewalDate = Calendar.getInstance();
                renewalDate.setTime(line.getPolicyPeriod().getRenewal());

//SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd"); 
                data[i++] = new Object[]{
                    line.getRiskPremium().getTransactionType(),
                    line.getPolicyType().getPolicyType(),
                    line.getDescription(),
                    inception.getTime(),
                    renewalDate.getTime(),
                    effetive.getTime(),};

            }
            riskModel.setData(data);
            riskModel.fireTableDataChanged();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


private void jtxtClientNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtClientNumberKeyTyped
// TODO add your handling code here:

}//GEN-LAST:event_jtxtClientNumberKeyTyped

private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
// TODO add your handling code here:
    try {
        override = true;
        categoriesLoad();
        //    jLabel11.setText("Transactional Premium");
        editRisk = true;
        fillFields();
        clearEndorsementFields();
        jTabbedPane1.setSelectedIndex(0);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}//GEN-LAST:event_jButton8ActionPerformed

    private void clearEndorsementFields() {

        jtxtLevies.setText("0");
        jtxtStampDuty.setText("0");
        jtxtBrokerageAmount.setText("0");
        jtxtBrokerageRate.setText("0.0");
        jtxtPremiumNet.setText("0.0");

    }

    private void fillFields() throws IncompatibleDataExeption {
        jlblPerson.setText(currentRisk.getClient().getName() + " " + currentRisk.getClient().getSurname());
        int x = 0;
        for (PolicyCategory pc : categories) {

            if (currentRisk.getPolicyType().getPolicyType().equals(pc.getCategory())) {
                jcboPoliciesTypes.setSelectedIndex(x);
            }
            x++;
        }

        jtxtInception.setFormat(1);
        jtxtEffectiveDate.setFormat(1);
        jtxtExpiry.setFormat(1);
        jtxtTransactionDate.setFormat(1);
        jtxtRenewal.setFormat(1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentRisk.getPolicyPeriod().getInception());
        Period period = new Period(calendar);
        PeriodSet ps = new PeriodSet(period);
        jtxtInception.setDefaultPeriods(ps);

        calendar = Calendar.getInstance();
        calendar.setTime(currentRisk.getPolicyPeriod().getEffectiveDate());
        period = new Period(calendar);
        ps = new PeriodSet(period);
        jtxtEffectiveDate.setDefaultPeriods(ps);

        calendar = Calendar.getInstance();
        calendar.setTime(currentRisk.getPolicyPeriod().getExpiry());

        period = new Period(calendar);
        ps = new PeriodSet(period);
        jtxtExpiry.setDefaultPeriods(ps);

        calendar = Calendar.getInstance();
        calendar.setTime(currentRisk.getPolicyPeriod().getRenewal());
        period = new Period(calendar);
        ps = new PeriodSet(period);
        jtxtRenewal.setDefaultPeriods(ps);

        calendar = Calendar.getInstance();
        calendar.setTime(currentRisk.getPolicyPeriod().getTransactionDate());

        period = new Period(calendar);
        ps = new PeriodSet(period);
        jtxtTransactionDate.setCurrent(calendar);

        jtxtCoverNote.setText(currentRisk.getCoverNoteNumber());
        jtxtTransactionDate.setCurrent(calendar);
        jtxtAnnualPremium.setText(currentRisk.getRiskPremium().getAnnualPremium().toString());
        jtxtNumberOfRisks.setText(currentRisk.getNumberOfRisks().toString());
        jtxtTotalSumInsured.setText(currentRisk.getTotalSumInsured().toString());
        jTextArea1.setText(currentRisk.getDescription());
        jtxtAnnualPremium.setText(currentRisk.getRiskPremium().getAnnualPremium().toString());
        jtxtPremiumRate.setText(currentRisk.getRiskPremium().getPremiumRate().toString());
        jtxtTransactionPremium.setText(currentRisk.getRiskPremium().getBasicPremium().toString());
        jtxtLevies.setText(currentRisk.getRiskPremium().getLevies().toString());
        jtxtStampDuty.setText(currentRisk.getRiskPremium().getStampDuty().toString());
        jtxtBrokerageRate.setText(currentRisk.getRiskPremium().getBrokerageRate().toString());
        jtxtBrokerageAmount.setText(currentRisk.getRiskPremium().getBrokerageAmount().toString());
        jtxtPremiumNet.setText(currentRisk.getRiskPremium().getPremiumNet().toString());
        //   calculateDates();
    }


private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
// TODO add your handling code here:
    try {
        override = true;
        categoriesLoad();
        editRisk = false;
        renewal = false;
        jTabbedPane1.setSelectedIndex(0);
        jlblPerson.setText(clientRisks.get(0).getClient().getName() + " "
                + clientRisks.get(0).getClient().getSurname());
        reset();
        calculateDates();
    } catch (Exception ex) {

    }
}//GEN-LAST:event_jButton7ActionPerformed

private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
// TODO add your handling code here:
    override = true;
    categoriesLoad();
    clearEndorsementFields();
    editRisk = false;
    renewal = true;
    try {
        fillFields();
        jTabbedPane1.setSelectedIndex(0);
        calculateDates();
    } catch (IncompatibleDataExeption ex) {
        Exceptions.printStackTrace(ex);
    }

}//GEN-LAST:event_jButton9ActionPerformed


private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
    try {
        // TODO add your handling code here:
        currentRisk = clientRisks.get(jTable1.getSelectedRow());
        jtxtTransactionPremium.setText(currentRisk.getRiskPremium().getBasicPremium().toString());
        jtxtBrokerageAmount.setText(currentRisk.getRiskPremium().getBrokerageAmount().toString());
        jtxtBrokerageRate.setText(currentRisk.getRiskPremium().getBrokerageRate().toString());

        //jtxtEffectiveDate.
        jtxtLevies.setText(currentRisk.getRiskPremium().getLevies().toString());
        jtxtNumberOfRisks.setText(currentRisk.getNumberOfRisks().toString());
        jtxtPremiumNet.setText(currentRisk.getRiskPremium().getPremiumNet().toString());
        fillFields();
    } catch (IncompatibleDataExeption ex) {
        Exceptions.printStackTrace(ex);
    }

}//GEN-LAST:event_jTable1MousePressed

private void jtxtTransactionPremiumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTransactionPremiumKeyTyped
// TODO add your handling code here:
    //calculateBasicPremium();
    //  calculateBrokerageAmount();
}//GEN-LAST:event_jtxtTransactionPremiumKeyTyped

private void jtxtExpiryOnCommit(datechooser.events.CommitEvent evt) {//GEN-FIRST:event_jtxtExpiryOnCommit
// TODO add your handling code here:
    calculateDates();
}//GEN-LAST:event_jtxtExpiryOnCommit

private void jtxtTransactionPremiumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtTransactionPremiumActionPerformed
// TODO add your handling code here:

}//GEN-LAST:event_jtxtTransactionPremiumActionPerformed

private void jtxtBrokerageRateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrokerageRateKeyTyped
// TODO add your handling code here:
//        calculateBrokerageAmount();
}//GEN-LAST:event_jtxtBrokerageRateKeyTyped

private void jtxtTransactionPremiumCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jtxtTransactionPremiumCaretUpdate
// TODO add your handling code here:

}//GEN-LAST:event_jtxtTransactionPremiumCaretUpdate

private void jtxtTransactionPremiumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTransactionPremiumFocusGained
// TODO add your handling code here:
//        calculateBasicPremium();
}//GEN-LAST:event_jtxtTransactionPremiumFocusGained

private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
// TODO add your handling code here:
    try {
        PolicyCategory category = new PolicyCategory();
        category.setCategory(jcboPolicyCategories.getSelectedItem().toString());
        policyCatManager.create(category);
        Message message = new NotifyDescriptor.Message("Policy Type Successfully saved");

        Object result = DialogDisplayer.getDefault().notify(message);
        categoriesLoad();
    } catch (Exception ex) {

        Exceptions.printStackTrace(ex);
    }
}//GEN-LAST:event_jButton6ActionPerformed

private void jtxtStampDutyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtStampDutyKeyTyped
// TODO add your handling code here:
//    try {
//        calculateBrokerageAmount();
//    }catch(Exception ex){
//        
//    }
}//GEN-LAST:event_jtxtStampDutyKeyTyped

private void jtxtLeviesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLeviesKeyTyped
// TODO add your handling code here:
//     try {
//        calculateBrokerageAmount();
//    }catch(Exception ex){
//        
//    }
}//GEN-LAST:event_jtxtLeviesKeyTyped

private void jtxtCoverNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCoverNoteActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jtxtCoverNoteActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        try {
            Insurer insurer = new Insurer();
            insurer.setName(jcboInsurer.getSelectedItem().toString());
            insurerManager.create(insurer);
            Message message = new NotifyDescriptor.Message("Insurer Successfully saved");

            Object result = DialogDisplayer.getDefault().notify(message);
            insurersLoad();
        } catch (Exception ex) {

            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

    // TODO add your handling code here:
    try {
        Risk risk = clientRisks.get(jTable1.getSelectedRow());
        riskManager.destroy(risk.getId());
        clientRisks = riskManager.findRiskEntities();
        displayPremiums();
    } catch (Exception ex) {

        Exceptions.printStackTrace(ex);
    }

}//GEN-LAST:event_jButton13ActionPerformed

private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
// TODO add your handling code here:
    try {
        calculateBasicPremium();
        //      calculateBrokerageAmount();
    } catch (NumberFormatException ex) {

        Message message = new NotifyDescriptor.Message("Please make sure that you do not put commas \n"
                + "e.g. for $5,000 use 5000 or for cents use 5000.34. On percentages use 0.20 for 20% \n"
                + "\n Please verify that the sum insured, premium rate, levies, "
                + "brokerage rate and stamp duty follow this format");

        Object result = DialogDisplayer.getDefault().notify(message);

    }
}//GEN-LAST:event_jButton14ActionPerformed

private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
// TODO add your handling code here:
    JdgQuickView jdgQuickView = new JdgQuickView(null, renewal, clientRisks.get(jTable1.getSelectedRow()));
    jdgQuickView.setVisible(true);

}//GEN-LAST:event_jButton15ActionPerformed

private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
// TODO add your handling code here:

    PrivilegeManagement management = new PrivilegeManagement();

    String messageDisplay = "";
    if (management.authenticateUserRights(user, RolesEnum.RECEIPTING.toString())) {
        Receipt receipt = new Receipt();
        receipt.setClient(currentRisk.getClient());
        receipt.setReceiptDate(new Date());
        receipt.setSystemUser(user.getId());
        receipt.setRisk(currentRisk);
        receipt.setAmount(new Double(jtxtReceipting.getText()));
        receiptManager.create(receipt);
        messageDisplay = "Client has been successfully Receipted";
        jtxtReceipting.setText("");

    } else {
        messageDisplay = "You are not authorized to Receipt";
    }

    Message message = new NotifyDescriptor.Message(messageDisplay,
            NotifyDescriptor.INFORMATION_MESSAGE);

    Object result = DialogDisplayer.getDefault().notify(message);
}//GEN-LAST:event_jButton16ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        jPopupMenu1.setVisible(true);
        jPopupMenu1.setLocation(jButton18.getLocation());

    }//GEN-LAST:event_jButton18ActionPerformed

    private void jmnuAddClaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuAddClaimActionPerformed
        // TODO add your handling code here:
        //Open the claims window and set the tabs on adding a new claim 
//        CentralLookup cl = CentralLookup.getDefault();
//        Collection infos = cl.lookupAll(Risk.class);
//
//        if (!infos.isEmpty()) {
//            Iterator it = infos.iterator();
//            while (it.hasNext()) {
//                Risk info = (Risk) it.next();
//                cl.remove(info);
//            }
//        }
//
//        
//        cl.add(currentRisk);
//        
//        TopComponent tc = (TopComponent) WindowManager.getDefault().findTopComponent("ClaimsTopComponent");
//        if (tc != null) {
//            tc.open();
//              tc.requestActive();
//        }
        jPopupMenu1.setVisible(false);
        JdgClaims jc = new JdgClaims(null, true, currentRisk, false, false, null);
        jc.setVisible(true);

    }//GEN-LAST:event_jmnuAddClaimActionPerformed

    private void jmnuViewClaimsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnuViewClaimsActionPerformed
        // TODO add your handling code here:
        jPopupMenu1.setVisible(false);
        JdgClaims jc = new JdgClaims(null, true, currentRisk, true, false, null);
        jc.setVisible(true);
    }//GEN-LAST:event_jmnuViewClaimsActionPerformed

    private void jPanel12FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel12FocusGained
        // TODO add your handling code here:

        //Check if the user is authorized to do this else dont let them. 

    }//GEN-LAST:event_jPanel12FocusGained

    private void jtxtReceiptingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtReceiptingKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtReceiptingKeyPressed

    private void jtxtReceiptingKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtReceiptingKeyTyped
        // TODO add your handling code here:

    }//GEN-LAST:event_jtxtReceiptingKeyTyped

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(5);
        details();

    }//GEN-LAST:event_jButton4ActionPerformed

    private void details() {
        StringBuilder sb = new StringBuilder();
        Risk risk = currentRisk;
        sb.append(" Insured: ").append(risk.getClient().getName()).append(" ").append(risk.getClient().getSurname());
        sb.append("\n\n Insurer: ").append(risk.getInsurer().getName());
        sb.append("\n\n Type: ").append(risk.getPolicyType().getPolicyType());
        sb.append("\n\n Inception: ").append(risk.getPolicyPeriod().getInception());
        sb.append("\n Expiry: ").append(risk.getPolicyPeriod().getExpiry());
        sb.append("\n Renewal: ").append(risk.getPolicyPeriod().getRenewal());
        sb.append("\n Effective: ").append(risk.getPolicyPeriod().getEffectiveDate());
        sb.append("\n Transaction Date: ").append(risk.getPolicyPeriod().getTransactionDate());
        sb.append("\n\n Description: ").append(risk.getDescription());
        sb.append("\n\n Cover Note: ").append(risk.getCoverNoteNumber());
        sb.append("\n\n Basic Premium: ").append(risk.getRiskPremium().getBasicPremium());
        sb.append("\n\n Annual Premium: ").append(risk.getRiskPremium().getAnnualPremium());
        sb.append("\n\n Insurer Premium: ").append(risk.getRiskPremium().getPremiumNet());

        jTextArea2.setText(sb.toString());
    }

    private void reset() {
        jtxtCoverNote.setText("");
        jtxtTransactionPremium.setText("0");
        jtxtBrokerageAmount.setText("0");
        jtxtBrokerageRate.setText("0.0");
        jtxtNumberOfRisks.setText("0");
        jtxtPremiumNet.setText("0");
        jtxtPremiumRate.setText("0.0");
        jtxtStampDuty.setText("0");
        jtxtTotalSumInsured.setText("0");
        jTextArea1.setText("");
        jtxtAnnualPremium.setText("0");
        jtxtLevies.setText("0");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Period period = new Period(calendar);
        PeriodSet ps = new PeriodSet(period);
        try {
            jtxtRenewal.setDefaultPeriods(ps);
            jtxtInception.setDefaultPeriods(ps);
            jtxtEffectiveDate.setDefaultPeriods(ps);
            jtxtExpiry.setDefaultPeriods(ps);
            jtxtTransactionDate.setDefaultPeriods(ps);

        } catch (IncompatibleDataExeption ex) {
            Exceptions.printStackTrace(ex);
        }
        editRisk = false;
        renewal = false;
    }

    public void calculateBrokerageAmount() {
        try {

            //     jtxtPremiumNet.setText(basicPremium.add(new BigDecimal(jtxtLevies.getText()))
            //      .add(new BigDecimal(jtxtStampDuty.getText())).subtract(brokerageAmount).toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void calculateBasicPremium() {

        // calculateBrokerageAmount();
        transactionPremium = new BigDecimal(jtxtTransactionPremium.getText());

        totalSumInsured = new BigDecimal(jtxtTotalSumInsured.getText());
        premiumRate = new BigDecimal(jtxtPremiumRate.getText());

        brokerageRate = new BigDecimal(jtxtBrokerageRate.getText());

        annualPremium = totalSumInsured.multiply(premiumRate);
        // basicPremium = transactionPremium.multiply(premiumRate);

        brokerageAmount = brokerageRate.multiply(transactionPremium);
        jtxtBrokerageAmount.setText(brokerageAmount.toString());

        jtxtAnnualPremium.setText(annualPremium.toString());

        jtxtPremiumNet.setText(transactionPremium.add(new BigDecimal(jtxtLevies.getText()))
                .add(new BigDecimal(jtxtStampDuty.getText())).subtract(brokerageAmount).toString());

    }

    private void savePremium() {

        try {

            override = false;

            List<PatientRecord> client = clientManager.getpatient(jtxtClientNumber.getText());
            if (client.isEmpty()) {
                Confirmation message = new NotifyDescriptor.Confirmation("Invalid Client ID\" please enter correct ID to proceed",
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.QUESTION_MESSAGE);

                Object result = DialogDisplayer.getDefault().notify(message);
                jTabbedPane1.setSelectedIndex(0);

                return;
            }
            Risk risk = new Risk();

            RiskPremium rp = new RiskPremium();
            if (editRisk == false && renewal == false) {
                rp.setTransactionType("N");

            }
            if (editRisk == true) {
                rp.setTransactionType("E");
                //rp.setRisk(currentRisk);

            }
            if (renewal == true) {
                rp.setTransactionType("R");
                //rp.setRisk(currentRisk);

            }

            rp.setAnnualPremium(new Double(jtxtAnnualPremium.getText()));

            rp.setDateCreated(new Date());
            rp.setBasicPremium(new Double(jtxtTransactionPremium.getText()));
            rp.setBrokerageAmount(new Double(jtxtBrokerageAmount.getText()));
            rp.setBrokerageRate(new Double(jtxtBrokerageRate.getText()));
            rp.setLevies(new Double(jtxtLevies.getText()));
            rp.setPremiumNet(new Double(jtxtPremiumNet.getText()));
            rp.setAnnualPremium(new Double(jtxtAnnualPremium.getText()));
            rp.setStampDuty(new Double(jtxtStampDuty.getText()));
            rp.setPremiumRate(new Double(jtxtPremiumRate.getText()));

            PolicyPeriod pp = new PolicyPeriod();
            pp.setEffectiveDate(jtxtEffectiveDate.getCurrent().getTime());

            pp.setExpiry(expiry);
            pp.setInception(jtxtInception.getCurrent().getTime());
            pp.setRenewal(jtxtRenewal.getCurrent().getTime());
            pp.setTransactionDate(jtxtTransactionDate.getCurrent().getTime());
            pp.setDateCreated(new Date());
//      
//       pp.setEffectiveDate(new Date ());
//      pp.setExpiry(new Date ());
//      pp.setInception(new Date());
//      pp.setRenewal(new Date ());
//      pp.setTransactionDate(new Date ());

            PolicyType pt = new PolicyType();
            pt.setPolicyType(jcboPoliciesTypes.getSelectedItem().toString());

            risk.setDescription(jTextArea1.getText());
            risk.setClient(client.get(0));
            risk.setNumberOfRisks(new BigDecimal(jtxtNumberOfRisks.getText()));
            risk.setPolicyPeriod(pp);
            risk.setPolicyType(pt);
            risk.setRiskPremium(rp);
            risk.setCoverNoteNumber(jtxtCoverNote.getText());
            risk.setTotalSumInsured(new BigDecimal(jtxtTotalSumInsured.getText()));
            int x = jcboRiskInsurer.getSelectedIndex();
            risk.setInsurer(insurers.get(x));
            risk.setDateCreated(new Date());

            RiskJpaController riskManagerR = new RiskJpaController();
            riskManagerR.create(risk);
            currentRisk = risk;
            Confirmation message = new NotifyDescriptor.Confirmation("Record has been successfully saved\"",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(message);

            showDebitNOte();

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    Date expiry = new Date();

    private void calculateDates() {
        try {

            expiry = jtxtExpiry.getCurrent().getTime();
            jtxtInception.setFormat(1);
            jtxtEffectiveDate.setFormat(1);
            jtxtExpiry.setFormat(1);
            jtxtTransactionDate.setFormat(1);
            jtxtRenewal.setFormat(1);
            
            // if (currentRisk.getRiskPremium().getTransactionType().)
            Calendar calendar = jtxtExpiry.getCurrent();
            //  jtxtExpiry.getCurrent().add(Calendar.YEAR, 1);
            jtxtExpiry.getCurrent().add(Calendar.DAY_OF_WEEK, 1);
            calendar.setTime(jtxtExpiry.getCurrent().getTime());
            Period period = new Period(calendar);
            PeriodSet ps = new PeriodSet(period);
            jtxtRenewal.setDefaultPeriods(ps);

            calendar.setTime(new Date());
            period = new Period(calendar);
            ps = new PeriodSet(period);
            jtxtTransactionDate.setDefaultPeriods(ps);

            if (renewal == true) {
                calendar = Calendar.getInstance();
                calendar.setTime(currentRisk.getPolicyPeriod().getRenewal());
                period = new Period(calendar);
                ps = new PeriodSet(period);
                jtxtInception.setDefaultPeriods(ps);

            }

        } catch (IncompatibleDataExeption ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void showDebitNOte() throws JRException {

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/debitNote.jrxml",
                null,
                false);

//             Confirmation message = new NotifyDescriptor.Confirmation("Report Path: "+reportFile.getPath(),
//                    NotifyDescriptor.OK_CANCEL_OPTION,
//                    NotifyDescriptor.QUESTION_MESSAGE);
//
//            Object result = DialogDisplayer.getDefault().notify(message);
//            
        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        DebitNoteBean dnb = new DebitNoteBean();
        dnb.setAddress(currentRisk.getClient().getAddress());
        dnb.setBasicPremium(new BigDecimal(currentRisk.getRiskPremium().getBasicPremium()));
        dnb.setLevies(new BigDecimal(currentRisk.getRiskPremium().getLevies()));
        dnb.setName(currentRisk.getClient().getName() + " " + currentRisk.getClient().getSurname());
        dnb.setNetPremium(new BigDecimal(currentRisk.getRiskPremium().getBasicPremium())
                .add(new BigDecimal(currentRisk.getRiskPremium().getLevies()))
                .add(new BigDecimal((currentRisk.getRiskPremium().getStampDuty()))));

        dnb.setPolicyType(currentRisk.getPolicyType().getPolicyType() + " [" + currentRisk.getInsurer().getName() + "]");
        dnb.setStampDuty(new BigDecimal(currentRisk.getRiskPremium().getStampDuty()));
        dnb.setEffectiveDate(currentRisk.getPolicyPeriod().getEffectiveDate().toString());
        dnb.setRenewalDate(currentRisk.getPolicyPeriod().getRenewal().toString());
        dnb.setExpiryDate(currentRisk.getPolicyPeriod().getExpiry().toString());

        List<DebitNoteBean> list = new ArrayList<DebitNoteBean>();

        list.add(dnb);

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JComboBox jcboInsurer;
    private javax.swing.JComboBox jcboPoliciesTypes;
    private javax.swing.JComboBox jcboPolicyCategories;
    private javax.swing.JComboBox jcboRiskInsurer;
    private javax.swing.JLabel jlblPerson;
    private javax.swing.JLabel jlblPremium;
    private javax.swing.JMenuItem jmnuAddClaim;
    private javax.swing.JMenuItem jmnuViewClaims;
    private javax.swing.JTextField jtxtAnnualPremium;
    private javax.swing.JTextField jtxtBrokerageAmount;
    private javax.swing.JTextField jtxtBrokerageRate;
    private javax.swing.JTextField jtxtClientNumber;
    private javax.swing.JTextField jtxtCoverNote;
    private datechooser.beans.DateChooserCombo jtxtEffectiveDate;
    private datechooser.beans.DateChooserCombo jtxtExpiry;
    private datechooser.beans.DateChooserCombo jtxtInception;
    private javax.swing.JTextField jtxtLevies;
    private javax.swing.JTextField jtxtNumberOfRisks;
    private javax.swing.JTextField jtxtPremiumNet;
    private javax.swing.JTextField jtxtPremiumRate;
    private javax.swing.JTextField jtxtReceipting;
    private datechooser.beans.DateChooserCombo jtxtRenewal;
    private javax.swing.JTextField jtxtStampDuty;
    private javax.swing.JTextField jtxtTotalSumInsured;
    private datechooser.beans.DateChooserCombo jtxtTransactionDate;
    private javax.swing.JTextField jtxtTransactionPremium;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        result = WindowManager.getDefault().findTopComponent("CustomerTopComponentTopComponent").getLookup().lookupResult(PatientRecord.class);
        result.addLookupListener(this);
        resultChanged(new LookupEvent(result));
//        RequestProcessor.getDefault().post(new Runnable() {
//
//            @Override
//            public void run() {
//                readCustomer();
//                      
//            }
//        });
    }

    private void readCustomer() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TopComponent tc = WindowManager.getDefault().findTopComponent("RiskModuleTopComponent");
                if (tc == null) {
                    // XXX: message box?
                    return;
                }

                result = tc.getLookup().lookupResult(PatientRecord.class);
                result.addLookupListener(RiskModuleTopComponent.this);
                resultChanged(new LookupEvent(result));
            }
        });
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        result.removeLookupListener(this);
        result = null;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version

    }

    public ExplorerManager getExplorerManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class SaveCookieImpl implements SaveCookie {

        @Override
        public void save() throws IOException {

            Confirmation message = new NotifyDescriptor.Confirmation("Do you want to save \""
                    + jtxtClientNumber.getText() + "\"?",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE);

            Object result = DialogDisplayer.getDefault().notify(message);

            //When user clicks "Yes", indicating they really want to save,
            //we need to disable the Save button and Save menu item,
            //so that it will only be usable when the next change is made
            //to the text field:
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                fire(false);
                if (customer.getId() != null) {
                    try {

                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {

                }
                // RiskModuleTopComponent.refreshNode();
            }

        }
    }

}
