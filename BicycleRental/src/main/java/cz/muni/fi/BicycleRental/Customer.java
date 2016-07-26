package cz.muni.fi.BicycleRental;

/**
 * @author  Lubo Terifaj
 */
public class Customer {
    private Long id;
    private String fullName;
    private String adress;
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (!getId().equals(customer.getId())) return false;
        if (getFullName() != null ? !getFullName().equals(customer.getFullName()) : customer.getFullName() != null)
            return false;
        if (getAdress() != null ? !getAdress().equals(customer.getAdress()) : customer.getAdress() != null)
            return false;
        return !(getPhone() != null ? !getPhone().equals(customer.getPhone()) : customer.getPhone() != null);

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getFullName() != null ? getFullName().hashCode() : 0);
        result = 31 * result + (getAdress() != null ? getAdress().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString(){
        String rv = this.getFullName();
        rv += "; ";
        rv += this.getAdress();
        rv += "; ";
        rv += this.getPhone();
        return rv;
    }
}
