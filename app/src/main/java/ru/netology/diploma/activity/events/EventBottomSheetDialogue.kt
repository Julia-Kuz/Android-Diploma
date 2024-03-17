package ru.netology.diploma.activity.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.netology.diploma.R
import ru.netology.diploma.databinding.FragmentEventBottomSheetDialogueBinding
import ru.netology.diploma.dto.EventType
import ru.netology.diploma.util.formatDateTime
import ru.netology.diploma.viewmodel.EventViewModel
import java.util.Calendar

class EventBottomSheetDialogue : BottomSheetDialogFragment() {

    lateinit var binding: FragmentEventBottomSheetDialogueBinding
    private val viewModel: EventViewModel by activityViewModels()

    // Переопределяем тему, чтобы использовать нашу с закруглёнными углами
    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEventBottomSheetDialogueBinding.bind(inflater.inflate(R.layout.fragment_event_bottom_sheet_dialogue, container, false))
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.let {
            // Находим сам bottomSheet и достаём из него Behaviour
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            // Выставляем высоту для состояния и выставляем состояние
            behavior.peekHeight = bottomSheet.height //(COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // *** radio button

        if (binding.radioButtonOnline.isChecked) {
            viewModel.setEventFormat(EventType.ONLINE)
        } else viewModel.setEventFormat(EventType.OFFLINE)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_button_online -> {viewModel.setEventFormat(EventType.ONLINE)}
                R.id.radio_button_offline -> {viewModel.setEventFormat(EventType.OFFLINE)}
            }
        }

        // *** calendar

        val calendar = Calendar.getInstance()
        fun showDateTimePickerDialog() {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val timePickerDialog = TimePickerDialog(requireContext(),
                        { _, selectedHour, selectedMinute ->

                            val selectedDateTime = String.format("%02d.%02d.%04d, %02d:%02d", selectedDay, selectedMonth + 1, selectedYear, selectedHour, selectedMinute)
                            viewModel.setEventDateTime (selectedDateTime)

                        }, hour, minute, true)

                    timePickerDialog.show()
                }, year, month, day)

            datePickerDialog.show()
        }

        binding.chooseDateTime.setOnClickListener {
            showDateTimePickerDialog()
        }

        viewModel.eventDateTime.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.chooseDateTime.text = formatDateTime(it)
            }
        }

        //dismiss()
    }
}