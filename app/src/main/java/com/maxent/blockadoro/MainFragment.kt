package com.maxent.blockadoro

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.maxent.blockadoro.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private lateinit var timer: TimerViewModel
    private var currentColor: Int = 0


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

        timer = ViewModelProvider(requireActivity())[TimerViewModel::class.java]
        // watch for phase change
        timer.phaseLiveData.observe(viewLifecycleOwner) { newPhase ->
            updateUIForPhase(newPhase)
        }

        binding.pauseButton.setOnClickListener {
            timer.toggleTimerState()
            val iconRes = if (timer.isTimerRunning) {
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
        binding.phaseView.text = newPhase
        // Because we poll from the queue, eventually the queue will only have 1 item in it. We check if that is so, so we can still display the next phase
        if (timer.currentPhaseIndex >= 0 && timer.currentPhaseIndex < timer.phaseQueue.size) {
            binding.phaseViewSmall.text  = timer.phaseQueue[timer.currentPhaseIndex].name
        } else {
            binding.phaseViewSmall.text  = "Short Break"
        }
        changeBackgroundColor(newPhase)
    }

    private fun changeBackgroundColor(newPhase: String) {
        val mainActivity = requireActivity() as MainActivity
        val mCoordinatorLayout = mainActivity.findViewById<CoordinatorLayout>(R.id.main_layout)
        val mToolBar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbar)

        val newColorId = when (newPhase) {
            "Deep Focus" -> R.color.deep_focus
            "Short Break" -> R.color.short_break
            "Long Break" -> R.color.long_break
            else -> R.color.bg_primary
        }

        // Translate the color string to RGB color
        val newColor = ContextCompat.getColor(mainActivity, newColorId)

        // Create the animator for switching between current and new color
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), currentColor, newColor)

        // When animation updates, change the background color
        colorAnimation.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            mCoordinatorLayout.setBackgroundColor(animatedValue)
            mToolBar.setBackgroundColor(animatedValue)
        }

        colorAnimation.duration = 2000
        colorAnimation.start()

        currentColor = newColor
    }
}