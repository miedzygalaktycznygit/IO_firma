package projekt.io.firma;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    private Scene createStyledScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private Scene createLoginScene() {
        Label titleLabel = new Label("Logowanie");
        titleLabel.getStyleClass().add("title");

        TextField loginField = new TextField();
        loginField.setPromptText("Login...");
        loginField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło...");
        passwordField.getStyleClass().add("input");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status");

        Button btnLogin = new Button("Zaloguj");
        btnLogin.getStyleClass().add("primary-button");
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

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(320);
        card.getStyleClass().add("card");
        card.getChildren().addAll(titleLabel, loginField, passwordField, btnLogin, statusLabel);

        StackPane layout = new StackPane(card);
        layout.getStyleClass().add("screen");

        return createStyledScene(layout, 520, 360);
    }

    private Scene createAdminScene() {
        Label titleLabel = new Label("Panel Administratora");
        titleLabel.getStyleClass().add("title");

        TextField txtLogin = new TextField();
        txtLogin.setPromptText("Login...");
        txtLogin.getStyleClass().add("input");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Hasło...");
        txtPassword.getStyleClass().add("input");

        TextField txtImie = new TextField();
        txtImie.setPromptText("Imię...");
        txtImie.getStyleClass().add("input");

        TextField txtNazwisko = new TextField();
        txtNazwisko.setPromptText("Nazwisko...");
        txtNazwisko.getStyleClass().add("input");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.values());
        roleBox.setPromptText("Wybierz rolę...");
        roleBox.getStyleClass().add("input");

        Button btnCreateAccount = new Button("Utwórz konto");
        btnCreateAccount.getStyleClass().add("primary-button");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(10);
        formGrid.add(new Label("Login"), 0, 0);
        formGrid.add(txtLogin, 1, 0);
        formGrid.add(new Label("Hasło"), 0, 1);
        formGrid.add(txtPassword, 1, 1);
        formGrid.add(new Label("Imię"), 0, 2);
        formGrid.add(txtImie, 1, 2);
        formGrid.add(new Label("Nazwisko"), 0, 3);
        formGrid.add(txtNazwisko, 1, 3);
        formGrid.add(new Label("Rola"), 0, 4);
        formGrid.add(roleBox, 1, 4);
        formGrid.add(btnCreateAccount, 1, 5);

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(140);
        taskListView.getStyleClass().add("list-view");
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
        empListView.getStyleClass().add("list-view");
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
                EmployeeDto created = apiClient.createEmployee(request);

                txtLogin.clear();
                txtPassword.clear();
                txtImie.clear();
                txtNazwisko.clear();
                roleBox.setValue(null);
                odswiezDane.run();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sukces");
                alert.setHeaderText("Utworzono pracownika");
                alert.setContentText("Pracownik #" + created.id() + " | " + created.firstName() + " " + created.lastName() + " (" + created.role() + ")");
                alert.showAndWait();
            } catch (Exception ex) {
                // ignore for now
            }
        });

        Button btnPrzypisz = new Button("Przypisz wybrane zadanie do wybranego krawca");
        btnPrzypisz.getStyleClass().add("secondary-button");
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
        btnBack.getStyleClass().add("ghost-button");
        btnBack.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(createLoginScene());
        });

        Label sectionCreateTitle = new Label("Nowe konto pracownika");
        sectionCreateTitle.getStyleClass().add("section-title");
        VBox createCard = new VBox(12, sectionCreateTitle, formGrid);
        createCard.getStyleClass().add("card");

        Label sectionAssignTitle = new Label("Przypisywanie zadań (zaznacz obiekty w obu listach)");
        sectionAssignTitle.getStyleClass().add("section-title");
        HBox listsRow = new HBox(12, taskListView, empListView);
        HBox.setHgrow(taskListView, Priority.ALWAYS);
        HBox.setHgrow(empListView, Priority.ALWAYS);
        VBox assignCard = new VBox(12, sectionAssignTitle, listsRow, btnPrzypisz);
        assignCard.getStyleClass().add("card");

        VBox layout = new VBox(16, titleLabel, createCard, assignCard, btnBack);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getStyleClass().add("screen");

        return createStyledScene(layout, 840, 720);
    }

    private Scene createDesignerScene() {
        Label titleLabel = new Label("Panel Projektanta - Zarządzanie Zadaniami");
        titleLabel.getStyleClass().add("title");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status");

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(200);
        taskListView.getStyleClass().add("list-view");
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
        txtTitle.getStyleClass().add("input");

        TextField txtDescription = new TextField();
        txtDescription.setPromptText("Wpisz opis zadania...");
        txtDescription.getStyleClass().add("input");

        AtomicReference<Produkt> currentProdukt = new AtomicReference<>();
        KierownikProdukcji kierownik = new KierownikProdukcji(new ProduktBuilder("Z szablonu"));

        Button btnBasic = new Button("Generuj: Bluza Basic");
        btnBasic.getStyleClass().add("secondary-button");
        btnBasic.setOnAction(e -> {
            Produkt p = kierownik.przygotujBluezeBasic();
            currentProdukt.set(p);
            txtTitle.setText("Bluza Basic");
            txtDescription.setText(p.toString().replace("\n", " | "));
        });

        Button btnPremium = new Button("Generuj: Kurtka Premium");
        btnPremium.getStyleClass().add("secondary-button");
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
        btnAdd.getStyleClass().add("primary-button");
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
        btnDelete.getStyleClass().add("danger-button");
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
        btnDuplicate.getStyleClass().add("secondary-button");
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
        btnBack.getStyleClass().add("ghost-button");
        btnBack.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(createLoginScene());
        });

        HBox buttonsLayout = new HBox(15);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(btnAdd, btnDelete, btnDuplicate, btnBack);

        Label listTitle = new Label("Lista zadań");
        listTitle.getStyleClass().add("section-title");
        VBox listCard = new VBox(12, listTitle, taskListView);
        listCard.getStyleClass().add("card");

        Label builderTitle = new Label("Kreator zadania");
        builderTitle.getStyleClass().add("section-title");
        VBox builderCard = new VBox(12, builderTitle, builderLayout, txtTitle, txtDescription);
        builderCard.getStyleClass().add("card");

        VBox layout = new VBox(16, titleLabel, statusLabel, listCard, builderCard, buttonsLayout);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getStyleClass().add("screen");

        return createStyledScene(layout, 760, 640);
    }

    private Scene createTailorScene() {
        Label titleLabel = new Label("Panel Krawca - Lista Zadań");
        titleLabel.getStyleClass().add("title");

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(250);
        taskListView.getStyleClass().add("list-view");

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
        btnAccept.getStyleClass().add("primary-button");
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
        btnComplete.getStyleClass().add("secondary-button");
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
        btnBack.getStyleClass().add("ghost-button");
        btnBack.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(createLoginScene());
        });

        HBox buttonsLayout = new HBox(15);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(btnAccept, btnComplete, btnBack);

        Label listTitle = new Label("Twoje zadania");
        listTitle.getStyleClass().add("section-title");
        VBox listCard = new VBox(12, listTitle, taskListView);
        listCard.getStyleClass().add("card");

        VBox layout = new VBox(16, titleLabel, listCard, buttonsLayout);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getStyleClass().add("screen");

        return createStyledScene(layout, 680, 520);
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
