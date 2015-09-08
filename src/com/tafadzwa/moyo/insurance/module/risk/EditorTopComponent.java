/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tafadzwa.moyo.insurance.module.risk;

import com.innate.cresterp.exceptions.NonexistentEntityException;
import com.innate.cresterp.insurance.risk.entities.SystemUser;
import com.innate.cresterp.medical.hospital.entities.PatientRecord;
import com.innate.cresterp.medical.hospital.persistence.PatientRecordJpaController;
import com.innate.cresterp.security.login.CentralLookup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.tafadzwa.moyo.insurance.module.risk//Editor//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "EditorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "com.tafadzwa.moyo.insurance.module.risk.EditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_EditorAction",
        preferredID = "EditorTopComponent"
)
@Messages({
    "CTL_EditorAction=Editor",
    "CTL_EditorTopComponent=Editor Window",
    "HINT_EditorTopComponent=This is a Editor window"
})
public final class EditorTopComponent extends TopComponent implements LookupListener {

    private Lookup.Result result = null;
    private final UndoRedo.Manager manager = new UndoRedo.Manager();
    private final SaveCookieImpl impl;
    private final InstanceContent content;
    private PatientRecord customer;
    private List<PatientRecord> customers = new ArrayList<PatientRecord>();
    private final PatientRecordJpaController patientManager = new PatientRecordJpaController();

    public EditorTopComponent() {
        initComponents();
        setName("Customer Information");
        setToolTipText("Customer Information Window");
        initializeManager();
        //Create a new instance of our SaveCookie implementation:
        impl = new SaveCookieImpl();

        //Create a new instance of our dynamic object:
        content = new InstanceContent();

        //Add the dynamic object to the TopComponent Lookup:
        associateLookup(new AbstractLookup(content));
        postInit();
    }

    private void postInit() {
        customers = patientManager.findPatientRecordEntities();
    }

    private void initializeManager() {

        jtxtName.getDocument().addUndoableEditListener(manager);
        jtxtName.getDocument().addDocumentListener(createDocListener());

        jtxtSurname.getDocument().addUndoableEditListener(manager);
        jtxtSurname.getDocument().addDocumentListener(createDocListener());

        jtxtContactAddress.getDocument().addUndoableEditListener(manager);
        jtxtContactAddress.getDocument().addDocumentListener(createDocListener());

        jtxtDOB.getDocument().addUndoableEditListener(manager);
        jtxtDOB.getDocument().addDocumentListener(createDocListener());

        jtxtHospitalNumber.getDocument().addUndoableEditListener(manager);
        jtxtHospitalNumber.getDocument().addDocumentListener(createDocListener());

        jtxtNationalID.getDocument().addDocumentListener(createDocListener());
        jtxtNationalID.getDocument().addUndoableEditListener(manager);

        jtxtPhoneNumber.getDocument().addDocumentListener(createDocListener());
        jtxtPhoneNumber.getDocument().addUndoableEditListener(manager);

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

        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jtxtNOKName = new javax.swing.JTextField();
        jtxtNOKSurname = new javax.swing.JTextField();
        jtxtNOKCell = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtxtNOKAddress = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jtxtDOB = new javax.swing.JTextField();
        jtxtDate = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jtxtHospitalNumber = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jtxtNationalID = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtxtContactAddress = new javax.swing.JTextArea();
        jLabel15 = new javax.swing.JLabel();
        jtxtName = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jtxtSurname = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jtxtPhoneNumber = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jtxtMedicalAid = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtEmail = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jPanel3.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma 12", 1, 12))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel21.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel22.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel23.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel24.text")); // NOI18N

        jtxtNOKAddress.setColumns(20);
        jtxtNOKAddress.setRows(5);
        jScrollPane3.setViewportView(jtxtNOKAddress);

        org.openide.awt.Mnemonics.setLocalizedText(jButton6, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jButton6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton7, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jButton7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel18.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel19.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel22)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxtNOKCell)
                            .addComponent(jtxtNOKSurname)
                            .addComponent(jtxtNOKName, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                        .addGap(79, 79, 79)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(297, 297, 297)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jtxtNOKName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jtxtNOKSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jtxtNOKCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6))
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addGap(42, 42, 42))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jPanel2.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma 12", 1, 12))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel9.text")); // NOI18N

        jtxtHospitalNumber.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel14.text")); // NOI18N

        jtxtContactAddress.setColumns(20);
        jtxtContactAddress.setRows(5);
        jScrollPane2.setViewportView(jtxtContactAddress);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel15.text")); // NOI18N

        jtxtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtNameKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel16.text")); // NOI18N

        jtxtSurname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtSurnameKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel17.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel20.text")); // NOI18N

        jtxtMedicalAid.setText(org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jtxtMedicalAid.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton8, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jButton8.text")); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel1.text")); // NOI18N

        jtxtEmail.setText(org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jtxtEmail.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel2.text")); // NOI18N

        jTextField2.setText(org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jTextField2.text")); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)
                            .addComponent(jLabel15)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel20)
                                .addComponent(jLabel16)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2)
                            .addComponent(jtxtEmail)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtMedicalAid, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton8, 0, 1, Short.MAX_VALUE))
                            .addComponent(jtxtSurname)
                            .addComponent(jtxtName)
                            .addComponent(jtxtNationalID)
                            .addComponent(jtxtHospitalNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jtxtHospitalNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jtxtNationalID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(jtxtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jtxtMedicalAid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jButton1.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        resetFields();
        jtxtNationalID.setFocusable(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try {
            save();
            customers = patientManager.findPatientRecordEntities();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void addCustomerToLookup(PatientRecord user) {
        CentralLookup cl = CentralLookup.getDefault();
        Collection infos = cl.lookupAll(PatientRecord.class);

        if (!infos.isEmpty()) {
            Iterator it = infos.iterator();
            while (it.hasNext()) {
                PatientRecord info = (PatientRecord) it.next();
                cl.remove(info);
            }
        }

        cl.add(user);
        infos = cl.lookupAll(PatientRecord.class);
    }

    private void typingFilter(String name, String surname) {

        try {

            for (PatientRecord c : customers) {
                if (c.getSurname().toUpperCase().equals(surname.trim().toUpperCase())
                        && c.getName().toUpperCase().equals(name.trim().toUpperCase())) {
                    //display and add to the filter 
                    jLabel3.setText(c.getName() + " " + c.getSurname() + " already exists");

                }
            }

        } catch (Exception e) {

        }

    }

    private void jtxtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyTyped
        // TODO add your handling code here:
        typingFilter(jtxtName.getText(), jtxtSurname.getText());

    }//GEN-LAST:event_jtxtNameKeyTyped

    private void jtxtSurnameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSurnameKeyTyped
        // TODO add your handling code here:
        typingFilter(jtxtName.getText(), jtxtSurname.getText());
    }//GEN-LAST:event_jtxtSurnameKeyTyped

    public void save() throws IOException {

        NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation("Do you want to save \""
                + jtxtName.getText() + " (" + jtxtSurname.getText() + ")\"?",
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
                    PatientRecord c = patientManager.findPatientRecord(customer.getId());
                    c.setName(jtxtName.getText());
                    c.setSurname(jtxtSurname.getText());
                    c.setAddress(jtxtContactAddress.getText());
                    c.setHospitalNumber(jtxtHospitalNumber.getText());
                    // c.setMedicalAid(jtxtMedicalAid.getText());
                    c.setNationalId(jtxtNationalID.getText());
                    c.setNokAddress(jtxtMedicalAid.getText());
                    c.setNokCell(jtxtNOKCell.getText());
                    c.setNokName(jtxtMedicalAid.getText());
                    c.setNokSurname(jtxtSurname.getText());
                    c.setEmail(jtxtEmail.getText());
                    c.setCell(jtxtPhoneNumber.getText());

                    if (jtxtPhoneNumber.getText().startsWith("2630")) {
                        throw new MobileNumberException("Please put number as 26399 and not 263099");
                    } else {
                        patientManager.edit(c);
                    }
                } catch (NonexistentEntityException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {

                customer.setName(jtxtName.getText());
                customer.setSurname(jtxtSurname.getText());
                customer.setAddress(jtxtContactAddress.getText());
                customer.setCell(jtxtPhoneNumber.getText());
                customer.setHospitalNumber(jtxtHospitalNumber.getText());
                //c.setMedicalAid(jtxtMedicalAid.);
                customer.setNationalId(jtxtNationalID.getText());
                customer.setNokAddress(jtxtMedicalAid.getText());
                customer.setNokCell(jtxtNOKCell.getText());
                customer.setNokName(jtxtMedicalAid.getText());
                customer.setNokSurname(jtxtSurname.getText());
                // customer.set
                patientManager.create(customer);
            }

            CustomerTopComponent.refreshNode();

        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextArea jtxtContactAddress;
    private javax.swing.JTextField jtxtDOB;
    private javax.swing.JTextField jtxtDate;
    private javax.swing.JTextField jtxtEmail;
    private javax.swing.JTextField jtxtHospitalNumber;
    private javax.swing.JTextField jtxtMedicalAid;
    private javax.swing.JTextArea jtxtNOKAddress;
    private javax.swing.JTextField jtxtNOKCell;
    private javax.swing.JTextField jtxtNOKName;
    private javax.swing.JTextField jtxtNOKSurname;
    private javax.swing.JTextField jtxtName;
    private javax.swing.JTextField jtxtNationalID;
    private javax.swing.JTextField jtxtPhoneNumber;
    private javax.swing.JTextField jtxtSurname;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        RequestProcessor.getDefault().post(new Runnable() {

            @Override
            public void run() {
                readCustomer();
                jPanel3.setVisible(false);

            }
        });
    }

    private void readCustomer() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TopComponent tc = WindowManager.getDefault().findTopComponent("CustomerTopComponentTopComponent");
                if (tc == null) {
                    // XXX: message box?
                    return;
                }
                result = tc.getLookup().lookupResult(PatientRecord.class);
                result.addLookupListener(EditorTopComponent.this);
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

    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection<PatientRecord> coll = r.allInstances();
        if (!coll.isEmpty()) {
            for (PatientRecord cust : coll) {
                customer = cust;
                jtxtName.setText(cust.getName());
                jtxtSurname.setText(cust.getSurname());
                jtxtContactAddress.setText(cust.getAddress());
                jtxtDOB.setText(cust.getDOB());
                jtxtHospitalNumber.setText(cust.getHospitalNumber() + "");
                jtxtEmail.setText(cust.getEmail());
                if (cust.getMedicalAid() != null) {
                    jtxtMedicalAid.setText(cust.getMedicalAid().getSociety());
                }
                jtxtNOKAddress.setText(cust.getNokAddress());
                jtxtNOKCell.setText(cust.getNokCell());
                jtxtMedicalAid.setText(cust.getNokName());
                jtxtNOKSurname.setText(cust.getSurname());
                jtxtNationalID.setText(cust.getNationalId());
                jtxtPhoneNumber.setText(cust.getCell());

            }
        } else {
            jtxtName.setText("[no name]");
            jtxtSurname.setText("[no city]");
            jtxtContactAddress.setText("[no address]");
            jtxtDOB.setText("[no DOB]");
            jtxtHospitalNumber.setText("[no Client Number]");
            jtxtMedicalAid.setText("[no Insurer]");
            jtxtNOKAddress.setText("[no Next Of Kin Address]");

            jtxtNationalID.setText("[no National ID]");
            jtxtPhoneNumber.setText("[no Mobile]");
            jtxtEmail.setText("[no Email]");

            /*    jtxtNOKCell.setText(" ");
             jtxtMedicalAid.setText("");
             jtxtNOKSurname.setText(" ");
             jtxtNationalID.setText(" ");
             jtxtPhoneNumber.setText("");
             jtxtEmail.setText("");*/
        }
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

    public void resetFields() {
        jLabel3.setText("");
        customer = new PatientRecord();
        jtxtName.setText("");
        jtxtSurname.setText("");
        jtxtContactAddress.setText("");
        jtxtDOB.setText("");
        jtxtHospitalNumber.setText("");
        jtxtMedicalAid.setText("");
        jtxtNOKAddress.setText("");
        jtxtNOKCell.setText(" ");
        jtxtMedicalAid.setText("");
        jtxtNOKSurname.setText(" ");
        jtxtNationalID.setText(" ");
        jtxtPhoneNumber.setText("263");
        jtxtEmail.setText(" ");
    }

    private class SaveCookieImpl implements SaveCookie {

        @Override
        public void save() throws IOException {

            NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation("Do you want to save \""
                    + jtxtName.getText() + " (" + jtxtSurname.getText() + ")\"?",
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
                        PatientRecord c = patientManager.findPatientRecord(customer.getId());
                        c.setName(jtxtName.getText());
                        c.setSurname(jtxtSurname.getText());
                        c.setAddress(jtxtContactAddress.getText());
                        c.setHospitalNumber(jtxtHospitalNumber.getText());
                        // c.setMedicalAid(jtxtMedicalAid.getText());
                        c.setNationalId(jtxtNationalID.getText());
                        c.setNokAddress(jtxtMedicalAid.getText());
                        c.setNokCell(jtxtNOKCell.getText());
                        c.setNokName(jtxtMedicalAid.getText());
                        c.setNokSurname(jtxtSurname.getText());
                        c.setEmail(jtxtEmail.getText());
                        c.setCell(jtxtPhoneNumber.getText());

                        if (jtxtPhoneNumber.getText().startsWith("2630")) {
                            throw new MobileNumberException("Please put number as 26399 and not 263099");
                        } else {
                            patientManager.edit(c);
                        }
                    } catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {

                    customer.setName(jtxtName.getText());
                    customer.setSurname(jtxtSurname.getText());
                    customer.setAddress(jtxtContactAddress.getText());
                    customer.setCell(jtxtDOB.getText());
                    customer.setHospitalNumber(jtxtHospitalNumber.getText());
                    //c.setMedicalAid(jtxtMedicalAid.);
                    customer.setNationalId(jtxtNationalID.getText());
                    customer.setNokAddress(jtxtMedicalAid.getText());
                    customer.setNokCell(jtxtNOKCell.getText());
                    customer.setNokName(jtxtMedicalAid.getText());
                    customer.setNokSurname(jtxtSurname.getText());
                    // customer.set
                    patientManager.create(customer);
                }

                CustomerTopComponent.refreshNode();
            }

        }
    }

}
