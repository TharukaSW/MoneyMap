package com.example.imilipocket.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.imilipocket.R
import com.example.imilipocket.databinding.FragmentSettingsBinding
import com.example.imilipocket.data.PreferenceManager
import com.example.imilipocket.util.PdfGenerator

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewModel: SettingsViewModel
    private lateinit var pdfGenerator: PdfGenerator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        viewModel = ViewModelProvider(this, SettingsViewModel.Factory)[SettingsViewModel::class.java]
        pdfGenerator = PdfGenerator(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        // Set initial dark mode state
        binding.switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun setupUI() {
        // Setup currency spinner
        val currencies = listOf(
            "USD - US Dollar",
            "EUR - Euro",
            "GBP - British Pound",
            "JPY - Japanese Yen",
            "INR - Indian Rupee",
            "AUD - Australian Dollar",
            "CAD - Canadian Dollar",
            "LKR - Sri Lankan Rupee",
            "CNY - Chinese Yuan",
            "SGD - Singapore Dollar",
            "MYR - Malaysian Ringgit",
            "THB - Thai Baht",
            "IDR - Indonesian Rupiah",
            "PHP - Philippine Peso",
            "VND - Vietnamese Dong",
            "KRW - South Korean Won",
            "AED - UAE Dirham",
            "SAR - Saudi Riyal",
            "QAR - Qatari Riyal"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencies)
        binding.spinnerCurrency.apply {
            setAdapter(adapter)
            threshold = 1
            
            // Set current currency
            val currentCurrency = preferenceManager.getSelectedCurrency()
            val currencyIndex = currencies.indexOfFirst { it.startsWith(currentCurrency) }
            if (currencyIndex != -1) {
                setText(currencies[currencyIndex], false)
            }
            
            setOnItemClickListener { _, _, position, _ ->
                val selectedCurrency = adapter.getItem(position).toString()
                val currencyCode = selectedCurrency.substring(0, 3)
                preferenceManager.setSelectedCurrency(currencyCode)
                Toast.makeText(requireContext(), "Currency updated to $selectedCurrency", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSaveCurrency.setOnClickListener {
                val selectedCurrency = spinnerCurrency.text.toString()
                viewModel.setSelectedCurrency(selectedCurrency)
                Toast.makeText(requireContext(), "Currency saved", Toast.LENGTH_SHORT).show()
            }

            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                val mode = if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }

            btnGeneratePdf.setOnClickListener {
                generateMonthlyReport()
            }
        }
    }

    private fun generateMonthlyReport() {
        try {
            val transactions = viewModel.getMonthlyTransactions()
            if (transactions.isEmpty()) {
                Toast.makeText(requireContext(), "No transactions found for this month", Toast.LENGTH_SHORT).show()
                return
            }

            val pdfFile = pdfGenerator.generateMonthlyReport(transactions)
            
            // Show success dialog with options
            AlertDialog.Builder(requireContext())
                .setTitle("PDF Generated")
                .setMessage("Monthly report has been saved to Downloads folder. What would you like to do?")
                .setPositiveButton("Open") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.fromFile(pdfFile), "application/pdf")
                        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(intent)
                }
                .setNeutralButton("Share") { _, _ ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile))
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share PDF"))
                }
                .setNegativeButton("Close", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        // Implement the logic to observe the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 