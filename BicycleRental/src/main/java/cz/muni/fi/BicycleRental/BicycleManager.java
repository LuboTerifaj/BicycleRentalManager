package cz.muni.fi.BicycleRental;

import java.util.*;

/**
 * @author  Lubo Terifaj, Matej Harcar
 */
public interface BicycleManager {

    void createBicycle(Bicycle bicycle) throws ServiceFailureException;

    Bicycle getBicycleByID(Long id) throws ServiceFailureException;

    List<Bicycle> findAllBicycles() throws ServiceFailureException;

    void updateBicycle(Bicycle bicycle) throws ServiceFailureException;

    void deleteBicycle(Long id) throws ServiceFailureException;

}
