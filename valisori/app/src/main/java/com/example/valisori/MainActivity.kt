package com.example.valisori

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.getSystemService
import com.example.valisori.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var userInput: TextInputLayout
    private val SPEECH_RECOGNIZER_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        setContentView(binding.root)
        userInput = binding.userInputText

        sendDataToFirebaseObserver()
        recordDataFromUserObserver()
    }

    private fun sendDataToFirebaseObserver() {
        //initialise Firebase database and reference
        val database = FirebaseDatabase.getInstance()
        val timestamp = System.currentTimeMillis().toString()
        val myRef = database.getReference("data")

        mainViewModel.onInputButtonClicked.observe(this) { value ->
            if (value) {
                val data = userInput.editText?.text.toString().trim()
                if (data.isNotEmpty()) {
                    val timestamp = System.currentTimeMillis().toString()
                    val childRef = myRef.child(timestamp)
                    childRef.setValue(data)
                    Toast.makeText(
                        this,
                        "Data pushed to server with timestamp $timestamp",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Please enter data", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun recordDataFromUserObserver() {
        mainViewModel.onVoiceButtonClicked.observe(this) { value ->
            if (value) {
                val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
                startActivityForResult(speechRecognizerIntent, SPEECH_RECOGNIZER_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_RECOGNIZER_REQUEST_CODE && resultCode == RESULT_OK) {
            val matches: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.isNotEmpty()) {
                val spokenText = matches[0]
                userInput.editText?.setText(spokenText)
            }
        }
    }







}