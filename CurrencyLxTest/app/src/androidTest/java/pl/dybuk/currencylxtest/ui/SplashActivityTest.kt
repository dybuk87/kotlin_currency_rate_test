package pl.dybuk.currencylxtest.ui

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pl.dybuk.currencylxtest.MockCoreContext
import pl.dybuk.currencylxtest.TestAppModule
import pl.dybuk.currencylxtest.any
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.di.DaggerAppComponent
import pl.dybuk.currencylxtest.ui.kernel.Either
import pl.dybuk.currencylxtest.ui.main.MainActivity

// ApplicationProvider.getApplicationContext<CoreContext>(),

@RunWith(AndroidJUnit4ClassRunner::class)
class SplashActivityTest {

    @Rule
    @JvmField
    val splashActivityTestRule = ActivityTestRule<SplashActivity>(SplashActivity::class.java, false, false)

    val repository : CurrencyRateRepository by lazy {
        ApplicationProvider.getApplicationContext<MockCoreContext>().repository
    }


    @Before
    fun initAppModule() {
        MockitoAnnotations.initMocks(this)

        Mockito.`when`(
            repository.getCurrencyRatesContinuously(
                Mockito.anyLong(), any(), any(), any()
            )
        ).thenReturn(
            Observable.just(Either.Left(RuntimeException()))
        )
    }

    @Test
    fun testOpenMainActivity() {
        // Given
        Intents.init()

        // When
        splashActivityTestRule.launchActivity(Intent())

        // Then
        intended(hasComponent(MainActivity::class.java.name))
        Intents.release()
    }


}