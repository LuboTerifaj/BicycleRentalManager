package cz.muni.fi.BicycleRental;

/**
 * @author  Lubo Terifaj, Matej Harcar
 */
public class Bicycle {

    private Long id;
    private int price;
    private boolean available;


    public Bicycle(){
        this.available=true;
    }
    
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bicycle bicycle = (Bicycle) o;

        if (getPrice() != bicycle.getPrice()) return false;
        if (isAvailable() != bicycle.isAvailable()) return false;
        return !(getId() != null ? !getId().equals(bicycle.getId()) : bicycle.getId() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getPrice();
        result = 31 * result + (isAvailable() ? 1 : 0);
        return result;
    }
    
    @Override
    public String toString(){
        String rv ="Bicycle #";
        rv += this.id;
        rv+=", price ";
        rv+= this.price;
        return rv;
    }
}
