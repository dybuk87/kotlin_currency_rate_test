package pl.dybuk.currencylxtest.backend.dto

import java.math.BigDecimal

data class ExchangeDto(
    val base : String,
    val date : String,
    val rates : Map<String, BigDecimal>
)