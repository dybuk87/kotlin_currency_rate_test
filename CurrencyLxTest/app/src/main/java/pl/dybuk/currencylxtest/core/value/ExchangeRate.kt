package pl.dybuk.currencylxtest.core.value

import java.math.BigDecimal

data class ExchangeRate(
    val base: CurrencyType,
    val dest: CurrencyType,
    val rate: BigDecimal
)