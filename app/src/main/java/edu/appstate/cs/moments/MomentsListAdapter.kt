package edu.appstate.cs.moments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.appstate.cs.moments.databinding.ListItemMomentBinding
import java.text.SimpleDateFormat
import java.util.UUID

class MomentHolder(
    private val binding: ListItemMomentBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(moment: Moment, onMomentClicked: (momentId: UUID) -> Unit) {
        binding.momentTitle.text = moment.title
        binding.momentDescription.text = moment.description
        binding.momentTimestamp.text = SimpleDateFormat.getDateTimeInstance().format(moment.timestamp)

        binding.root.setOnClickListener {
            onMomentClicked(moment.id)
        }
    }
}

class MomentsListAdapter(
    private val moments: List<Moment>,
    private val onMomentClicked: (momentId: UUID) -> Unit
) : RecyclerView.Adapter<MomentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemMomentBinding.inflate(inflater, parent, false)
        return MomentHolder(binding)
    }

    override fun getItemCount() = moments.size

    override fun onBindViewHolder(holder: MomentHolder, position: Int) {
        val moment = moments[position]
        holder.bind(moment, onMomentClicked)
    }
}