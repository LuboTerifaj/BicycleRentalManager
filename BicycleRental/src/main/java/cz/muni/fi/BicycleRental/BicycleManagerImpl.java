package cz.muni.fi.BicycleRental;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
/**
 * @author  Lubo Terifaj, Matej Harcar
 */
public class BicycleManagerImpl implements BicycleManager {

    final static org.slf4j.Logger log = LoggerFactory.getLogger(BicycleManagerImpl.class);

    private final DataSource dataSource;

    public BicycleManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createBicycle(Bicycle bicycle) throws ServiceFailureException {
        log.debug("BicycleManagerImpl.createBicycle"+bicycle.toString());
        if (bicycle == null) {
            throw new IllegalArgumentException("bicycle is null");
        }

        if (bicycle.getPrice() < 0) {
            throw new IllegalArgumentException("bicycle price is negative number");
        }

        if (!bicycle.isAvailable()) {
            throw new IllegalArgumentException("bicycle is not available");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO bicycle (price,available) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setInt(1, bicycle.getPrice());
                st.setBoolean(2, bicycle.isAvailable());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert bicycle " + bicycle);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                bicycle.setId(getKey(keyRS, bicycle));
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all bicycles", ex);
        }
    }

    private Long getKey(ResultSet keyRS, Bicycle bicycle) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert bicycle " + bicycle
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert bicycle " + bicycle
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert bicycle " + bicycle
                    + " - no key found");
        }
    }

    private Bicycle resultSetToBicycle(ResultSet rs) throws SQLException {
        Bicycle bicycle = new Bicycle();
        bicycle.setId(rs.getLong("ID"));
        bicycle.setPrice(rs.getInt("price"));
        bicycle.setAvailable(rs.getBoolean("available"));
        return bicycle;
    }

    @Override
    public List<Bicycle> findAllBicycles() throws ServiceFailureException {
        log.debug("finding all bicycles");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id,price,available FROM bicycle")) {
                ResultSet rs = st.executeQuery();
                List<Bicycle> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToBicycle(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all bicycles", ex);
        }
    }

    @Override
    public void updateBicycle(Bicycle bicycle) throws ServiceFailureException {
        log.debug("updating bicycle");
        if (bicycle == null) throw new IllegalArgumentException("bicycle pointer is null");
        if (bicycle.getId() == null) throw new IllegalArgumentException("bicycle with null id cannot be updated");

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE bicycle SET price=?,available=? WHERE id=?")) {
                st.setInt(1, bicycle.getPrice());
                st.setBoolean(2, bicycle.isAvailable());
                st.setLong(3, bicycle.getId());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("cannot update bicycle " + bicycle);
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when updating bicycle", ex);
        }
    }

    @Override
    public Bicycle getBicycleByID(Long id) throws ServiceFailureException {
        log.debug("Getting bicycle by ID");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, price,available FROM bicycle WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Bicycle bicycle = resultSetToBicycle(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: Multiple entities with the same id found "
                                        + "(source id: " + id + ", found " + bicycle + " and " + resultSetToBicycle(rs));
                    }
                    return bicycle;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving bicycle by ID", ex);
        }
    }

    @Override
    public void deleteBicycle(Long id) throws ServiceFailureException {
        log.debug("Deleting bicycle");
        if(id == null) {
            throw new  ServiceFailureException("bicycleID is null");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM bicycle WHERE id=?")) {
                st.setLong(1, id);
                if (st.executeUpdate() != 1) {
                    throw new ServiceFailureException("did not delete bicycle with id =" + id);
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when deleting bicycle with ID " + id, ex);
        }
    }


}
