package com.myapplication

import Person
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class PeopleAdapter(private val people: MutableList<Person>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    private var error: String? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (error != null) 1 else 2
//        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val resource: Int =
            if (viewType == 1)
                R.layout.error_item
            else R.layout.person_item


        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                resource,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (error != null)
            holder.textView.text = error
        else
            "${people[position].fullName} (${people[position].id})".also {
                holder.textView.text = it
            }
    }

    override fun getItemCount(): Int {
        return if (error != null) 1 else people.size
    }

    fun addPeople(people: List<Person>?) {
        if (people == null) return
        val len = this.people.size
        this.people.addAll(people)
        notifyItemRangeChanged(len, this.people.size)
    }

    fun clearPeople(error: String? = null) {
        this.error = error
//        val len: Int = people.size
        people.clear()
//        notifyItemRangeRemoved(0, len)
        notifyDataSetChanged()
    }


    fun getPeopleSize(): Int {
        return people.size
    }
}