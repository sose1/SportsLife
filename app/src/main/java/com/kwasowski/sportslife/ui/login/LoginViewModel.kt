package com.kwasowski.sportslife.ui.login

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.UnsupportedApiCallException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class LoginViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<LoginViewState>(LoginViewState.Default)
    val uiState: StateFlow<LoginViewState> = mutableState.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var user: FirebaseUser

    fun onActivityStart() {
        Timber.d("onActivityStart()")
        //jesli zalogowany to przypsiz i włacz main activity jesli nie to wlacz ekran logowania
        auth.currentUser?.let {
            user = it
            Timber.d("USER: ${user.displayName}")
        } ?: Timber.d("USER NULL")
    }

    fun onSignInClick() {
        mutableState.value = LoginViewState.OnSignInButtonClick
    }

    fun onSuccessSignInRequest(credential: SignInCredential) {
        Timber.d("onSuccessSignInRequest()")
        try {
            val idToken = credential.googleIdToken
            val email = credential.id
            val name = credential.displayName

            Timber.d("USER DATA: ")
            Timber.d("idToken: $idToken")
            Timber.d("email: $email")
            Timber.d("name: $name")

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            //DO innej funkcji żeby uporządkować kod
            auth.signInWithCredential(firebaseCredential)
                .addOnSuccessListener {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    Timber.d("USER $user")
                }
                .addOnFailureListener {
                    onSignInFailure(it)
                }

            //onSucces
            mutableState.value = LoginViewState.Default
        } catch (e: ApiException) {
            onSignInFailure(e)
        }
    }

    fun onCancelSignInRequest() {
        Timber.d("onCancelSignInRequest() - Set UI State to Default")
        mutableState.value = LoginViewState.Default
    }

    fun onSignInFailure(exception: Exception) {
        Timber.e(exception)
        when (exception) {
            is ApiException ->
                mutableState.value = LoginViewState.OnSignInConnectionError

            is IntentSender.SendIntentException ->
                mutableState.value = LoginViewState.OnSignInFailure

            is UnsupportedApiCallException ->
                mutableState.value = LoginViewState.OnSignInEmptyGoogleAccountsListError
        }
    }
}