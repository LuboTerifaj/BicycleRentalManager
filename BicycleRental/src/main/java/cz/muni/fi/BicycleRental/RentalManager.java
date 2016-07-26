package cz.muni.fi.BicycleRental;

import java.util.List;

/**
 * @author  Lubo Terifaj
 */
public interface RentalManager {

    void createRental(Rental rental) throws ServiceFailureException;

    Rental getRentalByID(Long rentalID) throws ServiceFailureException;

    List<Rental> findAllRentals() throws ServiceFailureException;

    List<Rental> findRentalsByCustomer(Customer customer) throws ServiceFailureException;

    List<Rental> findRentalsByBicycle(Bicycle bicycle) throws ServiceFailureException;

    void updateRental(Rental rental) throws ServiceFailureException;

    void deleteRental(Rental rental) throws ServiceFailureException;

}
