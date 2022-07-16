package com.mobilekosmos.android.shortly.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mobilekosmos.android.shortly.R
import com.mobilekosmos.android.shortly.data.model.ShortURLEntity
import com.mobilekosmos.android.shortly.data.repository.Injector
import com.mobilekosmos.android.shortly.data.repository.URLsRepository
import com.mobilekosmos.android.shortly.databinding.FragmentMainBinding
import com.mobilekosmos.android.shortly.ui.model.MyViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.net.MalformedURLException
import java.net.URL


class MainFragment : Fragment(R.layout.fragment_main), ListAdapter.OnListItemClickListener {

    private lateinit var clubsListAdapter: ListAdapter
    private lateinit var binding: FragmentMainBinding

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val viewModel: MyViewModel by viewModels {
        Injector.provideMyViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Use data binding (not view binding!) to easily switch views visibility before/while/after list loading.
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        // Set the lifecycleOwner so DataBinding can observe LiveData.
        binding.lifecycleOwner = viewLifecycleOwner

        // Initializes the viewModel variable in the xml layout file.
        binding.viewModel = viewModel

        clubsListAdapter = ListAdapter(this)
        // Since we have a list which shows the latest item on the top we must make sure the list scrolls to the top when updating the list.
        clubsListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        // We use view binding here just because we use data binding in this class so it comes handy.
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = clubsListAdapter
            binding.root.context.let {
                val dividerItemDecoration = MaterialDividerItemDecoration(
                    it,
                    MaterialDividerItemDecoration.VERTICAL
                )
                dividerItemDecoration.isLastItemDecorated = false

                // https://github.com/material-components/material-components-android/blob/master/docs/components/Divider.md
                // Needed if you did not set colorOnSurface in your theme -> default according to Material should be colorOnSurface (12% opacity applied automatically on top).
                // dividerItemDecoration.setDividerColorResource(it, R.color.colorPrimary)

                addItemDecoration(dividerItemDecoration)
            }
        }

        activity?.setTitle(R.string.app_title)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.shortenButton) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainUrlsContainer) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        val inputViewParent = binding.shortenUrlParent
        val inputView = binding.shortenUrl
        inputView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateInput(inputView, inputViewParent)
                // We return false so the IME gets closed. We use "return@setOnEditorActionListener" otherwise the code
                // will just jump to the next statement and false would be returned.
                return@setOnEditorActionListener false
            }
            false
        }
        inputView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && inputViewParent.isErrorEnabled) {
                inputViewParent.error = null
            }
        }

        binding.shortenButton.setOnClickListener {
            validateInput(inputView, inputViewParent)
        }

        viewModel.urls.observe(viewLifecycleOwner) { list ->
            list?.let {
                clubsListAdapter.submitList(list)
            }
        }

        viewModel.shortenURLError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.shortenURLError.value = null
            }
        }

        binding.clubsRetryButton.setOnClickListener {
            validateInput(inputView, inputViewParent)
        }
    }

    private fun validateInput(
        inputView: TextInputEditText,
        inputViewParent: TextInputLayout
    ) {
        if (inputView.text.isNullOrEmpty()) {
            inputView.clearFocus()
            inputViewParent.error = getString(R.string.error_empty)
        } else if (!isValidUrl(inputView.text.toString())){
            inputView.clearFocus()
            inputViewParent.error = getString(R.string.error_url_invalid)
        } else {
            viewModel.fetchShorterURL(inputView.text.toString())
        }
    }

    private fun isValidUrl(urlString: String): Boolean {
        try {
            URL(urlString)
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches()
        } catch (ignored: MalformedURLException) {
        }
        return false
    }

    /**
     * Factory for creating a [MyViewModel] with a constructor that takes a [URLsRepository].
     *
     * The @ExperimentalCoroutinesApi and @FlowPreview indicate that experimental APIs are being used.
     */
    @ExperimentalCoroutinesApi
    @FlowPreview
    class MyViewModelFactory(
        private val repository: URLsRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = MyViewModel(repository) as T
    }

    override fun onDeleteClick(urlEntity: ShortURLEntity) {
        viewModel.removeFromHistory(urlEntity)
    }

    override fun onCopyClick(shortLink: String) {
        activity?.let {
            val clipboardManager = it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(
                "shortly",
                shortLink
            )
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
        }
    }
}