package com.woynapp.wontto.presentation.add_habit

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.woynapp.wontto.R
import com.woynapp.wontto.core.notification.RemindersManager
import com.woynapp.wontto.core.utils.*
import com.woynapp.wontto.databinding.FragmentAddHabitBinding
import com.woynapp.wontto.domain.model.AlarmItem
import com.woynapp.wontto.domain.model.Category
import com.woynapp.wontto.domain.model.Habit
import com.woynapp.wontto.presentation.adapter.AlarmAdapter
import com.woynapp.wontto.presentation.adapter.CategoryAddHabitAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddHabitFragment : Fragment(R.layout.fragment_add_habit),
    CategoryAddHabitAdapter.CategoryItemListener, AlarmAdapter.OnItemClickListener {

    private lateinit var _binding: FragmentAddHabitBinding
    private val categoryAdapter: CategoryAddHabitAdapter by lazy { CategoryAddHabitAdapter(this) }
    private val viewModel: AddHabitViewModel by viewModels()
    private val args: AddHabitFragmentArgs by navArgs()

    private var selectedCategory: String? = null
    private var selectedEmoji: String? = null

    private var selectedTime: String? = null
    private var isCategoryClicked = false
    private var currentCategoryList: List<Category> = emptyList()

    private var habitId: String? = null

    private val alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter(this)
    }

    private val alarmList: ArrayList<AlarmItem> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddHabitBinding.bind(view)

        habitId = UUID.randomUUID().toString()

        initView()
        initRecyclerViews()
        observe()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        _binding.daysSeekBar.setOnSeekBarChangeListener(onSeekbarChangeListener)
        _binding.saveBtn.setOnClickListener {
            if (checkForValidHabit()) {
                val habit = getHabit()
                if (args.habit == null) {
                    viewModel.addHabit(habit)
                } else {
                    viewModel.updateHabit(habit)
                }
                clearViews()
                showAd()
            }
        }
        _binding.emojiContainer.setOnClickListener {
            val emojiList = getJsonFromAssets(
                requireContext(),
                Constants.emojiListJsonName
            )?.fromJsonToEmoji()
            emojiList?.let { list ->
                EmojiBottomSheet(
                    list
                ) {
                    selectedEmoji = it
                    _binding.emojiTv.text = selectedEmoji
                    _binding.emojiIg.isVisible = false
                    _binding.emojiTv.isVisible = true
                }.show(childFragmentManager, "Emoji bottom sheet")
            }
        }
        _binding.alertBtn.setOnClickListener {
            if (checkForValidHabit()) {
                showTimePicker()
            }
        }

        _binding.categoryCtn.setOnClickListener {
            isCategoryClicked = !isCategoryClicked
            if (isCategoryClicked) {
                _binding.categoryIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_keyboard_arrow_down_24
                    )
                )
                categoryAdapter.submitList(currentCategoryList)
            } else {
                _binding.categoryIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_chevron_right_24
                    )
                )
                categoryAdapter.submitList(
                    if (currentCategoryList.size >= 6)
                        currentCategoryList.take(6)
                    else currentCategoryList
                )
            }
        }
    }

    private fun initHabitIfExist() {
        args.habit?.let {
            _binding.habitNameInputLayout.editText?.setText(it.name)
            _binding.daySize.text = it.day_size.toString()
            _binding.daysSeekBar.progress = it.day_size
            _binding.emojiTv.text = it.emoji
            _binding.emojiTv.isVisible = true
            _binding.emojiIg.isVisible = false
            selectedCategory = it.category
            selectedEmoji = it.emoji
            categoryAdapter.setSelectedCategory(it.category)
        }
    }

    private fun getHabit(): Habit {
        val name = _binding.habitNameInputLayout.editText?.text.toString()
        val description = _binding.habitDescriptionInputLayout.editText?.text.toString()
        val daySize = _binding.daysSeekBar.progress
        if (args.habit == null) {
            return Habit(
                name = name,
                description = description,
                started = true,
                started_date = System.currentTimeMillis(),
                category = selectedCategory!!,
                day_size = daySize,
                uuid = habitId!!,
                emoji = selectedEmoji!!
            )
        }
        return args.habit!!.copy(
            description = description,
            started = true,
            started_date = System.currentTimeMillis(),
            category = selectedCategory!!,
            day_size = daySize,
            emoji = selectedEmoji!!
        )
    }

    private fun showAd() {
        PopUpDialog(
            onClose = {
                if (args.habit == null) {
                    Snackbar.make(
                        _binding.root,
                        getString(R.string.habit_saved_successfully),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    requireActivity()
                        .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                        .selectedItemId = R.id.dashboardFragment
                } else {
                    findNavController().popBackStack()
                }
            },
            onClick = {
                val action =
                    AddHabitFragmentDirections.actionHabitFragmentToWebViewFragment(
                        Constants.KARGO_BUL_URL
                    )
                findNavController().navigate(action)
            }
        ).show(childFragmentManager, "Ad Pop up")
    }

    private fun clearViews() {
        _binding.apply {
            _binding.habitNameInputLayout.editText?.setText("")
            _binding.habitDescriptionInputLayout.editText?.setText("")
            selectedTime = null
            timeTv.text = getString(R.string.time)
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { result ->
                    val categoryList = arrayListOf<Category>(Category(name = ""))
                    categoryList.addAll(result.reversed())
                    currentCategoryList = categoryList
                    if (args.habit != null) {
                        isCategoryClicked = true
                        _binding.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_baseline_keyboard_arrow_down_24
                            )
                        )
                        categoryAdapter.submitList(categoryList)
                    } else {
                        categoryAdapter.submitList(
                            if (isCategoryClicked) {
                                categoryList
                            } else {
                                if (categoryList.size >= 6)
                                    categoryList.take(6)
                                else
                                    categoryList
                            }
                        )
                    }
                    delay(100L)
                    initHabitIfExist()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addedHabit.collect { habit ->
                    if (habit != null) {
                        alertOn(habit.id!!)
                    }
                    clearViews()
                }
            }
        }
    }

    private fun initRecyclerViews() {
        _binding.categoryRv.apply {
            adapter = categoryAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            setHasFixedSize(false)
        }
        _binding.alarmRecyclerView.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private val onSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.daySize.text = progress.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    override fun addCategory() {
        AddHabitBottomSheet {
            viewModel.addCategory(Category(name = it))
        }.show(childFragmentManager, "Add Habit bottom sheet")
    }

    override fun onClick(item: Category) {
        selectedCategory = if (selectedCategory == item.name)
            null
        else item.name
    }

    private fun checkForValidHabit(): Boolean {
        when {
            _binding.habitNameInputLayout.editText?.text?.toString().isNullOrBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_write_habit_name),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            selectedCategory.isNullOrBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_category),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            selectedEmoji.isNullOrBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.select_emoji_message),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    override fun onLongClick(item: Category) {
        showAlertDialog(
            requireContext(),
            getString(R.string.delete_category),
            getString(R.string.delete_category_message)
        ) {
            viewModel.deleteCategory(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime = "$hourOfDay:$minute"
                _binding.timeTv.text = "${getString(R.string.time)} $selectedTime"
                val calendar: Calendar =
                    Calendar.getInstance(Locale.getDefault()).apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                alarmList.add(
                    AlarmItem(
                        uuid = randomId(),
                        time = calendar.timeInMillis,
                        message = _binding.habitNameInputLayout.editText?.text?.toString()!!,
                        is_mute = false,
                        habit_id = habitId!!
                    )
                )
                println(alarmList)
                alarmAdapter.submitList(alarmList.toList())
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun alertOn(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(
                requireContext(),
                AlarmManager::class.java
            )
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    requireContext().startActivity(intent)
                }
            } else {
                createNotificationsChannels()
                // TODO

                /*RemindersManager.startReminder(
                    requireContext(),
                    reminderTime = selectedTime!!,
                    reminderId = id,
                    message = _binding.habitNameInputLayout.editText?.text?.toString()!!
                )*/
            }
        } else {
            createNotificationsChannels()
            // TODO

            /*RemindersManager.startReminder(
                requireContext(),
                reminderTime = selectedTime!!,
                reminderId = id,
                message = _binding.habitNameInputLayout.editText?.text?.toString()!!
            )*/
        }
    }

    private fun createNotificationsChannels() {
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                getString(R.string.reminders_notification_channel_id),
                getString(R.string.reminders_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        ContextCompat.getSystemService(requireContext(), NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    override fun update(item: AlarmItem) {

    }

    override fun delete(item: AlarmItem) {
        alarmList.remove(item)
        alarmAdapter.submitList(alarmList)
    }

    override fun onStart() {
        super.onStart()
        if (args.habit != null) {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                .visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        if (args.habit != null) {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                .visibility = View.VISIBLE
        }
    }
}