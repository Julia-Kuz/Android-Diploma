package ru.netology.diploma.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentSignInBinding
import ru.netology.diploma.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModelSignIn: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signInEnter.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val login = binding.userLogin.text.toString()
                val password = binding.password.text.toString()

                binding.signInEnter.isEnabled = !(login.isEmpty() || password.isEmpty())
            }
        }

        with(binding) {
            userLogin.addTextChangedListener (textWatcher)
            password.addTextChangedListener (textWatcher)
        }


        with(binding) {

            signInEnter.setOnClickListener {
                viewModelSignIn.checkAndSetAuth(
                    userLogin.text.toString(),
                    password.text.toString()
                )
            }
        }

        viewModelSignIn.response.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.mainFragment)
        }

        viewModelSignIn.error.observe(viewLifecycleOwner) {
            Snackbar.make(binding.signInEnter, "", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.userLogin)
                .setTextMaxLines(3)
                .setText("Wrong login or password")
                .setBackgroundTint(android.graphics.Color.rgb(0, 102, 255))
                .show()
        }

        return binding.root
    }
}