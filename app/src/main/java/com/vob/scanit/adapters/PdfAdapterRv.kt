package com.vob.scanit.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.vob.scanit.R
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/* PdfAdapterRv class extends RecyclerView.Adapter and takes in arguments and a listener,
   along with overriding the methods needed for the Adapter to work. */

class PdfAdapterRv(private val context: Context, private val activity: Activity,
                   private val fileList: ArrayList<File>,private val pdfInterface: PdfAdapterInterface) :
        RecyclerView.Adapter<PdfAdapterRv.PdfAdapterViewHolder>(){

    private val searchList: ArrayList<File> = ArrayList()

    /* PdfAdapterViewHolder contains all the elements that gets displayed in the recycler view */
    inner class PdfAdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val fileName: TextView = itemView.findViewById(R.id.filename_TV)
        val optionsBtn: ImageView = itemView.findViewById(R.id.options_btn)

    }

    /* Informs the activity when the item gets clicked  */
    interface PdfAdapterInterface{
        fun onItemClicked(position: Int)
        fun onItemLongClicked(position: Int)
    }

    /*onCreateViewHolder() is called when the Adapter is created and returns an instance of the
      PdfAdapterViewHolder. In this function, we handle the onClick event by displaying a toast message*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfAdapterViewHolder {
        val viewHolder = PdfAdapterViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_pdf, parent, false))
        viewHolder.optionsBtn.setOnClickListener{
            DynamicToast.makeWarning(context, "Long press on the filename to open options menu",
                    Toast.LENGTH_LONG).show()
        }

        return viewHolder
    }

    /*onBindViewHolder() is called for each ViewHolder and used to define the actions on it */
    override fun onBindViewHolder(holder: PdfAdapterViewHolder, position: Int) {
        holder.fileName.text = fileList[position].name

        holder.itemView.setOnClickListener {
            pdfInterface.onItemClicked(position)
        }
        holder.itemView.setOnLongClickListener {
            pdfInterface.onItemLongClicked(position)
            true
        }
    }

    /* getItemCount() method returns the number of items in the list that we want to display */
    override fun getItemCount(): Int {
        return fileList.size
    }

    /* In the following method, we find the file searched for by the user.
       We convert the text we receive into the lowercase characters of the language in the region
       where the app is being used (using Locale.getDefault()).
       Using notifyDataSetChanged(), we can notify the listener attached to our adapter about the
       changes in the underlying data, so that any view reflecting this data can be refreshed accordingly */
    fun filter(characterText: String) {
        var characterText = characterText
        characterText = characterText.toLowerCase(Locale.getDefault())
        fileList.clear()
        if (characterText.length == 0) {
            fileList.addAll(searchList)
        } else {
            for (file in searchList) {
                if (file.name.toLowerCase(Locale.getDefault()).contains(characterText)) {
                    fileList.add(file)
                }
            }
        }
        notifyDataSetChanged()
    }
}