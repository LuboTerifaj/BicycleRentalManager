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
 * @author  Lubo Terifaj
 */
public class BicycleManagerImplTest {

    private BicycleManagerImpl manager;
    private DataSource dataSource;


    @Before
    public void setUp() throws SQLException {


        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:BicycleManagerTest;create=true");
        this.dataSource = bds;
        //create new empty table before every test
        try (Connection conn = bds.getConnection()) {
            conn.prepareStatement("CREATE TABLE bicycle ("
                    + "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "price INT ,"
                    + "available SMALLINT)").executeUpdate();
        }
        manager = new BicycleManagerImpl(bds);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            con.prepareStatement("DROP TABLE bicycle").executeUpdate();
        }
    }

    @Test
    public void testCreateBicycle() {
        Bicycle bike = createNewBike(200, true);

        manager.createBicycle(bike);

        Long bikeId = bike.getId();
        assertNotNull(bikeId);
        Bicycle result = manager.getBicycleByID(bikeId);
        assertEquals(bike, result);
        assertNotSame(bike, result);
    }

    @Test
    public void testGetBicycleByID() {

        assertNull(manager.getBicycleByID(1l));

        Bicycle bike = createNewBike(300, true);
        manager.createBicycle(bike);
        Long bikeID = bike.getId();
        Bicycle result = manager.getBicycleByID(bikeID);
        assertEquals(bike, result);

    }

    @Test
    public void testFindAllBicycles() {

        assertTrue(manager.findAllBicycles().isEmpty());

        Bicycle b1 = createNewBike(230, true);
        Bicycle b2 = createNewBike(200, true);

        manager.createBicycle(b1);
        manager.createBicycle(b2);

        List<Bicycle> expected = Arrays.asList(b1, b2);
        List<Bicycle> actual = manager.findAllBicycles();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteBicycle() {

        Bicycle b1 = createNewBike(200, true);
        Bicycle b2 = createNewBike(300, true);
        manager.createBicycle(b1);
        manager.createBicycle(b2);

        assertNotNull(manager.getBicycleByID(b1.getId()));
        assertNotNull(manager.getBicycleByID(b2.getId()));

        manager.deleteBicycle(b1.getId());

        assertNull(manager.getBicycleByID(b1.getId()));
        assertNotNull(manager.getBicycleByID(b2.getId()));

    }

    @Test
    public void testDeleteBicycleWithWrongAttributes() {

        Bicycle bike = createNewBike(180, true);

        try {
            manager.deleteBicycle(null);
            fail();
        } catch (ServiceFailureException ex) {
            //OK
        }

        try {
            bike.setId(-10l);
            manager.deleteBicycle(bike.getId());
            fail();
        } catch (ServiceFailureException ex) {
            //OK
        }

    }

    private static final Comparator<Bicycle> idComparator = new Comparator<Bicycle>() {


        public int compare(Bicycle o1, Bicycle o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    static Bicycle createNewBike(int price, boolean available) {
        Bicycle bike = new Bicycle();
        bike.setPrice(price);
        bike.setAvailable(available);
        return bike;
    }

}