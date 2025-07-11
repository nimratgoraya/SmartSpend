/**
 * Represents an expense entry in SmartSpend.
 */
public class Expense {
    private final String itemName;
    private final String category;
    private final String paymentMode;
    private final double amount;
    private final String date;

    public Expense(String itemName, String category, String paymentMode, double amount, String date) {
        this.itemName = itemName;
        this.category = category;
        this.paymentMode = paymentMode;
        this.amount = amount;
        this.date = date;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
