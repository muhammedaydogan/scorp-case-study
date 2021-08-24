package com.myapplication

import Person
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PeopleAdapter(private val people: MutableList<Person>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.person_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        "${people[position].fullName} (${people[position].id})".also { holder.textView.text = it }
    }

    override fun getItemCount(): Int {
        return people.size
    }

    fun addPeople(people: List<Person>?) {
        if (people == null) return
        val len = this.people.size
        this.people.addAll(people)
        notifyItemRangeChanged(len, this.people.size)
//        this.notifyDataSetChanged()
    }
}