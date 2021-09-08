package com.example.loginapplication.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.loginapplication.R
import com.example.loginapplication.databinding.ForgotPasswordDialogBinding
import com.example.loginapplication.databinding.FragmentLoginBinding
import com.example.loginapplication.util.getColorByAttribute
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            viewModel.loginClicked(
                binding.usernameEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }

        binding.signUpButton.setOnClickListener {
            viewModel.signUpClicked(
                binding.usernameEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }

        binding.forgotPasswordView.setOnClickListener {
            viewModel.forgotPasswordClicked()
        }

        with(lifecycleScope) {
            viewModel.state.onEach { state ->
                Timber.d(state.toString())
                binding.usernameLayout.error = if (state.isEmailValid) {
                    null
                } else {
                    resources.getString(R.string.login_invalid_email)
                }
                binding.passwordLayout.error = if (state.isPasswordValid) {
                    null
                } else {
                    resources.getString(R.string.login_invalid_password)
                }
            }.launchIn(this)

            viewModel.error.onEach {
                Timber.d(it.toString())
                showSnackbar(
                    if (it == LoginErrorType.LOGIN) {
                        resources.getString(R.string.login_failed)
                    } else {
                        resources.getString(R.string.login_error)
                    },
                    requireContext().getColorByAttribute(R.attr.colorError)
                )
            }.launchIn(this)

            viewModel.registerSuccess.onEach {
                Timber.d("Register success!")
                showSnackbar(
                    resources.getString(R.string.login_register_success),
                    requireContext().getColorByAttribute(R.attr.colorPrimary),
                    object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            viewModel.onRegistrationSnackbarDismissed()
                        }
                    }
                )
            }.launchIn(this)

            viewModel.bottomSheetShow.onEach {
                Timber.d("Showing forgot password dialog")
                showForgotBottomSheetDialog()
            }.launchIn(this)

            viewModel.forgotPasswordGetSuccess.onEach {
                Timber.d("Get password success")
                showSnackbar(
                    resources.getString(R.string.login_your_password_is, it),
                    requireContext().getColorByAttribute(R.attr.colorOnSecondary)
                )
            }.launchIn(this)

            viewModel.navigationToApp.onEach {
                findNavController().navigate(R.id.to_loggedIn)
            }.launchIn(this)
        }
    }

    private fun showForgotBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val layoutInflater = LayoutInflater.from(requireContext())
        val forgotPasswordDialogBinding = ForgotPasswordDialogBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(forgotPasswordDialogBinding.root)

        forgotPasswordDialogBinding.submitButton.setOnClickListener {
            viewModel.forgotPasswordSubmitClicked(
                forgotPasswordDialogBinding.usernameEditText.text.toString()
            )
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showSnackbar(
        message: String, backgroundTint: Int,
        callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null
    ) {
        val snackbar = Snackbar
            .make(
                requireContext(),
                binding.root,
                message,
                Snackbar.LENGTH_LONG
            )
            .setBackgroundTint(backgroundTint)

        callback?.apply {
            snackbar.addCallback(this)
        }

        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}