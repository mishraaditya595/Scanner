package com.vob.scanit.viewmodel

import android.app.Application
import android.content.Context
import android.database.ContentObserver
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vob.scanit.model.Documents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/* DocumentFragmentViewModel is an instance of the ViewModel class that is designed to
*  store data in a lifecycle aware manner (updates UI on rotation of the screen) */
class DocumentFragmentViewModel(): ViewModel() {

    val documents = MutableLiveData<ArrayList<File>>()
    private var contentObserver: ContentObserver? = null

    /*loadDocument() function displays the list of documents and survives configuration changes*/
    fun loadDocuments() {
        viewModelScope.launch {
            val dir = File(Environment.DIRECTORY_DOCUMENTS + File.separator + "ScanIt")
            val documentList = getFiles(dir)
            documents.postValue(documentList)
        }
    }

    /* The following suspend function obtains all the files in the given directory and returns a list
    *  that contains all the PDF files from the directory.*/
    suspend fun getFiles(dir: File): ArrayList<File> {
        var listFiles = dir.listFiles()
        var fileList: ArrayList<File> = ArrayList()

        if (listFiles != null && listFiles.size > 0)
        {
            for (item in listFiles)
            {
                if (item.isDirectory)
                {
                    getFiles(item)
                }
                else
                {
                    var flag: Boolean = false
                    if (item.name.endsWith(".pdf"))
                    {
                        for (element in fileList)
                        {
                            if (element.name.equals(item.name))
                            {
                                flag = true
                            }
                        }
                        if (flag)
                        {
                            flag = false
                        }
                        else
                        {
                            fileList.add(item)
                        }
                    }
                }
            }
        }
        return  fileList
    }

//    suspend fun queryFiles(): ArrayList<Documents> {
//        var documentsList = mutableListOf<Documents>()
//
//        withContext(Dispatchers.IO) {
//            val projection = arrayOf(
//                    MediaStore.MediaColumns._ID,
//                    MediaStore.MediaColumns.DISPLAY_NAME,
//                    MediaStore.MediaColumns.DATE_MODIFIED
//            )
//
//
//        }
//    }
}