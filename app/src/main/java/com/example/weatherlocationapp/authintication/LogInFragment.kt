package com.example.weatherlocationapp.authintication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.weatherlocationapp.R
import com.example.weatherlocationapp.databinding.FragmentLogInBinding
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {

    private val viewModel : LogInViewModel by viewModels()
    private lateinit var binding : FragmentLogInBinding

    companion object {
        const val TAG = "LogInFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_log_in,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
        binding.loginButton.setOnClickListener {
            launchSignInFlow()
        }
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe( viewLifecycleOwner, Observer {authenticationState ->
            when(authenticationState){
                LogInViewModel.AuthenticationState.AUTHENTICATED -> {
                    navToWeatherLocationsList()
                }
                else -> {
                    Log.e( TAG, "Unauthenticated $authenticationState" )
                    launchSignInFlow()
                }
            }
        })
    }



    private fun launchSignInFlow(){
        // Give users the option to sign in / register with their email or Google account.
        // If users choose to register with their email,
        // they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()

            // This is where you can provide more ways for users to register and
            // sign in.
        )

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.custome_authintication_layout)
            .setEmailButtonId(R.id.email_button)
            .setGoogleButtonId(R.id.google_button)
            .build()

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_REQUEST_CODE
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(providers)
                .build(),
            LogInFragment.SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                navToWeatherLocationsList()
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using the back button. Otherwise check // response.getError().getErrorCode() and handle the error.
                Toast.makeText(requireContext(), "Sign in unsuccessful ${response?.error?.errorCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navToWeatherLocationsList(){
        if(this.findNavController().currentDestination?.id == R.id.logInFragment)
            this.findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToWeatherLocationsListFragment())

    }
}