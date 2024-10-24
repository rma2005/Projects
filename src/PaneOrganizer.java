import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import sun.lwawt.macosx.CPrinterDevice;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class PaneOrganizer {

    private final Stage stage;
    private final VBox beginningPane;
    private Scene beginningScene;
    private final VBox loginPane;
    private Scene loginScene;
    private final String DB_URL = "jdbc:mysql://localhost:3306/login_schema";
    private final String DB_USER = "root";    // Your DB username
    private final String DB_PASSWORD = "Worse45%";

    public PaneOrganizer(Stage stage) {
        this.stage = stage;
        this.loginPane = new VBox(Constants.BUTTON_SPACING);
        this.beginningPane = new VBox(Constants.BUTTON_SPACING);
        this.beginningPane.setAlignment(Pos.TOP_CENTER);
        this.setUpLogin();
        this.setUpLabels();
        this.setUpButtons();
    }

    private void setUpLogin() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        Button login = new Button("Login");
        Button createAccountButton = new Button("Create Account");
        Label loginError = new Label();

        this.loginPane.getChildren().addAll(usernameField, passwordField, login, createAccountButton, loginError);
        this.loginPane.setAlignment(Pos.TOP_CENTER);
        this.loginPane.setSpacing(30);

        this.loginScene = new Scene(this.loginPane, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
        this.stage.setScene(loginScene);

        login.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            boolean successfulLogin = this.checkLogin(username, password);
            if(successfulLogin) {
                this.stage.setScene(this.beginningScene);
            }
            else {
                loginError.setText("Username or password is incorrect");
            }
        });
        createAccountButton.setOnAction(events -> this.accountMenu());


    }

    private void accountMenu() {
        VBox accountCreation = new VBox();
        accountCreation.setSpacing(30);
        accountCreation.setAlignment(Pos.TOP_CENTER);

        Scene accountMenu = new Scene(accountCreation, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

        this.stage.setScene(accountMenu);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter New Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter New Password");

        PasswordField passwordFieldConfirm = new PasswordField();
        passwordFieldConfirm.setPromptText("Confirm Password");

        Button submitButton = new Button("Submit");
        Label status = new Label();
        Button goBack = new Button("Go Back");
        accountCreation.getChildren().addAll(usernameField, passwordField, passwordFieldConfirm, submitButton, status, goBack);

        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String passwordConfirm = passwordFieldConfirm.getText();
            if(!password.equals(passwordConfirm)) {
                status.setText("Password does not equal text");
            }
            else if(createNewAccount(username, password)) {
                status.setText("Account created.");
            }
            else {
                status.setText("Username already taken!");
            }
        });

        goBack.setOnAction(event -> this.stage.setScene(this.loginScene));

    }

    private boolean createNewAccount(String username, String password) {
        boolean isCreated = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // First, check if the username is already taken
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, username);

            ResultSet checkResult = checkStmt.executeQuery();
            if (!checkResult.next()) {
                // If username does not exist, insert new user
                String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // You should hash this in production

                insertStmt.executeUpdate();
                isCreated = true; // Account created successfully
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isCreated;
    }

    private boolean checkLogin(String username, String password) {
        boolean isValid = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isValid = true;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
        return isValid;
    }

    // Create both scenes
    private void setUpButtons() {
        // First scene layout
        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");
        Button custom = new Button("Custom Mode");
        Button logout = new Button("Logout");

        this.beginningPane.getChildren().addAll(easyButton, mediumButton, hardButton, custom, logout);
        this.beginningPane.setStyle("-fx-background-color: black;");  // Set black background
        this.beginningScene = new Scene(beginningPane, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);

        easyButton.setOnAction(event -> new Game(this.stage, this.beginningScene, "easy"));
        mediumButton.setOnAction(event -> new Game(this.stage, this.beginningScene, "medium"));
        hardButton.setOnAction(event -> new Game(this.stage, this.beginningScene,"hard"));
        custom.setOnAction(event -> this.createCustomGame());
        logout.setOnAction(event -> this.stage.setScene(this.loginScene));
    }

    private void setUpLabels() {
        Label title = new Label("Welcome to FPS Practice!");
        title.setFont(Font.font("Calibri", Constants.TITLE_FONT_SIZE));
        title.setTextFill(Color.WHITE);
        title.setLayoutX(Constants.TITLE_X_POS);
        title.setLayoutY(Constants.TITLE_Y_POS);

        Label difficulties = new Label("Select your difficulty:");
        difficulties.setFont(Font.font("Calibri",Constants.DIFFICULTY_FONT_SIZE));
        difficulties.setTextFill(Color.WHITESMOKE);
        difficulties.setLayoutX(Constants.DIFFICULTY_X_POS);
        difficulties.setLayoutY(Constants.DIFFICULTY_Y_POS);

        this.beginningPane.getChildren().addAll(title, difficulties);
    }

    private void createCustomGame() {
        VBox customLabels = new VBox();
        customLabels.setAlignment(Pos.TOP_CENTER);
        Scene customSettings = new Scene(customLabels, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
        TextField customSize = new TextField();
        customSize.setPromptText("Enter target size");
        TextField customSpawnRate = new TextField();
        customSpawnRate.setPromptText("Enter spawn rate");
        TextField customDuration = new TextField();
        customDuration.setPromptText("Enter duration");
        Label error = new Label("");

        Button enter = new Button("Enter");
        Button goBack = new Button("Go Back");
        customLabels.setSpacing(30);
        customLabels.getChildren().addAll(customSize, customSpawnRate, customDuration, enter, goBack, error);

        this.stage.setScene(customSettings);


        enter.setOnAction(event -> {
            String size = customSize.getText().trim();
            String spawnRate = customSpawnRate.getText().trim();
            String duration = customDuration.getText().trim();
            if(size.isEmpty() || spawnRate.isEmpty() || duration.isEmpty()) {
                error.setText("Please enter integers into the fields.");
            }
            try {
                int targetSize = Integer.parseInt(size);
                int targetSpawn = Integer.parseInt(spawnRate);
                int targetDur = Integer.parseInt(duration);
                if(targetSize >= 0 && targetSize <= 800) {
                    String difficulty = size + "," + spawnRate + "," + duration;
                    new Game(this.stage, this.beginningScene, difficulty);
                }
            }
            catch (NumberFormatException e) {
                error.setText("Please enter proper integers into the fields.");
            }
        });
        goBack.setOnAction(event -> this.stage.setScene(this.beginningScene));
    }
}
