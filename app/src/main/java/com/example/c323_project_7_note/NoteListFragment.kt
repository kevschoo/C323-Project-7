package com.example.c323_project_7_note

import NoteAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.c323_project_7_note.databinding.FragmentNoteListBinding
import androidx.navigation.fragment.findNavController
import com.example.c323_project_7_note.model.Note
import androidx.appcompat.app.AlertDialog

class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel
    private lateinit var noteAdapter: NoteAdapter

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
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
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

        noteAdapter = NoteAdapter { note ->
            viewModel.selectNote(note)
            findNavController().navigate(R.id.action_noteListFragment_to_noteEditFragment)
        }

        binding.notesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
        }

        viewModel.notes.observe(viewLifecycleOwner, Observer { notes: List<Note> ->
            noteAdapter.submitList(notes)
        })

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            if (authenticationState == AuthenticationState.UNAUTHENTICATED) {
                noteAdapter.submitList(emptyList())
                findNavController().navigate(R.id.action_noteListFragment_to_loginFragment)
            }
        })

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_note -> {
                    viewModel.clearSelectedNote()
                    findNavController().navigate(R.id.action_noteListFragment_to_noteEditFragment)
                    true
                }
                R.id.action_login_logout -> {
                    if (viewModel.authenticationState.value == AuthenticationState.AUTHENTICATED) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Log Out")
                            .setMessage("Are you sure you want to log out?")
                            .setPositiveButton("Yes") { _, _ ->
                                viewModel.signOut()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    } else {
                        findNavController().navigate(R.id.action_noteListFragment_to_loginFragment)
                    }
                    true
                }
                else -> false
            }
        }
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