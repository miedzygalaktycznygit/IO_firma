package projekt.io.firma;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import projekt.io.firma.model.Task;
import projekt.io.firma.service.TaskManagementService;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import projekt.io.firma.model.Employee;
import projekt.io.firma.model.Role;




public class JavaFxApp extends Application {

    private ConfigurableApplicationContext springContext;
    private Stage primaryStage;

    @Override
    public void init() throws Exception {
        springContext = new SpringApplicationBuilder(FirmaApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setTitle("Firma Krawiecka IO - Panel Główny");
        stage.setScene(createLoginScene());
        stage.show();
    }

    private Scene createLoginScene() {
        Label titleLabel = new Label("Wybierz swój profil:");
        titleLabel.setFont(new Font("Arial", 20));

        Button btnAdmin = new Button("Zaloguj jako Administrator");
        Button btnDesigner = new Button("Zaloguj jako Projektant");
        Button btnTailor = new Button("Zaloguj jako Krawiec");

        btnAdmin.setPrefWidth(200);
        btnDesigner.setPrefWidth(200);
        btnTailor.setPrefWidth(200);

        btnAdmin.setOnAction(e -> primaryStage.setScene(createAdminScene()));
        btnDesigner.setOnAction(e -> primaryStage.setScene(createDesignerScene()));
        btnTailor.setOnAction(e -> primaryStage.setScene(createTailorScene()));

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, btnAdmin, btnDesigner, btnTailor);

        return new Scene(layout, 500, 400);
    }


    private Scene createAdminScene() {
        TaskManagementService taskService = springContext.getBean(TaskManagementService.class);

        Label titleLabel = new Label("Panel Administratora");
        titleLabel.setFont(new Font("Arial", 18));

        TextField txtImię = new TextField();
        txtImię.setPromptText("Imię...");
        TextField txtNazwisko = new TextField();
        txtNazwisko.setPromptText("Nazwisko...");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(Role.values());
        roleBox.setPromptText("Wybierz rolę...");

        Button btnZatrudnij = new Button("Zatrudnij");

        HBox dodawanieBox = new HBox(10);
        dodawanieBox.setAlignment(Pos.CENTER);
        dodawanieBox.getChildren().addAll(txtImię, txtNazwisko, roleBox, btnZatrudnij);

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(120);
        taskListView.setCellFactory(param -> new javafx.scene.control.ListCell<Task>() {
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

        ListView<Employee> empListView = new ListView<>();
        empListView.setPrefHeight(120);
        empListView.setCellFactory(param -> new javafx.scene.control.ListCell<Employee>() {
            @Override
            protected void updateItem(Employee emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText("Pracownik #" + emp.getId() + " | " + emp.getFirstName() + " (" + emp.getRole() + ")");
                }
            }
        });

        Runnable odswiezDane = () -> {
            taskListView.getItems().clear();
            taskListView.getItems().addAll(taskService.getAllTasks());

            empListView.getItems().clear();
            empListView.getItems().addAll(taskService.getAllEmployees());
        };
        odswiezDane.run();

        btnZatrudnij.setOnAction(e -> {
            if(!txtImię.getText().isEmpty() && roleBox.getValue() != null) {
                Employee emp = new Employee();
                emp.setFirstName(txtImię.getText());
                emp.setLastName(txtNazwisko.getText());
                emp.setRole(roleBox.getValue());
                taskService.addEmployee(emp);

                txtImię.clear();
                txtNazwisko.clear();
                roleBox.setValue(null);
                odswiezDane.run();
            }
        });

        Button btnPrzypisz = new Button("Przypisz wybrane zadanie do wybranego krawca");
        btnPrzypisz.setOnAction(e -> {
            Task task = taskListView.getSelectionModel().getSelectedItem();
            Employee emp = empListView.getSelectionModel().getSelectedItem();

            if (task != null && emp != null) {
                taskService.assignTaskToTailor(task.getId(), emp.getId());
                odswiezDane.run();
            }
        });

        Button btnBack = new Button("Wyloguj");
        btnBack.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 0 20 0 20;");
        layout.getChildren().addAll(
                titleLabel,
                new Label("--- 1. NOWY PRACOWNIK ---"), dodawanieBox,
                new Label("--- 2. PRZYPISYWANIE ZADAŃ (Zaznacz obiekt w 1 i w 2 liście) ---"),
                taskListView, empListView, btnPrzypisz, btnBack
        );

        return new Scene(layout, 600, 600);
    }


    private Scene createDesignerScene() {
        TaskManagementService taskService = springContext.getBean(TaskManagementService.class);

        Label titleLabel = new Label("Panel Projektanta - Zarządzanie Zadaniami");
        titleLabel.setFont(new Font("Arial", 18));

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(200);
        taskListView.setCellFactory(param -> new javafx.scene.control.ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText("Zadanie #" + task.getId() + " | " + task.getTitle() + " | Status: " + task.getStatus());
                }
            }
        });

        Runnable odswiezListe = () -> {
            taskListView.getItems().clear();
            taskListView.getItems().addAll(taskService.getAllTasks());
        };
        odswiezListe.run();

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Wpisz tytuł zadania...");

        TextField txtDescription = new TextField();
        txtDescription.setPromptText("Wpisz opis zadania...");

        Button btnAdd = new Button("Dodaj nowe zadanie");
        btnAdd.setOnAction(e -> {
            if (!txtTitle.getText().isEmpty() && !txtDescription.getText().isEmpty()) {
                Task newTask = new Task();
                newTask.setTitle(txtTitle.getText());
                newTask.setDescription(txtDescription.getText());

                taskService.createTask(newTask);

                txtTitle.clear();
                txtDescription.clear();
                odswiezListe.run();
            }
        });

        Button btnDelete = new Button("Usuń zaznaczone");
        btnDelete.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                taskService.deleteTask(selectedTask.getId());
                odswiezListe.run();
            }
        });

        Button btnBack = new Button("Wyloguj");
        btnBack.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        HBox buttonsLayout = new HBox(15);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(btnAdd, btnDelete, btnBack);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 0 20 0 20;");

        layout.getChildren().addAll(titleLabel, taskListView, txtTitle, txtDescription, buttonsLayout);

        return new Scene(layout, 500, 450);
    }


    private Scene createTailorScene() {
        TaskManagementService taskService = springContext.getBean(TaskManagementService.class);

        Label titleLabel = new Label("Panel Krawca - Lista Zadań");
        titleLabel.setFont(new Font("Arial", 18));

        ListView<Task> taskListView = new ListView<>();
        taskListView.setPrefHeight(250);

        taskListView.setCellFactory(param -> new javafx.scene.control.ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText("Zadanie #" + task.getId() + " | " + task.getTitle() + " | Status: " + task.getStatus());
                }
            }
        });

        Runnable odswiezListe = () -> {
            taskListView.getItems().clear();
            taskListView.getItems().addAll(taskService.getAllTasks());
        };

        odswiezListe.run();

        Button btnAccept = new Button("Przyjmij do realizacji");
        btnAccept.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                taskService.acceptTask(selectedTask.getId());
                odswiezListe.run();
            }
        });

        Button btnBack = new Button("Wyloguj");
        btnBack.setOnAction(e -> primaryStage.setScene(createLoginScene()));

        HBox buttonsLayout = new HBox(15);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(btnAccept, btnBack);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, taskListView, buttonsLayout);

        return new Scene(layout, 500, 400);
    }


    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}
