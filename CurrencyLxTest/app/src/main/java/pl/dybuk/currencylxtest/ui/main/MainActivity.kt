package pl.dybuk.currencylxtest.ui.main

import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import pl.dybuk.currencylxtest.R
import pl.dybuk.currencylxtest.core.CurrencyRateRepository
import pl.dybuk.currencylxtest.core.value.CurrencyType
import pl.dybuk.themoviedb.ui.kernel.bind
import pl.dybuk.themoviedb.ui.kernel.dagger
import pl.dybuk.themoviedb.ui.kernel.getViewModel
import javax.inject.Inject
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    val base: TextView by bind(R.id.base_currency_name)
    val dest: TextView by bind(R.id.dest_currency_name)
    val rate: TextView by bind(R.id.rating_value)
    val offline: TextView by bind(R.id.offline)

    @Inject
    lateinit var repository: CurrencyRateRepository

    fun getFormattedSpannableAmount(textValue: String, size: Float): SpannableString {
        val ss1 = SpannableString(textValue)
        var index = max(textValue.indexOf(","), textValue.indexOf("."))
        if (textValue[0] == '-') index++
        ss1.setSpan(RelativeSizeSpan(size), index, textValue.length, 0)
        return ss1
    }

    private val viewModel: MainActivityViewModel by lazy {
        getViewModel {
            MainActivityViewModel(
                Pair<CurrencyType, CurrencyType>(CurrencyType.PLN, CurrencyType.USD),
                repository
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dagger().inject(this)

        viewModel.state.observe(this, Observer { it?.let { state -> updateView(state) } })
    }

    private fun updateView(state: MainActivityState) {
        Log.i("CURRENCY", "TICK")
        dest.text = state.dest.value
        base.text = state.base.value
        when(state) {
            is MainActivityState.Online -> {
                rate.text = getFormattedSpannableAmount(state.rate.toPlainString(), 0.7f)
                offline.visibility = View.GONE
            }
            is MainActivityState.Offline -> {
                rate.text = getFormattedSpannableAmount("0.00", 0.7f)
                offline.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}