package pl.dybuk.currencylxtest

import android.app.Application
import pl.dybuk.currencylxtest.di.AppComponent
import pl.dybuk.currencylxtest.di.DaggerAppComponent
import pl.dybuk.currencylxtest.di.AppModule

open class CoreContext : Application() {
    open lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        init()
    }


    open fun init() {
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()

        appComponent.inject(this)
    }
}