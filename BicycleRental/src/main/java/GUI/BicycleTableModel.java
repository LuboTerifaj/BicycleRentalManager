/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import cz.muni.fi.BicycleRental.Bicycle;
import cz.muni.fi.BicycleRental.BicycleManager;
import cz.muni.fi.BicycleRental.Rental;
import cz.muni.fi.BicycleRental.ServiceFailureException;
import cz.muni.fi.BicycleRental.RentalManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lubo
 */
public class BicycleTableModel extends AbstractTableModel {

    private final List<Bicycle> bicycles;
    private final BicycleManager bicycleManager;
    private final RentalManager rentalManager;
    private final static Logger log = LoggerFactory.getLogger(BicycleTableModel.class);

    public BicycleTableModel() {
        this.bicycleManager = MainWindow.getBicycleManager();
        this.bicycles = new ArrayList<>();
        this.rentalManager = MainWindow.getRentalManager();
    }
    
    @Override
    public int getRowCount() {
        return bicycles.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        log.debug("BicycleTableModel.getValueAt {};{}",rowIndex,columnIndex);
        if(rowIndex == -1){
            log.debug("Bad selection of table element");
            JOptionPane.showMessageDialog(null, MainWindow.getMessage("texts", "ErrorNothingSelected"));
        }
        Bicycle bicycle = bicycles.get(rowIndex);
        NumberFormat priceFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        priceFormat.setCurrency(Currency.getInstance("EUR"));
        
        switch (columnIndex) {
            case 0:
                return bicycle.getId();
            case 1:
                return priceFormat.format(bicycle.getPrice());
            case 2:
                return bicycle.isAvailable();
            default:{
                log.error("IAE:BicycleTableModel.getValueAt {};{}",rowIndex,columnIndex);
                throw new IllegalArgumentException("columnIndex in bicycle table greater than 2");   
            }
        }
    }
    
    @Override
    public String getColumnName (int columnIndex){
        log.debug("BicycleTableModel.getColumnName {}",columnIndex);
        switch(columnIndex){
            case 0:
                return MainWindow.getMessage("texts", "ID");
            case 1:
                return MainWindow.getMessage("texts", "Main_bPrice");
            case 2:
                return MainWindow.getMessage("texts", "Main_bAvail");
            default:{
                log.error("IAE:BicycleTableModel.getColumnName {}",columnIndex);
                throw new IllegalArgumentException("columnIndex in bicycle table greater than 2");
            }
        }
        
    }

    private void checkAvail(){
        Date cdate = new Date();
        for(Bicycle b : bicycleManager.findAllBicycles()){
            for(Rental r : rentalManager.findAllRentals()){
                if(b.getId().equals(r.getBicycle().getId())){
                    if((cdate.compareTo(r.getRentedFrom())>=0) && (cdate.compareTo(r.getRentedTo())<=0)){
                    b.setAvailable(false);
                    this.bicycleManager.updateBicycle(b);
                    }
                }
            }
        }
    }
    
    /**
     * Reloads all bicycles from the database
     *
     */
    public void reload() {
        log.debug("Reloading bicycles after update to table");
        try{
        bicycles.clear();
        bicycles.addAll(bicycleManager.findAllBicycles());
        checkAvail();
        fireTableDataChanged();
        }catch(ServiceFailureException ex){
            log.error("SFE reloading bicycles after update");
            throw new ServiceFailureException(ex);
        }catch(IllegalArgumentException ex){
            log.error("IAE reloading bicycles");
        }
    }

}
