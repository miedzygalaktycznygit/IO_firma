package projekt.io.firma.model.builder;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Produkt implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nazwa;
    private String kolekcja;
    private String rozmiar;
    private String kolor;
    private double cena;

    @ManyToOne(cascade = CascadeType.ALL)
    private ProjektGraficzny projekt;

    private String technikaWykonania;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private java.util.Set<Material> listaMaterialow = new java.util.HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private java.util.Set<String> dodatki = new java.util.HashSet<>();

    public Produkt() {
    }

    Produkt(String nazwa, String kolekcja, String rozmiar, String kolor, double cena,
            ProjektGraficzny projekt, String technikaWykonania,
            List<Material> listaMaterialow, List<String> dodatki) {
        this.nazwa = nazwa;
        this.kolekcja = kolekcja;
        this.rozmiar = rozmiar;
        this.kolor = kolor;
        this.cena = cena;
        this.projekt = projekt;
        this.technikaWykonania = technikaWykonania;
        if (listaMaterialow != null) {
            this.listaMaterialow.addAll(listaMaterialow);
        }
        if (dodatki != null) {
            this.dodatki.addAll(dodatki);
        }
    }

    @Override
    public Produkt clone() {
        try {
            Produkt clone = (Produkt) super.clone();
            clone.setId(null);
            
            if (this.projekt != null) {
                clone.setProjekt(this.projekt.clone());
            }
            
            // Głębokie kopiowanie kolekcji
            java.util.Set<Material> klonMaterialow = new java.util.HashSet<>();
            for (Material m : this.listaMaterialow) {
                klonMaterialow.add(m.clone());
            }
            clone.setListaMaterialow(klonMaterialow);
            
            clone.setDodatki(new java.util.HashSet<>(this.dodatki));
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Produkt {" +
                "\n  Nazwa='" + nazwa + '\'' +
                "\n  Kolekcja='" + kolekcja + '\'' +
                "\n  Cena=" + cena + " PLN" +
                "\n  Wariant='" + kolor + " " + rozmiar + '\'' +
                "\n  Projekt=" + (projekt != null ? projekt : "Brak") +
                "\n  Technika='" + technikaWykonania + '\'' +
                "\n  Materiały=" + listaMaterialow +
                "\n  Dodatki=" + dodatki +
                "\n}";
    }
}
