package ru.netology.diploma.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentDialogSignOutBinding
import ru.netology.diploma.viewmodel.SignInViewModel

class DialogFragmentSignOut : Fragment() {

    private val viewModelSignIn: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDialogSignOutBinding.inflate(
            inflater,
            container,
            false
        )

        binding.noStay.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.yesSignOut.setOnClickListener {
            viewModelSignIn.removeAuth()
            findNavController().navigate(R.id.action_dialogFragmentSignOut_to_mainFragment)
        }
        return binding.root
    }
}