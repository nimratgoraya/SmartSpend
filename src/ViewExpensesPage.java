import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;

public class ViewExpensesPage extends Application {

    private int userId;

    public ViewExpensesPage(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        Pagination pagination = new Pagination();

        int rowsPerPage = 15;

        primaryStage.setTitle("SmartSpend - View Expenses");

        VBox sidebar = DashboardPage.buildSidebar(primaryStage, "View Expense", userId);

        Label heading = new Label("View Expenses");
        heading.setFont(Font.font("Montserrat", 40));
        heading.setUnderline(true);
        heading.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        HBox headingBox = new HBox(heading);
        headingBox.setAlignment(Pos.CENTER);

        HBox filterBar = new HBox(20);
        filterBar.setPadding(new Insets(10, 20, 10, 20));
        filterBar.setPrefSize(1000, 84);
        filterBar.setStyle("-fx-background-color: #FFF7CD; -fx-background-radius: 8;"
                + "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1),3,0,0,1);");

        VBox categoryFilter = new VBox(5);
        Label categoryLabel = new Label("Category");
        categoryLabel.setFont(Font.font("Inter", 20));
        categoryLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
                "All", "Groceries", "Rent", "Transport", "Entertainment", "Utilities", "Others"
        );
        categoryCombo.setValue("All");
        categoryCombo.setPrefSize(150, 35);
        categoryCombo.setStyle("-fx-background-color: white; -fx-border-color: grey; -fx-background-radius:8px;");
        categoryFilter.getChildren().addAll(categoryLabel, categoryCombo);

        VBox dateFilter = new VBox(5);
        Label dateLabel = new Label("Date");
        dateLabel.setFont(Font.font("Inter", 20));
        dateLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefSize(180, 35);
        datePicker.setStyle("-fx-background-color: white; -fx-border-color: grey; -fx-background-radius:8px;");
        dateFilter.getChildren().addAll(dateLabel, datePicker);

        VBox sortFilter = new VBox(5);
        Label sortLabel = new Label("Sort By");
        sortLabel.setFont(Font.font("Inter", 20));
        sortLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("None", "Amount Ascending", "Amount Descending", "Date Ascending", "Date Descending");
        sortCombo.setValue("None");
        sortCombo.setPrefSize(180, 35);
        sortCombo.setStyle("-fx-background-color: white; -fx-border-color: grey; -fx-background-radius:8px;");
        sortFilter.getChildren().addAll(sortLabel, sortCombo);

        StackPane searchStack = new StackPane();
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefSize(270, 45);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: grey; -fx-background-radius:8px;");
        Image searchIcon = new Image("file:/C:/Users/ASUS/OneDrive/Desktop/smartspend/assets/search.png");
        ImageView searchIconView = new ImageView(searchIcon);
        searchIconView.setFitWidth(20);
        searchIconView.setFitHeight(20);
        StackPane.setAlignment(searchIconView, Pos.CENTER_RIGHT);
        StackPane.setMargin(searchIconView, new Insets(0, 10, 0, 0));
        searchStack.getChildren().addAll(searchField, searchIconView);

        Button exportBtn = new Button("Export");
        exportBtn.setPrefSize(100, 45);
        exportBtn.setStyle("-fx-background-color: #92AAD1; -fx-text-fill: white; -fx-font-size: 20px;");

        Button printBtn = new Button("Print");
        printBtn.setPrefSize(100, 45);
        printBtn.setStyle("-fx-background-color: #56C45B; -fx-text-fill: white; -fx-font-size: 20px;");

        filterBar.getChildren().addAll(categoryFilter, dateFilter, sortFilter, searchStack, exportBtn, printBtn);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TableView<Expense> table = new TableView<>();
        table.setPrefSize(1026, 500);
        table.setStyle("-fx-border-color: grey; -fx-border-width: 1; -fx-border-radius: 8px;");
        table.setFixedCellSize(30);

        TableColumn<Expense, String> itemCol = new TableColumn<>("Item name");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, String> paymentCol = new TableColumn<>("Payment Mode");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMode"));

        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Expense, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setMinWidth(50);
                editBtn.setPrefHeight(20);
                editBtn.setStyle("-fx-background-color: #92AAD1; -fx-text-fill: white; -fx-font-size: 12px;");
                deleteBtn.setMinWidth(50);
                deleteBtn.setPrefHeight(20);
                deleteBtn.setStyle("-fx-background-color: #FF7E7E; -fx-text-fill: white; -fx-font-size: 12px;");
                HBox buttons = new HBox(5, editBtn, deleteBtn);
                buttons.setAlignment(Pos.CENTER);
                setGraphic(buttons);

                deleteBtn.setOnAction(e -> {
                    Expense exp = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Delete");
                    alert.setHeaderText("Delete Expense");
                    alert.setContentText("Are you sure you want to delete " + exp.getItemName() + "?");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try (var conn = DBConnection.getConnection()) {
                                var stmt = conn.prepareStatement("DELETE FROM expenses WHERE item_name=? AND user_id=?");
                                stmt.setString(1, exp.getItemName());
                                stmt.setInt(2, userId);
                                stmt.executeUpdate();
                                getTableView().getItems().remove(exp);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                });

                editBtn.setOnAction(e -> {
                    Expense exp = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(String.valueOf(exp.getAmount()));
                    dialog.setTitle("Edit Amount");
                    dialog.setHeaderText("Edit amount for " + exp.getItemName());
                    dialog.setContentText("New Amount:");
                    dialog.showAndWait().ifPresent(newAmountStr -> {
                        try {
                            double newAmount = Double.parseDouble(newAmountStr);
                            try (var conn = DBConnection.getConnection()) {
                                var stmt = conn.prepareStatement("UPDATE expenses SET amount=? WHERE item_name=? AND user_id=?");
                                stmt.setDouble(1, newAmount);
                                stmt.setString(2, exp.getItemName());
                                stmt.setInt(3, userId);
                                stmt.executeUpdate();
                                exp.amount = newAmount;
                                table.refresh();
                            }
                        } catch (NumberFormatException ex1) {
                            System.out.println("❌ Invalid number");
                        } catch (Exception ex2) {
                            ex2.printStackTrace();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : getGraphic());
            }
        });

        itemCol.prefWidthProperty().bind(table.widthProperty().divide(6));
        categoryCol.prefWidthProperty().bind(table.widthProperty().divide(6));
        amountCol.prefWidthProperty().bind(table.widthProperty().divide(6));
        paymentCol.prefWidthProperty().bind(table.widthProperty().divide(6));
        dateCol.prefWidthProperty().bind(table.widthProperty().divide(6));
        actionCol.prefWidthProperty().bind(table.widthProperty().divide(6));

        table.getColumns().addAll(itemCol, categoryCol, amountCol, paymentCol, dateCol, actionCol);

        ObservableList<Expense> expenses = FXCollections.observableArrayList();
        try (var conn = DBConnection.getConnection()) {
            var stmt = conn.prepareStatement("SELECT * FROM expenses WHERE user_id=?");
            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("payment_mode"),
                        rs.getDate("expense_date").toString()
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FilteredList<Expense> filtered = new FilteredList<>(expenses, p -> true);
        SortedList<Expense> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        
        pagination.setPageCount((int) Math.ceil((double) filtered.size() / rowsPerPage));


        searchField.textProperty().addListener((obs, oldVal, newVal) -> 
    updateFilters(filtered, categoryCombo, datePicker, searchField, pagination, sorted, rowsPerPage));

categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> 
    updateFilters(filtered, categoryCombo, datePicker, searchField, pagination, sorted, rowsPerPage));

datePicker.valueProperty().addListener((obs, oldVal, newVal) -> 
    updateFilters(filtered, categoryCombo, datePicker, searchField, pagination, sorted, rowsPerPage));

        sortCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case "Amount Ascending" -> table.getSortOrder().setAll(amountCol);
                case "Amount Descending" -> { table.getSortOrder().setAll(amountCol); amountCol.setSortType(TableColumn.SortType.DESCENDING); }
                case "Date Ascending" -> table.getSortOrder().setAll(dateCol);
                case "Date Descending" -> { table.getSortOrder().setAll(dateCol); dateCol.setSortType(TableColumn.SortType.DESCENDING); }
                default -> table.getSortOrder().clear();
            }
        });

        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Expenses");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("expenses.csv");
            var file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Item Name,Category,Amount,Payment Mode,Date\n");
                    for (Expense exp : table.getItems()) {
                        writer.write(String.format("%s,%s,%.2f,%s,%s\n",
                                exp.getItemName(), exp.getCategory(), exp.getAmount(), exp.getPaymentMode(), exp.getDate()));
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        printBtn.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(primaryStage)) {
                job.printPage(table);
                job.endJob();
            }
        });

       
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * rowsPerPage;
            int toIndex = Math.min(fromIndex + rowsPerPage, sorted.size());
        
            ObservableList<Expense> currentPageData = FXCollections.observableArrayList(
                    sorted.subList(fromIndex, toIndex)
            );
            table.setItems(currentPageData);
        
            return new VBox(table);
        });
        // Update pagination page count when filtered list changes
filtered.addListener((ListChangeListener<Expense>) change -> {
    int pageCount = (int) Math.ceil((double) filtered.size() / rowsPerPage);
    pagination.setPageCount(Math.max(pageCount, 1));
    pagination.setCurrentPageIndex(0); // Go back to first page
    pagination.setPageFactory(pageIndex -> {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filtered.size());
        ObservableList<Expense> currentPageData = FXCollections.observableArrayList(
            filtered.subList(fromIndex, toIndex)
        );
        table.setItems(currentPageData);
        return new VBox(table);
    });
    
});

        

        VBox centerContent = new VBox(headingBox, filterBar, pagination);
        centerContent.setSpacing(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setStyle("-fx-background-color: white;");

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

    private void updateFilters(FilteredList<Expense> filtered,
                           ComboBox<String> categoryCombo,
                           DatePicker datePicker,
                           TextField searchField,
                           Pagination pagination,
                           SortedList<Expense> sorted,
                           int rowsPerPage) {
    filtered.setPredicate(exp -> {
        // Get filter values
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryCombo.getValue();
        var selectedDate = datePicker.getValue();

        // Category filter
        boolean categoryMatch = selectedCategory == null || selectedCategory.equals("All") ||
                exp.getCategory().equalsIgnoreCase(selectedCategory);

        // Date filter
        boolean dateMatch = selectedDate == null ||
                exp.getDate().equals(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Search filter (searching in multiple fields)
        boolean searchMatch = search.isEmpty() ||
    exp.getItemName().toLowerCase().contains(search) ||
    exp.getCategory().toLowerCase().contains(search) ||
    exp.getPaymentMode().toLowerCase().contains(search);


        // Final condition: all 3 must match
        return categoryMatch && dateMatch && searchMatch;
    });

    // Update pagination count and reset to page 0
    int pageCount = (int) Math.ceil((double) filtered.size() / rowsPerPage);
    pagination.setPageCount(Math.max(pageCount, 1));
    pagination.setCurrentPageIndex(0);
}



    public static class Expense {
        private String itemName;
        private String category;
        private double amount;
        private String paymentMode;
        private String date;

        public Expense(String itemName, String category, double amount, String paymentMode, String date) {
            this.itemName = itemName;
            this.category = category;
            this.amount = amount;
            this.paymentMode = paymentMode;
            this.date = date;
        }
        public String getItemName() { return itemName; }
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
        public String getPaymentMode() { return paymentMode; }
        public String getDate() { return date; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
