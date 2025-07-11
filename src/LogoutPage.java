import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LogoutPage extends Application {
    private int userId;

    public LogoutPage() {
        // default constructor
    }

    public LogoutPage(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, userId);
    }

    public void start(Stage primaryStage, int userId) {
        this.userId = userId;

        primaryStage.setTitle("SmartSpend - Logout");

        // heading
        Label heading = new Label("Logout");
        heading.getStyleClass().add("logout-heading");

        // subline
        Label subline = new Label("Are you sure you want to logout?");
        subline.getStyleClass().add("logout-message");

        // confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("logout-confirm-button");
        confirmButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        // cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("logout-cancel-button");
        cancelButton.setOnAction(e -> {
            DashboardPage dashboard = new DashboardPage(userId);
            dashboard.start(primaryStage, userId);
        });

        HBox buttonsBox = new HBox(30, confirmButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(20, heading, subline, buttonsBox);
        contentBox.setAlignment(Pos.CENTER);

        // rectangle card
        StackPane rectangleContainer = new StackPane(contentBox);
        rectangleContainer.getStyleClass().add("logout-card");

        // outer transparent background
        StackPane outer = new StackPane(rectangleContainer);
        outer.getStyleClass().add("logout-root");
        outer.setPrefSize(1440, 1024);

        Scene scene = new Scene(outer, 1440, 1024);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

