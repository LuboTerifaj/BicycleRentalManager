package cz.muni.fi.BicycleRental;

import java.util.List;

/**
 * @author  Lubo Terifaj
 */
public interface CustomerManager {

    void createCustomer(Customer customer) throws ServiceFailureException;

    Customer getCustomerByID(Long id) throws ServiceFailureException;

    List<Customer> getCustomerByName(String name) throws ServiceFailureException;

    List<Customer> findAllCustomers() throws ServiceFailureException;

    void updateCustomer(Customer customer) throws ServiceFailureException;

    void deleteCustomer(Long id) throws ServiceFailureException;

}
