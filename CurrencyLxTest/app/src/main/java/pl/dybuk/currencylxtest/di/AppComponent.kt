package pl.dybuk.currencylxtest.di

import dagger.Component
import pl.dybuk.currencylxtest.CoreContext
import pl.dybuk.currencylxtest.ui.SplashActivity
import pl.dybuk.currencylxtest.ui.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(coreContext: CoreContext)
    fun inject(coreContext: SplashActivity)
    fun inject(mainActivity: MainActivity)

}