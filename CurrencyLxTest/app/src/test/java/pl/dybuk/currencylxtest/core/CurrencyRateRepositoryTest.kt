package pl.dybuk.currencylxtest.core

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import pl.dybuk.currencylxtest.TestSchedulerRule
import pl.dybuk.currencylxtest.backend.CurrencyRateService
import pl.dybuk.currencylxtest.backend.dto.ExchangeDto
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.currencylxtest.core.value.ExchangeRate
import pl.dybuk.currencylxtest.ui.kernel.Either
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


@RunWith(MockitoJUnitRunner::class)
class CurrencyRateRepositoryTest {

    @Mock
    lateinit var currencyRateService: CurrencyRateService

    @Rule
    @JvmField
    var testSchedulerRule = TestSchedulerRule()

    @Test
    public fun currencyOneTime() {
        // GIVEN
        val repository = CurrencyRateRepositoryImpl(currencyRateService)

        val exchangeAUDDto = ExchangeDto(
            CurrencyType.AUD.value, "20019", mapOf(
                Pair(CurrencyType.EUR.value, BigDecimal.TEN),
                Pair(CurrencyType.PLN.value, BigDecimal.ONE)
            )
        )

        val exchangeAUDParsed = exchangeAUDDto.rates.map {
            ExchangeRate(
                CurrencyType.create(exchangeAUDDto.base),
                CurrencyType.create(it.key),
                it.value
            )
        }


        // WHEN
        Mockito.`when`(
            currencyRateService.getCurrencyRate(
                CurrencyType.AUD.value,
                CurrencyType.EUR.value + "," + CurrencyType.PLN.value
            )
        )
            .thenReturn(Observable.just(exchangeAUDDto))


        val observer = TestObserver<List<ExchangeRate>>()

        repository.getCurrencyRates(CurrencyType.AUD, CurrencyType.EUR, CurrencyType.PLN)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(observer)

        testSchedulerRule.advanceTimeBy(20, TimeUnit.SECONDS)


        // THEN
        observer.assertComplete()
        observer.assertValueCount(1)

        observer.assertValue { it -> it == exchangeAUDParsed }

    }


    @Test
    public fun currencyContinuously() {
        // GIVEN
        val repository = CurrencyRateRepositoryImpl(currencyRateService)

        val counter = AtomicInteger(1)

        // this will change return value depend on fetch count, this will allow,
        // to verify fetching data with timer
        Mockito.`when`(
            currencyRateService.getCurrencyRate(
                CurrencyType.AUD.value,
                CurrencyType.EUR.value + "," + CurrencyType.PLN.value
            )
        )
            .thenReturn(
                Observable.fromCallable {
                    ExchangeDto(
                        CurrencyType.AUD.value, "20019", mapOf(
                            Pair(CurrencyType.EUR.value, BigDecimal(2 * counter.get())),
                            Pair(CurrencyType.PLN.value, BigDecimal(3 * counter.getAndIncrement()))
                        )
                    )
                }
            )


        // WHEN
        val observer = TestObserver<Either<Throwable, List<ExchangeRate>>>()


        repository.getCurrencyRatesContinuously(
            10, TimeUnit.SECONDS,
            CurrencyType.AUD, CurrencyType.EUR, CurrencyType.PLN
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(observer)


        // 25 sec should call timer 3 times ( 0, 10, 20 second)
        testSchedulerRule.advanceTimeBy(25, TimeUnit.SECONDS)



        observer.assertNotComplete()
        observer.assertValueCount(3)

        for (i in 0 until 3) {
            observer.assertValueAt(i) {
                it.either(
                    {
                        false
                    },
                    { rates ->
                        rates == listOf(
                            ExchangeRate(CurrencyType.AUD, CurrencyType.EUR, BigDecimal(2 * (i + 1))),
                            ExchangeRate(CurrencyType.AUD, CurrencyType.PLN, BigDecimal(3 * (i + 1)))
                        )
                    }) as Boolean
            }
        }
    }

    @Test
    public fun currencyContinuouslyOnFail() {
        // GIVEN
        val repository = CurrencyRateRepositoryImpl(currencyRateService)

        Mockito.`when`(
            currencyRateService.getCurrencyRate(
                CurrencyType.AUD.value,
                CurrencyType.EUR.value + "," + CurrencyType.PLN.value
            )
        )
            .thenReturn(
                Observable.fromCallable {
                    throw RuntimeException("TEST_ERROR")
                }
            )


        // WHEN
        val observer = TestObserver<Either<Throwable, List<ExchangeRate>>>()


        repository.getCurrencyRatesContinuously(
            10, TimeUnit.SECONDS,
            CurrencyType.AUD, CurrencyType.EUR, CurrencyType.PLN
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(observer)


        // 25 sec should call timer 3 times ( 0, 10, 20 second)
        testSchedulerRule.advanceTimeBy(25, TimeUnit.SECONDS)



        observer.assertNotComplete()
        observer.assertValueCount(3)


    }



}