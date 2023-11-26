package edu.appstate.cs.moments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import edu.appstate.cs.moments.databinding.FragmentMomentDetailBinding
import kotlinx.coroutines.launch
import java.util.Date

class MomentDetailFragment: Fragment() {
    private var _binding: FragmentMomentDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: MomentDetailFragmentArgs by navArgs()

    private val momentDetailViewModel: MomentDetailViewModel by viewModels {
        MomentDetailViewModelFactory(args.momentId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMomentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            momentTitle.doOnTextChanged { text, _, _, _ ->
                momentDetailViewModel.updateMoment { oldMoment ->
                    oldMoment.copy(title = text.toString())
                }
            }

            momentDescription.doOnTextChanged { text, _, _, _ ->
                momentDetailViewModel.updateMoment { oldMoment ->
                    oldMoment.copy(description = text.toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                momentDetailViewModel.moment.collect { moment ->
                    moment?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            momentDetailViewModel.updateMoment { it.copy(timestamp = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newDate = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            momentDetailViewModel.updateMoment { it.copy(timestamp = newDate) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_moment_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_moment -> {
                deleteCurrentMoment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteCurrentMoment() {
        momentDetailViewModel.deleteMoment()
        findNavController().navigateUp()
    }

    private fun updateUi(moment: Moment) {
        binding.apply {
            if (momentTitle.text.toString() != moment.title) {
                momentTitle.setText(moment.title)
            }
            if (momentDescription.text.toString() != moment.description) {
                momentDescription.setText(moment.description)
            }
            momentDate.text = DateFormat.format("MM dd yyyy", moment.timestamp)
            momentTime.text = DateFormat.format("HH:mm:ss z", moment.timestamp)
            momentDate.setOnClickListener {
                findNavController().navigate(
                    MomentDetailFragmentDirections.selectDate(moment.timestamp)
                )
            }
            momentTime.setOnClickListener {
                findNavController().navigate(
                    MomentDetailFragmentDirections.selectTime(moment.timestamp)
                )
            }
        }
    }
}