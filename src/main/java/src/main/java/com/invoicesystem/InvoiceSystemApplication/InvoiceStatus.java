package src.main.java.com.invoicesystem.InvoiceSystemApplication;

public enum InvoiceStatus {
    PENDING("pending"),
    PAID("paid"),
    VOID("void");

    private final String status;

    InvoiceStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

