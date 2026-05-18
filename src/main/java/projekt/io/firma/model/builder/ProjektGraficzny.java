package projekt.io.firma.model.builder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProjektGraficzny implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nazwa;
    private String urlZdjecia;

    public ProjektGraficzny() {}

    public ProjektGraficzny(String nazwa, String urlZdjecia) {
        this.nazwa = nazwa;
        this.urlZdjecia = urlZdjecia;
    }

    @Override
    public ProjektGraficzny clone() {
        try {
            ProjektGraficzny clone = (ProjektGraficzny) super.clone();
            clone.setId(null); // Klonujemy jako nowy obiekt bazodanowy
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Projekt: " + nazwa + " (" + urlZdjecia + ")";
    }
}
