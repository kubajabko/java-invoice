package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Map<Product, Integer> productsWithQuantity = new HashMap<>();

    public void addProduct(Product product) {
        productsWithQuantity.put(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (quantity == 0 || quantity < 0) {
            throw new IllegalArgumentException();
        }
        productsWithQuantity.put(product, quantity);
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = new BigDecimal(0);
        for (Product product : productsWithQuantity.keySet()) {
            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(productsWithQuantity.get(product))));
        }
        return subtotal;
    }

    public BigDecimal getTax() {
        BigDecimal taxesSum = new BigDecimal(0);
        for (Product product : productsWithQuantity.keySet()) {
            taxesSum = taxesSum.add(product.getTaxPercent().multiply(product.getPrice().multiply(BigDecimal.valueOf(productsWithQuantity.get(product)))));
        }
        return taxesSum;
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Product product : productsWithQuantity.keySet()) {
            total = total.add(product.getPriceWithTax().multiply(BigDecimal.valueOf(productsWithQuantity.get(product))));
        }
        return total;
    }
}
