package src.main.java.com.invoicesystem.InvoiceSystemApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdueRequest {
    private BigDecimal lateFee;
    private int overdueDays;
}
