package src.main.java.com.invoicesystem.InvoiceSystemApplication.Service;

import src.main.java.com.invoicesystem.InvoiceSystemApplication.Entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
        Invoice createInvoice(BigDecimal amount, LocalDate dueDate);
        List<Invoice> getAllInvoices();
        Invoice payInvoice(Long id, BigDecimal amount);
        void processOverdue(BigDecimal lateFee, int overdueDays);
}
