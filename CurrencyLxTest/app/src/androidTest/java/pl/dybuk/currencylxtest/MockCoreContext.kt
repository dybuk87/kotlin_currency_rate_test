package pl.dybuk.currencylxtest

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnitRunner
import dagger.Module
import dagger.Provides
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pl.dybuk.currencylxtest.backend.CurrencyRateService
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.di.AppModule
import pl.dybuk.currencylxtest.di.DaggerAppComponent
import javax.inject.Singleton


@Module
open class TestAppModule(val repository: CurrencyRateRepository, private var application: Application) :
    AppModule(application) {
    @Provides
    @Singleton
    override fun providesCurrencyRateRepository(service: CurrencyRateService): CurrencyRateRepository = repository

}


class MockCoreContext : CoreContext() {
    @Mock
    lateinit var repository: CurrencyRateRepository


    override fun init() {
        MockitoAnnotations.initMocks(this)
        appComponent = DaggerAppComponent
            .builder().appModule(
                TestAppModule(
                    repository, this
                )
            ).build()
    }
}

class MockTestRunner : AndroidJUnitRunner() {
    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MockCoreContext::class.java.name, context)
    }
}