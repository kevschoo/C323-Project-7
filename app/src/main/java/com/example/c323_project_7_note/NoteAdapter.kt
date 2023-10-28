import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.c323_project_7_note.databinding.ItemNoteBinding
import com.example.c323_project_7_note.model.Note
import com.example.c323_project_7_note.model.getTitle

/**
 * Adapter class for displaying a list of notes in a RecyclerView
 * @param onNoteClicked A lambda function that is invoked when a note is clicked. The clicked note is passed as a parameter
 */
class NoteAdapter(private val onNoteClicked: (Note) -> Unit) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback) {

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View
     * @return A new ViewHolder that holds a View of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param position The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
        holder.itemView.setOnClickListener {
            onNoteClicked(note)
        }
    }

    /**
     * ViewHolder for note items in the list
     * @param binding The binding for the note item layout
     */
    class NoteViewHolder(private var binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.title.text = note.title
            binding.description.text = note.text
        }
    }

    /**
     * Callback for calculating the diff between two non-null items in a list
     * Used by ListAdapter to calculate the minimum number of changes between and old list and a new list that's been passed to 'submitList'
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}