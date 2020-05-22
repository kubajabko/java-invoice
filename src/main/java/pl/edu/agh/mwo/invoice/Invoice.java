package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();
    private int invoiceNumber = 0;
    private static int invoiceNumberCount = 0;

    public Invoice() {
        this.invoiceNumber = invoiceNumberCount++;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.put(product, quantity);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public int getNumber() {
        return this.invoiceNumber;
    }

    public void print() {
//        printWriter.println("Kubek, Szt: 2, Cena: 5");
//        printWriter.println("Kozi Serek, Szt: 3, Cena: 10");
//        printWriter.println("Pinezka, Szt: 1000, Cena: 0.01");
//        printWriter.println("Liczba pozycji: 3");
        int itemsCounter = 0;
        System.out.println("Faktura nr: " + this.invoiceNumber);
        for (Product product : products.keySet()) {
            System.out.println(product.getName() + ", Szt: " + products.get(product) + ", Cena: " + product.getPrice());
            itemsCounter++;
        }
        System.out.println("Liczba pozycji: " + itemsCounter);
    }
}
