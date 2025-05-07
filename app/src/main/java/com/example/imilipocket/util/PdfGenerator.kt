package com.example.imilipocket.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.imilipocket.data.Transaction
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun generateMonthlyReport(transactions: List<Transaction>): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "MoneyMap_Report_${dateFormat.format(Date())}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Add title
        document.add(Paragraph("Monthly Transaction Report")
            .setFontSize(20f)
            .setBold())

        // Add date
        document.add(Paragraph("Generated on: ${SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())}")
            .setFontSize(12f))

        // Add table
        val table = Table(4)
        table.setWidth(500f)

        // Add table headers
        table.addHeaderCell(Cell().add(Paragraph("Date")))
        table.addHeaderCell(Cell().add(Paragraph("Title")))
        table.addHeaderCell(Cell().add(Paragraph("Amount")))
        table.addHeaderCell(Cell().add(Paragraph("Type")))

        // Add transactions
        transactions.forEach { transaction ->
            table.addCell(Cell().add(Paragraph(dateFormat.format(Date(transaction.date)))))
            table.addCell(Cell().add(Paragraph(transaction.title)))
            table.addCell(Cell().add(Paragraph(CurrencyFormatter.formatAmount(context, transaction.amount))))
            table.addCell(Cell().add(Paragraph(transaction.type.name)))
        }

        document.add(table)

        // Add summary
        val totalIncome = transactions.filter { it.type == Transaction.Type.INCOME }
            .sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == Transaction.Type.EXPENSE }
            .sumOf { it.amount }
        val balance = totalIncome - totalExpense

        document.add(Paragraph("\nSummary:")
            .setFontSize(14f)
            .setBold())
        document.add(Paragraph("Total Income: ${CurrencyFormatter.formatAmount(context, totalIncome)}"))
        document.add(Paragraph("Total Expense: ${CurrencyFormatter.formatAmount(context, totalExpense)}"))
        document.add(Paragraph("Balance: ${CurrencyFormatter.formatAmount(context, balance)}"))

        document.close()

        return file
    }
} 