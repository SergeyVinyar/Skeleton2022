package ru.vinyarsky.testapplication.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.vinyarsky.testapplication.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}