package ru.vinyarsky.testapplication.di

import androidx.lifecycle.ViewModelProvider
import dagger.Component
import ru.vinyarsky.testapplication.data.NetworkService
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    val networkService: NetworkService

    val viewModelFactory: ViewModelProvider.Factory
}