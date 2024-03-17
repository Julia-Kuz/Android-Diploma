package ru.netology.diploma.activity

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentMainBinding
import ru.netology.diploma.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModelAuth: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(
            inflater,
            container,
            false
        )

        val bottomNavigationView = binding.bottomNavigation

        bottomNavigationView.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.posts -> {
                    findNavController().navigate(R.id.action_mainFragment_to_allPostsFragment)
                    true
                }
                R.id.events -> {
                    findNavController().navigate(R.id.action_mainFragment_to_allEventsFragment)
                    true
                }
                R.id.users -> {
                    findNavController().navigate(R.id.action_mainFragment_to_allUsersFragment)
                    true
                }
                else -> false
            }
        }

        val toolbar: Toolbar = binding.toolbarMain
        toolbar.overflowIcon?.colorFilter = BlendModeColorFilter(
            Color.WHITE,
            BlendMode.SRC_ATOP
        ) // чтобы три точки стали белыми - (!) Color д. использоваться из android.graphics, а не из androidx.compose.ui.graphics


        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { //наблюдаем за авторизацией, только когда активити/фрагмент доступен для взаимодействия
                viewModelAuth.dataAuth.collect {
                    toolbar.invalidateMenu()
                }
            }
        }

        toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_login, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModelAuth.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModelAuth.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
                        true
                    }

                    R.id.signup -> {
                        findNavController().navigate(R.id.action_mainFragment_to_signUpFragment)
                        true
                    }

                    R.id.signout -> {
                        findNavController().navigate(R.id.action_mainFragment_to_dialogFragmentSignOut)
                        true
                    }

                    else -> false
                }
        })


        return binding.root
    }
}

