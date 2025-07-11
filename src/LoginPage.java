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

public class LoginPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SmartSpend - Login");

        // === LEFT SIDE ===
        Label logo = new Label("SmartSpend");
        logo.getStyleClass().add("logo");

        Label slogan = new Label("Track Smart, Spend Smart");
        slogan.getStyleClass().add("slogan");

        // Try loading from resources
java.net.URL imageUrl = getClass().getResource("/assets/login_image.jpg");
System.out.println("🔍 Trying to load image from /assets/login_image.jpg");
System.out.println("Image URL: " + imageUrl);

ImageView image;
if (imageUrl != null) {
    Image img = new Image(imageUrl.toExternalForm());
    image = new ImageView(img);
    System.out.println("✅ Image loaded successfully.");
} else {
    image = new ImageView(); // fallback empty image
    System.out.println("❌ Image NOT found. Check /assets/login_image.jpg path.");
}

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
        Label welcome = new Label("Welcome Back");
        welcome.getStyleClass().add("heading");

        Label instruction = new Label("Login to track your expenses");
        instruction.getStyleClass().add("subheading");

        VBox headingGroup = new VBox(4, welcome, instruction);
        headingGroup.setAlignment(Pos.TOP_CENTER);

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

        Button loginButton = new Button("LOGIN");
        loginButton.getStyleClass().add("button");

        // make login button check database
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                var stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                stmt.setString(2, password);

                var rs = stmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id"); // get the logged-in user's ID
                    DashboardPage dashboard = new DashboardPage(userId);
                    Stage dashboardStage = new Stage();
                    dashboard.start(dashboardStage, userId);
                    primaryStage.close();
                }
                 else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid email or password. Please try again.");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Database error: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        VBox.setMargin(loginButton, new Insets(30, 0, 0, 0));

        Label forgotPassword = new Label("Forgot Password?");
        forgotPassword.getStyleClass().add("link");
        forgotPassword.setOnMouseClicked(e -> {
            try {
                new ForgotPasswordPage().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        

        Label signUp = new Label("Don’t have an account? Sign up");
        signUp.getStyleClass().add("link");
        signUp.setOnMouseClicked(e -> {
            try {
                new SignupPage().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        

        VBox rightCard = new VBox(
                headingGroup,
                emailGroup,
                passwordGroup,
                loginButton,
                forgotPassword,
                signUp
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

        // === SCROLLPANE around the entire page ===
        ScrollPane scrollPane = new ScrollPane(pageContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 1440, 1024);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}