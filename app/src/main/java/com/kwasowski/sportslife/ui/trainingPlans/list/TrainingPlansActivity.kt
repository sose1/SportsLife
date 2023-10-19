package com.kwasowski.sportslife.ui.trainingPlans.list

import ParcelableLinkedHashMap
import ParcelableMutableList
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityTrainingPlansBinding
import com.kwasowski.sportslife.extensions.parcelable
import com.kwasowski.sportslife.ui.trainingPlans.form.TrainingPlanFormActivity
import com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities.CommunitiesTrainingPlansFragment
import com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own.OwnTrainingPlansFragment
import com.kwasowski.sportslife.utils.ActivityOpenMode
import com.kwasowski.sportslife.utils.Constants
import timber.log.Timber

class TrainingPlansActivity : AppCompatActivity(), OwnTrainingPlansFragment.DataPassListener,
    CommunitiesTrainingPlansFragment.DataPassListener {
    private lateinit var binding: ActivityTrainingPlansBinding
    private lateinit var pageAdapter: FragmentStateAdapter

    private val trainingsToAdd: ParcelableMutableList<ParcelableLinkedHashMap<String, String>> =
        ParcelableMutableList()

    val fragments = listOf(
        { OwnTrainingPlansFragment() } to R.string.own,
        { CommunitiesTrainingPlansFragment() } to R.string.communities
    )

    private inner class FragmentPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment {
            val fragment = fragments[position].first.invoke()
            Timber.d("Open mode ${getOpenModeFromIntent()}")

            val isOpenModeAddTraining =
                getOpenModeFromIntent() == ActivityOpenMode.ADD_TRAINING_PLAN_TO_CALENDAR_DAY
            fragment.arguments = (fragment.arguments ?: Bundle()).apply {
                putBoolean(Constants.CAN_ADD_TRAINING_TO_CALENDAR_DAY, isOpenModeAddTraining)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_plans)
        binding.lifecycleOwner = this

        pageAdapter = FragmentPagerAdapter(this.supportFragmentManager, lifecycle)
        binding.viewpager.apply {
            adapter = pageAdapter
            setPageTransformer(MarginPageTransformer(resources.getDimensionPixelOffset(R.dimen.margin_fragment)))
            offscreenPageLimit = 1
        }

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, pos ->
            tab.text = getString(fragments[pos].second)
        }.attach()

        binding.topAppBar.setNavigationOnClickListener { onActivityFinish() }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, TrainingPlanFormActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onActivityFinish()
            }
        })
    }

    private fun getOpenModeFromIntent(): ActivityOpenMode? =
        intent.parcelable(Constants.OPEN_MODE)

    private fun onActivityFinish() {
        if (getOpenModeFromIntent() == ActivityOpenMode.ADD_TRAINING_PLAN_TO_CALENDAR_DAY) {
            if (trainingsToAdd.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra(Constants.TRAININGS_TO_ADD, trainingsToAdd)
                setResult(Activity.RESULT_OK, intent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
        }
        finish()
    }

    override fun onAddedTrainingToCalendarDay(
        trainingPlanId: String,
        trainingPlanName: String,
        numberOfExercises: Int,
    ) {
        Timber.d("Add: $trainingPlanId | $trainingPlanName | $numberOfExercises")
        val exercise = ParcelableLinkedHashMap<String, String>().apply {
            this["trainingPlanId"] = trainingPlanId
            this["trainingPlanName"] = trainingPlanName
            this["numberOfExercises"] = numberOfExercises.toString()
        }
        trainingsToAdd.add(exercise)
    }
}