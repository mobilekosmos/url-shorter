package com.mobilekosmos.android.shortly.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mobilekosmos.android.shortly.R
import com.mobilekosmos.android.shortly.data.repository.Injector
import com.mobilekosmos.android.shortly.data.repository.URLsRepository
import com.mobilekosmos.android.shortly.ui.model.MyViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


class MainFragment : Fragment(R.layout.fragment_main_content) {

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val viewModel: MyViewModel by viewModels {
        Injector.provideMyViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById<MaterialButton>(R.id.shorten_button)) { v, windowInsets ->
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

        val inputViewParent = view.findViewById<TextInputLayout>(R.id.shorten_url_parent)
        val inputView = view.findViewById<TextInputEditText>(R.id.shorten_url)
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

        view.findViewById<Button>(R.id.shorten_button).setOnClickListener {
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
        } else {
            viewModel.fetchShorterURL(inputView.text.toString())
        }
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
}