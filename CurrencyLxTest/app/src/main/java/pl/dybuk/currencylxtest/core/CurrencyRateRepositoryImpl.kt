package pl.dybuk.currencylxtest.core

import io.reactivex.Observable
import pl.dybuk.currencylxtest.backend.CurrencyRateService
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.currencylxtest.core.value.ExchangeRate
import pl.dybuk.currencylxtest.ui.kernel.Either
import java.util.concurrent.TimeUnit

class NoData

class CurrencyRateRepositoryImpl(
    private val currencyRateService: CurrencyRateService
) : CurrencyRateRepository {


    override fun getCurrencyRatesContinuously(
        time: Long,
        units: TimeUnit,
        baseCurrency: CurrencyType,
        vararg destCurrencies: CurrencyType
    ): Observable<Either<Throwable, List<ExchangeRate>>> =
        Observable.interval(0, time, units)
            .switchMap {
                getCurrencyRates(baseCurrency, *destCurrencies).map { rates ->
                    Either.Right(rates) as Either<Throwable, List<ExchangeRate>>
                }.onErrorReturn { th -> Either.Left(th) }
            }


    override fun getCurrencyRates(
        baseCurrency: CurrencyType,
        vararg destCurrencies: CurrencyType
    ): Observable<List<ExchangeRate>> =
        currencyRateService.getCurrencyRate(
            baseCurrency.value,
            destCurrencies.joinToString(separator = ",") { it.value }
        )
            .map { response ->
                response.rates.map {
                    ExchangeRate(
                        CurrencyType.create(response.base),
                        CurrencyType.create(it.key),
                        it.value
                    )
                }
            }


}