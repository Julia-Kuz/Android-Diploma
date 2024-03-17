package ru.netology.diploma.activity.events

import ru.netology.diploma.activity.MediaLifecycleObserver
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
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.format
import ru.netology.diploma.R
import ru.netology.diploma.adapter.EventAdapter
import ru.netology.diploma.adapter.OnInteractionListenerEvent
import ru.netology.diploma.databinding.FragmentAllEventsBinding
import ru.netology.diploma.dto.Event
import ru.netology.diploma.util.EventDealtWith
import ru.netology.diploma.viewmodel.AuthViewModel
import ru.netology.diploma.viewmodel.EventViewModel

@AndroidEntryPoint
class AllEventsFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()

    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllEventsBinding.inflate(
            inflater,
            container,
            false
        )

        var trackId = -1

        val adapter = EventAdapter(object : OnInteractionListenerEvent {
            override fun like(event: Event) {
                if (viewModelAuth.authenticated) {
                    viewModel.likeEventById(event)
                } else {
                    findNavController().navigate(R.id.dialogFragmentSignIn)
                }
            }

            override fun remove(event: Event) {
                viewModel.removeEventById(event.id)
            }

            override fun edit(event: Event) {
                EventDealtWith.saveEventDealtWith(event)
                findNavController().navigate(R.id.action_allEventsFragment_to_editEventFragment)
            }

            override fun showEvent (event: Event) {
                EventDealtWith.saveEventDealtWith(event)
                findNavController().navigate(R.id.action_allEventsFragment_to_detailedOneEventFragment)
            }


            override fun playMusic(event: Event) {
                val thisTrackId = event.id
                if (mediaObserver.player?.isPlaying == true) {
                    mediaObserver.apply {
                        viewModel.updateIsPlayingEvent(event.id, false)
                        viewModel.updatePlayer()
                        stop()
                        if (thisTrackId != trackId ) {
                            trackId = thisTrackId
                            viewModel.updateIsPlayingEvent(event.id, true)
                            event.attachment?.url?.let { play(it) }
                        }
                    }
                } else {
                    mediaObserver.apply {
                        trackId = thisTrackId
                        viewModel.updateIsPlayingEvent(event.id, true)
                        event.attachment?.url?.let { play(it) }
                    }
                }
            }

        }
        )

        mediaObserver.player?.setOnCompletionListener {
            mediaObserver.player?.stop()
            viewModel.updatePlayer()
        }


        val toolbarAll: Toolbar = binding.toolbarPosts
        toolbarAll.overflowIcon?.colorFilter = BlendModeColorFilter(
            Color.WHITE,
            BlendMode.SRC_ATOP
        )

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModelAuth.dataAuth.collect {
                    toolbarAll.invalidateMenu()
                }
            }
        }

        toolbarAll.addMenuProvider(object : MenuProvider {
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
                        findNavController().navigate(R.id.signInFragment)
                        true
                    }

                    R.id.signup -> {
                        findNavController().navigate(R.id.signUpFragment)
                        true
                    }

                    R.id.signout -> {
                        findNavController().navigate(R.id.dialogFragmentSignOut)
                        true
                    }

                    else -> false
                }
        })

        binding.recyclerList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventData.collectLatest{
                    adapter.submitList(it)
                }
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { feedModelState ->
            binding.progress.isVisible = feedModelState.loading
            binding.errorGroup.isVisible = feedModelState.error
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadEvents()
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadEvents()
            binding.swiperefresh.isRefreshing = false
        }

        binding.addEvent.setOnClickListener {
            if (viewModelAuth.authenticated) {
                findNavController().navigate( R.id.action_allEventsFragment_to_newEventFragment)

            } else {
                findNavController().navigate(R.id.dialogFragmentSignIn)
            }

        }


        return binding.root
    }
}