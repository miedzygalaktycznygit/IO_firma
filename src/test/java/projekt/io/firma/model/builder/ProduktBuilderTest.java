package projekt.io.firma.model.builder;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ProduktBuilderTest {

    @Test
    void testBuildSimpleProduktWithDefaults() {
        Produkt produkt = new ProduktBuilder("T-Shirt")
                .setCena(49.99)
                .build();

        assertEquals("T-Shirt", produkt.getNazwa());
        assertEquals("Całoroczna", produkt.getKolekcja());
        assertEquals("M", produkt.getRozmiar());
        assertEquals("Biały", produkt.getKolor());
        assertEquals(49.99, produkt.getCena());
        assertNull(produkt.getProjekt());
        assertEquals("Gładkie", produkt.getTechnikaWykonania());
        assertTrue(produkt.getListaMaterialow().isEmpty());
        assertTrue(produkt.getDodatki().isEmpty());
    }

    @Test
    void testBuildShouldThrowIfNameIsEmpty() {
        ProduktBuilder builder = new ProduktBuilder("");
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Produkt musi mieć nazwę", exception.getMessage());
    }

    @Test
    void testBuildShouldThrowIfNameIsNull() {
        ProduktBuilder builder = new ProduktBuilder(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Produkt musi mieć nazwę", exception.getMessage());
    }

    @Test
    void testBuildShouldThrowIfCenaIsNegative() {
        ProduktBuilder builder = new ProduktBuilder("Czapka").setCena(-5.0);
        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Cena nie może być ujemna", exception.getMessage());
    }

    @Test
    void testBuildShouldThrowIfProjektSetButTechnikaIsGladkie() {
        ProjektGraficzny projekt = new ProjektGraficzny();
        projekt.setNazwa("logo.png");
        projekt.setUrlZdjecia("/images/logo.png");

        ProduktBuilder builder = new ProduktBuilder("Koszulka")
                .setProjekt(projekt)
                .setTechnikaWykonania("Gładkie");

        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Dla produktu z projektem graficznym należy określić technikę wykonania (np. Nadruk/Haft)", exception.getMessage());
    }

    @Test
    void testBuildSuccessWithProjektAndCustomTechnika() {
        ProjektGraficzny projekt = new ProjektGraficzny();
        projekt.setNazwa("logo.png");

        Produkt produkt = new ProduktBuilder("Koszulka")
                .setProjekt(projekt)
                .setTechnikaWykonania("Nadruk sitodrukowy")
                .setCena(60.0)
                .build();

        assertNotNull(produkt.getProjekt());
        assertEquals("Nadruk sitodrukowy", produkt.getTechnikaWykonania());
    }

    @Test
    void testResetBuilder() {
        ProduktBuilder builder = new ProduktBuilder("Sweter");
        builder.setKolekcja("Zima")
               .setCena(120.0)
               .setRozmiar("XL")
               .dodajDodatek("Guziki")
               .reset();

        Produkt produkt = builder.build();
        assertEquals("Sweter", produkt.getNazwa());
        assertEquals("Całoroczna", produkt.getKolekcja());
        assertEquals("M", produkt.getRozmiar());
        assertEquals(0.0, produkt.getCena());
        assertTrue(produkt.getDodatki().isEmpty());
    }

    @Test
    void testKierownikProdukcjiBasicBluza() {
        ProduktBuilder builder = new ProduktBuilder("Bluza");
        KierownikProdukcji kierownik = new KierownikProdukcji(builder);

        Produkt bluza = kierownikprzygotujBluezeBasic(kierownik);

        assertEquals("Bluza", bluza.getNazwa());
    }

    private Produkt kierownikPrzygotujBluezeBasic(KierownikProdukcji kierownik) {
        return kierownik.przygotujBluezeBasic();
    }

    @Test
    void testKierownikProdukcjiBasicBluzaDetails() {
        ProduktBuilder builder = new ProduktBuilder("Bluza testowa");
        KierownikProdukcji kierownik = new KierownikProdukcji(builder);
        Produkt bluza = kierownik.przygotujBluezeBasic();

        assertEquals("Bluza testowa", bluza.getNazwa());
        assertEquals("Basic 2024", bluza.getKolekcja());
        assertEquals("L", bluza.getRozmiar());
        assertEquals("Szary", bluza.getKolor());
        assertEquals(89.00, bluza.getCena());
        assertEquals("Brak (Gładkie)", bluza.getTechnikaWykonania());
        
        Set<Material> materialy = bluza.getListaMaterialow();
        assertEquals(1, materialy.size());
        Material mat = materialy.iterator().next();
        assertEquals("Bawełna czesana", mat.getNazwa());
        assertEquals("mb", mat.getJednostka());

        assertTrue(bluza.getDodatki().contains("Metka z logo"));
    }

    @Test
    void testKierownikProdukcjiKurtkaPremiumDetails() {
        ProduktBuilder builder = new ProduktBuilder("Kurtka testowa");
        KierownikProdukcji kierownik = new KierownikProdukcji(builder);
        
        ProjektGraficzny logo = new ProjektGraficzny();
        logo.setNazwa("herb_firmowy.svg");
        logo.setUrlZdjecia("/sciezka/herb.svg");

        Produkt kurtka = kierownik.przygotujKurtkePremium(logo);

        assertEquals("Kurtka testowa", kurtka.getNazwa());
        assertEquals("Zima 2024/2025", kurtka.getKolekcja());
        assertEquals("XL", kurtka.getRozmiar());
        assertEquals("Czarny Mat", kurtka.getKolor());
        assertEquals(450.00, kurtka.getCena());
        assertEquals(logo, kurtka.getProjekt());
        assertEquals("Haft komputerowy 3D", kurtka.getTechnikaWykonania());
        
        Set<Material> materialy = kurtka.getListaMaterialow();
        assertEquals(2, materialy.size());
        
        assertTrue(kurtka.getDodatki().contains("Zamek YKK"));
        assertTrue(kurtka.getDodatki().contains("Odpinany kaptur"));
    }

}
