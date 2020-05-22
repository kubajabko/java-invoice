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
        boolean productAlreadyExist = false;
        for (Product existingProduct : products.keySet()) {
            if (product.getName().equals(existingProduct.getName())) {
                products.computeIfPresent(existingProduct, (k, v) -> v + quantity);
                productAlreadyExist = true;
            }
        }
        if (!productAlreadyExist) {
            products.put(product, quantity);
        }
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
        int itemsCounter = 0;
        System.out.println("Faktura nr: " + this.invoiceNumber);
        for (Product product : products.keySet()) {
            System.out.println(product.getName() + ", Szt: "
                    + products.get(product) + ", Cena: " + product.getPrice());
            itemsCounter++;
        }
        System.out.println("Liczba pozycji: " + itemsCounter);
    }
}
