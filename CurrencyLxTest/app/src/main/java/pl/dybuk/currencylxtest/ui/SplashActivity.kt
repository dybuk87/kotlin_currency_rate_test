package pl.dybuk.currencylxtest.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.dybuk.currencylxtest.backend.CurrencyRateRetrofit
import pl.dybuk.currencylxtest.backend.CurrencyRateService
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.currencylxtest.ui.main.MainActivity
import pl.dybuk.themoviedb.ui.kernel.dagger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dagger().inject(this)

        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}