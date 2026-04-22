package com.markethub.order.domain;

import com.markethub.order.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateValidMoney() {
        var money = Money.of(new BigDecimal("10.50"), "BRL");
        assertThat(money.getAmount()).isEqualByComparingTo("10.50");
        assertThat(money.getCurrency()).isEqualTo("BRL");
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1"), "BRL"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectInvalidCurrency() {
        assertThatThrownBy(() -> Money.of(BigDecimal.TEN, "INVALID"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldAddTwoMoneyValues() {
        var a = Money.of(new BigDecimal("10.00"), "BRL");
        var b = Money.of(new BigDecimal("5.50"), "BRL");
        var sum = a.add(b);
        assertThat(sum.getAmount()).isEqualByComparingTo("15.50");
    }

    @Test
    void shouldNotAddDifferentCurrencies() {
        var brl = Money.of(BigDecimal.TEN, "BRL");
        var usd = Money.of(BigDecimal.TEN, "USD");
        assertThatThrownBy(() -> brl.add(usd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different currencies");
    }

    @Test
    void shouldMultiplyByQuantity() {
        var price = Money.of(new BigDecimal("25.00"), "BRL");
        var total = price.multiply(3);
        assertThat(total.getAmount()).isEqualByComparingTo("75.00");
    }

    @Test
    void shouldScaleToTwoDecimalPlaces() {
        var money = Money.of(new BigDecimal("10.999"), "BRL");
        assertThat(money.getAmount().scale()).isEqualTo(2);
    }
}
