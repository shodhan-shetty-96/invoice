package src.main.java.com.invoicesystem.InvoiceSystemApplication.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private String status; // "pending", "paid", "void"
}