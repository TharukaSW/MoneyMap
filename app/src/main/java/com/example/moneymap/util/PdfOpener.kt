package com.example.moneymap.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class PdfOpener(private val context: Context) {
    fun openPdf(pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            
            val chooser = Intent.createChooser(intent, "Open PDF with")
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 