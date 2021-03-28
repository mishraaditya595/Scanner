package com.vob.scanit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vob.scanit.R
import java.io.File

/* DocumentListAdapter class extends RecyclerView.Adapter, and overrides the methods needed for the
*  Adapter to work. */
class DocumentListAdapter(private val documentList: ArrayList<File>):
RecyclerView.Adapter<DocumentListAdapter.DocumentViewHolder>()
{
    /* DocumentViewHolder class extends RecyclerView.ViewHolder. It is used to represent the items in
       our list and display them. */
    class DocumentViewHolder(var view: View): RecyclerView.ViewHolder(view) {}

    /* The following method updates the list of documents and informs the listener*/
    fun updateDocumentList(newDocumentList: List<File>) {
        documentList.clear()
        documentList.addAll(newDocumentList)
        notifyDataSetChanged()
    }

    /*The following method is called when the Adapter is created and is used to initialise the ViewHolder.
    * LayoutInflater class is used to convert the xml file(adapter_pdf) to a view*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_pdf, parent, false)
        return DocumentViewHolder(view)
    }

    /* The function onBindViewHolder() is called for each ViewHolder and is used to bind it to the Adapter.
    *  We pass the data to our ViewHolder using this function*/
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val filename = holder.view.findViewById<TextView>(R.id.filename_TV)
        filename.text = documentList.get(position).name
    }

    /* getItemCount() method returns the number of items in the list that we want to display */
    override fun getItemCount(): Int {
        return documentList.size
    }
}