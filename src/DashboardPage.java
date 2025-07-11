import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;





public class DashboardPage extends Application {

    private int userId = -1;
    private Label remainingBudgetLabel;
private Label progressLabel;
private ProgressBar budgetProgress;
private VBox card4; // for "Remaining Budget"


    public DashboardPage() {}

    public DashboardPage(int userId) {
        this.userId = userId;
    }

    private VBox buildGifBox() {
        ImageView gifView = new ImageView(new Image(getClass().getResource("/assets/dashboardgif.gif").toExternalForm()));
        gifView.setFitWidth(180); // Same width you were using earlier
        gifView.setPreserveRatio(true);
    
        VBox gifBox = new VBox(gifView);
        gifBox.setAlignment(Pos.TOP_RIGHT);
        gifBox.setPadding(new Insets(20, 30, 0, 0)); // aligns top-right like greeting
        return gifBox;
    }
    
    public VBox createCard(String title, String value, Image image) {
        ImageView icon = new ImageView();
        if (image != null) {
            icon.setImage(image);
        }
        icon.setFitWidth(24);
        icon.setFitHeight(24);
    
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666666;");
    
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");
    
        HBox top = new HBox(10, icon, titleLabel);
        VBox card = new VBox(4, top, valueLabel);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
    
        return card;
    }
    
    public VBox createCard(String title, Label valueLabel, Image image) {
        ImageView icon = new ImageView();
        if (image != null) {
            icon.setImage(image);
        }
        icon.setFitWidth(24);
        icon.setFitHeight(24);
    
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666666;");
    
        HBox top = new HBox(10, icon, titleLabel);
        VBox card = new VBox(4, top, valueLabel);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
    
        return card;
    }
    

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, userId);

    }

    public void start(Stage primaryStage, int userId) {
        this.userId = userId;
        primaryStage.setTitle("SmartSpend - Dashboard");

        ImageView topRightGif = new ImageView(new Image(getClass().getResource("/assets/dashboardgif.gif").toExternalForm()));
topRightGif.setFitWidth(160); // adjust width if needed
topRightGif.setPreserveRatio(true);
StackPane gifOverlay = new StackPane(topRightGif);
StackPane.setAlignment(topRightGif, Pos.TOP_RIGHT);
gifOverlay.setPadding(new Insets(20, 40, 0, 0)); // just a bit of breathing space
gifOverlay.setMouseTransparent(true); // ensures clicks pass through to underlying nodes


        VBox sidebar = buildSidebar(primaryStage, "Dashboard", userId);

        // === TOP CARDS ===
        HBox cardsRow = new HBox();
        cardsRow.setAlignment(Pos.CENTER);
        cardsRow.setSpacing(50);
        cardsRow.setPadding(new Insets(30, 0, 0, 0));

        // fetch values
        String totalExpenses = "₹0";
        String thisMonth = "₹0";
        String topCategory = "N/A";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("SELECT SUM(amount) FROM expenses WHERE user_id = ?");
            ps1.setInt(1, userId);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                totalExpenses = "₹" + rs1.getDouble(1);
            }

            PreparedStatement ps2 = conn.prepareStatement("SELECT SUM(amount) FROM expenses WHERE user_id = ? AND MONTH(expense_date) = MONTH(CURDATE()) AND YEAR(expense_date) = YEAR(CURDATE())");
            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                thisMonth = "₹" + rs2.getDouble(1);
            }

            PreparedStatement ps3 = conn.prepareStatement("SELECT category, SUM(amount) as total FROM expenses WHERE user_id = ? GROUP BY category ORDER BY total DESC LIMIT 1");
            ps3.setInt(1, userId);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                topCategory = rs3.getString(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Image rupeeImage = null;
java.net.URL rupeeUrl = getClass().getResource("/assets/rupee.png");
if (rupeeUrl != null) {
    rupeeImage = new Image(rupeeUrl.toExternalForm());
} else {
    System.out.println("❌ Couldn't load /assets/rupee.png");
}
VBox card1 = createCard("Total Expenses", totalExpenses, rupeeImage);

Image walletImage = null;
java.net.URL walletUrl = getClass().getResource("/assets/wallet.png");
if (walletUrl != null) {
    walletImage = new Image(walletUrl.toExternalForm());
} else {
    System.out.println("❌ Couldn't load /assets/wallet.png");
}
VBox card2 = createCard("This Month", thisMonth, walletImage);

Image shoppingCartImage = null;
java.net.URL shoppingCartUrl = getClass().getResource("/assets/shopping_cart.png");
if (shoppingCartUrl != null) {
    shoppingCartImage = new Image(shoppingCartUrl.toExternalForm());
} else {
    System.out.println("❌ Couldn't load /assets/shopping_cart.png");
}
VBox card3 = createCard("Top Category", topCategory, shoppingCartImage);

        // calculate remaining budget dynamically
String remainingBudget = "₹0";
try (Connection conn = DBConnection.getConnection()) {
    PreparedStatement psBudget = conn.prepareStatement("SELECT monthly_budget FROM budgets WHERE user_id = ?");
    psBudget.setInt(1, userId);
    ResultSet rsBudget = psBudget.executeQuery();
    double budgetAmount = 0;
    if (rsBudget.next()) {
        budgetAmount = rsBudget.getDouble(1);
    }

    double totalExpensesValue = 0;
    PreparedStatement psExpenses = conn.prepareStatement("SELECT SUM(amount) FROM expenses WHERE user_id = ?");
    psExpenses.setInt(1, userId);
    ResultSet rsExpenses = psExpenses.executeQuery();
    if (rsExpenses.next()) {
        totalExpensesValue = rsExpenses.getDouble(1);
    }

    double remaining = budgetAmount - totalExpensesValue;
    remainingBudget = "₹" + remaining;
} catch (Exception e) {
    e.printStackTrace();
}

remainingBudgetLabel = new Label(remainingBudget);
Image piggyBankImage = null;
java.net.URL piggyBankUrl = getClass().getResource("/assets/piggy_bank.png");
if (piggyBankUrl != null) {
    piggyBankImage = new Image(piggyBankUrl.toExternalForm());
} else {
    System.out.println("❌ Couldn't load /assets/piggy_bank.png");
}
card4 = createCard("Remaining Budget", remainingBudgetLabel, piggyBankImage);



       // NEW: create the Set Budget button
       Button setBudgetButton = new Button("Set Budget");
       setBudgetButton.setPrefSize(200, 30);
       setBudgetButton.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
       + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.15), 6, 0, 0, 2);"
       + "-fx-font-size: 20px; -fx-font-weight: bold;"
       + "-fx-text-fill: #88DF8C;");

       VBox alertContainer = new VBox();  // top-level container for alerts
       alertContainer.setPadding(new Insets(0, 50, 0, 50));
       
       // handle click to show popup
       setBudgetButton.setOnAction(e -> {
       TextInputDialog dialog = new TextInputDialog();
       dialog.setTitle("Set Budget");
       dialog.setHeaderText("Enter your budget amount");
       dialog.setContentText("Amount:");
       dialog.showAndWait().ifPresent(amount -> {
        try (Connection conn = DBConnection.getConnection()) {
            double value = Double.parseDouble(amount);
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO budgets (user_id, monthly_budget) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE monthly_budget = VALUES(monthly_budget)"
            );
            ps.setInt(1, userId);
            ps.setDouble(2, value);
            ps.executeUpdate();
            updateRemainingBudget(userId, alertContainer);
         // 🔁 refresh card4 with new value + color
            try (Connection conn3 = DBConnection.getConnection()) {
                PreparedStatement newSpentStmt = conn3.prepareStatement(
                    "SELECT SUM(amount) FROM expenses WHERE user_id = ? AND MONTH(expense_date) = MONTH(CURDATE()) AND YEAR(expense_date) = YEAR(CURDATE())"
                );
                newSpentStmt.setInt(1, userId);
                ResultSet newSpentRs = newSpentStmt.executeQuery();
                double newSpent = 0;
                if (newSpentRs.next()) {
                    newSpent = newSpentRs.getDouble(1);
                }
            
                double newProgress = value == 0 ? 0 : newSpent / value;
                newProgress = Math.min(newProgress, 1.0);
                budgetProgress.setProgress(newProgress);
            
                String color;
                if (newProgress >= 1.0) {
                    color = "#FF7E7E"; // Red
                } else if (newProgress >= 0.7) {
                    color = "#FFCC84"; // Orange
                } else {
                    color = "#88DF8C"; // Green
                }
                budgetProgress.setStyle("-fx-accent: " + color + ";");
            
                progressLabel.setText("₹" + String.format("%.2f", newSpent) + " of ₹" + String.format("%.2f", value) + " spent");
            
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            

            if (value > 0) {
                // Check if already over budget
                double spent = 0;
                try (Connection conn2 = DBConnection.getConnection()) {
                    PreparedStatement spentStmt = conn2.prepareStatement(
                        "SELECT SUM(amount) FROM expenses WHERE user_id = ?"
                    );
                    spentStmt.setInt(1, userId);
                    ResultSet rs = spentStmt.executeQuery();
                    if (rs.next()) {
                        spent = rs.getDouble(1);
                    }
                }
            
                if (spent >= value) {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Budget Alert");
                    warning.setHeaderText(null);
                    warning.setContentText("⚠ You’re over budget!");
                    warning.showAndWait();
                }
            }
            
            // show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Budget Set");
            alert.setHeaderText(null);
            alert.setContentText("Your budget was set to ₹" + value);
            alert.showAndWait();
            // optionally, refresh dashboard
            //start(primaryStage, userId);
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to set budget.");
            alert.showAndWait();
        }
    });
    
  });

// pack the 4th card and button vertically
VBox budgetCardBox = new VBox();
budgetCardBox.setAlignment(Pos.CENTER);
budgetCardBox.setSpacing(8);  // spacing between button and card



Button setGoalButton = new Button("Set Savings Goal");
setGoalButton.setPrefWidth(150);
setGoalButton.setMinWidth(150);
setGoalButton.setMaxWidth(150);
setGoalButton.setPrefHeight(35);
setGoalButton.setMinHeight(35);
setGoalButton.setMaxHeight(35);

// Set compact style
setGoalButton.setStyle(
    "-fx-background-color: #88DF8C; " +
    "-fx-text-fill: white; " +
    "-fx-font-weight: bold; " +
    "-fx-font-size: 14px; " +
    "-fx-background-radius: 10;"
);
VBox.setVgrow(setGoalButton, Priority.NEVER); // don't allow it to expand


// === SAVINGS GOAL TRACKER COMPONENTS ===
/*Label savingsHeader = new Label("Savings Goal Tracker");
savingsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

Label savingsProgressLabel = new Label("₹0 saved out of ₹0");
savingsProgressLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

ProgressBar savingsProgressBar = new ProgressBar(0);
savingsProgressBar.setPrefWidth(950);


VBox savingsBox = new VBox(10, savingsHeader, savingsProgressLabel, savingsProgressBar, setGoalButton);
savingsBox.setAlignment(Pos.CENTER_LEFT); // ✅ Prevents stretching
VBox.setVgrow(setGoalButton, Priority.NEVER); // ✅ Prevents vertical stretching*/




setGoalButton.setOnAction(e -> {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Set Savings Goal");
    dialog.setHeaderText("Enter your monthly savings goal:");
    dialog.setContentText("Amount:");

    dialog.showAndWait().ifPresent(goalAmount -> {
        try (Connection conn = DBConnection.getConnection()) {
            double value = Double.parseDouble(goalAmount);

            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO savings_goals (user_id, goal_amount)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE goal_amount = VALUES(goal_amount)
            """);
            ps.setInt(1, userId);
            ps.setDouble(2, value);
            ps.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Goal Set");
            alert.setHeaderText(null);
            alert.setContentText("Your savings goal of ₹" + value + " has been saved.");
            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to set savings goal.");
            alert.showAndWait();
        }
    });
});

budgetCardBox.getChildren().addAll(setBudgetButton, card4);





// keep styles
for (VBox card : new VBox[]{card1, card2, card3}) {
    card.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
            + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.15), 6, 0, 0, 2);");
    card.setPrefHeight(85);
    card.setMaxHeight(85);
}
card4.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
        + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.15), 6, 0, 0, 2);");
card4.setPrefHeight(85);
card4.setMaxHeight(85);

// update HBox
cardsRow.getChildren().clear();
cardsRow.getChildren().addAll(card1, card2, card3, budgetCardBox);


        for (VBox card : new VBox[]{card1, card2, card3, card4}) {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
                    + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.15), 6, 0, 0, 2);");
            card.setPrefHeight(85);
            card.setMaxHeight(85);
        }
       

        StackPane cardsBackground = new StackPane();
        cardsBackground.setPrefSize(1188, 202);
        cardsBackground.setStyle("-fx-background-color: #D9D9D9;");
        cardsBackground.setPadding(new Insets(0, 0, 20, 0));
        cardsBackground.getChildren().add(cardsRow);
        boolean noExpenses = false;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psExp = conn.prepareStatement("SELECT COUNT(*) FROM expenses WHERE user_id = ?");
            psExp.setInt(1, userId);
            ResultSet rsExp = psExp.executeQuery();
            if (rsExp.next()) {
                noExpenses = rsExp.getInt(1) == 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // === BAR CHART ===
VBox barChartContainer;

if (noExpenses) {
    Label barPlaceholder = new Label("📊 No expense data yet. Add some expenses to see your monthly trends!");
    barPlaceholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");
    barPlaceholder.setWrapText(true);
    barPlaceholder.setAlignment(Pos.CENTER);
    barPlaceholder.setMaxWidth(700);

    barChartContainer = new VBox(barPlaceholder);
    barChartContainer.setAlignment(Pos.CENTER);
    barChartContainer.setPadding(new Insets(20));
    barChartContainer.setPrefSize(750, 322);
    barChartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
            "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),6,0,0,2);");
    barChartContainer.setTranslateX(1);

} else {
    CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel("Month");
    xAxis.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    xAxis.setTickLabelsVisible(true);
    xAxis.setCategories(FXCollections.observableArrayList(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ));
    xAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Amount (₹)");
    yAxis.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    yAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
    yAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 12));

    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setHorizontalGridLinesVisible(true);
    barChart.setVerticalGridLinesVisible(false);
    barChart.setCategoryGap(20);
    barChart.setBarGap(3);
    barChart.setPrefSize(750, 322);
    barChart.setLegendVisible(false);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.getData().clear();

    try (Connection conn = DBConnection.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT MONTH(expense_date) AS month, SUM(amount) AS total " +
                        "FROM expenses " +
                        "WHERE user_id = ? AND YEAR(expense_date) = YEAR(CURDATE()) " +
                        "GROUP BY MONTH(expense_date) ORDER BY MONTH(expense_date)"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        double[] monthTotals = new double[12];

        while (rs.next()) {
            int month = rs.getInt("month");
            double total = rs.getDouble("total");
            monthTotals[month - 1] = total;
        }

        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 0; i < 12; i++) {
            series.getData().add(new XYChart.Data<>(monthNames[i], monthTotals[i]));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    barChart.getData().add(series);
    xAxis.setTickLabelRotation(0);

    barChartContainer = new VBox(barChart);
    barChartContainer.setPadding(new Insets(20));
    barChartContainer.setPrefSize(750, 322);
    barChartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
            "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),6,0,0,2);");
    barChartContainer.setTranslateX(1);
}


        // === PIE CHART ===
Map<String, Double> categoryTotals = new HashMap<>();

try (Connection conn = DBConnection.getConnection()) {
    String sql = """
        SELECT category, SUM(amount)
        FROM expenses
        WHERE user_id = ?
          AND MONTH(expense_date) = MONTH(CURDATE())
          AND YEAR(expense_date) = YEAR(CURDATE())
        GROUP BY category
    """;
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
        categoryTotals.put(rs.getString(1), rs.getDouble(2));
    }

} catch (Exception e) {
    e.printStackTrace();
}

Label pieChartTitle = new Label("Category-wise Spending");
pieChartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

VBox pieChartContainer = new VBox(10, pieChartTitle);

pieChartContainer.setAlignment(Pos.CENTER);
pieChartContainer.setPrefSize(300, 300);

if (categoryTotals.isEmpty()) {
    Label piePlaceholder = new Label("🍰 No expenses yet.\nStart adding to see spending categories!");
    piePlaceholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");
    pieChartContainer.getChildren().add(piePlaceholder);
} else {
    PieChart pieChart = new PieChart();
    pieChart.setPrefSize(300, 300);
    pieChart.setLegendVisible(false);
    pieChart.setLabelsVisible(false); // we’re using tooltips instead

    String[] sliceColors = {
        "#FF7E7E", "#698AB6", "#88DF8C", "#FFEA7E",
        "#FFCC84", "#BDD6FF", "#FCA7EB", "#FFAFAF"
    };

    int colorIndex = 0;
    for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
        PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
        pieChart.getData().add(data);
    }

    // Apply colors + tooltips
    Platform.runLater(() -> {
        int i = 0;
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            if (i < sliceColors.length) {
                node.setStyle("-fx-pie-color: " + sliceColors[i] + ";");
            }

            Tooltip tooltip = new Tooltip(data.getName() + " ₹" + String.format("%.2f", data.getPieValue()));
            tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            Tooltip.install(node, tooltip);

            i++;
        }
    });

    pieChartContainer.getChildren().add(pieChart);
}

HBox chartsSection = new HBox(30, barChartContainer, pieChartContainer);
chartsSection.setAlignment(Pos.CENTER_LEFT);
chartsSection.setPadding(new Insets(0, 50, 0, 50));

String trendMessage = "";
try (Connection conn = DBConnection.getConnection()) {
    String sql = """
        SELECT category,
               SUM(CASE WHEN MONTH(expense_date) = MONTH(CURDATE()) THEN amount ELSE 0 END) AS this_month,
               SUM(CASE WHEN MONTH(expense_date) = MONTH(CURDATE()) - 1 THEN amount ELSE 0 END) AS last_month
        FROM expenses
        WHERE user_id = ? AND YEAR(expense_date) = YEAR(CURDATE())
        GROUP BY category
    """;

    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();

    String maxCategory = null;
    double maxPercent = 0;

    while (rs.next()) {
        String category = rs.getString("category");
        double currentMonth = rs.getDouble("this_month");
        double previousMonth = rs.getDouble("last_month");

        if (previousMonth > 0) {
            double percentChange = ((currentMonth - previousMonth) / previousMonth) * 100;
            if (percentChange > maxPercent) {
                maxPercent = percentChange;
                maxCategory = category;
            }
        }
    }

    if (maxCategory != null && maxPercent > 0) {
        trendMessage = String.format("📉 You spent %.0f%% more on %s than last month", maxPercent, maxCategory);
    }

} catch (Exception e) {
    e.printStackTrace();
}


   
// === BUDGET PROGRESS BAR ===
Label budgetHeading = new Label("Budget Progress");
budgetHeading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

Image walletIcon = null;
java.net.URL wallet1Url = getClass().getResource("/assets/wallet1.png");
if (wallet1Url != null) {
    walletIcon = new Image(wallet1Url.toExternalForm());
} else {
    System.out.println("❌ Couldn't load /assets/wallet1.png");
}

ImageView walletView = new ImageView(walletIcon);
walletView.setFitWidth(20);
walletView.setFitHeight(20);

HBox budgetHeader = new HBox(10, walletView, budgetHeading);
budgetHeader.setAlignment(Pos.CENTER_LEFT);

// --- Fetch real data ---
double monthlyBudget = 0.0;
double spentThisMonth = 0.0;

try (Connection conn = DBConnection.getConnection()) {
    // Fetch monthly budget
    String budgetQuery = "SELECT monthly_budget FROM budgets WHERE user_id = ?";
    PreparedStatement budgetStmt = conn.prepareStatement(budgetQuery);
    budgetStmt.setInt(1, userId);
    ResultSet budgetRs = budgetStmt.executeQuery();
    if (budgetRs.next()) {
        monthlyBudget = budgetRs.getDouble("monthly_budget");
    }

    // Fetch total spent this month
    String spentQuery = """
        SELECT SUM(amount) FROM expenses
        WHERE user_id = ?
          AND MONTH(expense_date) = MONTH(CURDATE())
          AND YEAR(expense_date) = YEAR(CURDATE())
    """;
    PreparedStatement spentStmt = conn.prepareStatement(spentQuery);
    spentStmt.setInt(1, userId);
    ResultSet spentRs = spentStmt.executeQuery();
    if (spentRs.next()) {
        spentThisMonth = spentRs.getDouble(1);
    }
} catch (Exception e) {
    e.printStackTrace();
}
if (monthlyBudget > 0 && spentThisMonth > monthlyBudget) {
    Platform.runLater(() -> {
        Alert overBudgetAlert = new Alert(Alert.AlertType.WARNING);
        overBudgetAlert.setTitle("Budget Alert");
        overBudgetAlert.setHeaderText(null);
        overBudgetAlert.setContentText("⚠ You’re over budget!");
        overBudgetAlert.showAndWait();
    });
}


// --- Calculate and show progress ---
double progress = (monthlyBudget == 0) ? 0 : spentThisMonth / monthlyBudget;
progress = Math.min(progress, 1.0); // cap at 100%

budgetProgress = new ProgressBar(progress);

budgetProgress.setPrefWidth(950);

// Set bar color dynamically
String color;
if (progress >= 1.0) {
    color = "#FF7E7E"; // Red
} else if (progress >= 0.7) {
    color = "#FFCC84"; // Orange
} else {
    color = "#88DF8C"; // Green
}
budgetProgress.setStyle("-fx-accent: " + color + ";");


Label progressLabel = new Label("₹" + String.format("%.2f", spentThisMonth) + " of ₹" + String.format("%.2f", monthlyBudget) + " spent");
progressLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

// --- Final container ---
VBox budgetBox = new VBox(10, budgetHeader, progressLabel, budgetProgress);
budgetBox.setPadding(new Insets(20, 50, 20, 50));
budgetBox.setPrefWidth(1030);
budgetBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;"
        + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");

// ✅ Don't forget to add budgetBox to your main layout or scene!


// === SMART SAVING TIP ===
Label tipHeading = new Label("💡 Smart Saving Tip");
tipHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");

Label tipLabel = new Label(); // placeholder for dynamic tip
tipLabel.setWrapText(true);
tipLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

// Logic to calculate tip
try (Connection conn = DBConnection.getConnection()) {
    // Step 1: Check if user has any expenses at all
    String checkExpensesQuery = "SELECT COUNT(*) FROM expenses WHERE user_id = ?";
    PreparedStatement checkStmt = conn.prepareStatement(checkExpensesQuery);
    checkStmt.setInt(1, userId);
    ResultSet checkRs = checkStmt.executeQuery();

    if (checkRs.next() && checkRs.getInt(1) == 0) {
        // No expenses at all
        tipLabel.setText("🎉 Just getting started? Add your first expense to unlock personalized saving tips and take control of your finances!");
    } else {
        // Step 2: Check top category excluding fixed ones
        String topQuery = """
            SELECT category, SUM(amount) AS total
            FROM expenses
            WHERE user_id = ? 
              AND MONTH(expense_date) = MONTH(CURDATE()) 
              AND YEAR(expense_date) = YEAR(CURDATE())
              AND category NOT IN ('Education', 'Healthcare', 'Insurance', 'Savings', 'Investment', 'Debt Payment', 'Rent')
            GROUP BY category
            ORDER BY total DESC
            LIMIT 1
        """;

        PreparedStatement stmt = conn.prepareStatement(topQuery);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String topCategoryTip = rs.getString("category");
            double categoryTotal = rs.getDouble("total");

            double percent = (monthlyBudget == 0) ? 0 : (categoryTotal / monthlyBudget) * 100;

            if (percent >= 60) {
                tipLabel.setText("You've spent over " + (int) percent + "% of your budget on " + topCategoryTip +
                    ". Try setting a weekly spending cap or looking for cheaper alternatives to cut back.");
            } else if (percent >= 30) {
                tipLabel.setText("Your " + topCategoryTip + " spending is moderate. Explore discount options, avoid impulse purchases, and track your limits.");
            } else {
                tipLabel.setText("Nice work! You're managing your " + topCategoryTip + " expenses well. Keep it up and redirect savings toward your goals.");
            }
        } else {
            tipLabel.setText("Most of your spending is on fixed essentials this month. Keep tracking your expenses for tailored saving tips.");
        }
    }
} catch (Exception e) {
    e.printStackTrace();
    tipLabel.setText("Unable to generate tips at the moment.");
}



// Add to UI
VBox tipBox = new VBox(5, tipHeading, tipLabel);
tipBox.setPadding(new Insets(20, 50, 20, 50));
tipBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;"
        + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");





        // === RECENT TRANSACTIONS TABLE ===
        Label transactionsHeading = new Label("Recent Transactions");
        transactionsHeading.getStyleClass().add("section-heading");

        TableView<String[]> table = new TableView<>();
String[] columnNames = {"Date", "Category", "Description", "Amount", "Payment Mode"};

for (int i = 0; i < columnNames.length; i++) {
    final int colIndex = i;
    TableColumn<String[], String> col = new TableColumn<>(columnNames[i]);
    col.setPrefWidth(1095.0 / columnNames.length);
    col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[colIndex]));
    col.setStyle("-fx-alignment: center;");
    table.getColumns().add(col);
}

boolean hasData = false;

VBox transactionsBox = new VBox(transactionsHeading, table);
transactionsBox.setPadding(new Insets(20));

try (Connection conn = DBConnection.getConnection()) {
    PreparedStatement ps = conn.prepareStatement(
        "SELECT expense_date, category, item_name, amount, payment_mode " +
        "FROM expenses WHERE user_id = ? ORDER BY expense_date DESC LIMIT 10"
    );
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
        String date = rs.getString("expense_date");
        String category = rs.getString("category");
        String description = rs.getString("item_name");
        String amount = "₹" + String.format("%.2f", rs.getDouble("amount"));
        String payment = rs.getString("payment_mode");

        table.getItems().add(new String[]{date, category, description, amount, payment});
        hasData = true;
    }
} catch (Exception e) {
    e.printStackTrace();
}

// === Show friendly empty state if no data ===
if (!hasData) {
    Label title = new Label("🧾 No Recent Transactions Found");
    title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    Label tip = new Label("Start by adding your first expense! Once you do, your recent transactions will appear here.");
    tip.setStyle("-fx-font-size: 15px; -fx-text-fill: #555555;");
    tip.setWrapText(true);

    VBox emptyStateBox = new VBox(5, title, tip);
    emptyStateBox.setPadding(new Insets(10));
    transactionsBox.getChildren().add(emptyStateBox);
}



        table.setPrefSize(1095, 219);
        table.setStyle("-fx-background-radius: 12;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),6,0,0,2);");


        Label trendLabel = new Label(trendMessage);
trendLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

HBox trendBox = new HBox(trendLabel);
trendBox.setPadding(new Insets(10, 0, 0, 60));  // Adjust left indent

        VBox savingsGoalBox = buildSavingsGoalBox(userId);
        VBox dailySpendingChartBox = buildDailySpendingChart(userId);

   // === COMPACT WELCOME SECTION (NO IMAGES) ===
VBox greetingSection = new VBox(0); // very little space between welcome and encouragement
greetingSection.setAlignment(Pos.TOP_LEFT);
greetingSection.setPadding(new Insets(20, 30,0, 30)); // breathing space from edges

Label welcomeLabel = new Label();
welcomeLabel.setStyle("-fx-font-size: 44px; -fx-font-weight: bold; -fx-text-fill: #333;");

Label encouragementLabel = new Label();
encouragementLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #666; -fx-font-weight: bold; -fx-font-style: italic;");
encouragementLabel.setAlignment(Pos.CENTER_LEFT);

String[] encouragementMessages = {
    "✨ Every rupee you track brings you closer to your goals!",
    "🌱 Start small. Save big.",
    "📊 Track today. Grow tomorrow.",
    "💡 Smart choices = big savings.",
    "🚀 Begin your journey to smarter spending!"
};

Timeline encouragementTimeline = new Timeline();
encouragementTimeline.setCycleCount(Timeline.INDEFINITE);
for (int i = 0; i < encouragementMessages.length; i++) {
    int index = i;
    encouragementTimeline.getKeyFrames().add(
        new KeyFrame(Duration.seconds(i * 4), e -> {
            encouragementLabel.setText(encouragementMessages[index]);
        })
    );
}
encouragementTimeline.play();

// === DB Check ===
boolean isNewUser = false;
String userName = "there";
try (Connection conn = DBConnection.getConnection()) {
    PreparedStatement psName = conn.prepareStatement("SELECT name FROM users WHERE id = ?");
    psName.setInt(1, userId);
    ResultSet rsName = psName.executeQuery();
    if (rsName.next()) userName = rsName.getString("name");

    PreparedStatement psExp = conn.prepareStatement("SELECT COUNT(*) FROM expenses WHERE user_id = ?");
    psExp.setInt(1, userId);
    ResultSet rsExp = psExp.executeQuery();
    noExpenses = rsExp.next() && rsExp.getInt(1) == 0;

    PreparedStatement psBud = conn.prepareStatement("SELECT COUNT(*) FROM budgets WHERE user_id = ?");
    psBud.setInt(1, userId);
    ResultSet rsBud = psBud.executeQuery();
    boolean noBudget = rsBud.next() && rsBud.getInt(1) == 0;

    PreparedStatement psGoal = conn.prepareStatement("SELECT COUNT(*) FROM savings_goal WHERE user_id = ?");
    psGoal.setInt(1, userId);
    ResultSet rsGoal = psGoal.executeQuery();
    boolean noGoal = rsGoal.next() && rsGoal.getInt(1) == 0;

    isNewUser = noExpenses && noBudget && noGoal;
    welcomeLabel.setText(isNewUser ? "Welcome to SmartSpend, " + userName + "! 🎉" : "Welcome back, " + userName + "!");
} catch (Exception e) {
    e.printStackTrace();
    welcomeLabel.setText("Welcome, there!");
}

// === Add welcome + encouragement to VBox ===
greetingSection.getChildren().addAll(welcomeLabel, encouragementLabel);

// === Add tips if new user ===
if (isNewUser) {
    Label tip1 = new Label("✅ Add your first expense");
    Label tip2 = new Label("📊 Set your monthly budget");
    Label tip3 = new Label("🎯 Create a savings goal");
    Label tip4 = new Label("💬 Explore Smart Saving Tips");

    for (Label tip : List.of(tip1, tip2, tip3, tip4)) {
        tip.setStyle("-fx-font-size: 23px; -fx-text-fill: #4C4C4C; -fx-background-color: #F3F3F3; -fx-padding: 6 12 6 12; -fx-background-radius: 8;");
        greetingSection.getChildren().add(tip);
    }
}

Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);

HBox greetingRow = new HBox(20, greetingSection, spacer, buildGifBox());
greetingRow.setAlignment(Pos.TOP_LEFT);



VBox centerContent = new VBox(
    greetingRow,
    alertContainer,
    trendBox,
    cardsBackground,
    chartsSection,
    budgetBox,
    savingsGoalBox,
    dailySpendingChartBox,
    tipBox,
    transactionsBox
);

// Remove global spacing

centerContent.setStyle("-fx-background-color: #E7F8E8;");
// === Reset global VBox spacing and padding ===
centerContent.setSpacing(0);
centerContent.setPadding(new Insets(0)); // no global spacing/padding

// === Remove gap between greetingSection and cardsBackground ===
VBox.setMargin(greetingRow, Insets.EMPTY);
VBox.setMargin(cardsBackground, Insets.EMPTY);

// === Apply 20px top margin between the rest of the sections ===
VBox.setMargin(alertContainer, new Insets(20, 0, 0, 0));
VBox.setMargin(trendBox, new Insets(20, 0, 0, 0));
VBox.setMargin(chartsSection, new Insets(20, 0, 0, 0));
VBox.setMargin(budgetBox, new Insets(20, 0, 0, 0));
VBox.setMargin(savingsGoalBox, new Insets(20, 0, 0, 0));
VBox.setMargin(dailySpendingChartBox, new Insets(20, 0, 0, 0));
VBox.setMargin(tipBox, new Insets(20, 0, 0, 0));
VBox.setMargin(transactionsBox, new Insets(20, 0, 0, 0));



ScrollPane scrollPane = new ScrollPane(centerContent);
scrollPane.setFitToWidth(true);
scrollPane.setStyle("-fx-background: transparent;");
scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

BorderPane rootLayout = new BorderPane();
rootLayout.setLeft(sidebar);
rootLayout.setCenter(scrollPane);

StackPane root = new StackPane(rootLayout);

root.setPrefSize(1440, 1024);

Scene scene = new Scene(root, 1440, 1024);
scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
primaryStage.setScene(scene);
primaryStage.show();
}



    public static VBox buildSidebar(Stage primaryStage, String activePage, int userId) {
        Label logoText = new Label("SmartSpend");
        logoText.getStyleClass().add("logo-text");

        String logoPath = "file:/C:/Users/ASUS/OneDrive/Desktop/smartSpend/assets/login_image.jpg";
        Image logoImage = new Image(logoPath);
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(196);
        logoImageView.setFitHeight(143);

        VBox logoBox = new VBox(logoText, logoImageView);
        logoBox.setAlignment(Pos.TOP_CENTER);
        logoBox.setSpacing(10);
        VBox.setMargin(logoText, new Insets(100, 0, 0, 0));
        VBox.setMargin(logoImageView, new Insets(5, 0, 0, 0));

        VBox navBox = new VBox(30);
        navBox.setPadding(new Insets(50, 0, 0, 40));
        navBox.setAlignment(Pos.TOP_LEFT);

        navBox.getChildren().addAll(
                createNavItem("/assets/dashboard_logo.png", "Dashboard", activePage.equals("Dashboard"), primaryStage, userId),
                createNavItem("/assets/add_expense_logo.png", "Add Expense", activePage.equals("Add Expense"), primaryStage, userId),
                createNavItem("/assets/view_expenses_logo.png", "View Expense", activePage.equals("View Expense"), primaryStage, userId),
                createNavItem("/assets/view_expenses_logo.png", "Monthly\nSummary", activePage.equals("Monthly Summary"), primaryStage, userId),
                createNavItem("/assets/settings_logo.png", "Settings", activePage.equals("Settings"), primaryStage, userId),
                createNavItem("/assets/logout_logo.png", "Logout", false, primaryStage, userId)
        );

        VBox sidebar = new VBox(logoBox, navBox);
        sidebar.setPrefSize(252, 1024);
        sidebar.setStyle("-fx-background-color: #F8F9FA;");
        sidebar.setAlignment(Pos.TOP_CENTER);
        return sidebar;
    }

    private static HBox createNavItem(String iconName, String text, boolean isActive, Stage primaryStage, int userId) {
        String iconPath = "file:/C:/Users/ASUS/OneDrive/Desktop/smartspend/" + iconName;
        Image icon = new Image(iconPath);
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(25);
        iconView.setFitHeight(25);

        Label label = new Label(text);
        label.getStyleClass().add("nav-item");
        if (isActive) {
            label.getStyleClass().add("nav-item-active");
        }

        HBox box = new HBox(10, iconView, label);
        box.setAlignment(Pos.CENTER_LEFT);

        box.setOnMouseClicked(event -> {
            try {
                switch (text) {
                    case "Dashboard":
                        new DashboardPage(userId).start(primaryStage, userId);
                        break;
                    case "Add Expense":
                        new AddExpensePage(userId).start(primaryStage, userId);
                        break;
                    case "View Expense":
                        new ViewExpensesPage(userId).start(primaryStage);
                        break;
                    case "Settings":
                        new SettingsPage(userId).start(primaryStage, userId);
                        break;
                    case "Logout":
                        new LogoutPage(userId).start(primaryStage, userId);
                        break;
                    case "Monthly\nSummary":
                        new MonthlySummaryPage(userId).start(primaryStage);
                        break;
                        
                    default:
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return box;
    }

    private VBox createCard(String topText, String midText, String iconName) {
        Label topLabel = new Label(topText);
        topLabel.getStyleClass().add("card-top");

        Label midLabel = new Label(midText);
        midLabel.getStyleClass().add("card-mid");

        Image icon = new Image("file:/C:/Users/ASUS/OneDrive/Desktop/smartspend/" + iconName);
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(20);
        iconView.setFitHeight(20);

        VBox card = new VBox(topLabel, midLabel, iconView);
        card.setPrefSize(200, 85);
        card.setMaxHeight(85);
        card.setAlignment(Pos.CENTER);
        card.setSpacing(3);
        return card;
    }
    private VBox createCard(String topText, Label midLabel, String iconName) {
    Label topLabel = new Label(topText);
    topLabel.getStyleClass().add("card-top");

    midLabel.getStyleClass().add("card-mid");

    Image icon = new Image("file:/C:/Users/ASUS/OneDrive/Desktop/smartspend/" + iconName);
    ImageView iconView = new ImageView(icon);
    iconView.setFitWidth(20);
    iconView.setFitHeight(20);

    VBox card = new VBox(topLabel, midLabel, iconView);
    card.setPrefSize(200, 85);
    card.setMaxHeight(85);
    card.setAlignment(Pos.CENTER);
    card.setSpacing(3);
    return card;
}

private void updateRemainingBudget(int userId, VBox alertContainer){
    try (Connection conn = DBConnection.getConnection()) {
        // Get budget from DB
        PreparedStatement psBudget = conn.prepareStatement("SELECT monthly_budget FROM budgets WHERE user_id = ?");
        psBudget.setInt(1, userId);
        ResultSet rsBudget = psBudget.executeQuery();
        final double budgetAmount = rsBudget.next() ? rsBudget.getDouble(1) : 0;

        // Get total expenses
        PreparedStatement psExpenses = conn.prepareStatement("SELECT SUM(amount) FROM expenses WHERE user_id = ?");
        psExpenses.setInt(1, userId);
        ResultSet rsExpenses = psExpenses.executeQuery();
        final double totalExpensesValue = rsExpenses.next() ? rsExpenses.getDouble(1) : 0;

        Platform.runLater(() -> {
            double remaining = budgetAmount - totalExpensesValue;
            remainingBudgetLabel.setText("₹" + String.format("%.2f", remaining));
            // === Alert Card Logic ===
alertContainer.getChildren().clear();  // Remove any previous alert
if (budgetAmount > 0 && totalExpensesValue >= budgetAmount) {
    alertContainer.getChildren().add(buildOverBudgetAlertCard());
}


            // Change card color based on budget usage
            if (budgetAmount == 0) {
                card4.setStyle("-fx-background-color: #FFFFFF;");
            } else {
                double percentUsed = totalExpensesValue / budgetAmount;
                if (percentUsed >= 1.0) {
                    card4.setStyle("-fx-background-color: #FFB3B3;"); // red
                } else if (percentUsed >= 0.7) {
                    card4.setStyle("-fx-background-color: #FFEB99;"); // yellow
                } else {
                    card4.setStyle("-fx-background-color: #D9F7BE;"); // green
                }
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
    }
}

private VBox buildSavingsGoalBox(int userId) {
    double savingsGoal = 0;
    final double[] totalSaved = {0};

    try (Connection conn = DBConnection.getConnection()) {
        PreparedStatement ps = conn.prepareStatement("SELECT goal_amount, saved_amount FROM savings_goal WHERE user_id = ?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            savingsGoal = rs.getDouble("goal_amount");
            totalSaved[0] = rs.getDouble("saved_amount");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    Label heading = new Label("Savings Goal Tracker");
    heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");
    if (savingsGoal == 0 && totalSaved[0] == 0) {
        Label emptyTitle = new Label("🎯 No savings goal set yet");
        emptyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    
        Label emptyTip = new Label("Set your first goal to start tracking your future savings!");
        emptyTip.setStyle("-fx-font-size: 15px; -fx-text-fill: #555555;");
        emptyTip.setWrapText(true);
    
        Button setGoalButton = new Button("Set Goal");
        setGoalButton.setPrefWidth(150);
        setGoalButton.setPrefHeight(35);
        setGoalButton.setStyle("-fx-background-color: #88DF8C; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
    
        setGoalButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Set Savings Goal");
            dialog.setHeaderText("Enter your savings goal amount");
            dialog.setContentText("Goal Amount:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    double newGoal = Double.parseDouble(input);
                    try (Connection conn = DBConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO savings_goal (user_id, goal_amount, saved_amount) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE goal_amount = VALUES(goal_amount)"
                        );
                        ps.setInt(1, userId);
                        ps.setDouble(2, newGoal);
                        ps.setDouble(3, totalSaved[0]);
                        ps.executeUpdate();
                    }
    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Savings Goal");
                    alert.setHeaderText(null);
                    alert.setContentText("Goal updated to ₹" + newGoal);
                    alert.showAndWait();
    
                    new DashboardPage(userId).start((Stage) setGoalButton.getScene().getWindow(), userId);
    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
    
        VBox emptyBox = new VBox(10, emptyTitle, emptyTip, setGoalButton);
        emptyBox.setPadding(new Insets(20, 50, 20, 50));
        emptyBox.setPrefWidth(1030);
        emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        return emptyBox;
    }
    

    double progress = (savingsGoal == 0) ? 0 : totalSaved[0] / savingsGoal;
    progress = Math.min(progress, 1.0);

    ProgressBar goalProgress = new ProgressBar(progress);
    goalProgress.setPrefWidth(950);
    String color = progress >= 1.0 ? "#88DF8C" : "#698AB6";
    goalProgress.setStyle("-fx-accent: " + color + ";");

    Label progressLabel = new Label("₹" + String.format("%.2f", totalSaved[0]) + " of ₹" + String.format("%.2f", savingsGoal) + " saved");
    progressLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

    
    Button setGoalButton = new Button("Set Goal");
    setGoalButton.setPrefWidth(150);
    setGoalButton.setMinWidth(150);
    setGoalButton.setMaxWidth(150);
    setGoalButton.setPrefHeight(35);
    setGoalButton.setMinHeight(35);
    setGoalButton.setMaxHeight(35);
    setGoalButton.setStyle("-fx-background-color: #88DF8C; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");


    setGoalButton.setOnAction(e -> {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Savings Goal");
        dialog.setHeaderText("Enter your savings goal amount");
        dialog.setContentText("Goal Amount:");
        dialog.showAndWait().ifPresent(input -> {
            try {
                double newGoal = Double.parseDouble(input);
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO savings_goal (user_id, goal_amount, saved_amount) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE goal_amount = VALUES(goal_amount)"
                    );
                    ps.setInt(1, userId);
                    ps.setDouble(2, newGoal);
                    ps.setDouble(3, totalSaved[0]); // use array
                    ps.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Savings Goal");
                alert.setHeaderText(null);
                alert.setContentText("Goal updated to ₹" + newGoal);
                alert.showAndWait();

                new DashboardPage(userId).start((Stage) goalProgress.getScene().getWindow(), userId);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    });

    VBox box = new VBox(10, heading, progressLabel, goalProgress, setGoalButton);
    box.setPadding(new Insets(20, 50, 20, 50));
    box.setPrefWidth(1030);
    box.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
            + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");

    return box;
}

private VBox buildDailySpendingChart(int userId) {
    CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel("Date");
    
    xAxis.setTickLabelFill(Color.BLACK);
    xAxis.setTickLabelFont(Font.font("Arial",  14));
    

    xAxis.setAutoRanging(true);

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Amount Spent (₹)");
    yAxis.setTickLabelFill(Color.BLACK);
    yAxis.setTickLabelFont(Font.font("Arial", 14));
    
    yAxis.setTickLabelFormatter(new StringConverter<Number>() {
        @Override
        public String toString(Number object) {
            return "₹" + String.format("%.0f", object.doubleValue());
        }

        @Override
        public Number fromString(String string) {
            return Double.parseDouble(string.replace("₹", ""));
        }
    });

    LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Your Spending Trend - Last 7 Days");
    lineChart.setLegendVisible(false);
    lineChart.setPrefSize(950, 250);
    lineChart.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
    lineChart.setAnimated(false);

    XYChart.Series<String, Number> series = new XYChart.Series<>();

    try (Connection conn = DBConnection.getConnection()) {
        String sql = """
            SELECT DATE(expense_date) AS expense_date, SUM(amount) AS total
            FROM expenses
            WHERE user_id = ? AND expense_date >= CURDATE() - INTERVAL 6 DAY
            GROUP BY DATE(expense_date)
            ORDER BY DATE(expense_date)
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        // Prepare date → total map with all 7 days
        Map<String, Double> dateAmountMap = new LinkedHashMap<>();
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("d MMM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dateAmountMap.put(date.format(displayFormat), 0.0);
        }

        while (rs.next()) {
            LocalDate dbDate = rs.getDate("expense_date").toLocalDate();
            String displayDate = dbDate.format(displayFormat);
            double total = rs.getDouble("total");
            dateAmountMap.put(displayDate, total);
        }

        boolean hasData = false;
for (Map.Entry<String, Double> entry : dateAmountMap.entrySet()) {
    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    if (entry.getValue() > 0) hasData = true;
}

if (!hasData) {
    Label emptyChartTitle = new Label("📉 No Spending Trend Yet!");
    emptyChartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    Label emptyChartTip = new Label("Your daily spending graph will appear once you start adding expenses. Let's begin your smart saving journey! 🚀");
    emptyChartTip.setWrapText(true);
    emptyChartTip.setStyle("-fx-font-size: 15px; -fx-text-fill: #555555;");

    VBox chartBox = new VBox(10, emptyChartTitle, emptyChartTip);
    chartBox.setPadding(new Insets(20, 50, 20, 50));
    chartBox.setStyle("-fx-background-color: white; -fx-background-radius: 12;"
            + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");

    return chartBox;
}



    } catch (Exception e) {
        e.printStackTrace();
    }

    lineChart.getData().add(series);
    xAxis.lookup(".axis-label").setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
    yAxis.lookup(".axis-label").setStyle("-fx-font-weight: bold; -fx-font-size: 16;");


    VBox chartBox = new VBox(lineChart);
    chartBox.setPadding(new Insets(20, 50, 20, 50));
    chartBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
            + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");

    return chartBox;
}

private HBox buildOverBudgetAlertCard() {
    ImageView alertIcon = new ImageView(new Image("file:/C:/Users/ASUS/OneDrive/Desktop/smartspend/warning_icon.png"));
    alertIcon.setFitWidth(24);
    alertIcon.setFitHeight(24);

    Label alertLabel = new Label("⚠ You're over budget!");
    alertLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #990000;");

    HBox alertBox = new HBox(10, alertIcon, alertLabel);
    alertBox.setPadding(new Insets(15));
    alertBox.setStyle("-fx-background-color: #FFE6E6; -fx-background-radius: 10; "
            + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 2);");
    alertBox.setAlignment(Pos.CENTER_LEFT);
    alertBox.setPrefWidth(950);

    return alertBox;
}





    public static void main(String[] args) {
        launch(args);
    }
}