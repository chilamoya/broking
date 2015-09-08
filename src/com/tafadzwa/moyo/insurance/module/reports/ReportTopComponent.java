/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.reports;

import com.innate.cresterp.insurance.risk.entities.Claim;
import com.innate.cresterp.insurance.risk.entities.ClaimConfiguration;
import com.innate.cresterp.insurance.risk.entities.Insurer;
import com.innate.cresterp.insurance.risk.entities.PolicyCategory;
import com.innate.cresterp.insurance.risk.entities.PolicyType;
import com.innate.cresterp.insurance.risk.entities.Receipt;
import com.innate.cresterp.insurance.risk.entities.Risk;
import com.innate.cresterp.insurance.risk.persistence.ClaimConfigurationJpaController;
import com.innate.cresterp.insurance.risk.persistence.ClaimJpaController;
import com.innate.cresterp.insurance.risk.persistence.InsurerJpaController;
import com.innate.cresterp.insurance.risk.persistence.PolicyCategoryJpaController;
import com.innate.cresterp.insurance.risk.persistence.RiskJpaController;
import com.innate.cresterp.security.persistence.CompanyJpaController;
import com.innate.erp.broking.claims.Configuration;
import com.tafadzwa.moyo.insurance.module.reports.beans.BodStatementBean;
import com.tafadzwa.moyo.insurance.module.reports.beans.ClaimsReportBean;
import com.tafadzwa.moyo.insurance.module.reports.beans.ClaimsTotalsReportBean;
import com.tafadzwa.moyo.insurance.module.reports.beans.IncomeReportBean;
import com.tafadzwa.moyo.insurance.module.reports.beans.ReceiptsBean;
import com.tafadzwa.moyo.insurance.module.reports.beans.WReportBean;
import com.tafadzwa.moyo.insurance.module.utilities.UtilDate;
import java.awt.Dimension;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.tafadzwa.moyo.insurance.module.reports//Report//EN",
        autostore = false)
@TopComponent.Description(preferredID = "ReportTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.tafadzwa.moyo.insurance.module.reports.ReportTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ReportAction",
        preferredID = "ReportTopComponent")
public final class ReportTopComponent extends TopComponent {

    private final RiskJpaController riskManager = new RiskJpaController();
    private final DefaultListModel renewalModel = new DefaultListModel();
    private final InsurerJpaController insurerManager = new InsurerJpaController();
    private final PolicyCategoryJpaController policyCategoryManager
            = new PolicyCategoryJpaController();
    private List<Insurer> insurers = new ArrayList<Insurer>();
    private final ClaimJpaController claimsManager = new ClaimJpaController();
    private List<ClaimConfiguration> configurations = new ArrayList<ClaimConfiguration>();
    private final List<ClaimConfiguration> premiumStatuses = new ArrayList<ClaimConfiguration>();
    private final ClaimConfigurationJpaController claimConfigManager = new ClaimConfigurationJpaController();
    private final CompanyJpaController companyController = new CompanyJpaController(null);

    List<Risk> required = new ArrayList<Risk>();

    public ReportTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ReportTopComponent.class, "CTL_ReportTopComponent"));
        setToolTipText(NbBundle.getMessage(ReportTopComponent.class, "HINT_ReportTopComponent"));
        jList1.setModel(renewalModel);

        postInitComponents();
    }

    private void postInitComponents() {

        insurersLoad();
        loadConfigurations();

    }

    private void insurersLoad() {
        try {
            insurers = insurerManager.findInsurerEntities();
            cboInsurer.removeAllItems();

            for (Insurer pc : insurers) {
                cboInsurer.addItem(pc.getName());

            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcboReport = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        startDate = new datechooser.beans.DateChooserCombo();
        jLabel3 = new javax.swing.JLabel();
        endDate = new datechooser.beans.DateChooserCombo();
        jbtnView = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cboInsurer = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jToolBar1 = new javax.swing.JToolBar();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcboClaimSettledStatus = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jcboClaimUnSettledStatus = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jLabel1.text")); // NOI18N

        jcboReport.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bod Statement", "Renewals", "Gross Premium", "Claims Report", "Claims Follow Up", "Total Claims Report", "Income Report", "Receivables Report", "Receivables Summary", "Business Summary Insurers", "Business Summary By Class", " " }));
        jcboReport.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcboReportItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jbtnView, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jbtnView.text")); // NOI18N
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(57, 57, 57)
                                .addComponent(endDate, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jcboReport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnView)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jcboReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(endDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jbtnView)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jLabel4.text")); // NOI18N

        cboInsurer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboInsurer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboInsurerItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(40, 40, 40)
                .addComponent(cboInsurer, 0, 421, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cboInsurer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jToolBar1.setRollover(true);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jTextField1.text")); // NOI18N
        jToolBar1.add(jTextField1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);
        jToolBar1.add(jSeparator1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 516, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 105, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jLabel5.text")); // NOI18N

        jcboClaimSettledStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jLabel6.text")); // NOI18N

        jcboClaimUnSettledStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcboClaimUnSettledStatus, 0, 372, Short.MAX_VALUE)
                    .addComponent(jcboClaimSettledStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jcboClaimSettledStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jcboClaimUnSettledStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ReportTopComponent.class, "ReportTopComponent.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jcboReportItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcboReportItemStateChanged
// TODO add your handling code here:
    if (evt.getItem().toString().equals("Bod Statement")) {
        jTabbedPane1.setSelectedIndex(0);
    }
    if (evt.getItem().toString().equals("Gross Premium")) {
        jTabbedPane1.setSelectedIndex(2);
    }
    if (evt.getItem().toString().equals("Renewals")) {
        jTabbedPane1.setSelectedIndex(1);

    }
}//GEN-LAST:event_jcboReportItemStateChanged

    public void showClaimsReport() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/claimsReport.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        Confirmation message = new NotifyDescriptor.Confirmation("Report Path: " + reportFile.getPath(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE);

        //Object result = DialogDisplayer.getDefault().notify(message);
        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader 
        List<Claim> claims = new ArrayList<Claim>();
        List<ClaimsReportBean> list = new ArrayList<ClaimsReportBean>();
////        claims = new ClaimJpaController().findClaimEntities();
        claims = new ClaimJpaController().getEntityManager().createQuery("Select c from Claim c where c.claimPolicy.insurer =?1")
                .setParameter(1, insurers.get(cboInsurer.getSelectedIndex())).getResultList();
        double total = 0.0;

        for (Claim c : claims) {

            ClaimsReportBean bean = new ClaimsReportBean();
            bean.setBetterment(c.getBetterment());
            bean.setClaimStatus(c.getClaimStatus().getValue());
            bean.setClaimType(c.getClaimType().getValue());
            bean.setClaimant(c.getClaimant());
            bean.setExcess(c.getExcess());
            bean.setGrossClaim(c.getGrossClaim());
            bean.setName(c.getClaimPolicy().getClient().getName() + " " + c.getClaimPolicy().getClient().getSurname());
            bean.setNetClaim(c.getNetClaim());
            bean.setPolicyType(c.getClaimPolicy().getPolicyType().getPolicyType());
            bean.setPremiumStatus(c.getPremiumStatus().getValue());
            bean.setRiskInsurer(c.getClaimPolicy().getInsurer().getName());
            bean.setSubmissionDate(c.getDatesubmittedNotification());
            bean.setRegistrationDate(c.getRegistrationDate());
            bean.setFollowUpDate(c.getFollowUpDate());
            bean.setDateOfLoss(c.getDateOfLoss());
            total = total + c.getNetClaim();
            bean.setTotalNetClaim(total);
            bean.setReportName(companyName);
            list.add(bean);
        }

        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private long findTotalClaims(Insurer insurer) {
        Query q = riskManager.getEntityManager().createQuery("Select COUNT(c) from Claim c where c.claimPolicy.insurer = ?1 and c.datesubmittedNotification >= ?2 and c.datesubmittedNotification <= ?3");
        q.setParameter(1, insurer);
        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());

        long total = (Long) q.getSingleResult();

        return total;

    }

    private long findTotalClaimsByStatus(ClaimConfiguration premiumStatus,
            Insurer insurer, boolean closed) {

        Query q = riskManager.getEntityManager().createQuery("Select c from Claim c where c.claimPolicy.insurer = ?1 "
                + "and c.datesubmittedNotification >= ?2"
                + " and c.datesubmittedNotification <= ?3 ");
        q.setParameter(1, insurer);
        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());

        List<Claim> claims = new ArrayList<Claim>();
        claims = q.getResultList();

        Long counter = Long.parseLong("0");

        for (Claim c : claims) {
            if (c.getClaimStatus().isCloseClaim() == closed) {
                counter = counter + 1;
            }
        }
        return counter;
    }

    private double findValueClaimsByStatus(ClaimConfiguration premiumStatus, Insurer insurer, boolean closed) {
        Query q = riskManager.getEntityManager().createQuery("Select c from Claim c where c.claimPolicy.insurer = ?1 and c.datesubmittedNotification >= ?2"
                + " and c.datesubmittedNotification <= ?3 ");
        q.setParameter(1, insurer);
        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());
        List<Claim> claims = new ArrayList<Claim>();
        claims = q.getResultList();

        double total = 0.0;

        for (Claim c : claims) {
            if (c.getClaimStatus().isCloseClaim() == closed) {
                total = total + c.getNetClaim();
            }
        }

        return total;
    }

    private double findValueClaims(Insurer insurer) {
        Query q = riskManager.getEntityManager().createQuery("Select c from Claim c "
                + "where c.claimPolicy.insurer = ?1 and c.datesubmittedNotification >= ?2 "
                + "and c.datesubmittedNotification <= ?3");
        q.setParameter(1, insurer);
        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());

        List<Claim> claims = new ArrayList<Claim>();
        claims = q.getResultList();

        double total = 0.0;

        for (Claim c : claims) {

            total = total + c.getNetClaim();

        }

        return total;

    }

    private void loadConfigurations() {
        jcboClaimSettledStatus.removeAllItems();
        jcboClaimUnSettledStatus.removeAllItems();
        premiumStatuses.clear();

        configurations = claimConfigManager.findClaimConfigurationEntities();

        for (ClaimConfiguration cc : configurations) {

            if (cc.getConfig().trim().equals(Configuration.PREMIUM_STATUS.toString().trim())) {

                jcboClaimSettledStatus.addItem(cc.getValue());
                jcboClaimUnSettledStatus.addItem(cc.getValue());
                premiumStatuses.add(cc);
            }

        }

    }

    public void showTotalClaimsReport() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/totalClaimsReport.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        Confirmation message = new NotifyDescriptor.Confirmation("Report Path: " + reportFile.getPath(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE);

        //Object result = DialogDisplayer.getDefault().notify(message);
        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader 
        List<Claim> claims = new ArrayList<Claim>();
        List<ClaimsTotalsReportBean> list = new ArrayList<ClaimsTotalsReportBean>();
        claims = new ClaimJpaController().findClaimEntities();
        long totalClaims = 0;
        double total = 0.0;

        for (Insurer insurer : insurers) {
            ClaimsTotalsReportBean ctrb = new ClaimsTotalsReportBean();
           //Get the total claims in a certain period \//Get the value of claims in the period 
            //Get the total settled claims 
            //Get the value of the settled claims 
            //Get the total unsettled claims 
            //Get the value of the unsettled claims 

            //This is pathetic coding 
            ctrb.setTotalClaims(findTotalClaims(insurer));
            ctrb.setReportName(companyName);

            ctrb.setInsurer(insurer.getName());
            ctrb.setQuarter(endDate.getText());
            ctrb.setTotalSettled(findTotalClaimsByStatus(null, insurer, true));
            ctrb.setTotalUnsetteled(findTotalClaimsByStatus(null, insurer, false));
            ctrb.setTotalValue(findValueClaims(insurer));
            ctrb.setValueSetteled(findValueClaimsByStatus(null, insurer, true));
            ctrb.setValueUnsettled(findValueClaimsByStatus(null, insurer, false));

            list.add(ctrb);

        }

        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showReport2() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/CompanyBodStatement.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        Confirmation message = new NotifyDescriptor.Confirmation("Report Path: " + reportFile.getPath(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE);

        //Object result = DialogDisplayer.getDefault().notify(message);
        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        List<Risk> risks = new ArrayList<Risk>();

        // Get all the risks by the filters        
        Query q = riskManager.getEntityManager().createQuery("Select r from Risk r where r.insurer = ?1 and r.policyPeriod.inception >= ?2 and r.policyPeriod.inception <= ?3");
        q.setParameter(1, insurers.get(cboInsurer.getSelectedIndex()));
        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());

        risks = q.getResultList();
        double totalNet = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        List<BodStatementBean> list = new ArrayList<BodStatementBean>();
        for (Risk risk : risks) {
            BodStatementBean bean1 = new BodStatementBean();
            bean1.setDateRange("From: " + sdf.format(startDate.getCurrent().getTime()) + " To: "
                    + sdf.format(endDate.getCurrent().getTime()));
            bean1.setBrokerageAmount(risk.getRiskPremium().getBrokerageAmount());
            bean1.setBrokerageRate(risk.getRiskPremium().getBrokerageRate());
            bean1.setGrossPremium(risk.getRiskPremium().getBasicPremium());
            System.out.println(" Risk ID: " + risk.getId() + "  Levie: " + risk.getRiskPremium().getLevies());
            bean1.setLevies(risk.getRiskPremium().getLevies());
            bean1.setMonthOf(new Date());
            bean1.setTransactionDate(risk.getDateCreated());
            bean1.setName(risk.getClient().getName() + " " + risk.getClient().getSurname());
            bean1.setNetPremium(risk.getRiskPremium().getPremiumNet());
            bean1.setPolicyType(risk.getRiskPremium().getTransactionType()
                    + ": " + risk.getPolicyType().getPolicyType() + " [" + risk.getCoverNoteNumber() + "]");
            bean1.setStampDuty(risk.getRiskPremium().getStampDuty());
            bean1.setReportName(companyName);
            bean1.setRiskInsurer(risk.getInsurer().getName());
            totalNet = totalNet + risk.getRiskPremium().getPremiumNet();
            bean1.setTotalInsNet(totalNet);
            try {

            } catch (Exception ex) {

                System.out.println(" Risk with issues ID: " + risk.getId());

                ex.printStackTrace();
            }

            list.add(bean1);
        }

        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showIncomeReport() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/incomeReport.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        Confirmation message = new NotifyDescriptor.Confirmation("Report Path: " + reportFile.getPath(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE);

        //Object result = DialogDisplayer.getDefault().notify(message);
        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        List<Risk> risks = new ArrayList<Risk>();

        // Get all the risks by the filters        
        Query q = riskManager.getEntityManager().createQuery("Select r from Risk r where  r.policyPeriod.inception >= ?2 and r.policyPeriod.inception <= ?3");
        // q.setParameter(1, insurers.get(cboInsurer.getSelectedIndex()));
        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());

        risks = q.getResultList();
        double gpRecievable = 0.0;
        double gpPayable = 0.0;
        double commision = 0.0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        List<IncomeReportBean> list = new ArrayList<IncomeReportBean>();
        for (Risk risk : risks) {
            gpRecievable = gpRecievable + risk.getRiskPremium().getBasicPremium();
            commision = commision + risk.getRiskPremium().getBrokerageAmount();
        }
        gpPayable = gpRecievable - commision;
        IncomeReportBean bean = new IncomeReportBean();
        bean.setCommission(commision);
        bean.setGpPayable(gpPayable);
        bean.setGpRecievable(gpRecievable);
        bean.setQuarter("Income Report: [" + startDate.getText() + " TO " + endDate.getText() + "]");
        bean.setReportName(companyName);

        list.add(bean);

        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showBusinessSummary() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();
        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/businessSummaries.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        List<Risk> risks = new ArrayList<Risk>();
        List<ClaimsReportBean> list = new ArrayList<ClaimsReportBean>();
        double premiums = 0, commissions = 0;
        int count = 0;
        String period = "";
        for (Insurer insurer : insurers) {

            // Get all the risks by the filters        
            Query q = riskManager.getEntityManager().createQuery("Select r from Risk r where r.insurer = ?1 and r.policyPeriod.inception >= ?2 and r.policyPeriod.inception <= ?3");
            q.setParameter(1, insurer);
            q.setParameter(2, startDate.getCurrent().getTime());
            q.setParameter(3, endDate.getCurrent().getTime());
            risks = q.getResultList();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

            for (Risk risk : risks) {

                premiums = premiums + risk.getRiskPremium().getBasicPremium();
                commissions = commissions + risk.getRiskPremium().getBrokerageAmount();
            }

            //Insert into the bean list to be used as the report data source.
            count = risks.size();
            if (count > 0) {
                ClaimsReportBean bean = new ClaimsReportBean();
                bean.setReportName(companyName);
                bean.setRiskInsurer(insurer.getName());
                bean.setGrossClaim(premiums);
                bean.setNetClaim(commissions);
                bean.setPolicyType(count + "");
                bean.setTotalNetClaim(count);
                list.add(bean);
            }
            premiums = 0;
            commissions = 0;
            count = 0;
        }
        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showBusinessClassSummary() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();
        List<String> categories = companyController.getEntityManager().
                createQuery("Select DISTINCT p.policyType from PolicyType p")
                .getResultList();
        List<WReportBean> list = new ArrayList<WReportBean>();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/businessClassSummary.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        String reportName = reportFile.getPath();

        for (String pc : categories) {

            //Fill the report with parameter, connection and the stream reader  
            List<Risk> policiesRisk = new ArrayList<Risk>();

            // Get all the risks by the filters        
            Query q = riskManager.getEntityManager().createQuery("Select r from Risk  r "
                    + "where  r.policyType.policyType = ?1 and r.policyPeriod.inception >= ?2 and r.policyPeriod.inception <= ?3");

            q.setParameter(2, startDate.getCurrent().getTime());
            q.setParameter(3, endDate.getCurrent().getTime());
            q.setParameter(1, pc.trim());

            double totalPremiums = 0.0, commission = 0.0;
            int count = 0;
            policiesRisk = q.getResultList();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

            for (Risk r : policiesRisk) {

                count = count + 1;
                totalPremiums = totalPremiums + r.getRiskPremium().getBasicPremium();
                commission = commission + r.getRiskPremium().getBrokerageAmount();

            }

            WReportBean report = new WReportBean();
            report.setReportName(companyName);
            report.setTitle("Summary of Business by Class [" + sdf.format(startDate.getCurrent().getTime())
                    + " TO " + sdf.format(endDate.getCurrent().getTime()) + "]");
            report.setsField1(pc);
            report.setdField1(totalPremiums);
            report.setiField1(count);
            report.setdField2(commission);
            list.add(report);

        }
        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);
        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showClaimsFollowUp() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();

        List<WReportBean> list = new ArrayList<WReportBean>();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/claimsFollowUp.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        String reportName = reportFile.getPath();

        // Get all the risks by the filters        
        Query q = riskManager.getEntityManager().createQuery("Select c from Claim  c "
                + "where c.followUpDate >= ?1 and c.followUpDate <= ?2");

        q.setParameter(1, startDate.getCurrent().getTime());
        q.setParameter(2, endDate.getCurrent().getTime());
        List<Claim> claims = new ArrayList<Claim>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        claims = q.getResultList();

        for (Claim c : claims) {
            WReportBean report = new WReportBean();
            report.setReportName(companyName);
            report.setTitle("Claims Follow Up Status [" + sdf.format(startDate.getCurrent().getTime())
                    + " TO " + sdf.format(endDate.getCurrent().getTime()) + "]");
            report.setsField1(c.getId() + "");
            report.setsField2(c.getClaimPolicy().getInsurer().getName());
            report.setsField3(c.getClaimant());
            report.setsField4(c.getClaimType().getValue());
            report.setsField5(c.getClaimStatus().getValue());
            report.setDateField1(c.getFollowUpDate());
            list.add(report);

        }
        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);
        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showReceivablesSummary() throws JRException {
        String companyName = companyController.findCompanyEntities().get(0).getName();

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/receiptsSummary.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());

        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        List<Receipt> receipts = new ArrayList<Receipt>();

        // Get all the risks by the filters        
        Query q = riskManager.getEntityManager().createQuery("Select r from Receipt r where  r.receiptDate >= ?2 and r.receiptDate <= ?3");

        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());
        receipts = q.getResultList();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        List<ReceiptsBean> list = new ArrayList<ReceiptsBean>();
        double total = 0;
        for (Insurer insurer : insurers) {
            total = 0;
            for (Receipt receipt : receipts) {
                if (receipt.getRisk().getInsurer().equals(insurer)) {
                    total = total + receipt.getAmount();
                }

            }
            String narration = "Summary of Receipts: "
                    + sdf.format(startDate.getCurrent().getTime()) + " to "
                    + sdf.format(endDate.getCurrent().getTime());

            if (total > 0) {
                ReceiptsBean bean = new ReceiptsBean();

                bean.setTendered(total);
                bean.setReportName(companyName);
                bean.setName(insurer.getName());
                bean.setTenderString("USD " + total);
                bean.setNarration(narration);
                bean.setInsured(narration);

                // bean.setUser(user receipt.getSystemUser());
                list.add(bean);

            }

        }

        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void showReceivablesReport() throws JRException {

        File reportFile = InstalledFileLocator.getDefault().locate(
                "reports/receiptingTranscations.jrxml",
                null,
                false);

        System.out.println("Report Path: " + reportFile.getPath());
        String companyName = companyController.findCompanyEntities().get(0).getName();

        Confirmation message = new NotifyDescriptor.Confirmation("Report Path: " + reportFile.getPath(),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE);

        //Object result = DialogDisplayer.getDefault().notify(message);
        String reportName = reportFile.getPath();

        //Fill the report with parameter, connection and the stream reader  
        List<Receipt> receipts = new ArrayList<Receipt>();

        // Get all the risks by the filters        
        Query q = riskManager.getEntityManager().createQuery("Select r from Receipt r where  r.receiptDate >= ?2 and r.receiptDate <= ?3");

        q.setParameter(2, startDate.getCurrent().getTime());
        q.setParameter(3, endDate.getCurrent().getTime());
        receipts = q.getResultList();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");

        List<ReceiptsBean> list = new ArrayList<ReceiptsBean>();
        for (Receipt receipt : receipts) {
            ReceiptsBean bean = new ReceiptsBean();
            bean.setInsured(receipt.getRisk().getInsurer().getName());
            bean.setTendered(receipt.getAmount());
            bean.setReportName(companyName);
            bean.setTransactionDate(receipt.getReceiptDate());
            bean.setName(receipt.getRisk().getClient().getName() + " "
                    + receipt.getRisk().getClient().getName());
            // bean.setUser(user receipt.getSystemUser());
            list.add(bean);
        }

        JasperPrint jp = null;

        JasperReport jr = JasperCompileManager.compileReport(reportName);
        jp = JasperFillManager.fillReport(jr, null, new JRBeanCollectionDataSource(list));

        //Viewer for JasperReport
        JRViewer jv = new JRViewer(jp);

        //Insert viewer to a JFrame to make it showable
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.addLayoutComponent(reportName, jv);

        JFrame jf = new JFrame();

        jf.getContentPane().add(jv);

        //       jScrollPane1.add(jv);
        jf.validate();
        jf.setVisible(true);
        jf.setSize(new Dimension(800, 600));

        jf.setLocation(300, 100);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

private void cboInsurerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboInsurerItemStateChanged
// TODO add your handling code here
    if (evt.getItem().toString().equals("Bod Statement")) {
        jTabbedPane1.setSelectedIndex(0);
    }
    if (evt.getItem().toString().equals("Gross Premium")) {
        jTabbedPane1.setSelectedIndex(2);
    }
    if (evt.getItem().toString().equals("Renewals")) {
        jTabbedPane1.setSelectedIndex(1);

    }
}//GEN-LAST:event_cboInsurerItemStateChanged

private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
// TODO add your handling code here:
    if (jcboReport.getSelectedItem().toString().equals("Bod Statement")) {
        try {
            showReport2();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    if (jcboReport.getSelectedItem().toString().equals("Claims Report")) {
        try {
            showClaimsReport();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    if (jcboReport.getSelectedItem().toString().equals("Total Claims Report")) {
        try {
            showTotalClaimsReport();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    if (jcboReport.getSelectedItem().toString().equals("Income Report")) {
        try {
            showIncomeReport();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    if (jcboReport.getSelectedItem().toString().equals("Receivables Report")) {
        try {
            showReceivablesReport();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    if (jcboReport.getSelectedItem().toString().equals("Receivables Summary")) {
        try {
            showReceivablesSummary();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    if (jcboReport.getSelectedItem().toString().equals("Business Summary Insurers")) {
        try {
            showBusinessSummary();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    if (jcboReport.getSelectedItem().toString().equals("Business Summary By Class")) {
        try {
            showBusinessClassSummary();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    if (jcboReport.getSelectedItem().toString().equals("Claims Follow Up")) {
        try {
            showClaimsFollowUp();
        } catch (JRException ex) {
            Exceptions.printStackTrace(ex);
        }
    }


}//GEN-LAST:event_jbtnViewActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
    generateRenewalList();
}//GEN-LAST:event_jButton1ActionPerformed

    private void generateRenewalList() {
        Date start = new Date();
        Date end = null;
        SimpleDateFormat sm = new SimpleDateFormat("mm-dd-yyyy");

        List<Risk> items = riskManager.findRiskEntities();
        required.clear();
        int diff = 0, value = new BigDecimal(jTextField1.getText()).intValue();
        UtilDate util = new UtilDate();
        renewalModel.removeAllElements();

        //  Query q = riskManager.getEntityManager().createQuery("Select r From Risk where r.policyPeriod.")
        for (Risk risk : items) {
            end = risk.getPolicyPeriod().getRenewal();
            // diff = util.numberOfDays(sm.format(end),sm.format(start));
            //if (diff == value){
            required.add(risk);
            renewalModel.addElement(" " + risk.getClient().getName() + " " + risk.getClient().getName()
                    + " " + risk.getCoverNoteNumber() + " " + sm.format(end));
            //}
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboInsurer;
    private datechooser.beans.DateChooserCombo endDate;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtnView;
    private javax.swing.JComboBox jcboClaimSettledStatus;
    private javax.swing.JComboBox jcboClaimUnSettledStatus;
    private javax.swing.JComboBox jcboReport;
    private datechooser.beans.DateChooserCombo startDate;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
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
}
