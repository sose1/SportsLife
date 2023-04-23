package com.kwasowski.sportslife.ui.login

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityLoginBinding
import com.kwasowski.sportslife.ui.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModel()

    private lateinit var binding: ActivityLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            Timber.d(
                "onActivityResultLauncher(): resultCode: ${result.resultCode}"
            )

            when (result.resultCode) {
                RESULT_OK -> viewModel.onSuccessSignInRequest(
                    oneTapClient.getSignInCredentialFromIntent(
                        result.data
                    )
                )

                RESULT_CANCELED -> viewModel.onCancelSignInRequest()
            }
        }

    override fun onStart() {
        super.onStart()
        viewModel.onActivityStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        window.statusBarColor = getColor(R.color.black)

        onViewStateChanged()
        initSignInClient()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultLauncher.unregister()
        binding.unbind()
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    is LoginViewState.Default -> Unit
                    is LoginViewState.OnSignInButtonClick -> openGoogleSignInActivity()
                    is LoginViewState.OnSignInSuccess -> openMainActivity()
                    is LoginViewState.OnSignInFailure ->
                        showSnackBarInfo(R.string.sign_in_method_problem_please_try_again_later)

                    is LoginViewState.OnSignInConnectionError ->
                        showSnackBarInfo(R.string.network_connection_error_please_try_again_later)

                    is LoginViewState.OnSignInEmptyGoogleAccountsListError ->
                        showSnackBarInfo(R.string.not_found_google_accounts_on_device)

                    is LoginViewState.OnPrivacyPolicyClick -> showAlertDialog(
                        R.string.privacy_policy_title,
                        R.string.privacy_policy_description,
                        viewModel.onPrivacyPolicyClosed()
                    )
                }
            }
        }
    }

    private fun initSignInClient() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()
    }

    private fun openGoogleSignInActivity() {
        Timber.d("openGoogleSignInActivity()")

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    activityResultLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    viewModel.onSignInFailure(e)
                }
            }
            .addOnFailureListener {
                viewModel.onSignInFailure(it)
            }
    }

    private fun openMainActivity() {
        Timber.d("openMainActivity()")
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showSnackBarInfo(stringId: Int) {
        Snackbar.make(binding.root, stringId, Snackbar.LENGTH_LONG).show()
    }

    private fun showAlertDialog(titleId: Int, messageID: Int, positiveButtonAction: Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle(titleId)
            .setMessage(messageID)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                positiveButtonAction
            }.show()
    }
}