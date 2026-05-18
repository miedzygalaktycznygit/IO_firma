package projekt.io.firma.model.builder;

import java.util.ArrayList;
import java.util.List;

public class ProduktBuilder {
    private String nazwa;
    private String kolekcja = "Całoroczna";
    private String rozmiar = "M";
    private String kolor = "Biały";
    private double cena = 0.0;

    private ProjektGraficzny projekt = null;
    private String technikaWykonania = "Gładkie";
    private List<Material> listaMaterialow = new ArrayList<>();
    private List<String> dodatki = new ArrayList<>();

    public ProduktBuilder(String nazwa) {
        this.nazwa = nazwa;
    }

    public ProduktBuilder setKolekcja(String kolekcja) {
        this.kolekcja = kolekcja;
        return this;
    }

    public ProduktBuilder setRozmiar(String rozmiar) {
        this.rozmiar = rozmiar;
        return this;
    }

    public ProduktBuilder setKolor(String kolor) {
        this.kolor = kolor;
        return this;
    }

    public ProduktBuilder setCena(double cena) {
        this.cena = cena;
        return this;
    }

    public ProduktBuilder setProjekt(ProjektGraficzny projekt) {
        this.projekt = projekt;
        return this;
    }

    public ProduktBuilder setTechnikaWykonania(String technika) {
        this.technikaWykonania = technika;
        return this;
    }

    public ProduktBuilder dodajMaterial(Material material) {
        this.listaMaterialow.add(material);
        return this;
    }

    public ProduktBuilder dodajDodatek(String dodatek) {
        this.dodatki.add(dodatek);
        return this;
    }

    public ProduktBuilder reset() {
        this.kolekcja = "Całoroczna";
        this.rozmiar = "M";
        this.kolor = "Biały";
        this.cena = 0.0;
        this.projekt = null;
        this.technikaWykonania = "Gładkie";
        this.listaMaterialow = new ArrayList<>();
        this.dodatki = new ArrayList<>();
        return this;
    }

    public Produkt build() {
        validateBeforeBuild();
        return new Produkt(nazwa, kolekcja, rozmiar, kolor, cena, projekt, technikaWykonania, listaMaterialow, dodatki);
    }

    private void validateBeforeBuild() {
        if (nazwa == null || nazwa.trim().isEmpty()) {
            throw new IllegalStateException("Produkt musi mieć nazwę");
        }
        if (cena < 0) {
            throw new IllegalStateException("Cena nie może być ujemna");
        }
        if (projekt != null && (technikaWykonania == null || technikaWykonania.equals("Gładkie"))) {
            throw new IllegalStateException("Dla produktu z projektem graficznym należy określić technikę wykonania (np. Nadruk/Haft)");
        }
    }
}
