import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignupPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SmartSpend - Sign Up");

        // === LEFT SIDE ===
        Label logo = new Label("SmartSpend");
        logo.getStyleClass().add("logo");

        Label slogan = new Label("Track Smart, Shop Smart");
        slogan.getStyleClass().add("slogan");

        String absPath = "file:/C:/Users/ASUS/OneDrive/Desktop/smartspend/assets/login_image.jpg";
        Image imageAsset = new Image(absPath);
        ImageView image = new ImageView(imageAsset);
        image.setFitWidth(500);
        image.setFitHeight(365);
        image.setPreserveRatio(false);

        VBox logoSlogan = new VBox(8, logo, slogan);
        logoSlogan.setAlignment(Pos.TOP_CENTER);

        VBox leftPane = new VBox(logoSlogan, image);
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setPrefSize(701, 1024);
        leftPane.setPadding(new Insets(140, 20, 40, 20));
        VBox.setMargin(image, new Insets(30, 0, 0, 0));
        leftPane.getStyleClass().add("left-pane");

        // === RIGHT SIDE ===
        Label heading = new Label("Create an Account");
        heading.getStyleClass().add("heading");

        Label subheading = new Label("Sign up to start tracking your expenses");
        subheading.getStyleClass().add("subheading");

        VBox headingGroup = new VBox(4, heading, subheading);
        headingGroup.setAlignment(Pos.TOP_CENTER);

        Label nameLabel = new Label("Full Name");
        nameLabel.getStyleClass().add("input-label");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");
        nameField.getStyleClass().add("input");

        VBox nameGroup = new VBox(2, nameLabel, nameField);
        nameGroup.setAlignment(Pos.CENTER_LEFT);

        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("input-label");
        TextField emailField = new TextField();
        emailField.setPromptText("example@mail.com");
        emailField.getStyleClass().add("input");

        VBox emailGroup = new VBox(2, emailLabel, emailField);
        emailGroup.setAlignment(Pos.CENTER_LEFT);

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("input-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("");
        passwordField.getStyleClass().add("input");

        VBox passwordGroup = new VBox(2, passwordLabel, passwordField);
        passwordGroup.setAlignment(Pos.CENTER_LEFT);

        Button signupButton = new Button("SIGN UP");
        signupButton.getStyleClass().add("button");

        signupButton.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (name, email, password) VALUES (?, ?, ?)"
                );
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Account created successfully!");

                new LoginPage().start(primaryStage);  // Redirect to login
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Something went wrong. Try again.");
            }
        });

        VBox.setMargin(signupButton, new Insets(30, 0, 0, 0));

        Label loginLabel = new Label("Already have an account? Log in");
        loginLabel.getStyleClass().add("link");
        loginLabel.setOnMouseClicked(e -> {
            try {
                new LoginPage().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox rightCard = new VBox(
                headingGroup,
                nameGroup,
                emailGroup,
                passwordGroup,
                signupButton,
                loginLabel
        );
        rightCard.setSpacing(16);
        rightCard.setAlignment(Pos.TOP_CENTER);
        rightCard.setPadding(new Insets(70, 0, 0, 0));
        rightCard.getStyleClass().add("card");

        StackPane rightPane = new StackPane(rightCard);
        rightPane.setPrefSize(739, 1024);
        StackPane.setAlignment(rightCard, Pos.TOP_CENTER);

        HBox pageContent = new HBox(leftPane, rightPane);
        pageContent.setPrefSize(1440, 1024);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(pageContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 1440, 1024);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
