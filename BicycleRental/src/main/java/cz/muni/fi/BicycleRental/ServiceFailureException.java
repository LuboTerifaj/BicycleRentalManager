package cz.muni.fi.BicycleRental;

/**
 * @author  Lubo Terifaj
 */
public class ServiceFailureException extends RuntimeException{

    public ServiceFailureException(String message) {
        super(message);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
