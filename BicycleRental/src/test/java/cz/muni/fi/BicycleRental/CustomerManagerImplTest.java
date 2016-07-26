package cz.muni.fi.BicycleRental;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Matej Harcar,Lubo Terifaj
 */
public class CustomerManagerImplTest {
    private CustomerManagerImpl manager;
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:CustomerManagerTest;create=true");
        this.dataSource = bds;
        //create new empty table before every test
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE customer ("
                    + "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "fullname VARCHAR(50),"
                    + "address VARCHAR(200),"
                    + "phone VARCHAR (30))").executeUpdate();
        }
        manager = new CustomerManagerImpl(bds);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            con.prepareStatement("DROP TABLE customer").executeUpdate();
        }
    }

    static Customer createNewCustomer(String name, String phone, String address){
        Customer customer = new Customer();
        customer.setFullName(name);
        customer.setPhone(phone);
        customer.setAdress(address);
        return customer;
    }

    private static final Comparator<Customer> idComp = new Comparator<Customer>() {
        public int compare(Customer o1, Customer o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    @Test
    public void testCreateCustomer() {
        Customer customer = createNewCustomer("John Winston Smith IV","563-645-897-213","2800 Brookmere Road, Arcadia,Pennsylvania, USA");
        manager.createCustomer(customer);
        Long cusID= customer.getId();
        assertNotNull(cusID);
        Customer result = manager.getCustomerByID(cusID);
        assertEquals(customer, result);
        assertNotSame(customer, result);
    }

    @Test
    public void testGetCustomerByID() {
        assertNull(manager.getCustomerByID(1l));
        Customer customer = createNewCustomer("John Winston Smith IV", "563-645-897-213", "2800 Brookmere Road, Arcadia,Pennsylvania, USA");
        manager.createCustomer(customer);
        Long cusID = customer.getId();
        Customer result = manager.getCustomerByID(cusID);
        assertEquals(customer, result);
    }
    @Test
    public void testFindAllCustomers() {

        assertTrue(manager.findAllCustomers().isEmpty());

        Customer c1 = createNewCustomer("John Winston Smith IV", "563-645-897-213", "2800 Brookmere Road, Arcadia,Pennsylvania, USA");
        Customer c2 = createNewCustomer("Reginald Sylvester Pound V","686-713-365-994","3256 Golden Avenue, Spokane, Oregon, USA");

        manager.createCustomer(c1);
        manager.createCustomer(c2);

        List<Customer> expected = Arrays.asList(c1, c2);
        List<Customer> actual = manager.findAllCustomers();

        Collections.sort(actual, idComp);
        Collections.sort(expected, idComp);

        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteCustomer() {

        Customer c1 = createNewCustomer("John Winston Smith IV", "563-645-897-213", "2800 Brookmere Road, Arcadia,Pennsylvania, USA");
        Customer c2 = createNewCustomer("Reginald Sylvester Pound V", "686-713-365-994", "3256 Golden Avenue, Spokane, Oregon, USA");

        manager.createCustomer(c1);
        manager.createCustomer(c2);

        assertNotNull(manager.getCustomerByID(c1.getId()));
        assertNotNull(manager.getCustomerByID(c2.getId()));

        manager.deleteCustomer(c1.getId());

        assertNull(manager.getCustomerByID(c1.getId()));
        assertNotNull(manager.getCustomerByID(c2.getId()));

    }


}