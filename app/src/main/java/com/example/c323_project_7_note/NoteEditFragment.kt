package com.example.c323_project_7_note

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.c323_project_7_note.databinding.FragmentNoteEditBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import com.example.c323_project_7_note.model.Note
import com.example.c323_project_7_note.model.getTitle

class NoteEditFragment : Fragment() {

    private var _binding: FragmentNoteEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_note_edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_note -> {
                deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Inflates the view for this fragment
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return The View for the fragment's UI
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = FragmentNoteEditBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        return view
    }

    /**
     * Called immediately after onCreateView
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            binding.etNoteTitle.setText(note?.title ?: "")
            binding.etNoteDescription.setText(note?.text ?: "")
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etNoteTitle.text.toString().trim()
            val description = binding.etNoteDescription.text.toString().trim()

            if (title.isEmpty() && description.isEmpty()) {
                Toast.makeText(context, "Note is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = viewModel.selectedNote.value?.copy(
                title = title.ifEmpty { "No Title" },
                text = description.ifEmpty { "No Description" }
            ) ?: Note("", title, description, FirebaseAuth.getInstance().currentUser?.uid ?: "")

            viewModel.createOrUpdateNote(note)
            findNavController().popBackStack()
        }

    }
    /**
     * Called when the delete menu button is pressed
     * Deletes a note and brings the user back to the note list
     */
    private fun deleteNote() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.selectedNote.value?.id?.let { noteId ->
                    viewModel.deleteNote(noteId)
                }
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Called when the view is destroyed
     */
    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}