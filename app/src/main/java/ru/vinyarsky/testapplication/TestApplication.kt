package ru.vinyarsky.testapplication

import android.app.Application
import android.content.Context
import ru.vinyarsky.testapplication.di.AppComponent
import ru.vinyarsky.testapplication.di.DaggerAppComponent

class TestApplication : Application() {

    val appComponent: AppComponent = DaggerAppComponent.create()
}

val Context.appComponent: AppComponent
    get() = (applicationContext as TestApplication).appComponent