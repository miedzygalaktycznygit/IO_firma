package projekt.io.firma;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import projekt.io.firma.client.ApiClient;
import projekt.io.firma.dto.CreateEmployeeRequest;
import projekt.io.firma.dto.EmployeeDto;
import projekt.io.firma.model.Role;
import projekt.io.firma.model.Task;
import projekt.io.firma.model.builder.KierownikProdukcji;
import projekt.io.firma.model.builder.Produkt;
import projekt.io.firma.model.builder.ProduktBuilder;
import projekt.io.firma.model.builder.ProjektGraficzny;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class JavaFxApp extends Application {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final ApiClient apiClient = new ApiClient(DEFAULT_BASE_URL);

    private Stage primaryStage;
    private EmployeeDto currentUser;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Firma Krawiecka IO - Panel Główny");
        stage.setScene(createLoginScene());
        stage.show();
    }

    private Scene createLoginScene() {
        Label titleLabel = new Label("Logowanie");
        titleLabel.setFont(new Font("Arial", 20));

        TextField loginField = new TextField();
        loginField.setPromptText("Login...");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło...");

        Label statusLabel = new Label();

        Button btnLogin = new Button("Zaloguj");
        btnLogin.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();
            if (login.isBlank() || password.isBlank()) {
                statusLabel.setText("Podaj login i hasło.");
                return;
            }

            try {
                EmployeeDto employee = apiClient.login(login, password);
                if (employee == null) {
                    statusLabel.setText("Niepoprawne dane logowania.");
                    return;
                }
                currentUser = employee;
                switch (employee.role()) {
                    case ADMINISTRATOR -> primaryStage.setScene(createAdminScene());
                    case PROJEKTANT -> primaryStage.setScene(createDesignerScene());
                    case KRAWIEC -> primaryStage.setScene(createTailorScene());
                }
            } catch (Exception ex) {
                statusLabel.setText("Błąd połączenia z serwerem.");
            }
        });

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, loginField, passwordField, btnLogin, statusLabel);

        return new Scene(layout, 400, 300);
    }

    private Scene createAdminScene() {
        Label titleLabel = new Label("Panel Administratora");
        titleLabel.setFont(new Font("Arial", 18));

        TextField txtLogin = new TextField();
        txtLogin.setPromptText("Login...");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Hasło...");

        TextField txtImie = new TextField();
        txtImie.setPromptText("Imię...");

        TextField txtNazwisko = new TextField();
        txtNazwisko.setPromptText("Nazwisko...");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.values());
        roleBox.setPromptText("Wybierz rolę...");

        Button btnCreateAccount = new Button("Utwórz konto");

        HBox dodawanieBox = new HBox(10);
        dodawanieBox.setAlignment(Pos.CENTER);
        dodawanieBox.getChildren().addAll(txtLogin, txtPassword, txtImie, txtNazwisko, roleBox, btnCreateAccount);

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(140);
        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    String tailorName = (task.getTailor() != null) ? task.getTailor().getFirstName() : "BRAK";
                    setText("Zadanie #" + task.getId() + " | Krawiec: " + tailorName);
                }
            }
        });

        ListView<EmployeeDto> empListView = new ListView<>();
        empListView.setPrefHeight(140);
        empListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EmployeeDto emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText("Pracownik #" + emp.id() + " | " + emp.firstName() + " (" + emp.role() + ")");
                }
            }
        });

        Runnable odswiezDane = () -> {
            try {
                List<Task> tasks = apiClient.getTasks();
                taskListView.getItems().setAll(tasks);

                List<EmployeeDto> employees = apiClient.getEmployees();
                empListView.getItems().setAll(employees);
            } catch (Exception ex) {
                taskListView.getItems().clear();
                empListView.getItems().clear();
            }
        };
        odswiezDane.run();

        btnCreateAccount.setOnAction(e -> {
            if (txtLogin.getText().isBlank() || txtPassword.getText().isBlank()
                    || txtImie.getText().isBlank() || roleBox.getValue() == null) {
                return;
            }

            try {
                CreateEmployeeRequest request = new CreateEmployeeRequest(
                        txtLogin.getText(),
                        txtPassword.getText(),
                        txtImie.getText(),
                        txtNazwisko.getText(),
                        roleBox.getValue()
                );
                apiClient.createEmployee(request);

                txtLogin.clear();
                txtPassword.clear();
                txtImie.clear();
                txtNazwisko.clear();
                roleBox.setValue(null);
                odswiezDane.run();
            } catch (Exception ex) {
                // ignore for now
            }
        });

        Button btnPrzypisz = new Button("Przypisz wybrane zadanie do wybranego krawca");
        btnPrzypisz.setOnAction(e -> {
            Task task = taskListView.getSelectionModel().getSelectedItem();
            EmployeeDto emp = empListView.getSelectionModel().getSelectedItem();

            if (task != null && emp != null) {
                try {
                    apiClient.assignTaskToTailor(task.getId(), emp.id());
                    odswiezDane.run();
                } catch (Exception ex) {
                    // ignore for now
                }
            }
        });

        Button btnBack = new Button("Wyloguj");
        btnBack.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(createLoginScene());
        });

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 0 20 0 20;");
        layout.getChildren().addAll(
                titleLabel,
                new Label("--- 1. NOWE KONTO PRACOWNIKA ---"), dodawanieBox,
                new Label("--- 2. PRZYPISYWANIE ZADAŃ (Zaznacz obiekt w 1 i w 2 liście) ---"),
                taskListView, empListView, btnPrzypisz, btnBack
        );

        return new Scene(layout, 700, 650);
    }

    private Scene createDesignerScene() {
        Label titleLabel = new Label("Panel Projektanta - Zarządzanie Zadaniami");
        titleLabel.setFont(new Font("Arial", 18));

        Label statusLabel = new Label();

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(200);
        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    String prodInfo = (task.getProdukt() != null)
                            ? " | Szczegóły: " + task.getProdukt().toString().replace("\n", " | ")
                            : "";
                    setText("Zadanie #" + task.getId() + " | " + task.getTitle()
                            + " | Status: " + task.getStatus() + prodInfo);
                }
            }
        });

        Runnable odswiezListe = () -> {
            try {
                taskListView.getItems().setAll(apiClient.getTasks());
                statusLabel.setText("");
            } catch (Exception ex) {
                taskListView.getItems().clear();
                statusLabel.setText("Blad pobierania listy: " + ex.getMessage());
            }
        };
        odswiezListe.run();

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Wpisz tytuł zadania...");

        TextField txtDescription = new TextField();
        txtDescription.setPromptText("Wpisz opis zadania...");

        AtomicReference<Produkt> currentProdukt = new AtomicReference<>();
        KierownikProdukcji kierownik = new KierownikProdukcji(new ProduktBuilder("Z szablonu"));

        Button btnBasic = new Button("Generuj: Bluza Basic");
        btnBasic.setOnAction(e -> {
            Produkt p = kierownik.przygotujBluezeBasic();
            currentProdukt.set(p);
            txtTitle.setText("Bluza Basic");
            txtDescription.setText(p.toString().replace("\n", " | "));
        });

        Button btnPremium = new Button("Generuj: Kurtka Premium");
        btnPremium.setOnAction(e -> {
            ProjektGraficzny logo = new ProjektGraficzny();
            logo.setNazwa("Logo Premium");
            logo.setUrlZdjecia("/img/logo.png");
            Produkt p = kierownik.przygotujKurtkePremium(logo);
            currentProdukt.set(p);
            txtTitle.setText("Kurtka Premium");
            txtDescription.setText(p.toString().replace("\n", " | "));
        });

        HBox builderLayout = new HBox(15);
        builderLayout.setAlignment(Pos.CENTER);
        builderLayout.getChildren().addAll(btnBasic, btnPremium);

        Button btnAdd = new Button("Dodaj nowe zadanie");
        btnAdd.setOnAction(e -> {
            if (!txtTitle.getText().isEmpty() && !txtDescription.getText().isEmpty()) {
                Task newTask = new Task();
                newTask.setTitle(txtTitle.getText());
                newTask.setDescription(txtDescription.getText());
                if (currentProdukt.get() != null) {
                    newTask.setProdukt(currentProdukt.get());
                }

                try {
                    apiClient.createTask(newTask);
                    txtTitle.clear();
                    txtDescription.clear();
                    currentProdukt.set(null);
                    odswiezListe.run();
                } catch (Exception ex) {
                    statusLabel.setText("Blad dodawania: " + ex.getMessage());
                }
            }
        });

        Button btnDelete = new Button("Usuń zaznaczone");
        btnDelete.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                try {
                    apiClient.deleteTask(selectedTask.getId());
                    odswiezListe.run();
                } catch (Exception ex) {
                    // ignore for now
                }
            }
        });

        Button btnDuplicate = new Button("Duplikuj wybrane zadanie");
        btnDuplicate.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                Task clonedTask = selectedTask.clone();
                try {
                    apiClient.createTask(clonedTask);
                    odswiezListe.run();
                } catch (Exception ex) {
                    // ignore for now
                }
            }
        });

        Button btnBack = new Button("Wyloguj");
        btnBack.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(createLoginScene());
        });

        HBox buttonsLayout = new HBox(15);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(btnAdd, btnDelete, btnDuplicate, btnBack);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 0 20 0 20;");

        layout.getChildren().addAll(titleLabel, statusLabel, taskListView, builderLayout, txtTitle, txtDescription, buttonsLayout);

        return new Scene(layout, 600, 450);
    }

    private Scene createTailorScene() {
        Label titleLabel = new Label("Panel Krawca - Lista Zadań");
        titleLabel.setFont(new Font("Arial", 18));

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(250);

        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    String prodInfo = (task.getProdukt() != null)
                            ? " | Szczegóły: " + task.getProdukt().toString().replace("\n", " | ")
                            : "";
                    setText("Zadanie #" + task.getId() + " | " + task.getTitle()
                            + " | Status: " + task.getStatus() + prodInfo);
                }
            }
        });

        Runnable odswiezListe = () -> {
            try {
                taskListView.getItems().setAll(apiClient.getTasks());
            } catch (Exception ex) {
                taskListView.getItems().clear();
            }
        };

        odswiezListe.run();

        Button btnAccept = new Button("Przyjmij do realizacji");
        btnAccept.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                try {
                    apiClient.acceptTask(selectedTask.getId());
                    odswiezListe.run();
                } catch (Exception ex) {
                    // ignore for now
                }
            }
        });

        Button btnComplete = new Button("Zakończ zadanie");
        btnComplete.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                try {
                    apiClient.completeTask(selectedTask.getId());
                    odswiezListe.run();
                } catch (Exception ex) {
                    // ignore for now
                }
            }
        });

        Button btnBack = new Button("Wyloguj");
        btnBack.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(createLoginScene());
        });

        HBox buttonsLayout = new HBox(15);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(btnAccept, btnComplete, btnBack);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, taskListView, buttonsLayout);

        return new Scene(layout, 500, 400);
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
