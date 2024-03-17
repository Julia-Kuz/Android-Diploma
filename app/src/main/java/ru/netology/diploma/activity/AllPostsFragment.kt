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
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.diploma.R
import ru.netology.diploma.adapter.OnInteractionListener
import ru.netology.diploma.adapter.PostsAdapter
import ru.netology.diploma.databinding.FragmentAllPostsBinding
import ru.netology.diploma.dto.Post
import ru.netology.diploma.util.PostDealtWith
import ru.netology.diploma.viewmodel.AuthViewModel
import ru.netology.diploma.viewmodel.PostViewModel

@AndroidEntryPoint
class AllPostsFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()

    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllPostsBinding.inflate(
            inflater,
            container,
            false
        )

        var trackId = -1

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun like(post: Post) {
                if (viewModelAuth.authenticated) {
                    viewModel.likeById(post)
                } else {
                    findNavController().navigate(R.id.action_allPostsFragment_to_dialogFragmentSignIn)
                }
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                PostDealtWith.savePostDealtWith(post)
                findNavController().navigate(R.id.action_allPostsFragment_to_editPostFragment)
            }

            override fun showPost(post: Post) {
                PostDealtWith.savePostDealtWith(post)
                findNavController().navigate(R.id.action_allPostsFragment_to_detailedOnePostFragment)
            }

            override fun playMusic(post: Post) {

                val thisTrackId = post.id
                if (mediaObserver.player?.isPlaying == true) {
                    mediaObserver.apply {
                        viewModel.updateIsPlaying(post.id, false)
                        viewModel.updatePlayer()
                        stop()
                        if (thisTrackId != trackId ) {
                            trackId = thisTrackId
                            viewModel.updateIsPlaying(post.id, true)
                            post.attachment?.url?.let { play(it) }
                        }
                    }
                } else {
                    mediaObserver.apply {
                        trackId = thisTrackId
                        viewModel.updateIsPlaying(post.id, true)
                        post.attachment?.url?.let { play(it) }
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
        ) // чтобы три точки стали белыми - (!) Color д. использоваться из android.graphics, а не из androidx.compose.ui.graphics

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { //наблюдаем за авторизацией, только когда активити/фрагмент доступен для взаимодействия
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
                        findNavController().navigate(R.id.action_allPostsFragment_to_signInFragment)
                        true
                    }

                    R.id.signup -> {
                        findNavController().navigate(R.id.action_allPostsFragment_to_signUpFragment)
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
                viewModel.data.collectLatest{
                    adapter.submitList(it)
                }
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { feedModelState ->
            binding.progress.isVisible = feedModelState.loading
            binding.errorGroup.isVisible = feedModelState.error
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadPosts()
            binding.swiperefresh.isRefreshing = false
        }

        binding.addPost.setOnClickListener {
            if (viewModelAuth.authenticated) {
                findNavController().navigate(
                    R.id.action_allPostsFragment_to_newPostFragment,
                )
            } else {
                findNavController().navigate(R.id.action_allPostsFragment_to_dialogFragmentSignIn)
            }

        }

        return binding.root
    }
}

