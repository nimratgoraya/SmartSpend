import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ForgotPasswordPage extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Reset Password");

        // === Heading ===
        Label heading = new Label("Reset Your Password");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // === Email ===
        Label emailLabel = new Label("Registered Email");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your registered email");

        // === New Password ===
        Label newPasswordLabel = new Label("New Password");
        newPasswordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");

        // === Confirm Password ===
        Label confirmLabel = new Label("Confirm New Password");
        confirmLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm new password");

        // === Reset Button ===
        Button resetButton = new Button("Reset Password");
        resetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setOnAction(e -> {
            String email = emailField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmField.getText();

            if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
                ps.setString(1, newPassword);
                ps.setString(2, email);
                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Password has been reset successfully.");
                    stage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No user found with this email.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(10,
                heading,
                emailLabel, emailField,
                newPasswordLabel, newPasswordField,
                confirmLabel, confirmField,
                resetButton
        );
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
