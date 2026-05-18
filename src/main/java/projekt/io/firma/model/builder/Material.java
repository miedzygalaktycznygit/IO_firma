package projekt.io.firma.model.builder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Material implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nazwa;
    private String jednostka;

    public Material() {}

    public Material(String nazwa, String jednostka) {
        this.nazwa = nazwa;
        this.jednostka = jednostka;
    }

    @Override
    public Material clone() {
        try {
            Material clone = (Material) super.clone();
            clone.setId(null); // Nowy ID do bazy
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return nazwa + " (" + jednostka + ")";
    }
}
