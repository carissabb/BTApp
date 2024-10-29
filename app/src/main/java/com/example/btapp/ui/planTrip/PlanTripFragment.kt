package com.example.btapp.ui.planTrip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.btapp.databinding.FragmentPlanTripBinding

class PlanTripFragment : Fragment() {

    private var _binding: FragmentPlanTripBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val planTripViewModel =
            ViewModelProvider(this).get(PlanTripViewModel::class.java)

        _binding = FragmentPlanTripBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPlanTrip
        planTripViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}