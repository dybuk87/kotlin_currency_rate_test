package pl.dybuk.currencylxtest.ui.main

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.reactivex.Observable
import org.hamcrest.Matchers.not
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import pl.dybuk.currencylxtest.MockCoreContext
import pl.dybuk.currencylxtest.R
import pl.dybuk.currencylxtest.any
import pl.dybuk.currencylxtest.capture
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.currencylxtest.core.value.CurrencyType.Companion.PLN
import pl.dybuk.currencylxtest.core.value.CurrencyType.Companion.USD
import pl.dybuk.currencylxtest.core.value.ExchangeRate
import pl.dybuk.currencylxtest.ui.kernel.Either
import java.math.BigDecimal
import java.util.concurrent.TimeUnit


@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {

    @Rule
    @JvmField
    val mainActivityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    @Before
    fun initAppModule() {
        MockitoAnnotations.initMocks(this)
    }



    val repository : CurrencyRateRepository by lazy {
        ApplicationProvider.getApplicationContext<MockCoreContext>().repository
    }

    @Test
    fun checkCurrencyListener2SecondTimer() {
        // When
        val timeCaptor: ArgumentCaptor<Long> = ArgumentCaptor.forClass(Long::class.java)
        val timeUnitsCaptor: ArgumentCaptor<TimeUnit> = ArgumentCaptor.forClass(TimeUnit::class.java)
        val base: ArgumentCaptor<CurrencyType> = ArgumentCaptor.forClass(CurrencyType::class.java)
        val src: ArgumentCaptor<CurrencyType> = ArgumentCaptor.forClass(CurrencyType::class.java)

        Mockito.`when`(
            repository.getCurrencyRatesContinuously(
                capture(timeCaptor), capture(timeUnitsCaptor),
                capture(base), capture(src)
            )
        ).thenReturn(
            Observable.just(Either.Left(RuntimeException()))
        )

        // Given
        mainActivityRule.launchActivity(Intent())


        // Then
        Assert.assertEquals(2L, timeCaptor.value)
        Assert.assertEquals(TimeUnit.SECONDS, timeUnitsCaptor.value)
        Assert.assertEquals(CurrencyType.PLN, base.value)
        Assert.assertEquals(CurrencyType.USD, src.value)

        mainActivityRule.finishActivity()
    }


    @Test
    fun checkOfflineViewOnStart() {
        // When
        Mockito.`when`(
            repository.getCurrencyRatesContinuously(
                Mockito.anyLong(), any(), any(), any()
            )
        ).thenReturn(
            Observable.just(Either.Left(RuntimeException()))
        )

        // Given
        mainActivityRule.launchActivity(Intent())


        // Then
        onView(withId(R.id.base_currency_name)).check(matches(withText(PLN.value)))

        onView(withId(R.id.dest_currency_name)).check(matches(withText(USD.value)))

        onView(withId(R.id.rating_value)).check(matches(withText("0.00")))

        onView(withId(R.id.offline))
            .check(matches(withText("Offline")))
            .check(matches(isDisplayed()))

        mainActivityRule.finishActivity()
    }


    @Test
    fun checkOnlineViewOnDataReceived() {
        // When
        Mockito.`when`(
            repository.getCurrencyRatesContinuously(
                Mockito.anyLong(), any(), any(), any()
            )
        ).thenReturn(
            Observable.just(Either.Right(
                listOf(ExchangeRate(PLN, USD, BigDecimal("0.43"))
            ))
        ))

        // Given
        mainActivityRule.launchActivity(Intent())

        // Then
        onView(withId(R.id.base_currency_name)).check(matches(withText(PLN.value)))

        onView(withId(R.id.dest_currency_name)).check(matches(withText(USD.value)))

        onView(withId(R.id.rating_value)).check(matches(withText("0.43")))

        onView(withId(R.id.offline))
            .check(matches(withText("Offline")))
            .check(matches(not(isDisplayed())))

        mainActivityRule.finishActivity()
    }

}