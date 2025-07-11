import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;


public class MonthlySummaryPage extends Application {
    private int userId;

    public MonthlySummaryPage(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setLeft(DashboardPage.buildSidebar(primaryStage, "Monthly\nSummary", userId));

        VBox content = buildSummaryContent(userId);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent;");

        root.setCenter(scroll);
        root.setPrefSize(1440, 1024);

        Scene scene = new Scene(root, 1440, 1024);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Monthly\nSummary");
        primaryStage.show();
    }

    private VBox buildSummaryContent(int userId) {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20, 50, 20, 50));
        box.setStyle("-fx-background-color: #E7F8E8;");
        box.setAlignment(Pos.TOP_CENTER);  // Center heading + other items horizontally

        Label heading = new Label("Monthly Summary");
heading.setFont(Font.font("Montserrat", 40));
heading.setUnderline(true);
heading.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
HBox headingBox = new HBox(heading);
headingBox.setAlignment(Pos.CENTER);


        



        ComboBox<String> monthSelector = new ComboBox<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        for (int i = 0; i < 6; i++) {
            LocalDate date = LocalDate.now().minusMonths(i);
            monthSelector.getItems().add(date.format(formatter));
        }
        monthSelector.setValue(monthSelector.getItems().get(0));

        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(10));

        monthSelector.setOnAction(e -> {
            summaryBox.getChildren().clear();
            loadMonthlySummary(userId, monthSelector.getValue(), summaryBox);
        });

        loadMonthlySummary(userId, monthSelector.getValue(), summaryBox);

        box.getChildren().addAll(headingBox, monthSelector, summaryBox);

        return box;
    }

    private void loadMonthlySummary(int userId, String monthYear, VBox summaryBox) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        LocalDate selectedDate = LocalDate.parse("01 " + monthYear, DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        int month = selectedDate.getMonthValue();
        int year = selectedDate.getYear();

        double totalSpent = 0;
        double budget = 0;
        String topCategory = "N/A";
        Map<String, Double> categoryBreakdown = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psTotal = conn.prepareStatement(
                "SELECT SUM(amount) FROM expenses WHERE user_id = ? AND MONTH(expense_date) = ? AND YEAR(expense_date) = ?");
            psTotal.setInt(1, userId);
            psTotal.setInt(2, month);
            psTotal.setInt(3, year);
            ResultSet rsTotal = psTotal.executeQuery();
            if (rsTotal.next()) totalSpent = rsTotal.getDouble(1);

            PreparedStatement psBudget = conn.prepareStatement("SELECT monthly_budget FROM budgets WHERE user_id = ?");
            psBudget.setInt(1, userId);
            ResultSet rsBudget = psBudget.executeQuery();
            if (rsBudget.next()) budget = rsBudget.getDouble(1);

            PreparedStatement psTop = conn.prepareStatement(
                "SELECT category, SUM(amount) AS total FROM expenses WHERE user_id = ? AND MONTH(expense_date) = ? AND YEAR(expense_date) = ? GROUP BY category ORDER BY total DESC LIMIT 1");
            psTop.setInt(1, userId);
            psTop.setInt(2, month);
            psTop.setInt(3, year);
            ResultSet rsTop = psTop.executeQuery();
            if (rsTop.next()) topCategory = rsTop.getString("category");

            PreparedStatement psBreakdown = conn.prepareStatement(
                "SELECT category, SUM(amount) FROM expenses WHERE user_id = ? AND MONTH(expense_date) = ? AND YEAR(expense_date) = ? GROUP BY category");
            psBreakdown.setInt(1, userId);
            psBreakdown.setInt(2, month);
            psBreakdown.setInt(3, year);
            ResultSet rsBreak = psBreakdown.executeQuery();
            while (rsBreak.next()) {
                categoryBreakdown.put(rsBreak.getString(1), rsBreak.getDouble(2));
            }
            if (categoryBreakdown.isEmpty()) {
                Label noDataLabel = new Label("📝 No expenses recorded this month.\nStart adding some through the 'Add Expense Page' to see your summary here!");
                noDataLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #698AB6; -fx-font-weight: bold; -fx-text-alignment: center;");
                noDataLabel.setWrapText(true);
                noDataLabel.setAlignment(Pos.CENTER);
                noDataLabel.setMaxWidth(400);
            
                VBox noDataBox = new VBox(noDataLabel);
                noDataBox.setPadding(new Insets(30));
                noDataBox.setAlignment(Pos.CENTER);
                noDataBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.05), 4, 0, 0, 2);");
            
                summaryBox.getChildren().add(noDataBox);
                return; // Exit early, no need to build breakdown or insights
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        Label totalSpentLabel = new Label("\uD83D\uDCB2 Total Spent: ₹" + String.format("%.2f", totalSpent));
        totalSpentLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");

        Label budgetLabel = new Label("\uD83D\uDCCA Budget: ₹" + String.format("%.2f", budget));
        budgetLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");

        double savings = Math.max(0, budget - totalSpent);
        Label savingsLabel = new Label("\uD83C\uDFE6 Estimated Savings: ₹" + String.format("%.2f", savings));
        savingsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");

        Label topCatLabel = new Label("\uD83C\uDF1F Top Spending Category: " + topCategory);
        topCatLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #333;");

        Label breakdownLabel = new Label("\uD83D\uDCCB Category Breakdown:");
        breakdownLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000;");

        TableView<Map.Entry<String, Double>> table = new TableView<>();
        table.setPrefHeight(200);
        
        

        // Category Column
        TableColumn<Map.Entry<String, Double>, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey()));
        categoryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5).subtract(1));
        
        // Amount Column
        TableColumn<Map.Entry<String, Double>, String> amountCol = new TableColumn<>("Amount (₹)");
        amountCol.setCellValueFactory(cellData -> {
            String amount = "₹" + String.format("%.2f", cellData.getValue().getValue());
            return new javafx.beans.property.SimpleStringProperty(amount);
        });
        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5).subtract(1));
        
        // Apply styling
        table.setStyle("-fx-font-size: 14px;");
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Map.Entry<String, Double> item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && "Total".equals(item.getKey())) {
                    setStyle("-fx-font-weight: bold; -fx-background-color: #F0FFF0;");
                } else {
                    setStyle(""); // reset style
                }
            }
        });
        
        categoryCol.setStyle("-fx-font-weight: bold;");
        table.getColumns().addAll(categoryCol, amountCol);
        
        // Set data
        table.getItems().addAll(categoryBreakdown.entrySet());
        // Add a Total row
double totalCategoryAmount = categoryBreakdown.values().stream().mapToDouble(Double::doubleValue).sum();
Map.Entry<String, Double> totalEntry = new Map.Entry<String, Double>() {
    @Override
    public String getKey() {
        return "Total";
    }

    @Override
    public Double getValue() {
        return totalCategoryAmount;
    }

    @Override public Double setValue(Double value) { return null; }
};
table.getItems().add(totalEntry);


       // Wrap label + table in a toggleable VBox
VBox breakdownSection = new VBox(5, breakdownLabel, table);
breakdownSection.setVisible(false); // Initially hidden

// Toggle Button
Button toggleButton = new Button("Show Breakdown ⬇");
toggleButton.setStyle("-fx-background-color: #88DF8C; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
toggleButton.setOnAction(e -> {
    boolean isVisible = breakdownSection.isVisible();
    breakdownSection.setVisible(!isVisible);
    toggleButton.setText(isVisible ? "Show Breakdown ⬇" : "Hide Breakdown ⬆");
});

// Final card
VBox card = new VBox(10,
    totalSpentLabel,
    budgetLabel,
    savingsLabel,
    topCatLabel,
    toggleButton,
    breakdownSection
);
card.setPadding(new Insets(20));
card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
        "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");

summaryBox.getChildren().add(card);
// ==== Monthly Change Insights ====
Map<String, Double> prevMonthData = new HashMap<>();
LocalDate prevDate = selectedDate.minusMonths(1);
int prevMonth = prevDate.getMonthValue();
int prevYear = prevDate.getYear();

try (Connection conn = DBConnection.getConnection()) {
    PreparedStatement psPrev = conn.prepareStatement(
        "SELECT category, SUM(amount) FROM expenses WHERE user_id = ? AND MONTH(expense_date) = ? AND YEAR(expense_date) = ? GROUP BY category");
    psPrev.setInt(1, userId);
    psPrev.setInt(2, prevMonth);
    psPrev.setInt(3, prevYear);
    ResultSet rsPrev = psPrev.executeQuery();
    while (rsPrev.next()) {
        prevMonthData.put(rsPrev.getString(1), rsPrev.getDouble(2));
    }
} catch (Exception e) {
    e.printStackTrace();
}

Label changeLabel = new Label("📈 Monthly Change Insights:");
changeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000;");
VBox insightsBox = new VBox(5);

for (Map.Entry<String, Double> entry : categoryBreakdown.entrySet()) {
    String category = entry.getKey();
    double current = entry.getValue();
    if (prevMonthData.containsKey(category)) {
        double previous = prevMonthData.get(category);
        double change = current - previous;
        double percent = (previous != 0) ? (change / previous) * 100 : 0;
        String trend = (change > 0) ? "more" : "less";
        String msg = String.format("🔸 %s: You spent %.2f%% %s than in %s %d.",
                category, Math.abs(percent), trend,
                prevDate.getMonth(), prevYear);
        Label insight = new Label(msg);
        insight.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        insightsBox.getChildren().add(insight);
    } else {
        Label insight = new Label("🔹 " + category + ": No spending in previous month.");
        insight.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        insightsBox.getChildren().add(insight);
    }
}

VBox insightsCard = new VBox(10, changeLabel, insightsBox);
insightsCard.setPadding(new Insets(20));
insightsCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
        "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");

summaryBox.getChildren().add(insightsCard);

 

        

        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
