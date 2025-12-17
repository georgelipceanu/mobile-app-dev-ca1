package com.example.ca1.views.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.databinding.ActivityAuthBinding
import com.example.ca1.views.cloudjoblist.CloudJobListView
import com.google.android.material.snackbar.Snackbar

class AuthView : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var presenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = AuthPresenter(this)

        binding.btnSignIn.setOnClickListener {
            presenter.doSignIn(
                binding.emailField.text.toString(),
                binding.passwordField.text.toString()
            )
        }
        binding.btnSignUp.setOnClickListener {
            presenter.doSignUp(
                binding.emailField.text.toString(),
                binding.passwordField.text.toString()
            )
        }
    }

    fun showError(message: String) { // ref for chatgpt chat: https://chatgpt.com/c/6942e05e-bd04-8329-ab70-22b9efc587d8
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).setAction("Dismiss", null).show()
    }

    fun navigateToApp() {
        startActivity(Intent(this, CloudJobListView::class.java))
        finish()
    }
}

