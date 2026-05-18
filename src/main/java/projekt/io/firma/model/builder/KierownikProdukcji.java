package projekt.io.firma.model.builder;

public class KierownikProdukcji {
    private ProduktBuilder builder;

    public KierownikProdukcji(ProduktBuilder builder) {
        this.builder = builder;
    }

    public void setBuilder(ProduktBuilder builder) {
        this.builder = builder;
    }

    public Produkt przygotujBluezeBasic() {
        return builder.reset()
                .setKolekcja("Basic 2024")
                .setRozmiar("L")
                .setKolor("Szary")
                .setCena(89.00)
                .setTechnikaWykonania("Brak (Gładkie)")
                .dodajMaterial(new Material("Bawełna czesana", "mb"))
                .dodajDodatek("Metka z logo")
                .build();
    }

    public Produkt przygotujKurtkePremium(ProjektGraficzny logo) {
        return builder.reset()
                .setKolekcja("Zima 2024/2025")
                .setRozmiar("XL")
                .setKolor("Czarny Mat")
                .setCena(450.00)
                .setProjekt(logo)
                .setTechnikaWykonania("Haft komputerowy 3D")
                .dodajMaterial(new Material("Poliester wodoodporny", "m2"))
                .dodajMaterial(new Material("Ocieplina puchowa", "kg"))
                .dodajDodatek("Zamek YKK")
                .dodajDodatek("Metalowe guziki (Złote)")
                .dodajDodatek("Odpinany kaptur")
                .build();
    }
}
