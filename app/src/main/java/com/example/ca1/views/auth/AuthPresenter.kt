package com.example.ca1.views.auth

import com.google.firebase.auth.FirebaseAuth

class AuthPresenter(private val view: AuthView) {

    private val auth = FirebaseAuth.getInstance()

    fun doSignIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            view.showError("Email and password required")
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { // ref: https://firebase.google.com/docs/auth/android/password-auth, https://developers.google.com/android/reference/com/google/android/gms/tasks/Task
                view.navigateToApp()
            }
            .addOnFailureListener {
                view.showError(it.message ?: "Sign in failed")
            }
    }

    fun doSignUp(email: String, password: String) {
        val passRegEx = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\$") // ref to chatgpt chat: https://chatgpt.com/share/6942f5b3-1b98-8013-8cec-ac59c2c662ca
        // if (password.length < 8) { // for demo only
        if (passRegEx.matches(password)) {
            view.showError("Password must be at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character")
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                view.navigateToApp()
            }
            .addOnFailureListener {
                view.showError(it.message ?: "Sign up failed")
            }
    }
}
