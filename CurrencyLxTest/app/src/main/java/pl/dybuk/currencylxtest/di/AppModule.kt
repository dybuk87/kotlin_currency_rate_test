package pl.dybuk.currencylxtest.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import pl.dybuk.currencylxtest.backend.CurrencyRateServiceImpl
import pl.dybuk.currencylxtest.backend.CurrencyRateRetrofit
import pl.dybuk.currencylxtest.backend.CurrencyRateService
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.core.CurrencyRateRepositoryImpl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
open class AppModule(private val application: Application) {
    @Provides
    open fun providesGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    open fun providesRetrofit(gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.exchangeratesapi.io")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    open fun providesCurrencyServicesRetrofit(retrofit: Retrofit) =
        CurrencyRateRetrofit.create(retrofit)

    @Provides
    open fun providesCurrencyServices(retrofit: CurrencyRateRetrofit): CurrencyRateService =
        CurrencyRateServiceImpl(retrofit)

    @Provides
    @Singleton
    open fun providesCurrencyRateRepository(service : CurrencyRateService) : CurrencyRateRepository =
            CurrencyRateRepositoryImpl(service)

}