package src.main.java.com.invoicesystem.InvoiceSystemApplication.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Entity.Invoice;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Service.InvoiceService;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.dto.OverdueRequest;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoiceRequest) {
        Invoice invoice = invoiceService.createInvoice(invoiceRequest.getAmount(), invoiceRequest.getDueDate());
        return new ResponseEntity<>(invoice, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<Invoice> payInvoice(@PathVariable Long id, @RequestBody BigDecimal amount) {
        return ResponseEntity.ok(invoiceService.payInvoice(id, amount));
    }

    @PostMapping("/process-overdue")
    public ResponseEntity<Void> processOverdue(@RequestBody OverdueRequest overdueRequest) {
        invoiceService.processOverdue(overdueRequest.getLateFee(), overdueRequest.getOverdueDays());
        return ResponseEntity.ok().build();
    }
}
