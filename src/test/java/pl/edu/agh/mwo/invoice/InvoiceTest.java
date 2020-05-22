package pl.edu.agh.mwo.invoice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.*;

import static org.junit.Assert.assertEquals;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test
    public void testInvoiceHasNumber() {
        int number = invoice.getNumber();
        Assert.assertTrue(number > 0);
    }

    @Test
    public void testTwoInvoicesHaveDifferentNumbers() {
        int number = invoice.getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertNotEquals(number, number2);
    }

    @Test
    public void testTwoInvoicesHaveConsequentNumbers() {
        int number = invoice.getNumber();
        int number2 = new Invoice().getNumber();
        assertEquals(number, number2 - 1);
    }
    @Test
    public void testInvoicePrint() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        invoice.print();
        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(expectedStringWriter);
        printWriter.println("Faktura nr: " + invoice.getNumber());
        printWriter.println("Kubek, Szt: 2, Cena: 5");
        printWriter.println("Kozi Serek, Szt: 3, Cena: 10");
        printWriter.println("Pinezka, Szt: 1000, Cena: 0.01");
        printWriter.println("Liczba pozycji: 3");
        printWriter.close();
        String expected = expectedStringWriter.toString();
        assertEquals(expected.trim(), outContent.toString().trim());
    }
    @Test
    public void testInvoicePrintDuplicates() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 3);
        invoice.print();
        StringWriter expectedStringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(expectedStringWriter);
        printWriter.println("Faktura nr: " + invoice.getNumber());
        printWriter.println("Kubek, Szt: 5, Cena: 5");
        printWriter.println("Kozi Serek, Szt: 3, Cena: 10");
        printWriter.println("Pinezka, Szt: 1000, Cena: 0.01");
        printWriter.println("Liczba pozycji: 3");
        printWriter.close();
        String expected = expectedStringWriter.toString();
        assertEquals(expected.trim(), outContent.toString().trim());
    }
    @Test
    public void testBottleOfWine() throws  Exception {
        invoice.addProduct(new Alcohol("Wino", new BigDecimal("20")), 2);
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 1);
        Assert.assertThat(new BigDecimal("65.32"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }
    @Test
    public void testFuelCanisterDateOK() throws  Exception {
        invoice.addProduct(new LiquidFuel("KanisterBenzyny", new BigDecimal("100")), 1);
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        ParsePosition pp1 = new ParsePosition(0);
        Date TransportDayDate = sdf.parse("26/04", pp1);
        invoice.setInvoiceDate(TransportDayDate);
        Assert.assertThat(new BigDecimal("110.56"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }
    @Test
    public void testFuelCanisterDateNOK() throws  Exception {
        invoice.addProduct(new LiquidFuel("KanisterBenzyny", new BigDecimal("100")), 1);
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        ParsePosition pp1 = new ParsePosition(0);
        Date DefinitelyNotTransportDayDate = sdf.parse("05/05", pp1);
        invoice.setInvoiceDate(DefinitelyNotTransportDayDate);
        Assert.assertThat(new BigDecimal("133.56"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }
}
