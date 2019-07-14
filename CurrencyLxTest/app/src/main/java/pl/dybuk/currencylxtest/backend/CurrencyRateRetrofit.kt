package pl.dybuk.currencylxtest.backend

import io.reactivex.Observable
import pl.dybuk.currencylxtest.backend.dto.ExchangeDto
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRateRetrofit {

    @GET("latest")
    fun getCurrencyRate(
        @Query("base") baseCurrency : String,
        @Query("symbols") destCurrencies : String)  : Observable<ExchangeDto>

    companion object {
        fun create(retrofit: Retrofit) : CurrencyRateRetrofit {
            return retrofit.create(CurrencyRateRetrofit::class.java)
        }
    }
}