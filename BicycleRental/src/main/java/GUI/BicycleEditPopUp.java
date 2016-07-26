/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import cz.muni.fi.BicycleRental.Bicycle;
import cz.muni.fi.BicycleRental.BicycleManager;
import cz.muni.fi.BicycleRental.ServiceFailureException;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author StrikerSVK
 */
public class BicycleEditPopUp extends javax.swing.JDialog {

    private static final BicycleManager bicycleManager = MainWindow.getBicycleManager();
    private static BicycleTableModel bicycleTableModel;
    private static final Logger log = LoggerFactory.getLogger(BicycleEditPopUp.class);
    private final int selectedRow;
    private final Bicycle bicycle;
    
    private class BicycleEditSwingWorker extends SwingWorker<Integer, Void> {
        private final Bicycle bicycle;
        
        public BicycleEditSwingWorker(Bicycle bicycle) {
            this.bicycle = bicycle;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            log.debug("Creating bicycle");
            bicycleManager.updateBicycle(bicycle);
            return 0;
        }
        
        @Override
        protected void done() {
            log.debug("BicycleEditPopUp:done");
            try {
                get();
                bicycleTableModel.reload();
            }
            catch (ExecutionException exex) {
                if(exex.getCause().getClass().equals(IllegalArgumentException.class)){
                    log.debug("IAE:fields@bicycle update");
                    JOptionPane.showMessageDialog(rootPane,MainWindow.getMessage("texts", "Error_Fields"));
                
                }
                if(exex.getCause().getClass().equals(ServiceFailureException.class)){
                    log.debug("SFE:failed bicycle update");
                    JOptionPane.showMessageDialog(rootPane, MainWindow.getMessage("texts", "ErrorUpdateBicycle"));
                }                
            }
            catch(InterruptedException iex){
                log.error("Done method updating bicycle interrupted");
                throw new RuntimeException("Done method updating bicycle interrupted",iex);
            }
        }
        
    }
    
    private void editBicycle() {
        log.debug("Editing bicycle");
        try {
            this.bicycle.setPrice(Integer.parseInt(this.priceField.getText()));
            BicycleEditSwingWorker besw = new BicycleEditSwingWorker(bicycle);
            besw.execute();
        }catch(NumberFormatException ex) {
            log.error("Could not parse integer from text field");
            throw new IllegalArgumentException(ex);
        }
        }
    /**
     * Creates new form BicycleEditPopUpX
     */
    public BicycleEditPopUp(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        bicycleTableModel = ((MainWindow) getParent()).getBicycleTableModel();
        selectedRow = ((MainWindow) getParent()).getBicycleTable().getSelectedRow();
        bicycle = bicycleManager.getBicycleByID((Long)bicycleTableModel.getValueAt(selectedRow, 0));
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        DescriptionLabel = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        priceField = new javax.swing.JTextField();
        OKButton = new javax.swing.JButton();
        NOButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        DescriptionLabel.setText(MainWindow.getMessage("texts", "BEP_desc") + bicycle.getId());

        priceLabel.setText(MainWindow.getMessage("texts", "BP_price") +": ");

        priceField.setText(((Integer)this.bicycle.getPrice()).toString());

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
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(DescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(priceLabel)
                                .addGap(18, 18, 18)
                                .addComponent(priceField, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(OKButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                                .addComponent(NOButton)))
                        .addGap(42, 42, 42))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(DescriptionLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(priceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(NOButton))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        editBicycle();
        bicycleTableModel.reload();
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
            java.util.logging.Logger.getLogger(BicycleEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BicycleEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BicycleEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BicycleEditPopUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BicycleEditPopUp dialog = new BicycleEditPopUp(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel DescriptionLabel;
    private javax.swing.JButton NOButton;
    private javax.swing.JButton OKButton;
    private javax.swing.JTextField priceField;
    private javax.swing.JLabel priceLabel;
    // End of variables declaration//GEN-END:variables
}