package pl.rb.manager.model;

public enum UserAction {
    BUY("Buy"), SELL("Sell");

    public static String getZondaUserAction(UserAction userAction) {
        return switch (userAction) {
            case BUY -> "Buy";
            case SELL -> "Sell";
        };
    }

    public static String getBinanceTransactionType(UserAction userAction) {
        return switch (userAction) {
            case BUY -> "0";
            case SELL -> "1";
        };
    }

    private final String value;

    private UserAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
