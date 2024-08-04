package src.main.java.com.invoicesystem.InvoiceSystemApplication;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Entity.Invoice;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Repositories.InvoiceRepository;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Service.InvoiceService;
import src.main.java.com.invoicesystem.InvoiceSystemApplication.Service.InvoiceServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class InvoiceServiceTests {

	private final InvoiceRepository invoiceRepository = Mockito.mock(InvoiceRepository.class);
	private final InvoiceService invoiceService = new InvoiceServiceImpl(invoiceRepository);

	@Test
	public void testCreateInvoice() {
		Invoice invoice = new Invoice();
		invoice.setAmount(BigDecimal.valueOf(199.99));
		invoice.setDueDate(LocalDate.of(2021, 9, 11));
		invoice.setStatus(InvoiceStatus.PENDING.getStatus());
		Mockito.when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

		Invoice createdInvoice = invoiceService.createInvoice(BigDecimal.valueOf(199.99), LocalDate.of(2021, 9, 11));
		assertNotNull(createdInvoice);
		assertEquals(BigDecimal.valueOf(199.99), createdInvoice.getAmount());
		assertEquals(LocalDate.of(2021, 9, 11), createdInvoice.getDueDate());
		assertEquals(InvoiceStatus.PENDING.getStatus(), createdInvoice.getStatus());
	}

	@Test
	public void testGetAllInvoices() {
		List<Invoice> invoices = new ArrayList<>();
		invoices.add(new Invoice());
		Mockito.when(invoiceRepository.findAll()).thenReturn(invoices);

		List<Invoice> result = invoiceService.getAllInvoices();
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testPayInvoice() {
		Invoice invoice = new Invoice();
		invoice.setId(1234L);
		invoice.setAmount(BigDecimal.valueOf(199.99));
		invoice.setPaidAmount(BigDecimal.valueOf(0));
		invoice.setStatus(InvoiceStatus.PENDING.getStatus());
		Mockito.when(invoiceRepository.findById(1234L)).thenReturn(Optional.of(invoice));
		Mockito.when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

		Invoice paidInvoice = invoiceService.payInvoice(1234L, BigDecimal.valueOf(159.99));
		assertNotNull(paidInvoice);
		assertEquals(BigDecimal.valueOf(159.99), paidInvoice.getPaidAmount());
		assertEquals(InvoiceStatus.PENDING.getStatus(), paidInvoice.getStatus());

		paidInvoice = invoiceService.payInvoice(1234L, BigDecimal.valueOf(40.00));
		assertNotNull(paidInvoice);
		assertEquals(BigDecimal.valueOf(199.99), paidInvoice.getPaidAmount());
		assertEquals(InvoiceStatus.PAID.getStatus(), paidInvoice.getStatus());
	}

	@Test
	public void testProcessOverdue() {
		Invoice invoice1 = new Invoice();
		invoice1.setId(1234L);
		invoice1.setAmount(BigDecimal.valueOf(199.99));
		invoice1.setPaidAmount(BigDecimal.ZERO);
		invoice1.setDueDate(LocalDate.now().minusDays(11));
		invoice1.setStatus(InvoiceStatus.PENDING.getStatus());

		Invoice invoice2 = new Invoice();
		invoice2.setId(1235L);
		invoice2.setAmount(BigDecimal.valueOf(150.00));
		invoice2.setPaidAmount(BigDecimal.valueOf(100.00));
		invoice2.setDueDate(LocalDate.now().minusDays(15));
		invoice2.setStatus(InvoiceStatus.PENDING.getStatus());

		List<Invoice> overdueInvoices = List.of(invoice1, invoice2);
		Mockito.when(invoiceRepository.findByStatus(InvoiceStatus.PENDING.getStatus())).thenReturn(overdueInvoices);
		Mockito.when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

		invoiceService.processOverdue(BigDecimal.valueOf(10.5), 10);

		ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
		Mockito.verify(invoiceRepository, Mockito.times(4)).save(captor.capture());
		List<Invoice> savedInvoices = captor.getAllValues();

		assertEquals(4, savedInvoices.size());

		// Check original invoice1 marked as void
		assertEquals(InvoiceStatus.VOID.getStatus(), savedInvoices.get(0).getStatus());
		// Check new invoice created for invoice1
		assertEquals(BigDecimal.valueOf(210.49), savedInvoices.get(1).getAmount());

		// Check original invoice2 marked as paid
		assertEquals(InvoiceStatus.PAID.getStatus(), savedInvoices.get(2).getStatus());
		// Check new invoice created for invoice2
		assertEquals(BigDecimal.valueOf(60.5), savedInvoices.get(3).getAmount());
	}
}