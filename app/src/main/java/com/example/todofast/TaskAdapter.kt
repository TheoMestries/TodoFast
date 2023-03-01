import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todofast.MainActivity
import com.example.todofast.R
import com.example.todofast.Task

class TaskAdapter(private val items: ArrayList<Task>, private val context: Context) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titleTextView: TextView = itemView.findViewById(R.id.title)
        init {
            itemView.findViewById<ImageButton>(R.id.actionTask).setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            // Get the position of the item that was clicked
            val position: Int = adapterPosition
            // Do something with the clicked item
            Log.d("TaskAdapter", "Item clicked at position $position")

            val mainActivity = view?.context as MainActivity
            mainActivity.setItemToFirst(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.titleTextView.text = task.title
    }

    override fun getItemCount(): Int {
        return items.size
    }

}