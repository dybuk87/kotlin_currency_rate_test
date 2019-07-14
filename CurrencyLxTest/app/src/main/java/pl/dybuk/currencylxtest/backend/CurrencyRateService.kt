package pl.dybuk.currencylxtest.backend

import io.reactivex.Observable
import pl.dybuk.currencylxtest.backend.dto.ExchangeDto

interface CurrencyRateService {
    fun getCurrencyRate(baseCurrency : String, destCurrencies : String)  : Observable<ExchangeDto>
}