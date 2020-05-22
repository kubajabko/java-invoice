package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    private final BigDecimal excise;

    protected Product(String name, BigDecimal price, BigDecimal tax, BigDecimal excise) {
        if (name == null || name.equals("") || price == null || tax == null
                || tax.compareTo(new BigDecimal(0)) < 0
                || price.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
        this.excise = excise;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getExcise() {
        return excise;
    }

    public BigDecimal getPriceWithTax() {
        return price.multiply(taxPercent).add(price).add(excise);
    }
}
