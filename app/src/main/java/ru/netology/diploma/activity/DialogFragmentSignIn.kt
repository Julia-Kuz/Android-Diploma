package ru.netology.diploma.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentDialogSignInBinding

class DialogFragmentSignIn : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDialogSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signInRequest.setOnClickListener {
            findNavController().navigate(R.id.action_dialogFragmentSignIn_to_signInFragment)
        }

        binding.signUpRequest.setOnClickListener {
            findNavController().navigate(R.id.action_dialogFragmentSignIn_to_signUpFragment)
        }

        binding.noThankYou.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}