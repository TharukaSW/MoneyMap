import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.imilipocket.databinding.FragmentSettingsBinding
import com.example.moneymap.ui.settings.SettingsViewModel
import com.example.imilipocket.util.PdfGenerator
import com.example.moneymap.util.PdfOpener

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private lateinit var pdfGenerator: PdfGenerator
    private lateinit var pdfOpener: PdfOpener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        pdfGenerator = PdfGenerator(requireContext())
        pdfOpener = PdfOpener(requireContext())
        
        setupUI()
        setupClickListeners()
        
        return binding.root
    }

    private fun setupUI() {
        // Setup currency spinner
        val currencies = resources.getStringArray(com.example.imilipocket.R.array.currencies)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, currencies)
        binding.spinnerCurrency.setAdapter(adapter)

        // Observe currency changes
        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            binding.spinnerCurrency.setText(currency, false)
        }

        // Setup dark mode switch
        binding.switchDarkMode.isChecked = viewModel.isDarkModeEnabled()
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
        }
    }

    private fun setupClickListeners() {
        // Save currency button
        binding.btnSaveCurrency.setOnClickListener {
            val selectedCurrency = binding.spinnerCurrency.text.toString()
            if (selectedCurrency.isNotEmpty()) {
                viewModel.updateCurrency(selectedCurrency)
                Toast.makeText(requireContext(), "Currency updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // Generate PDF button
        binding.btnGeneratePdf.setOnClickListener {
            try {
                viewModel.getMonthlyTransactions().let { transactions ->
                    if (transactions.isNotEmpty()) {
                        val pdfFile = pdfGenerator.generateMonthlyReport(transactions)
                        pdfOpener.openPdf(pdfFile)
                        Toast.makeText(requireContext(), "PDF generated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "No transactions to generate report", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 