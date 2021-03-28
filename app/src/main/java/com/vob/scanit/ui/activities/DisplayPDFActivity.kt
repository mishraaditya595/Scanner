package com.vob.scanit.ui.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.vob.scanit.R
import java.io.File

/*DisplayPDFActivity class has methods that display the contents of each PDF file and provide further functionality*/
class DisplayPDFActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {

    lateinit var pdfView: PDFView
    var pageNumber: Int = 0
    lateinit var fileName: String
    var position: Int = -1
    //lateinit var file: File

    /*The following function calls the displayPDF() and passes it the filepath*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_pdf)

        initialiseFields()
        fileName = intent.getStringExtra("filename").toString()
        val path = intent.getStringExtra("uri")
        val uri = Uri.parse(path)
        val file = File(uri.path)
        displayPDF(file)

        setupToolbar()
        //val pdfView = findViewById<com.joanzapata.pdfview.PDFView>(R.id.newpdfview)
        //pdfView.fromFile(file).load()

    }

    /*setupToolbar() sets up and displays the toolbar*/
    private fun setupToolbar() {
        var toolbar = findViewById<Toolbar>(R.id.pdf_view_toolbar)
        toolbar.setTitle(fileName)
        toolbar.setTitleTextAppearance(applicationContext, R.style.TextAppearance_AppCompat_Title)
        toolbar.setTitleTextColor(-0x1)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    /*We initialise pdfView in the following function */
    private fun initialiseFields() {
       pdfView = findViewById(R.id.pdf_viewer)
    }

    /*displayPDF() displays the PDF file*/
    private fun displayPDF(file: File) {
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .load()
    }

    /*The following functions handles the activities that occur when a user swipes through the pages*/
    override fun onPageChanged(page: Int, pageCount: Int) {
        val pageNum = page
        setTitle(String.format("%s %s / %s", fileName, page+1, pageCount))
    }

    /*When the document gets loaded, the bookmarks get displayed with a call to printBookmarksTree()*/
    override fun loadComplete(nbPages: Int) {
        val meta = pdfView.documentMeta
        printBookmarksTree(pdfView.tableOfContents,"-")
    }

    /*printBookmarksTree fetches all the bookmarks made by the user*/
    private fun printBookmarksTree(tableOfContents: List<com.shockwave.pdfium.PdfDocument.Bookmark>, s: String) {
        for (item: com.shockwave.pdfium.PdfDocument.Bookmark  in tableOfContents) {
            if (item.hasChildren())
            {
                printBookmarksTree(item.children, s+"-")
            }
        }
    }
}