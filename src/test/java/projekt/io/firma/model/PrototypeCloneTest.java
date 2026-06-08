package projekt.io.firma.model;

import org.junit.jupiter.api.Test;
import projekt.io.firma.model.builder.Material;
import projekt.io.firma.model.builder.Produkt;
import projekt.io.firma.model.builder.ProjektGraficzny;
import projekt.io.firma.model.state.NewState;

import static org.junit.jupiter.api.Assertions.*;

class PrototypeCloneTest {

    @Test
    void testProduktCloneDeepCopy() {
        // Arrange
        ProjektGraficzny projekt = new ProjektGraficzny();
        projekt.setId(10L);
        projekt.setNazwa("Wzorzec");
        projekt.setUrlZdjecia("http://wzorzec.pl/img");

        Material material1 = new Material("Jedwab", "mb");
        material1.setId(20L);
        
        Material material2 = new Material("Guziki perłowe", "szt");
        material2.setId(21L);

        Produkt oryginal = new Produkt();
        oryginal.setId(1L);
        oryginal.setNazwa("Suknia wieczorowa");
        oryginal.setKolekcja("Lato 2025");
        oryginal.setRozmiar("S");
        oryginal.setKolor("Czerwień");
        oryginal.setCena(1500.0);
        oryginal.setProjekt(projekt);
        oryginal.setTechnikaWykonania("Szycie ręczne");
        
        oryginal.getListaMaterialow().add(material1);
        oryginal.getListaMaterialow().add(material2);
        oryginal.getDodatki().add("Metka jedwabna");
        oryginal.getDodatki().add("Wstążka ozdobna");

        // Act
        Produkt klon = oryginal.clone();

        // Assert basic fields
        assertNotNull(klon);
        assertNotSame(oryginal, klon);
        assertNull(klon.getId(), "Klon powinien mieć zresetowane ID (null)");
        assertEquals(oryginal.getNazwa(), klon.getNazwa());
        assertEquals(oryginal.getKolekcja(), klon.getKolekcja());
        assertEquals(oryginal.getRozmiar(), klon.getRozmiar());
        assertEquals(oryginal.getKolor(), klon.getKolor());
        assertEquals(oryginal.getCena(), klon.getCena());
        assertEquals(oryginal.getTechnikaWykonania(), klon.getTechnikaWykonania());

        // Assert Projekt (Deep Copy)
        assertNotNull(klon.getProjekt());
        assertNotSame(oryginal.getProjekt(), klon.getProjekt(), "Projekt graficzny powinien być głęboką kopią");
        assertNull(klon.getProjekt().getId(), "ID sklonowanego projektu powinno być null");
        assertEquals(oryginal.getProjekt().getNazwa(), klon.getProjekt().getNazwa());
        assertEquals(oryginal.getProjekt().getUrlZdjecia(), klon.getProjekt().getUrlZdjecia());

        // Assert ListaMaterialow (Deep Copy)
        assertNotSame(oryginal.getListaMaterialow(), klon.getListaMaterialow(), "Kolekcja materiałów powinna być nową instancją");
        assertEquals(oryginal.getListaMaterialow().size(), klon.getListaMaterialow().size());
        
        for (Material klonMat : klon.getListaMaterialow()) {
            assertNull(klonMat.getId(), "ID sklonowanego materiału powinno być null");
            // Powinien istnieć pasujący materiał w oryginale o tej samej nazwie
            boolean found = oryginal.getListaMaterialow().stream()
                    .anyMatch(origMat -> origMat.getNazwa().equals(klonMat.getNazwa()) 
                            && origMat.getJednostka().equals(klonMat.getJednostka())
                            && origMat != klonMat);
            assertTrue(found, "Każdy sklonowany materiał musi odpowiadać oryginalnemu pod kątem danych, ale mieć inną referencję");
        }

        // Assert Dodatki (Deep Copy)
        assertNotSame(oryginal.getDodatki(), klon.getDodatki(), "Dodatki powinny być nową instancją kolekcji");
        assertEquals(oryginal.getDodatki().size(), klon.getDodatki().size());
        assertTrue(klon.getDodatki().containsAll(oryginal.getDodatki()));
    }

    @Test
    void testTaskCloneDeepCopy() {
        // Arrange
        Produkt produkt = new Produkt();
        produkt.setId(5L);
        produkt.setNazwa("Marynarka");
        produkt.setCena(350.0);

        Task oryginal = new Task();
        oryginal.setId(100L);
        oryginal.setTitle("Uszycie marynarki");
        oryginal.setDescription("Zadanie produkcyjne");
        oryginal.setStatus("W_REALIZACJI");
        oryginal.setProdukt(produkt);
        oryginal.init(); // Inicjalizacja stanu na InProgressState

        // Act
        Task klon = oryginal.clone();

        // Assert
        assertNotNull(klon);
        assertNotSame(oryginal, klon);
        assertNull(klon.getId(), "Klon zadania powinien mieć ID = null");
        assertEquals("Uszycie marynarki (Kopia)", klon.getTitle());
        assertEquals("Zadanie produkcyjne", klon.getDescription());
        assertEquals("NOWE", klon.getStatus(), "Status sklonowanego zadania powinien być NOWE");
        assertTrue(klon.getState() instanceof NewState, "Stan sklonowanego zadania powinien być NewState");

        // Assert deep copy of associated Produkt
        assertNotNull(klon.getProdukt());
        assertNotSame(oryginal.getProdukt(), klon.getProdukt(), "Powiązany produkt powinien być głęboką kopią");
        assertNull(klon.getProdukt().getId(), "ID sklonowanego produktu powinno być null");
        assertEquals(oryginal.getProdukt().getNazwa(), klon.getProdukt().getNazwa());
        assertEquals(oryginal.getProdukt().getCena(), klon.getProdukt().getCena());
    }

    @Test
    void testTaskCloneWithNullProdukt() {
        // Arrange
        Task oryginal = new Task();
        oryginal.setId(100L);
        oryginal.setTitle("Zadanie bez produktu");
        oryginal.setStatus("NOWE");
        oryginal.init();

        // Act
        Task klon = oryginal.clone();

        // Assert
        assertNotNull(klon);
        assertNull(klon.getProdukt(), "Produkt klonu powinien pozostać null");
    }
}
