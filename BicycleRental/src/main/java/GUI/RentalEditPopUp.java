/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import cz.muni.fi.BicycleRental.Bicycle;
import cz.muni.fi.BicycleRental.Customer;
import cz.muni.fi.BicycleRental.Rental;
import cz.muni.fi.BicycleRental.RentalManager;
import cz.muni.fi.BicycleRental.ServiceFailureException;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author StrikerSVK
 */
public class RentalEditPopUp extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(RentalEditPopUp.class);
    private final Rental rental;
    private static final RentalManager rm = MainWindow.getRentalManager();
    private static RentalTableModel rtm;
    private final int selectedRow;
    
    private class REditSW extends SwingWorker <Integer,Void> {
        private final Rental rental;
        
        public REditSW(Rental r) {
            this.rental = r;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            log.debug("REdit.doInBG");
            rm.updateRental(rental);            
            return 0;
        }
        
        @Override
        protected void done(){
            log.debug("REditSW.done");
            try{
                get();
                rtm.reload();
            }catch(ExecutionException exex){
                if(exex.getCause().getClass().equals(IllegalArgumentException.class)){
                    log.debug("IAE:fields@rental editation");
                    JOptionPane.showMessageDialog(rootPane,MainWindow.getMessage("texts", "Error_Fields"));
                }
                if(exex.getCause().getClass().equals(ServiceFailureException.class)){
                    log.debug("SFE:failed rental editation");
                    JOptionPane.showMessageDialog(rootPane, MainWindow.getMessage("texts", "ErrorUpdateRental"));
                }
            }catch(InterruptedException iex){
                log.error("Done method editing rental interrupted");
                throw new RuntimeException(MainWindow.getMessage("texts", "ErrorEditingInterruptedRen"),iex);
            }
        }
    }
    /**
     * Creates new form RentalEditPopUp
     */
    public RentalEditPopUp(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        log.debug("RentalEditPopUp ctor");
        rtm = ((MainWindow)getParent()).getRentalTableModel();
        selectedRow = ((MainWindow) getParent()).getRentalTable().getSelectedRow();
        rental = rm.getRentalByID((Long)rtm.getValueAt(selectedRow,0));
        initComponents();
    }
    
    private void editRental(RentalManager rm){
        log.debug("Editing rental");
        try{
            this.rental.setRentedFrom(dateFromChooser.getDate());
            this.rental.setRentedTo(dateUntilChooser.getDate());
            this.rental.setBicycle((Bicycle) bicycleSelector.getSelectedItem());
            this.rental.setCustomer((Customer) customerSelector.getSelectedItem());
            REditSW resw = new REditSW(rental);
            resw.execute();
        }catch(ServiceFailureException ex){
            log.error("Error creating rental");
            throw new ServiceFailureException(MainWindow.getMessage("texts", "ErrorUpdateRental"),ex);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        descriptionLabel = new javax.swing.JLabel();
        rentedFromLabel = new javax.swing.JLabel();
        dateFromChooser = new com.toedter.calendar.JDateChooser();
        rentedToLabel = new javax.swing.JLabel();
        dateUntilChooser = new com.toedter.calendar.JDateChooser();
        customerLabel = new javax.swing.JLabel();
        customerSelector = new javax.swing.JComboBox();
        bicycleIDLabel = new javax.swing.JLabel();
        bicycleSelector = new javax.swing.JComboBox();
        OKButton = new javax.swing.JButton();
        NOButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        descriptionLabel.setText(MainWindow.getMessage("texts", "REP_desc"));

        rentedFromLabel.setText(MainWindow.getMessage("texts", "RFrom"));

        dateFromChooser.setDate(rental.getRentedFrom());

        rentedToLabel.setText(MainWindow.getMessage("texts", "RUntil"));

        dateUntilChooser.setDate(rental.getRentedTo());

        customerLabel.setText(MainWindow.getMessage("texts", "RCustomer"));

        customerSelector.setModel(new javax.swing.DefaultComboBoxModel(((ArrayList)(MainWindow.getCustomerManager().findAllCustomers())).toArray()));
        customerSelector.setSelectedItem(this.rental.getCustomer());

        bicycleIDLabel.setText(MainWindow.getMessage("texts", "RBicycle"));

        bicycleSelector.setModel(new javax.swing.DefaultComboBoxModel(((ArrayList)(MainWindow.getBicycleManager().findAllBicycles())).toArray()));
        bicycleSelector.setSelectedItem(this.rental.getBicycle());

        OKButton.setText(MainWindow.getMessage("texts", "Popup_OK"));
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        NOButton.setText(MainWindow.getMessage("texts", "Popup_NO"));
        NOButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NOButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(OKButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(NOButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(customerLabel)
                                    .addComponent(bicycleIDLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(customerSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(bicycleSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(rentedFromLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rentedToLabel, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dateFromChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                    .addComponent(dateUntilChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(descriptionLabel)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rentedFromLabel)
                    .addComponent(dateFromChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rentedToLabel)
                    .addComponent(dateUntilChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customerLabel)
                    .addComponent(customerSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bicycleIDLabel)
                    .addComponent(bicycleSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(NOButton))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        editRental(rm);
        rtm.reload();
        this.setVisible(false);
        this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_OKButtonActionPerformed

    private void NOButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NOButtonActionPerformed
        this.setVisible(false);
        this.dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_NOButtonActionPerformed

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
            java.util.logging.Logger.getLogger(RentalEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RentalEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RentalEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RentalEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RentalEditPopUp dialog = new RentalEditPopUp(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton NOButton;
    private javax.swing.JButton OKButton;
    private javax.swing.JLabel bicycleIDLabel;
    private javax.swing.JComboBox bicycleSelector;
    private javax.swing.JLabel customerLabel;
    private javax.swing.JComboBox customerSelector;
    private com.toedter.calendar.JDateChooser dateFromChooser;
    private com.toedter.calendar.JDateChooser dateUntilChooser;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel rentedFromLabel;
    private javax.swing.JLabel rentedToLabel;
    // End of variables declaration//GEN-END:variables
}
