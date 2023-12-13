package com.maxent.blockadoro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.maxent.blockadoro.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private lateinit var timerViewModel: TimerViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerViewModel = ViewModelProvider(requireActivity())[TimerViewModel::class.java]

        // watch for phase change
        timerViewModel.phaseLiveData.observe(viewLifecycleOwner) { newPhase ->
            updateUIForPhase(newPhase)
        }

        binding.pauseButton.setOnClickListener {
            timerViewModel.toggleTimerState()
            val iconRes = if (timerViewModel.isTimerRunning) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_skip
            }
            binding.pauseButton.setIconResource(iconRes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUIForPhase(newPhase: String) {
        // Update UI elements based on the new phase
//        requireActivity().findViewById<View>(R.id.coordinatorLayout).setBackgroundColor(Color.RED)
        binding.phaseView.text = newPhase
    }
}