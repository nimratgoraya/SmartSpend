import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddExpensePage extends Application {

    private int userId;

    public AddExpensePage() {
        // fallback if launched without user id
        this.userId = 1;
    }

    public AddExpensePage(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, userId);
    }

    // custom start with userId
    public void start(Stage primaryStage, int userId) {
        this.userId = userId;
        primaryStage.setTitle("SmartSpend - Add Expense");

        VBox sidebar = DashboardPage.buildSidebar(primaryStage, "Add Expense", userId);

        Label heading = new Label("Add Expense");
        heading.setFont(Font.font("Montserrat", 40));
        heading.setUnderline(true);
        heading.setStyle("-fx-font-weight: bold;");

        HBox headingBox = new HBox(heading);
        headingBox.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(30, 0, 30, 60));
        formBox.setAlignment(Pos.TOP_LEFT);

        double inputWidth = 400;

        Label itemNameLabel = new Label("Item Name:");
        itemNameLabel.setFont(Font.font("Montserrat", 29));
        TextField itemNameField = new TextField();
        itemNameField.setPrefSize(inputWidth, 40);
        itemNameField.setStyle("-fx-background-color: #CCCCCC; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),3,0,0,1);");

        Label categoryLabel = new Label("Category:");
        categoryLabel.setFont(Font.font("Montserrat", 29));
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
                "Groceries", "Rent", "Transport", "Entertainment", "Utilities", "Healthcare",
                "Education", "Insurance", "Shopping", "Savings", "Investment", "Debt Payment",
                "Gifts/Donations", "Travel", "Others"
        );
        categoryCombo.setPrefSize(inputWidth, 40);
        categoryCombo.setStyle("-fx-background-color: #CCCCCC; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),3,0,0,1);");

        Label amountLabel = new Label("Amount:");
        amountLabel.setFont(Font.font("Montserrat", 29));
        TextField amountField = new TextField();
        amountField.setPrefSize(inputWidth, 40);
        amountField.setStyle("-fx-background-color: #CCCCCC; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),3,0,0,1);");

        Label paymentModeLabel = new Label("Payment Mode:");
        paymentModeLabel.setFont(Font.font("Montserrat", 29));
        ComboBox<String> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("Cash", "Card", "UPI", "Bank Transfer", "Others");
        paymentCombo.setPrefSize(inputWidth, 40);
        paymentCombo.setStyle("-fx-background-color: #CCCCCC; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),3,0,0,1);");

        Label dateLabel = new Label("Date:");
        dateLabel.setFont(Font.font("Montserrat", 29));
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefSize(inputWidth, 40);
        datePicker.setStyle("-fx-background-color: #CCCCCC; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),3,0,0,1);");

        Button addButton = new Button("Add Expense");
        addButton.setPrefSize(300, 40);
        addButton.setStyle(
                "-fx-background-color: #A5D6A7; -fx-background-radius: 8;"
                        + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),4,0,0,1);"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 22px;"
        );
        VBox.setMargin(addButton, new Insets(30, 0, 30, 0));

        addButton.setOnAction(e -> {
            String itemName = itemNameField.getText();
            String category = categoryCombo.getValue();
            String amountText = amountField.getText();
            String paymentMode = paymentCombo.getValue();
            var date = datePicker.getValue();

            if (itemName.isEmpty() || category == null || amountText.isEmpty() || paymentMode == null || date == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields!");
                alert.showAndWait();
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO expenses (item_name, category, payment_mode, amount, expense_date, user_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, itemName);
                stmt.setString(2, category);
                stmt.setString(3, paymentMode);
                stmt.setBigDecimal(4, new java.math.BigDecimal(amountText));
                stmt.setDate(5, java.sql.Date.valueOf(date));
                stmt.setInt(6, userId);

                stmt.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Expense added successfully!");
                alert.showAndWait();

                // clear fields
                itemNameField.clear();
                categoryCombo.setValue(null);
                amountField.clear();
                paymentCombo.setValue(null);
                datePicker.setValue(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        formBox.getChildren().addAll(
                headingBox,
                itemNameLabel, itemNameField,
                categoryLabel, categoryCombo,
                amountLabel, amountField,
                paymentModeLabel, paymentCombo,
                dateLabel, datePicker,
                addButton
        );

        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(scrollPane);
        root.setPrefSize(1440, 1024);

        Scene scene = new Scene(root, 1440, 1024);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
