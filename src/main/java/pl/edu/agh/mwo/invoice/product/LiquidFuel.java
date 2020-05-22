package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import pl.edu.agh.mwo.invoice.Invoice;

public class LiquidFuel extends Product {
    public LiquidFuel(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23"), new BigDecimal("5.56"));
    }

    @Override
    public BigDecimal getPriceWithTax() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        ParsePosition pp1 = new ParsePosition(0);
        Date transportDay = sdf.parse("26/04", pp1);
        String transportDayStr = sdf.format(transportDay);
        String currentDayStr = sdf.format(Invoice.currentDate);
        if (transportDayStr.equals(currentDayStr)) {
            return getPrice().add(getExcise());
        } else {
            return super.getPriceWithTax();
        }
    }
}
