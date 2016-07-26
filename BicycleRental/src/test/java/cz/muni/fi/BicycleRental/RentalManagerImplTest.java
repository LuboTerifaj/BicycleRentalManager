package cz.muni.fi.BicycleRental;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static cz.muni.fi.BicycleRental.BicycleManagerImplTest.createNewBike;
import static cz.muni.fi.BicycleRental.CustomerManagerImplTest.createNewCustomer;
import static org.junit.Assert.*;

//import java.util.Date;

/**
 * @author Lubo Terifaj, Matej Harcar
 */
public class RentalManagerImplTest {

    private DataSource dataSource;
    private RentalManagerImpl rentalManager;
    private BicycleManagerImpl bicycleManager;
    private CustomerManagerImpl customerManager;
/*
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //memory database
        ds.setUrl("jdbc:derby:memory:RentalManagerTest;create=true");
        return ds;
    }
    */

    private Bicycle b1, b2, b3, bicycleWithNullId, bicycleNotInDB;
    private Customer c1, c2, c3, customerWithNullId, customerNotInDB;

    private void prepareTestData() {

        b1 = createNewBike(100,true);
        b2 = createNewBike(120,true);
        b3 = createNewBike(130,true);

        bicycleManager.createBicycle(b1);
        bicycleManager.createBicycle(b2);
        bicycleManager.createBicycle(b3);

        c1 = createNewCustomer("Jakub Podlomnik", "+421944687671", "Lipova 48, 608 00 Brno");
        c2 = createNewCustomer("Tomas Kluka", "+420753689427", "Masarykova 16, 608 00 Brno");
        c3 = createNewCustomer("Peter Slivka","+420758436971","Botanicka 47, 608 00 Brno");

        customerManager.createCustomer(c1);
        customerManager.createCustomer(c2);
        customerManager.createCustomer(c3);

        bicycleWithNullId = createNewBike(200,true);
        bicycleNotInDB = createNewBike(175, true);
        bicycleNotInDB.setId(b3.getId() + 100);

        customerWithNullId = createNewCustomer("Jakub Podlomnik", "+421944687671", "Lipova 48, 608 00 Brno");
        customerNotInDB = createNewCustomer("Jakub Podlomnik", "+421944687671", "Lipova 48, 608 00 Brno");
        customerNotInDB.setId(c3.getId() + 100);

    }

    @Before
    public void setUp() throws SQLException {
        BasicDataSource bds = new BasicDataSource();
        //memory database
        bds.setUrl("jdbc:derby:memory:RentalManagerTest;create=true");


       this.dataSource = bds;

        bicycleManager = new BicycleManagerImpl(dataSource);
        customerManager = new CustomerManagerImpl(dataSource);
        rentalManager = new RentalManagerImpl(dataSource);

        //create new empty tables before every test
        try (Connection conn = bds.getConnection()) {

            conn.prepareStatement("CREATE TABLE bicycle ("
                    + "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "price INT,"
                    + "available SMALLINT)").executeUpdate();

            conn.prepareStatement("CREATE TABLE customer ("
                    + "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "fullname VARCHAR(50),"
                    + "address VARCHAR(200),"
                    + "phone VARCHAR (30))").executeUpdate();

            conn.prepareStatement("CREATE TABLE rental ("
                    + "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "rentedFrom DATE,"
                    + "rentedTo DATE,"
                    + "customerID BIGINT REFERENCES customer (id),"
                    + "bicycleID BIGINT REFERENCES bicycle (id))").executeUpdate();
        }
        prepareTestData();
    }


    @After
    public void tearDown() throws SQLException {
        try (Connection con = dataSource.getConnection()) {

            con.prepareStatement("DROP TABLE rental").executeUpdate();
            con.prepareStatement("DROP TABLE bicycle").executeUpdate();
            con.prepareStatement("DROP TABLE customer").executeUpdate();
        }
    }


    @Test
    public void testCreateRental() {
        Rental rental = newRental(b1, c1, date("2017-05-10"), date("2017-05-10"));
        rentalManager.createRental(rental);

        Long rentalId = rental.getRentalID();
        assertNotNull(rentalId);

        Rental result = rentalManager.getRentalByID(rentalId);

        assertEquals(rental, result);
        assertNotSame(rental, result);
        assertDeepEquals(rental, result);

/*
        try {
            rental.setRentalID(-1L);
            rentalManager.createRental(rental);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }
        */
    }

    @Test
    public void testCreateRentalCustomerNotInDB() {
        Rental rental = newRental(b1,customerNotInDB, date("2017-05-10"),date("2017-05-10"));
        try{
            rentalManager.createRental(rental);
            fail();
        }
        catch (ServiceFailureException ex) {
            //ok
        }
    }

    @Test
    public void testCreateRentalCustomerWithNullId() {
        Rental rental = newRental(b1, customerWithNullId, date("2017-05-10"), date("2017-05-10"));
        try{
            rentalManager.createRental(rental);
            fail();
        }
        catch (ServiceFailureException ex) {
            //ok
        }
    }

    @Test
    public void testCreateRentalBicycleNotInDB() {
        Rental rental = newRental(bicycleNotInDB, c1, date("2017-05-10"), date("2017-05-10"));
        try{
            rentalManager.createRental(rental);
            fail();
        }
        catch (ServiceFailureException ex) {
            //ok
        }
    }

 /*   @Test
    public void testCreateRentalBicycleWithNullId() {
        Rental rental = newRental(bicycleWithNullId, c1, date("2015-05-10"), date("2015-05-10"));
        try{
            rentalManager.createRental(rental);
            fail();
        }
        catch (ServiceFailureException ex) {
            //ok
        }
    }

*/
    @Test
    public void testCreateRentalInvalidDate(){
        Rental rental = newRental(b1, c1, date("2017-05-02"), date("2017-04-28"));
        try{
            rentalManager.createRental(rental);
            fail();
        }
        catch (IllegalArgumentException ex){
            //ok
        }
    }

    @Test
    public void testCreateRentedBicycle(){
        Rental rental1 = newRental(b1, c1, date("2017-05-17"),date("2017-05-17"));
        Rental rental2 = newRental(b1, c2, date("2017-05-17"),date("2017-05-17"));

        try{
            rentalManager.createRental(rental1);
            rentalManager.createRental(rental2);
            fail();
        }
        catch (IllegalArgumentException ex){
            //ok
        }
    }



    @Test
    public void testGetRentalByID() {
        Rental rental = newRental(b1, c1, date("2017-06-03"), date("2017-06-03"));
        rentalManager.createRental(rental);

        Long rentalId = rental.getRentalID();
        assertNotNull(rentalId);

        Rental result = rentalManager.getRentalByID(rentalId);

        assertEquals(rental, result);
        assertDeepEquals(rental,result);
    }

    @Test
    public void testFindRentalByCustomer() {
        assertTrue(rentalManager.findRentalsByCustomer(c1).isEmpty());
        assertTrue(rentalManager.findRentalsByCustomer(c2).isEmpty());


        Rental rental1 = newRental(b1, c1, date("2017-06-17"), date("2017-06-18"));
        Rental rental2 = newRental(b2, c1, date("2017-06-17"), date("2017-06-18"));

        rentalManager.createRental(rental1);
        rentalManager.createRental(rental2);

        List<Rental> expected = Arrays.asList(rental1, rental2);
        List<Rental> result = rentalManager.findRentalsByCustomer(c1);

        assertTrue(rentalManager.findRentalsByCustomer(c2).isEmpty());
        assertDeepEquals(expected, result);

        try {
            rentalManager.findRentalsByCustomer(null);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            rentalManager.findRentalsByCustomer(customerWithNullId);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            rentalManager.findRentalsByCustomer(customerNotInDB);
            fail();
        } catch (ServiceFailureException ex) {}
    }

    @Test
    public void testFindRentalsByBicycle() {
        assertTrue(rentalManager.findRentalsByBicycle(b1).isEmpty());
        assertTrue(rentalManager.findRentalsByBicycle(b2).isEmpty());


        Rental rental1 = newRental(b1, c1, date("2017-06-17"), date("2017-06-18"));
        Rental rental2 = newRental(b2, c2, date("2017-06-17"), date("2017-06-18"));

        rentalManager.createRental(rental1);
        rentalManager.createRental(rental2);

        List<Rental> expected = Arrays.asList(rental1);
        List<Rental> result = rentalManager.findRentalsByBicycle(b1);

        //assertTrue(rentalManager.findRentalsByBicycle(b2).isEmpty());
        assertDeepEquals(expected, result);

        try {
            rentalManager.findRentalsByBicycle(null);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            rentalManager.findRentalsByBicycle(bicycleWithNullId);
            fail();
        } catch (IllegalArgumentException ex) {}

        try {
            rentalManager.findRentalsByBicycle(bicycleNotInDB);
            fail();
        } catch (ServiceFailureException ex) {}
    }

    @Test
    public void testFindAllRentals() {
        assertTrue(rentalManager.findAllRentals().isEmpty());

        Rental rental1 = newRental(b1, c1, date("2017-05-04"), date("2017-05-04"));
        Rental rental2 = newRental(b2, c1, date("2017-05-06"), date("2017-05-07"));
        Rental rental3 = newRental(b1, c2, date("2017-05-07"), date("2017-05-08"));

        rentalManager.createRental(rental1);
        rentalManager.createRental(rental2);
        rentalManager.createRental(rental3);

        List<Rental> expected = Arrays.asList(rental1,rental2, rental3);
        List<Rental> result = rentalManager.findAllRentals();

        Collections.sort(expected, idComparator);
        Collections.sort(result,idComparator);

        assertDeepEquals(expected,result);
    }

    @Test
    public void testUpdateRental() {
        Rental rental = newRental(b1, c1, date("2017-06-12"), date("2017-06-12"));
        rentalManager.createRental(rental);
        Long id = rental.getRentalID();

        rental = rentalManager.getRentalByID(id);

        rental.setRentedFrom(date("2017-06-07"));
        rentalManager.updateRental(rental);
        Rental result = rentalManager.getRentalByID(id);
        assertDeepEquals(rental, result);

        rental.setRentedTo(date("2017-06-08"));
        rentalManager.updateRental(rental);
        result = rentalManager.getRentalByID(id);
        assertDeepEquals(rental, result);

        rental.setBicycle(b2);
        rentalManager.updateRental(rental);
        result = rentalManager.getRentalByID(id);
        assertDeepEquals(rental, result);

        rental.setCustomer(c2);
        rentalManager.updateRental(rental);
        result = rentalManager.getRentalByID(id);
        assertDeepEquals(rental, result);
    }

    @Test
    public void testUpdateRentalWrongAttr() {
        Rental r = newRental(b1, c1, date("2017-05-05"), date("2017-05-06"));
        rentalManager.createRental(r);
        Long id = r.getRentalID();

        try{
            rentalManager.updateRental(null);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }

        try{
            r.setRentalID(null);
            rentalManager.updateRental(r);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }

        r.setRentalID(id);

        try {
            r.setRentedFrom(date("2017-05-17"));
            rentalManager.updateRental(r);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }

        try{
            r.setRentedTo(date("2017-04-17"));
            rentalManager.updateRental(r);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }
        r.setRentedTo(date("2017-05-30"));

        try {
            r.setCustomer(customerWithNullId);
            rentalManager.updateRental(r);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }
        r.setCustomer(c1);

        try{
            r.setBicycle(bicycleWithNullId);
            rentalManager.updateRental(r);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }
        r.setBicycle(b1);

        try{
            r.setBicycle(bicycleNotInDB);
            rentalManager.updateRental(r);
            fail();
        }
        catch(ServiceFailureException ex) {
            //ok
        }
        r.setBicycle(b1);

        try{
            r.setCustomer(customerNotInDB);
            rentalManager.updateRental(r);
            fail();
        }
        catch(ServiceFailureException ex) {
            //ok
        }
        r.setCustomer(c1);
    }

    @Test
    public void testDeleteRental() {
        Rental rental1 = newRental(b1, c1, date("2017-05-05"), date("2017-05-05"));
        Rental rental2 = newRental(b2, c1, date("2017-05-05"), date("2017-05-05"));

        rentalManager.createRental(rental1);
        rentalManager.createRental(rental2);

        assertNotNull(rentalManager.getRentalByID(rental1.getRentalID()));
        assertNotNull(rentalManager.getRentalByID(rental2.getRentalID()));

        rentalManager.deleteRental(rental2);

        assertNull(rentalManager.getRentalByID(rental2.getRentalID()));
        assertNotNull(rentalManager.getRentalByID(rental1.getRentalID()));
    }

    @Test
    public void testDeleteRentalWrongAttr() {
        Rental rental = newRental(b2, c1, date("2017-05-16"),date("2017-05-17"));
        rental.setRentalID(-1L);

        try{
            rentalManager.deleteRental(rental);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }

        try{
            rentalManager.deleteRental(null);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }

        try{
            rental.setRentalID(null);
            rentalManager.deleteRental(rental);
            fail();
        }
        catch(IllegalArgumentException ex) {
            //ok
        }
    }

    private Rental newRental(Bicycle bicycle, Customer customer, Date rentedFrom, Date rentedTo) {
        Rental rental = new Rental();
        bicycle.setAvailable(true);
        rental.setBicycle(bicycle);
        rental.setCustomer(customer);
        rental.setRentedFrom(rentedFrom);
        rental.setRentedTo(rentedTo);
        return  rental;
    }

    private Date date(String date) {
        return Date.valueOf(date);
    }

    private void assertDeepEquals(Rental expected, Rental actual) {
        assertEquals(expected.getRentedFrom(), actual.getRentedFrom());
        assertEquals(expected.getRentedTo(), actual.getRentedTo());
        assertEquals(expected.getBicycle(), actual.getBicycle());
        assertEquals(expected.getCustomer(), actual.getCustomer());
    }

    private void assertDeepEquals(List<Rental> expectedList, List<Rental> actualList) {
        assertEquals(expectedList.size(),actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            Rental expected = expectedList.get(i);
            Rental actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private static final Comparator<Rental> idComparator = new Comparator<Rental>() {

        @Override
        public int compare(Rental o1, Rental o2) {
            return o1.getRentalID().compareTo(o2.getRentalID());
        }
    };

}