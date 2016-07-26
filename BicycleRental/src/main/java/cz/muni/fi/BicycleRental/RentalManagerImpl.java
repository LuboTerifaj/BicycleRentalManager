package cz.muni.fi.BicycleRental;

import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
/**
 * @author  Lubo Terifaj, Matej Harcar
 */
public class RentalManagerImpl implements RentalManager {
    final static org.slf4j.Logger log = LoggerFactory.getLogger(BicycleManagerImpl.class);

    private final DataSource dataSource;

    public RentalManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createRental(Rental rental) throws ServiceFailureException {
        if(rental==null){
            throw new IllegalArgumentException("Rental pointer is null");
        }
        if(rental.getRentedFrom()==null || rental.getRentedTo()==null){
            throw new IllegalArgumentException("Rental date is null");
        }
        if(rental.getCustomer()==null){
            throw new IllegalArgumentException("Customer pointer is null");
        }
        if(rental.getRentedFrom().compareTo(rental.getRentedTo())>0){
            throw new IllegalArgumentException("Invalid rental dates: start after end");
        }
        if(rental.getRentedFrom().compareTo(new Date()) < 0){
            throw new IllegalArgumentException("Invalid rental dates: start before present");
        }
        if(rental.getRentedTo().compareTo(new Date()) < 0){
            throw new IllegalArgumentException("Invalid rental dates: end before present");
        }
        if(rental.getBicycle()==null){
            throw new IllegalArgumentException("Bicycle pointer is null");
        }
        for(Rental r: this.findAllRentals()){
            if(rental.getBicycle().equals(r.getBicycle())
                && rental.getCustomer().equals(r.getCustomer())
                && rental.getRentedFrom().equals(r.getRentedFrom())
                && rental.getRentedTo().equals(r.getRentedTo()))
            {
                throw new IllegalArgumentException("Rental already exists");
            }
        }
        CustomerManagerImpl cmi= new CustomerManagerImpl(dataSource);
        Customer c = rental.getCustomer();
        if(c.getId()==null){
            throw new ServiceFailureException("Customer ID is null");
        }
        if(cmi.getCustomerByID(c.getId())==null || !c.equals(cmi.getCustomerByID(c.getId()))){
            throw new ServiceFailureException("Customer is not in database");
        }

        BicycleManagerImpl bmi = new BicycleManagerImpl(dataSource);
        Bicycle b = rental.getBicycle();
        if(b.getId()==null){
            throw new ServiceFailureException("Bicycle ID is null");
        }
        if(bmi.getBicycleByID(b.getId())==null || !b.equals(bmi.getBicycleByID(b.getId()))){
            throw new ServiceFailureException("Bicycle is not in database");
        }

        if(rental.getRentedFrom().getTime() > rental.getRentedTo().getTime()){
            throw new IllegalArgumentException("End date is earlier than start date");
        }
        if(!rental.getBicycle().isAvailable()){
            throw new IllegalArgumentException("Bicycle is already rented");
        }
        for(Rental r :findRentalsByBicycle(b) ) {
            Long dx = rental.getRentedFrom().getTime();
            Long d1 = r.getRentedFrom().getTime();
            Long d2 = r.getRentedTo().getTime();
            if(dx <= d2 && dx >= d1){
                throw new IllegalArgumentException("Bicycle is already rented");
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO rental (rentedFrom,rentedTo,customerID,bicycleID) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                java.sql.Date from = new java.sql.Date(rental.getRentedFrom().getTime());
                java.sql.Date to = new java.sql.Date(rental.getRentedTo().getTime());
                st.setDate(1, from);
                st.setDate(2, to);
                st.setLong(3, rental.getCustomer().getId());
                st.setLong(4, rental.getBicycle().getId());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert rental " + rental);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                rental.setRentalID(getKey(keyRS, rental));
            }
        } catch (SQLException ex) {
            log.error("Database connection problem", ex);
            throw new ServiceFailureException("Error when creating rental", ex);
        }
        Date cdate = new Date();
        if((cdate.compareTo(rental.getRentedFrom())) >= 0 && (cdate.compareTo(rental.getRentedFrom()) <= 0)){
            b.setAvailable(false);
            bmi.updateBicycle(b);
        }
    }

    private Long getKey(ResultSet keyRS, Rental rental) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert rental " + rental
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert rental " + rental
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert rental " + rental
                    + " - no key found");
        }
    }

    @Override
    public Rental getRentalByID(Long rentalID) throws ServiceFailureException {
        log.debug("finding rental by ID");
        if(rentalID==null){
            throw new IllegalArgumentException("ID to search for is null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM rental WHERE id = ?")) {
                st.setLong(1, rentalID);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Rental rental = resultSetToRental(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: Multiple entities with the same id found "
                                        + "(source id: " + rentalID + ", found " + rental + " and " + resultSetToRental(rs));
                    }
                    return rental;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when retrieving rental by ID", ex);
        }
    }

    private Rental resultSetToRental(ResultSet rs) throws SQLException{
        CustomerManagerImpl cm = new CustomerManagerImpl(this.dataSource);
        BicycleManagerImpl bm = new BicycleManagerImpl(this.dataSource);
        Rental rental=new Rental();
        rental.setRentalID(rs.getLong("ID"));
        rental.setCustomer(cm.getCustomerByID(rs.getLong("customerID")));
        rental.setBicycle(bm.getBicycleByID(rs.getLong("bicycleID")));
        java.util.Date from = new java.util.Date(rs.getDate("rentedFrom").getTime());
        java.util.Date to = new java.util.Date(rs.getDate("rentedTo").getTime());
        rental.setRentedFrom(from);
        rental.setRentedTo(to);
        return rental;
    }

    @Override
    public List<Rental> findAllRentals() throws ServiceFailureException {
        log.debug("finding all rentals");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM rental")) {
                ResultSet rs = st.executeQuery();
                ArrayList<Rental> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToRental(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all rentals", ex);
        }
    }

    @Override
    public List<Rental> findRentalsByCustomer(Customer customer) {
        log.debug("finding rentals by customer");
        if(customer==null){
            throw new IllegalArgumentException("Customer to search for is null");
        }
        if(customer.getId()==null){
            throw new IllegalArgumentException("Customer to search for has null ID");
        }
        CustomerManagerImpl cmi = new CustomerManagerImpl(dataSource);
        Long cid = customer.getId();
        if(cmi.getCustomerByID(cid)==null || !cmi.getCustomerByID(cid).equals(customer)){
            throw new ServiceFailureException("Customer not in DB");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM rental WHERE customerID=?")) {
                st.setLong(1, customer.getId());
                ResultSet rs = st.executeQuery();
                ArrayList<Rental> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToRental(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when retrieving rentals by customer", ex);
        }
    }

    @Override
    public List<Rental> findRentalsByBicycle(Bicycle bicycle) throws ServiceFailureException {
        log.debug("finding rentals by bicycle");
        if(bicycle==null){
            throw new IllegalArgumentException("Bicycle is null");
        }
        if(bicycle.getId()==null){
            throw new IllegalArgumentException("Bicycle ID is null");
        }
        BicycleManagerImpl bmi = new BicycleManagerImpl(dataSource);
        Long bid = bicycle.getId();
        if(bmi.getBicycleByID(bid)==null || !bmi.getBicycleByID(bid).equals(bicycle)){
            throw new ServiceFailureException("Bicycle not in DB");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM rental WHERE bicycleID=?")) {
                st.setLong(1, bicycle.getId());
                ResultSet rs = st.executeQuery();
                ArrayList<Rental> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToRental(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when retrieving rentals by bicycle", ex);
        }
    }

    @Override
    public void updateRental(Rental rental) throws ServiceFailureException {
        log.debug("updating rental");
        if (rental == null) throw new IllegalArgumentException("Rental pointer is null");
        if (rental.getRentedFrom()==null) throw new IllegalArgumentException("Rental date is null");
        if (rental.getCustomer()==null) throw new IllegalArgumentException("Rental customer pointer is null");
        if (rental.getBicycle()==null) throw new IllegalArgumentException("Rental bicycle pointer is null");
        if (rental.getRentalID() == null) throw new IllegalArgumentException("Customer with null id cannot be updated");
        if(rental.getRentedFrom().compareTo(rental.getRentedTo())>0){
            throw new IllegalArgumentException("Invalid rental dates: start after end");
        }
        if(rental.getRentedFrom().compareTo(new Date()) < 0){
            throw new IllegalArgumentException("Invalid rental dates: start before present");
        }
        if(rental.getRentedTo().compareTo(new Date()) < 0){
            throw new IllegalArgumentException("Invalid rental dates: end before present");
        }
        if(rental.getRentalID() < 1L) throw new IllegalArgumentException("Invalid Rental ID");
        if(rental.getCustomer().getId()==null) throw new IllegalArgumentException("Null customer ID");
        if(rental.getBicycle().getId()==null) throw new IllegalArgumentException("Null bicycle ID");
        BicycleManagerImpl bmi = new BicycleManagerImpl(dataSource);
        Long bid = rental.getBicycle().getId();
        if(bmi.getBicycleByID(bid)==null || !bmi.getBicycleByID(bid).equals(rental.getBicycle())){
            throw new ServiceFailureException("Bicycle not in DB");
        }
        CustomerManagerImpl cmi = new CustomerManagerImpl(dataSource);
        Long cid = rental.getCustomer().getId();
        if(cmi.getCustomerByID(cid)==null || !cmi.getCustomerByID(cid).equals(rental.getCustomer())){
            throw new ServiceFailureException("Customer not in DB");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE rental SET rentedFrom=?,rentedTo=?,customerID=?,bicycleID=?  WHERE id=?")) {
                st.setDate(1, new java.sql.Date(rental.getRentedFrom().getTime()));
                st.setDate(2, new java.sql.Date(rental.getRentedTo().getTime()));
                st.setLong(3, rental.getCustomer().getId());
                st.setLong(4, rental.getBicycle().getId());
                st.setLong(5, rental.getRentalID());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("cannot update rental " + rental);
                }
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when updating customer", ex);
        }
        Date cdate = new Date();
        if((cdate.compareTo(rental.getRentedFrom())) >= 0 && (cdate.compareTo(rental.getRentedFrom()) <= 0)){
            bmi.getBicycleByID(bid).setAvailable(false);
            bmi.updateBicycle(bmi.getBicycleByID(bid));
        }
    }

    @Override
    public void deleteRental(Rental rental) throws ServiceFailureException {
        log.debug("deleting rental");
        if(rental==null) {
            throw new IllegalArgumentException("Rental to delete is null");
        }
        if(rental.getRentalID()==null) {
            throw new IllegalArgumentException("Rental ID is null");
        }
        if(rental.getRentalID() < 1) {
            throw new IllegalArgumentException("Rental ID invalid (less than 1)");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM rental WHERE id=?")) {
                st.setLong(1, rental.getRentalID());
                if (st.executeUpdate() != 1) {
                    throw new ServiceFailureException("Did not delete rental with id =" + rental.getRentalID());
                }
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when deleting rental with ID " + rental.getRentalID(), ex);
        }
    }

}

