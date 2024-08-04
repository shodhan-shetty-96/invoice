package src.main.java.com.invoicesystem.InvoiceSystemApplication.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Entity.Invoice;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(String status);
}
