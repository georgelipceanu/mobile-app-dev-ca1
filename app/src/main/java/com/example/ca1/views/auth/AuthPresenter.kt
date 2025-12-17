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
        if (email.length < 8) {
            view.showError("Password must be at least 8 characters")
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
