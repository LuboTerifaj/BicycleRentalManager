/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import cz.muni.fi.BicycleRental.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author StrikerSVK
 */
public class CustomerTableModel extends AbstractTableModel {
    
    private final List<Customer> customers;
    private final CustomerManager customerManager;
    private final static Logger log = LoggerFactory.getLogger(CustomerTableModel.class);

    public CustomerTableModel() {
        this.customers = new ArrayList<>();
        this.customerManager = MainWindow.getCustomerManager();
    }
    @Override
    public int getRowCount() {
        return customers.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        log.debug("CustomerTableModel.getValueAt {};{}",rowIndex,columnIndex);
        if(rowIndex == -1){
            log.debug("Bad selection of table element");
            JOptionPane.showMessageDialog(null, MainWindow.getMessage("texts", "ErrorNothingSelected"));
        }
        Customer customer = customers.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return customer.getId();
            case 1:
                return customer.getFullName();
            case 2:
                return customer.getAdress();
            case 3:
                return customer.getPhone();
            default:{
                log.error("IAE:CustomerTableModel.getValueAt {};{}",rowIndex,columnIndex);
                throw new IllegalArgumentException("columnIndex in customer table invalid");
            }
        }
    }
    @Override
    public String getColumnName (int columnIndex){
        log.debug("CustomerTableModel.getColumnName {}",columnIndex);
        switch(columnIndex){
            case 0:
                return MainWindow.getMessage("texts", "ID");
            case 1:
                return MainWindow.getMessage("texts", "Main_cName");
            case 2:
                return MainWindow.getMessage("texts", "Main_cAddress");
            case 3:
                return MainWindow.getMessage("texts", "Main_cPhone");
            default:{
                log.error("IAE:CustomerTableModel.getColumnName {}",columnIndex);
                throw new IllegalArgumentException("columnIndex in customer table greater than 3");
            }
        }
        
    }
    public void reload() {
        log.debug("Reloading customers after updating table");
        try{
        customers.clear();
        customers.addAll(customerManager.findAllCustomers());
        fireTableDataChanged();
        }catch(ServiceFailureException ex){
            log.error("SFE reloading customers after update");
            throw new ServiceFailureException(ex);
        }
    }
    
}
