package pl.dybuk.currencylxtest.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.currencylxtest.core.value.ExchangeRate
import pl.dybuk.currencylxtest.ui.kernel.Either
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

sealed class MainActivityState {
    abstract val base: CurrencyType
    abstract val dest: CurrencyType

    data class Offline(
        override val base: CurrencyType,
        override val dest: CurrencyType,
        val exception: Throwable? = null,
        val errorMessage: String? = null
    ) : MainActivityState()

    data class Online(
        override val base: CurrencyType,
        override val dest: CurrencyType,
        val rate: BigDecimal
    ) : MainActivityState()

}

class MainActivityViewModel(
    currencyPair: Pair<CurrencyType, CurrencyType>,
    private val currencyRateRepository: CurrencyRateRepository
) : ViewModel() {

    val state: MutableLiveData<MainActivityState> = MutableLiveData()

    private var listener: Disposable? = null

    init {
        state.value = MainActivityState.Offline(currencyPair.first, currencyPair.second)
    }

    fun onResume() {
        subscribe(state.value ?: MainActivityState.Offline(CurrencyType.PLN, CurrencyType.USD))
    }

    fun onPause() {
        unsubscribe()
    }

    private fun unsubscribe() {
        listener?.dispose()
        listener = null
    }

    private fun reduceState(
        mainActivityState: MainActivityState,
        result: Either<Throwable, List<ExchangeRate>>
    ): MainActivityState =
        result.either(
            {
                MainActivityState.Offline(
                    mainActivityState.base,
                    mainActivityState.dest,
                    exception = it
                )
            },
            { rates ->
                MainActivityState.Online(
                    mainActivityState.base, mainActivityState.dest,
                    rates.firstOrNull { it.base == mainActivityState.base && it.dest == mainActivityState.dest }?.rate
                        ?: BigDecimal.ZERO
                )
            }) as MainActivityState


    private fun subscribe(mainActivityState: MainActivityState) {
        unsubscribe()

        listener = currencyRateRepository.getCurrencyRatesContinuously(
            2, TimeUnit.SECONDS,
            mainActivityState.base, mainActivityState.dest
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->

                state.value = reduceState(mainActivityState, result)

            }, {
                state.value = MainActivityState.Offline(mainActivityState.base, mainActivityState.dest, exception = it)
            })
    }


}