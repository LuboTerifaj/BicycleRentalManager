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
public class RentalTableModel extends AbstractTableModel {

    private final List<Rental> rentals;
    private final RentalManager rentalManager;
    private final static Logger log = LoggerFactory.getLogger(RentalTableModel.class);

    public RentalTableModel() {
        this.rentalManager = MainWindow.getRentalManager();
        this.rentals = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return rentals.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        log.debug("RentalTableModel.getValueAt {};{}",rowIndex,columnIndex);
        if(rowIndex == -1){
            log.debug("Bad selection of table element");
            JOptionPane.showMessageDialog(null, MainWindow.getMessage("texts", "ErrorNothingSelected"));
        }
        Rental r = rentals.get(rowIndex);
        
        switch(columnIndex){
            case 0: return r.getRentalID();
            case 1: return r.getRentedFrom();
            case 2: return r.getRentedTo();
            case 3: return r.getCustomer().toString();
            case 4: return r.getBicycle().getId();
            default: {
                log.error("IAE:@RentalTable.getValueAt {};{}",rowIndex,columnIndex);
                throw new IllegalArgumentException("Column Index in rentals table greater than 4");
            }
        }
    }
    
    @Override
    public String getColumnName (int columnIndex){
        log.debug("RentalTableModel.getColumnName {}",columnIndex);
        switch(columnIndex){
            case 0:
                return MainWindow.getMessage("texts", "ID");
            case 1:
                return MainWindow.getMessage("texts", "RFrom");
            case 2:
                return MainWindow.getMessage("texts", "RUntil");
            case 3:
                return MainWindow.getMessage("texts", "RCustomer");
            case 4:
                return MainWindow.getMessage("texts", "RBicycle");
            default:{
                log.error("IAE:RentalTableModel.getColumnName {}",columnIndex);
                throw new IllegalArgumentException("columnIndex");
            }
        }
        
    }
    
    public void reload() {
        log.debug("Reloading rentals");
        try{
        rentals.clear();
        rentals.addAll(rentalManager.findAllRentals());
        fireTableDataChanged();
        }catch(ServiceFailureException ex){
            log.error("SFE reloading rentals after update");
            throw new ServiceFailureException(ex);
        }
    }
    
}
