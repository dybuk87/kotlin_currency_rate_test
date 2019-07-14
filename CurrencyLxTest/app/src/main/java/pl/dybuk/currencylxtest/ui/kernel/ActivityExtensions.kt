package pl.dybuk.themoviedb.ui.kernel

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import pl.dybuk.currencylxtest.CoreContext


fun AppCompatActivity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

fun <T : View> AppCompatActivity.bind(@IdRes res : Int) : Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy { findViewById<T>(res) }
}

fun <T : View> Fragment.bind(root : View?, @IdRes res : Int) : Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy { root?.findViewById<T>(res)!! }
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}


class BaseViewModelFactory<T> (val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }

}


fun AppCompatActivity.dagger() = (this.application as CoreContext).appComponent
