package com.example.apptest2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.my_text)
        val button = findViewById<Button>(R.id.my_button)

        button.setOnClickListener {
            text.text = "버튼이 눌렸습니다!"
        }
    }
}
