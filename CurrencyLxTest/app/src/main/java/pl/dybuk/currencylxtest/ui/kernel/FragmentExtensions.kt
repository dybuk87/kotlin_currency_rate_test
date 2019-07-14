package pl.dybuk.themoviedb.ui.kernel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import pl.dybuk.currencylxtest.CoreContext


inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

fun Fragment.dagger() = (this.activity?.application as CoreContext).appComponent