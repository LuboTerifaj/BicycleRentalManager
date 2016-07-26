package cz.muni.fi.BicycleRental;

import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
/**
 * @author  Lubo Terifaj
 */
public class CustomerManagerImpl implements CustomerManager {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(BicycleManagerImpl.class);

    private final DataSource dataSource;

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createCustomer(Customer customer) throws ServiceFailureException {
        if(customer==null){
            throw new IllegalArgumentException("Customer to create is null");
        }
        if(customer.getFullName()==null){
            throw new IllegalArgumentException("Customer name is null");
        }
        if(customer.getAdress()==null){
            throw new IllegalArgumentException("Customer address is null");
        }
        if(customer.getPhone()==null){
            throw new IllegalArgumentException("Customer phone number is null");
        }
        for(Customer c: this.findAllCustomers()){
            if(c.getFullName().equals(customer.getFullName()) && c.getAdress().equals(customer.getAdress()) && c.getPhone().equals(customer.getPhone()))
                throw new IllegalArgumentException("Customer already exists");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO customer (fullname,address,phone) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, customer.getFullName());
                st.setString(2, customer.getAdress());
                st.setString(3, customer.getPhone());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert customer " + customer);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                customer.setId(getKey(keyRS, customer));
            }
        } catch (SQLException ex) {
            log.error("Database connection problem", ex);
            throw new ServiceFailureException("Error when creating customer", ex);
        }
    }
    private Long getKey(ResultSet keyRS, Customer customer) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert customer " + customer
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert customer " + customer
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert customer " + customer
                    + " - no key found");
        }
    }

    private Customer resultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer=new Customer();
        customer.setId(rs.getLong("id"));
        customer.setAdress(rs.getString("address"));
        customer.setFullName(rs.getString("fullname"));
        customer.setPhone(rs.getString("phone"));
        return customer;
    }
    @Override
    public Customer getCustomerByID(Long id) throws ServiceFailureException {
        log.debug("finding customer by ID");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM customer WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Customer customer = resultSetToCustomer(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: Multiple entities with the same id found "
                                        + "(source id: " + id + ", found " + customer + " and " + resultSetToCustomer(rs));
                    }
                    return customer;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when retrieving customer by ID", ex);
        }
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws ServiceFailureException {
        log.debug("finding all customers");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM bicycle WHERE fullname = ?")) {
                st.setString(1, name);
                ResultSet rs = st.executeQuery();
                List<Customer> customerList = new ArrayList<>();
                while (rs.next()) {
                    customerList.add(resultSetToCustomer(rs));
                }
                return customerList;
            }
        } catch (SQLException ex) {
            log.error("Database connection problem", ex);
            throw new ServiceFailureException("Error when retrieving customers by name", ex);
        }
    }

    @Override
    public List<Customer> findAllCustomers() throws ServiceFailureException {
        log.debug("finding all customers");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM customer")) {
                ResultSet rs = st.executeQuery();
                ArrayList<Customer> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToCustomer(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all customers", ex);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws ServiceFailureException {
        log.debug("updating customer");
        if (customer == null) throw new IllegalArgumentException("Customer pointer is null");
        if (customer.getId() == null) throw new IllegalArgumentException("Customer with null id cannot be updated");

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE customer SET fullname=?,address=?,phone=? WHERE id=?")) {
                st.setString(1, customer.getFullName());
                st.setString(2, customer.getAdress());
                st.setString(3, customer.getPhone());
                st.setLong(4, customer.getId());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("cannot update customer " + customer);
                }
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when updating customer", ex);
        }
    }

    @Override
    public void deleteCustomer(Long id) throws ServiceFailureException {
        log.debug("deleting customer");
        if(id == null) {
            throw new  ServiceFailureException("Customer ID is null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM customer WHERE id=?")) {
                st.setLong(1, id);
                if (st.executeUpdate() != 1) {
                    throw new ServiceFailureException("Did not delete customer with id =" + id);
                }
            }
        } catch (SQLException ex) {
            log.error("DB connection problem", ex);
            throw new ServiceFailureException("Error when deleting customer with ID " + id, ex);
        }
    }
}
