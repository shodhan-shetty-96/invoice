package src.main.java.com.invoicesystem.InvoiceSystemApplication.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Entity.Invoice;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.InvoiceStatus;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Repositories.InvoiceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice createInvoice(BigDecimal amount, LocalDate dueDate) {
        Invoice invoice = new Invoice();
        invoice.setAmount(amount);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setDueDate(dueDate);
        invoice.setStatus(InvoiceStatus.PENDING.getStatus());
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice payInvoice(Long id, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setPaidAmount(invoice.getPaidAmount().add(amount));
        if (invoice.getPaidAmount().compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID.getStatus());
        }
        return invoiceRepository.save(invoice);
    }

    @Override
    public void processOverdue(BigDecimal lateFee, int overdueDays) {
        LocalDate thresholdDate = LocalDate.now().minusDays(overdueDays);
        List<Invoice> overdueInvoices = invoiceRepository.findByStatus(InvoiceStatus.PENDING.getStatus());

        for (Invoice invoice : overdueInvoices) {
            if (invoice.getDueDate().isBefore(thresholdDate)) {
                BigDecimal remainingAmount = invoice.getAmount().subtract(invoice.getPaidAmount());
                Invoice newInvoice = new Invoice();
                newInvoice.setAmount(remainingAmount.add(lateFee));
                newInvoice.setPaidAmount(BigDecimal.ZERO);
                newInvoice.setDueDate(LocalDate.now().plusDays(overdueDays));
                newInvoice.setStatus(InvoiceStatus.PENDING.getStatus());
                invoiceRepository.save(newInvoice);

                if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                    invoice.setStatus("paid");
                } else {
                    invoice.setStatus("void");
                }
                invoiceRepository.save(invoice);
            }
        }
    }
}
