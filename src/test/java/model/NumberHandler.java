package model;

import com.github.fnwib.databing.valuehandler.ValueHandler;

import java.math.BigDecimal;

public class NumberHandler implements ValueHandler<BigDecimal> {

    private BigDecimal hundred = BigDecimal.TEN.multiply(BigDecimal.TEN);

    @Override
    public BigDecimal convert(BigDecimal value) {
        return value.multiply(hundred);
    }
}
