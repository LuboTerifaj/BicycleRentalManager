package cz.muni.fi.BicycleRental;

import java.util.Date;

/**
 * @author  Lubo Terifaj
 */
public class Rental {
    private Long rentalID;
    private Date rentedFrom;
    private Date rentedTo;
    private Customer customer;
    private Bicycle bicycle;



    public Long getRentalID() {
        return rentalID;
    }

    public void setRentalID(Long rentalID) {
        this.rentalID = rentalID;
    }

    public Date getRentedFrom() {
        return rentedFrom;
    }

    public void setRentedFrom(Date rentedFrom) {
        this.rentedFrom = rentedFrom;
    }

    public Date getRentedTo() {
        return rentedTo;
    }

    public void setRentedTo(Date rentedTo) {
        this.rentedTo = rentedTo;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Bicycle getBicycle() {
        return bicycle;
    }

    public void setBicycle(Bicycle bicycle) {
        this.bicycle = bicycle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rental rental = (Rental) o;

        if (!getRentalID().equals(rental.getRentalID())) return false;
        if (!getRentedFrom().equals(rental.getRentedFrom())) return false;
        if (getRentedTo() != null ? !getRentedTo().equals(rental.getRentedTo()) : rental.getRentedTo() != null)
            return false;
        if (!getCustomer().equals(rental.getCustomer())) return false;
        return getBicycle().equals(rental.getBicycle());

    }

    @Override
    public int hashCode() {
        int result = getRentalID().hashCode();
        result = 31 * result + getRentedFrom().hashCode();
        result = 31 * result + (getRentedTo() != null ? getRentedTo().hashCode() : 0);
        result = 31 * result + getCustomer().hashCode();
        result = 31 * result + getBicycle().hashCode();
        return result;
    }
}
