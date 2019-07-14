package pl.dybuk.currencylxtest.backend

import io.reactivex.Observable
import pl.dybuk.currencylxtest.backend.CurrencyRateRetrofit
import pl.dybuk.currencylxtest.backend.CurrencyRateService
import pl.dybuk.currencylxtest.backend.dto.ExchangeDto

class CurrencyRateServiceImpl(private val retrofit: CurrencyRateRetrofit) : CurrencyRateService {
    override fun getCurrencyRate(baseCurrency: String, destCurrencies: String): Observable<ExchangeDto> =
        retrofit.getCurrencyRate(baseCurrency, destCurrencies)

}