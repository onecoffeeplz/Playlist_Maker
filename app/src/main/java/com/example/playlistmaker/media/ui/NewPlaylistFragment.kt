package com.example.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.presentation.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentNewPlaylistBinding must not be null!")

    private var uri: Uri? = null

    private val viewModel by viewModel<NewPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.playlistCreate.isEnabled = s?.isNotEmpty() ?: false
            }
        }
        binding.playlistName.addTextChangedListener(textWatcher)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { selectedUri ->
                if (selectedUri != null) {
                    uri = selectedUri
                    binding.playlistCover.setImageURI(uri)
                }
            }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            })

        binding.playlistCreate.setOnClickListener{
            viewModel.createPlaylist(
                binding.playlistName.text.toString(),
                binding.playlistDescription.text.toString(),
                uri?.toString()
            )
            findNavController().popBackStack()
            showToast(binding.playlistName.text.toString())
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_dialog_title)
            .setMessage(R.string.playlist_dialog_body)
            .setNeutralButton(R.string.playlist_dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.playlist_dialog_finish) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun handleBackPressed() {
        if (binding.playlistName.text.toString()
                .isNotEmpty() || binding.playlistDescription.text.toString()
                .isNotEmpty() || uri != null
        ) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showToast(playlistName: String) {
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_created, playlistName),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}