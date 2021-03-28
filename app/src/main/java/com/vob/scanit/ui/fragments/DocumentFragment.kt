package com.vob.scanit.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vob.scanit.R
import com.vob.scanit.adapters.DocumentListAdapter
import com.vob.scanit.databinding.FragmentDocumentBinding
import com.vob.scanit.viewmodel.DocumentFragmentViewModel
import com.vob.scanit.viewmodel.DocumentViewModelFactory
import java.io.File

/*DocumentFragment is a Fragment class and represents a reusable portion of the app's UI. We override
* the methods of the Fragment class */
class DocumentFragment : Fragment() {

    private lateinit var binding: FragmentDocumentBinding
    private lateinit var viewModel: DocumentFragmentViewModel
    private val documentListAdapter = DocumentListAdapter(arrayListOf())
    private val documentListObserver = Observer<ArrayList<File>> {list->
        documentListAdapter.updateDocumentList(list)
    }

    /*onCreateView() is used to inflate the layout*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document, container, false)
    }

    /*onViewCreated() ensures the view is fully created, and is called immediately after onCreateView()*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDocumentBinding.bind(view)

        val viewModelFactory = DocumentViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(DocumentFragmentViewModel::class.java)

        val layoutManager = LinearLayoutManager(activity!!.applicationContext)
        binding.filesListRv.layoutManager = layoutManager
        binding.filesListRv.adapter = documentListAdapter

        viewModel.loadDocuments()
        viewModel.documents.observe(viewLifecycleOwner, Observer {
            if (it == null || it.size == 0)
                Toast.makeText(context,"Empty",Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context,it.size,Toast.LENGTH_LONG).show()
            documentListAdapter.updateDocumentList(it)
        })


    }

}