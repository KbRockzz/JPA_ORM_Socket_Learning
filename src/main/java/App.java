import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        Persistence.createEntityManagerFactory("mariadb-pu");
        
    }
}
