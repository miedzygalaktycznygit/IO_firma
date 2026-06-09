# Dokumentacja systemu IO_firma

## 1. Opis systemu

### 1.1 Podstawowe informacje o systemie
IO_firma to system informatyczny wspierający proces obsługi zadań w firmie produkcyjno-projektowej. System składa się z części serwerowej opartej o Spring Boot oraz części klienckiej w JavaFX.

System umożliwia:
- logowanie użytkowników,
- tworzenie i przeglądanie zadań,
- przypisywanie zadań do krawców,
- akceptację i zakończenie realizacji zadań,
- zakładanie kont pracowników,
- generowanie przykładowych produktów projektowych w oparciu o wzorzec Builder,
- kontrolę dostępu zależną od roli użytkownika.

### 1.2 Opis modelowanej rzeczywistości
System modeluje prosty proces pracy w firmie odzieżowej lub produkcyjno-projektowej, w której występują trzy główne role:
- **Administrator** – zarządza kontami pracowników i porządkuje pracę zespołu,
- **Projektant** – tworzy nowe zadania projektowe,
- **Krawiec** – realizuje przydzielone zadania.

Modelowana rzeczywistość obejmuje przepływ pracy zadania od jego utworzenia, przez przypisanie wykonawcy, aż do zakończenia. Zadanie może zawierać opis, status, dane projektanta, dane krawca oraz powiązany produkt.

### 1.3 Nakreślenie użytkowników systemu
#### Administrator
Odpowiada za zarządzanie użytkownikami oraz przydzielanie zadań. Ma dostęp do wszystkich zadań i listy pracowników.

#### Projektant
Tworzy zadania związane z projektem produktu. Widzi tylko swoje zadania.

#### Krawiec
Przyjmuje zadania do realizacji i oznacza je jako ukończone. Widzi tylko zadania przypisane do siebie.

### 1.4 Opis funkcjonalności systemu
Najważniejsze funkcje systemu:
- logowanie do systemu,
- pobieranie listy zadań zgodnie z uprawnieniami użytkownika,
- tworzenie nowego zadania,
- edycja tytułu i opisu zadania,
- usuwanie zadania,
- akceptacja zadania przez krawca,
- zakończenie zadania przez krawca,
- przypisanie zadania do konkretnego krawca przez administratora,
- tworzenie kont pracowników,
- pobieranie listy pracowników,
- generowanie gotowych produktów przykładowych w panelu projektanta.

### 1.5 Opis założeń niefunkcjonalnych
- **Architektura klient-serwer** – logika biznesowa znajduje się po stronie serwera.
- **Bezpieczeństwo** – dostęp do zasobów chroniony jest przez JWT oraz role użytkowników.
- **Rozszerzalność** – architektura pozwala na dodawanie kolejnych ról, statusów i operacji.
- **Użyteczność** – interfejs JavaFX jest podzielony na osobne panele dla każdej roli.
- **Przenośność** – aplikacja kliencka i serwerowa są oparte na Javie.
- **Spójność danych** – dane przechowywane są w bazie danych poprzez JPA/Hibernate.
- **Niezawodność** – operacje na zadaniach są obsługiwane przez warstwę serwisową i repozytoria.

## 2. Słownik pojęć

| Pojęcie | Opis |
|---|---|
| Zadanie | Jednostka pracy opisująca czynność do wykonania w systemie. |
| Produkt | Obiekt reprezentujący modelowany wyrób odzieżowy lub projekt. |
| Administrator | Użytkownik zarządzający systemem, kontami i przypisywaniem zadań. |
| Projektant | Użytkownik tworzący zadania projektowe. |
| Krawiec | Użytkownik wykonujący i kończący zadania. |
| Status zadania | Stan zadania: NOWE, W_REALIZACJI, ZAKONCZONE. |
| JWT | Token używany do autoryzacji żądań HTTP. |
| REST API | Interfejs komunikacji klienta z serwerem. |
| Builder | Wzorzec projektowy służący do budowania złożonych obiektów krok po kroku. |
| State | Wzorzec projektowy reprezentujący zachowanie zależne od stanu obiektu. |
| Prototype | Wzorzec projektowy służący do klonowania złożonych obiektów. |

## 3. User Stories

### US-01 Logowanie
Jako użytkownik chcę zalogować się do systemu, aby uzyskać dostęp do funkcji przypisanych do mojej roli.

### US-02 Tworzenie zadania
Jako projektant chcę utworzyć nowe zadanie, aby przekazać je do realizacji.

### US-03 Przegląd własnych zadań
Jako projektant chcę widzieć tylko swoje zadania, aby skupić się na swojej pracy.

### US-04 Przypisanie wykonawcy
Jako administrator chcę przypisać zadanie do konkretnego krawca, aby wskazać odpowiedzialną osobę.

### US-05 Akceptacja zadania
Jako krawiec chcę przyjąć zadanie do realizacji, aby rozpocząć jego wykonywanie.

### US-06 Zakończenie zadania
Jako krawiec chcę oznaczyć zadanie jako zakończone, aby poinformować system o wykonaniu pracy.

### US-07 Zarządzanie pracownikami
Jako administrator chcę tworzyć konta pracowników, aby umożliwiać im korzystanie z systemu.

### US-08 Generowanie produktu
Jako projektant chcę generować gotowe warianty produktów, aby szybciej przygotować nowy projekt.

## 4. Diagram Przypadków Użycia

### 4.1 Aktorzy
- Administrator
- Projektant
- Krawiec

### 4.2 Przypadki użycia
- Logowanie
- Przegląd zadań
- Tworzenie zadania
- Edycja zadania
- Usuwanie zadania
- Przypisanie zadania do krawca
- Akceptacja zadania
- Zakończenie zadania
- Tworzenie konta pracownika
- Przegląd pracowników
- Generowanie produktu

### 4.3 Opis tekstowy diagramu
Administrator ma najszersze uprawnienia: może zarządzać pracownikami, przeglądać wszystkie zadania i przypisywać je do krawców.
Projektant tworzy zadania oraz przegląda tylko zadania, które sam utworzył.
Krawiec przegląda zadania przypisane do siebie, może je akceptować i kończyć.

## 5. Scenariusze przypadków użycia

### 5.1 Logowanie
**Aktor:** każdy użytkownik

**Warunki wstępne:** użytkownik posiada konto.

**Scenariusz główny:**
1. Użytkownik podaje login i hasło.
2. System weryfikuje dane.
3. System generuje token JWT.
4. System przekierowuje użytkownika do odpowiedniego panelu.

**Scenariusz alternatywny:**
- Jeśli dane są niepoprawne, system odrzuca logowanie.

### 5.2 Tworzenie zadania
**Aktor:** projektant

**Warunki wstępne:** użytkownik jest zalogowany jako projektant.

**Scenariusz główny:**
1. Projektant wprowadza tytuł i opis zadania.
2. System zapisuje zadanie ze statusem NOWE.
3. Zadanie pojawia się na liście użytkownika.

### 5.3 Przypisanie zadania do krawca
**Aktor:** administrator

**Warunki wstępne:** administrator jest zalogowany.

**Scenariusz główny:**
1. Administrator wybiera zadanie.
2. Administrator wybiera krawca.
3. System zapisuje powiązanie zadania z krawcem.

### 5.4 Akceptacja zadania
**Aktor:** krawiec

**Warunki wstępne:** zadanie jest przypisane do krawca.

**Scenariusz główny:**
1. Krawiec wybiera zadanie.
2. Krawiec uruchamia akcję akceptacji.
3. System zmienia status na W_REALIZACJI.

### 5.5 Zakończenie zadania
**Aktor:** krawiec

**Warunki wstępne:** zadanie jest w realizacji.

**Scenariusz główny:**
1. Krawiec wybiera zadanie.
2. Krawiec oznacza je jako zakończone.
3. System zmienia status na ZAKONCZONE.

## 6. Diagramy aktywności

### 6.1 Aktywność: logowanie i wybór panelu
Opis przebiegu:
- wprowadzenie danych logowania,
- walidacja danych,
- pobranie roli,
- przejście do panelu administratora, projektanta lub krawca.

### 6.2 Aktywność: tworzenie i przypisywanie zadania
Opis przebiegu:
- projektant tworzy zadanie,
- system zapisuje zadanie,
- administrator wybiera zadanie,
- administrator przypisuje krawca,
- system zapisuje zmiany.

### 6.3 Aktywność: realizacja zadania przez krawca
Opis przebiegu:
- krawiec przegląda listę zadań,
- wybiera zadanie,
- akceptuje zadanie,
- wykonuje pracę,
- oznacza zadanie jako zakończone.

## 7. Diagramy klas

### 7.1 Główne klasy domenowe
- **Employee** – reprezentuje pracownika systemu,
- **Task** – reprezentuje zadanie,
- **Role** – określa typ pracownika,
- **TaskState** i klasy implementujące – reprezentują logikę stanów zadania,
- **Produkt** oraz klasy buildera – reprezentują złożony produkt projektowy.

### 7.2 Relacje
- Task ma jednego projektanta i jednego krawca,
- Task zawiera produkt,
- Employee posiada rolę,
- Task wykorzystuje wzorzec State do obsługi statusu,
- Produkt tworzony jest przez wzorzec Builder.

### 7.3 Interpretacja
Diagram klas pokazuje rozdzielenie odpowiedzialności pomiędzy encje, logikę stanu i mechanizm tworzenia produktu. Warstwa serwisowa operuje na repozytoriach, a kontrolery wystawiają API.

## 8. Diagramy sekwencji

### 8.1 Logowanie
1. Użytkownik wysyła dane do AuthController.
2. AuthController wywołuje AuthService.
3. AuthService weryfikuje dane.
4. JwtService generuje token.
5. System zwraca AuthResponse.

### 8.2 Tworzenie zadania
1. Klient wysyła żądanie do AppController.
2. AppController przekazuje dane do TaskManagementService.
3. Serwis przypisuje projektanta, jeśli użytkownik ma rolę PROJEKTANT.
4. Repozytorium zapisuje zadanie.
5. System zwraca utworzone zadanie.

### 8.3 Akceptacja zadania
1. Klient wysyła żądanie PUT /accept.
2. AppController wywołuje TaskManagementService.
3. Task pobiera swój stan i wykonuje accept().
4. Stan przełącza status na W_REALIZACJI.
5. Repozytorium zapisuje zmianę.

### 8.4 Zakończenie zadania
1. Klient wysyła żądanie PUT /complete.
2. AppController wywołuje TaskManagementService.
3. Task wykonuje complete().
4. Stan przełącza status na ZAKONCZONE.
5. Repozytorium zapisuje zmianę.

## 9. Wzorce projektowe

W projekcie zidentyfikowano **trzy wzorce projektowe**: **Builder**, **State** oraz **Prototype**.

### 9.1 Builder

#### 1. Możliwość wykorzystania wzorca w systemie
Wzorzec Builder został wykorzystany do tworzenia obiektu `Produkt`, który ma wiele atrybutów i może występować w kilku wariantach.

#### 2. Problem motywujący wykorzystanie wzorca
Tworzenie produktu bez wzorca Builder powodowałoby konstrukcję rozbudowanego konstruktora lub wielu metod fabrykujących. Przy dużej liczbie pól byłoby to nieczytelne, podatne na błędy i trudne do rozwinięcia w przyszłości.

#### 3. Opis wzorca uwzględniający specyfikację systemu
W systemie rolę dyrektora budowy obiektu pełni `KierownikProdukcji`, a budowniczym jest `ProduktBuilder`. Metody takie jak `przygotujBluezeBasic()` tworzą gotowe konfiguracje produktu krok po kroku, ustawiając wszystkie niezbędne atrybuty (nazwę, rozmiar, kolor, materiały, dodatki). Wzorzec umożliwia generowanie wiele wariantów produktów bez modyfikacji kodu.

### 9.2 State

#### 1. Możliwość wykorzystania wzorca w systemie
Wzorzec State został zastosowany do zarządzania statusem zadania: NOWE, W_REALIZACJI, ZAKONCZONE.

#### 2. Problem motywujący wykorzystanie wzorca
Logika przejść pomiędzy statusami zadania nie powinna być rozproszona po całym kodzie w postaci wielu instrukcji warunkowych. Każdy status ma inne dozwolone operacje, a niektóre akcje są możliwe tylko w określonych stanach (np. zakończenie zadania tylko gdy jest ono w realizacji).

#### 3. Opis wzorca uwzględniający specyfikację systemu
Klasa `Task` przechowuje obiekt `TaskState`, a rzeczywiste zachowanie zależy od aktualnego stanu. Interfejs `TaskState` definiuje operacje `accept()` i `complete()`. Konkretne implementacje stanu (`NewState`, `InProgressState`, `CompletedState`) zawierają logikę zmiany statusu zadania. Każdy stan wiedzieć o dozwolonych przejściach, zapewniając spójność przepływu pracy.

### 9.3 Prototype

#### 1. Możliwość wykorzystania wzorca w systemie
Wzorzec Prototype został wykorzystany do klonowania złożonych obiektów domenowych takich jak `Produkt`, `Task`, `Material` i `ProjektGraficzny`. Pozwala na szybkie tworzenie kopii istniejących obiektów bez konieczności tworzenia ich od zera.

#### 2. Problem motywujący wykorzystanie wzorca
W systemie projekcyjno-produkcyjnym projektant często potrzebuje powielać istniejące zadania lub produkty i wprowadzać w nich niewielkie modyfikacje. Kopiowanie obiektów ręczne — poprzez skopiowanie każdego pola — byłoby podatne na błędy, szczególnie w przypadku złożonych struktur zawierających wiele powiązanych obiektów (materiały, projekty graficzne, dodatki).

#### 3. Opis wzorca uwzględniający specyfikację systemu
Każda z klas domenowych implementuje interfejs `Cloneable` i nadpisuje metodę `clone()`. Klasy proste (`Material`, `ProjektGraficzny`) wykonują płytkie klonowanie z resetowaniem ID. Klasy złożone (`Produkt`, `Task`) realizują **głębokie klonowanie** — oprócz klonowania pól prostych, klonują również wszystkie powiązane obiekty i kolekcje. W przypadku `Task` dodatkowa logika resetuje status na NOWE i dodaje sufiks `(Kopia)` do tytułu, przygotowując zadanie do nowego cyklu pracy. Proces klonowania jest dobrze przetestowany i gwarantuje niezawodne duplikowanie danych.

## 10. Podsumowanie

### Osiągnięcia projektu
System IO_firma to kompletne rozwiązanie wspierające zarządzanie zadaniami w firmie produkcyjno-projektowej. Projekt demonstruje praktyczne zastosowanie zaawansowanych wzorców projektowych i architektur oprogramowania:

#### Architektura i bezpieczeństwo
- **Klient-serwer**: Logika biznesowa centralizowana na serwerze (Spring Boot), komunikacja poprzez REST API oraz JWT.
- **Kontrola dostępu**: Implementacja ról (Administrator, Projektant, Krawiec) zapewnia bezpieczeństwo danych i zgodność z zasadą najmniejszych uprawnień.
- **Autentykacja**: Tokeny JWT gwarantują bezpieczną autoryzację żądań HTTP.

#### Wzorce projektowe
Projekt integruje **trzy kluczowe wzorce**:

1. **Builder** – umożliwia tworzenie złożonych produktów w czytelny i elastyczny sposób, bez rozbudowanych konstruktorów.
2. **State** – enkapsuluje logikę stanów zadania, eliminując warunkową logikę rozsianych po kodzie i zapewniając spójne przejścia między statusami.
3. **Prototype** – ułatwia duplikowanie złożonych obiektów poprzez głębokie klonowanie, wspierając workflow powielania zadań i produktów.

#### Funkcjonalność i użyteczność
- Kompletny cykl życia zadania: tworzenie → przypisanie → akceptacja → realizacja → zakończenie
- Intuicyjny interfejs JavaFX z osobnymi panelami dla każdej roli
- Generowanie gotowych wariantów produktów z wykorzystaniem wzorca Builder
- Możliwość klonowania istniejących zadań i produktów dla przyspieszenia pracy projektanta

#### Spójność i niezawodność
- Dane przechowywane w bazie danych poprzez JPA/Hibernate
- Warstwowa architektura z oddzielnym serwisem, repozytoriami i kontrolerami
- Kompleksowe testy weryfikujące poprawność działania wzorców (szczególnie klonowania)
- Transakcyjność operacji na zadaniach

#### Rozszerzalność
Architektura pozwala na:
- Dodanie nowych ról użytkowników bez modyfikacji istniejącego kodu
- Rozszerzenie statusów zadań poprzez dodanie nowych klas stanu
- Wprowadzenie nowych typów produktów bez zmian w kliencie
- Integrację z dodatkowymi systemami poprzez REST API

### Wnioski
IO_firma reprezentuje solidne podejście do projektowania systemów informatycznych z uwzględnieniem najlepszych praktyk inżynierii oprogramowania. Zastosowanie wzorców projektowych nie tylko poprawia czytelność i utrzymywalność kodu, ale także ułatwia rozbudowę i adaptację systemu do zmieniających się wymagań biznesowych. System jest gotowy do wykorzystania w rzeczywistym środowisku produkcyjnym oraz stanowi doskonały materiał edukacyjny dla nauki zaawansowanych koncepcji programistycznych w Javie.
