import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsPage extends Application {

    private int userId;

    public SettingsPage(int userId) {
        this.userId = userId;
    }

    public SettingsPage() {
        this.userId = 1; // fallback for testing
    }

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, userId); // fallback from Application.launch
    }

    public void start(Stage primaryStage, int userId) {
        this.userId = userId;
        primaryStage.setTitle("SmartSpend - Settings");

        VBox sidebar = DashboardPage.buildSidebar(primaryStage, "Settings", userId);

        // === HEADING ===
        Label heading = new Label("Settings");
        heading.setFont(Font.font("Montserrat", 40));
        heading.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        heading.setUnderline(true);
        HBox headingBox = new HBox(heading);
        headingBox.setAlignment(Pos.CENTER);

        // === CHANGE PASSWORD FORM ===
        Label changePasswordHeading = new Label("Change Password");
        changePasswordHeading.setFont(Font.font("Poppins", 24));
        changePasswordHeading.setStyle("-fx-font-weight: bold;");

        GridPane changePasswordGrid = new GridPane();
        changePasswordGrid.setVgap(15);
        changePasswordGrid.setHgap(10);
        changePasswordGrid.setAlignment(Pos.CENTER_LEFT);

        Label currentPassLabel = new Label("Current Password:");
        currentPassLabel.setFont(Font.font("Poppins", 20));
        PasswordField currentPassField = createPasswordField();

        Label newPassLabel = new Label("New Password:");
        newPassLabel.setFont(Font.font("Poppins", 20));
        PasswordField newPassField = createPasswordField();

        Label confirmPassLabel = new Label("Confirm Password:");
        confirmPassLabel.setFont(Font.font("Poppins", 20));
        PasswordField confirmPassField = createPasswordField();

        changePasswordGrid.add(currentPassLabel, 0, 0);
        changePasswordGrid.add(currentPassField, 1, 0);
        changePasswordGrid.add(newPassLabel, 0, 1);
        changePasswordGrid.add(newPassField, 1, 1);
        changePasswordGrid.add(confirmPassLabel, 0, 2);
        changePasswordGrid.add(confirmPassField, 1, 2);

        Button changePassBtn = new Button("CHANGE");
        changePassBtn.getStyleClass().add("settings-button");
        changePassBtn.setOnAction(e -> {
            String currentPass = currentPassField.getText().trim();
            String newPass = newPassField.getText().trim();
            String confirmPass = confirmPassField.getText().trim();
        
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showConfirmationPopup("All fields are required!", primaryStage, "settings");
                return;
            }
        
            if (!newPass.equals(confirmPass)) {
                showConfirmationPopup("New passwords do not match!", primaryStage, "settings");
                return;
            }
        
            try (var conn = DBConnection.getConnection()) {
                var checkStmt = conn.prepareStatement("SELECT password FROM users WHERE id = ?");
                checkStmt.setInt(1, userId);
                var rs = checkStmt.executeQuery();
        
                if (rs.next()) {
                    String actualPassword = rs.getString("password");
        
                    if (!currentPass.equals(actualPassword)) {
                        showConfirmationPopup("Current password is incorrect!", primaryStage, "settings");
                        return;
                    }
        
                    var updateStmt = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?");
                    updateStmt.setString(1, newPass);
                    updateStmt.setInt(2, userId);
                    int updated = updateStmt.executeUpdate();
        
                    if (updated > 0) {
                        showConfirmationPopup("PASSWORD CHANGED SUCCESSFULLY!", primaryStage, "settings");
                    } else {
                        showConfirmationPopup("Failed to change password!", primaryStage, "settings");
                    }
        
                } else {
                    showConfirmationPopup("User not found!", primaryStage, "settings");
                }
        
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        

        VBox changePasswordSection = new VBox(10, changePasswordHeading, changePasswordGrid, changePassBtn);
        changePasswordSection.setPadding(new Insets(20, 0, 20, 0));

        // === PROFILE DETAILS FORM ===
        Label profileHeading = new Label("Profile Details");
        profileHeading.setFont(Font.font("Poppins", 24));
        profileHeading.setStyle("-fx-font-weight: bold;");

        GridPane profileGrid = new GridPane();
        profileGrid.setVgap(15);
        profileGrid.setHgap(10);
        profileGrid.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Poppins", 20));
        TextField nameField = createInputField();

        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Poppins", 20));
        TextField emailField = createInputField();

        Label phoneLabel = new Label("Phone:");
        phoneLabel.setFont(Font.font("Poppins", 20));
        TextField phoneField = createInputField();

        try (var conn = DBConnection.getConnection()) {
            var stmt = conn.prepareStatement("SELECT name, email, phone FROM users WHERE id = ?");
            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        



        profileGrid.add(nameLabel, 0, 0);
        profileGrid.add(nameField, 1, 0);
        profileGrid.add(emailLabel, 0, 1);
        profileGrid.add(emailField, 1, 1);
        profileGrid.add(phoneLabel, 0, 2);
        profileGrid.add(phoneField, 1, 2);

        Button saveProfileBtn = new Button("SAVE");
        saveProfileBtn.getStyleClass().add("settings-button");
        saveProfileBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
        
            try (var conn = DBConnection.getConnection()) {
                var stmt = conn.prepareStatement("UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?");
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.setInt(4, userId);
        
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    showConfirmationPopup("UPDATES MADE SUCCESSFULLY!", primaryStage, "settings");
                } else {
                    showConfirmationPopup("FAILED TO UPDATE PROFILE!", primaryStage, "settings");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        

        VBox profileSection = new VBox(10, profileHeading, profileGrid, saveProfileBtn);
        profileSection.setPadding(new Insets(20, 0, 20, 0));

        // === NOTIFICATION PREFERENCES ===
        Label notifHeading = new Label("Notification Preferences");
        notifHeading.setFont(Font.font("Poppins", 24));
        notifHeading.setStyle("-fx-font-weight: bold;");

        CheckBox emailCheck = new CheckBox("Receive Email alerts");
        emailCheck.setStyle("-fx-font-size: 18px;");
        CheckBox smsCheck = new CheckBox("Receive SMS alerts");
        smsCheck.setStyle("-fx-font-size: 18px;");
        try (Connection conn = DBConnection.getConnection();
     PreparedStatement stmt = conn.prepareStatement("SELECT email_alerts, sms_alerts FROM users WHERE id = ?")) {
    stmt.setInt(1, userId);
    ResultSet rs = stmt.executeQuery();
    if (rs.next()) {
        emailCheck.setSelected(rs.getBoolean("email_alerts"));
        smsCheck.setSelected(rs.getBoolean("sms_alerts"));
    }
} catch (Exception ex) {
    ex.printStackTrace();
}


        Button notifBtn = new Button("UPDATE");
        notifBtn.getStyleClass().add("settings-button");
        notifBtn.setOnAction(e -> {
            boolean emailPref = emailCheck.isSelected();
            boolean smsPref = smsCheck.isSelected();
        
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET email_alerts = ?, sms_alerts = ? WHERE id = ?")) {
                stmt.setBoolean(1, emailPref);
                stmt.setBoolean(2, smsPref);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
        
                showConfirmationPopup("UPDATES MADE SUCCESSFULLY!", primaryStage, "settings");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        

        VBox notifSection = new VBox(10, notifHeading, emailCheck, smsCheck, notifBtn);
        notifSection.setPadding(new Insets(20, 0, 20, 0));

        // === DELETE ACCOUNT BUTTON ===
        Button deleteBtn = new Button("DELETE ACCOUNT");
        deleteBtn.getStyleClass().add("settings-delete-button");
        deleteBtn.setOnAction(e -> {
            showDeleteConfirmation(primaryStage);
        });

        HBox deleteBox = new HBox(deleteBtn);
        deleteBox.setAlignment(Pos.CENTER);

        // === MAIN CONTENT ===
        VBox centerContent = new VBox(30,
                headingBox,
                changePasswordSection,
                profileSection,
                notifSection,
                deleteBox
        );
        centerContent.setAlignment(Pos.TOP_LEFT);
        centerContent.setPadding(new Insets(30, 0, 30, 60));

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scrollPane);
        root.setPrefSize(1440, 1024);

        Scene scene = new Scene(root, 1440, 1024);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createInputField() {
        TextField field = new TextField();
        field.setPrefSize(300, 25);
        field.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 7;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 1);");
        return field;
    }

    private PasswordField createPasswordField() {
        PasswordField field = new PasswordField();
        field.setPrefSize(300, 25);
        field.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 7;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 1);");
        return field;
    }

    private void showConfirmationPopup(String message, Stage owner, String fromPage) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(owner);

        Label msg = new Label(message);
        msg.setFont(Font.font("Poppins", 32));
        msg.setStyle("-fx-text-fill: #555555;");
        msg.setAlignment(Pos.CENTER);

        Button doneBtn = new Button("DONE");
        doneBtn.setPrefSize(156, 54);
        doneBtn.setFont(Font.font(28));
        doneBtn.setStyle("-fx-background-color: white; -fx-text-fill: #555555;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");

        doneBtn.setOnAction(e -> popup.close());

        VBox box = new VBox(20, msg, doneBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #88DF8C;");
        popup.setScene(new Scene(box, 600, 150));
        popup.show();
    }

    private void showDeleteConfirmation(Stage owner) {
        Stage confirmPopup = new Stage();
        confirmPopup.initModality(Modality.APPLICATION_MODAL);
        confirmPopup.initOwner(owner);

        Label msg = new Label("Are you sure?");
        msg.setFont(Font.font("Poppins", 32));
        msg.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        msg.setAlignment(Pos.CENTER);

        Button yesBtn = new Button("YES");
        yesBtn.setPrefSize(156, 54);
        yesBtn.setFont(Font.font(28));
        yesBtn.setStyle("-fx-background-color: #88DF8C; -fx-text-fill: #333;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");

                yesBtn.setOnAction(e -> {
                    try (Connection conn = DBConnection.getConnection()) {
                        // Step 1: Delete user's expenses first (optional but recommended)
                        try (PreparedStatement deleteExpenses = conn.prepareStatement("DELETE FROM expenses WHERE user_id = ?")) {
                            deleteExpenses.setInt(1, userId);
                            deleteExpenses.executeUpdate();
                        }
                
                        // Step 2: Delete user from the users table
                        try (PreparedStatement deleteUser = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                            deleteUser.setInt(1, userId);
                            int deleted = deleteUser.executeUpdate();
                
                            if (deleted > 0) {
                                confirmPopup.close();
                                showAccountDeleted(owner);  // Redirect to login
                            } else {
                                confirmPopup.close();
                                showConfirmationPopup("Failed to delete account!", owner, "settings");
                            }
                        }
                
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        confirmPopup.close();
                        showConfirmationPopup("Something went wrong!", owner, "settings");
                    }
                });
                

        Button noBtn = new Button("NO");
        noBtn.setPrefSize(156, 54);
        noBtn.setFont(Font.font(28));
        noBtn.setStyle("-fx-background-color: #FF7E7E; -fx-text-fill: white;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");

        noBtn.setOnAction(e -> confirmPopup.close());

        HBox buttons = new HBox(20, yesBtn, noBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(20, msg, buttons);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #333333;");
        confirmPopup.setScene(new Scene(box, 600, 150));
        confirmPopup.show();
    }

    private void showAccountDeleted(Stage owner) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(owner);

        Label msg = new Label("ACCOUNT DELETED SUCCESSFULLY!");
        msg.setFont(Font.font("Poppins", 32));
        msg.setStyle("-fx-text-fill: #555555;");
        msg.setAlignment(Pos.CENTER);

        Button doneBtn = new Button("DONE");
        doneBtn.setPrefSize(156, 54);
        doneBtn.setFont(Font.font(28));
        doneBtn.setStyle("-fx-background-color: white; -fx-text-fill: #555555;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");

        doneBtn.setOnAction(e -> {
            popup.close();
            LoginPage login = new LoginPage();
            Stage loginStage = new Stage();
            try {
                login.start(loginStage);
                owner.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox box = new VBox(20, msg, doneBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #88DF8C;");
        popup.setScene(new Scene(box, 600, 150));
        popup.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
