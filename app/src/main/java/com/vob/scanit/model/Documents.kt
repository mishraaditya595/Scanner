package com.vob.scanit.model

import android.net.Uri
import java.util.*

/* We define a data class and specify the arguments to be passed into its constructor */
data class Documents(
        val id: Long,
        val displayName: String,
        val date: Date,
        val contentUri: Uri
)