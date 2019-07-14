package pl.dybuk.currencylxtest.core

import io.reactivex.Observable
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.currencylxtest.core.value.ExchangeRate
import pl.dybuk.currencylxtest.ui.kernel.Either
import java.util.concurrent.TimeUnit

interface CurrencyRateRepository {
    fun getCurrencyRates(baseCurrency: CurrencyType, vararg destCurrencies: CurrencyType): Observable<List<ExchangeRate>>

    fun getCurrencyRatesContinuously(
        time : Long, units : TimeUnit,
        baseCurrency: CurrencyType, vararg destCurrencies: CurrencyType): Observable<Either<Throwable, List<ExchangeRate>>>
}