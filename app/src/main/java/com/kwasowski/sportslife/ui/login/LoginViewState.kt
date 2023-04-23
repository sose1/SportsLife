package com.kwasowski.sportslife.ui.login

sealed class LoginViewState {
    object Default : LoginViewState()
    object OnSignInButtonClick : LoginViewState()
    object OnSignInSuccess : LoginViewState()
    object OnSignInFailure : LoginViewState()
    object OnSignInConnectionError : LoginViewState()
    object OnSignInEmptyGoogleAccountsListError : LoginViewState()
    object OnPrivacyPolicyClick : LoginViewState()
}