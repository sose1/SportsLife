package com.kwasowski.sportslife.ui.exercise.exerciseList.activity

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
import com.kwasowski.sportslife.databinding.ActivityExercisesListBinding
import com.kwasowski.sportslife.extensions.parcelable
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.communities.CommunitiesExerciseListFragment
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.fav.FavExerciseListFragment
import com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.own.OwnExerciseListFragment
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormActivity
import com.kwasowski.sportslife.utils.ActivityOpenMode
import com.kwasowski.sportslife.utils.Constants
import timber.log.Timber

class ExercisesListActivity : AppCompatActivity(), OwnExerciseListFragment.DataPassListener,
    CommunitiesExerciseListFragment.DataPassListener, FavExerciseListFragment.DataPassListener {
    private lateinit var binding: ActivityExercisesListBinding
    private lateinit var pagerAdapter: FragmentStateAdapter

    private val exercisesToAdd: ParcelableMutableList<ParcelableLinkedHashMap<String, String>> =
        ParcelableMutableList()

    val fragments = listOf(
        { OwnExerciseListFragment() } to R.string.own,
        { CommunitiesExerciseListFragment() } to R.string.communities,
        { FavExerciseListFragment() } to R.string.favorites
    )

    private inner class FragmentPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            val fragment = fragments[position].first.invoke()

            Timber.d("Open mode ${getOpenModeFromIntent()}")

            val isOpenModeAddExercise =
                getOpenModeFromIntent() == ActivityOpenMode.ADD_EXERCISE_TO_TRAINING_PLAN
            fragment.arguments = (fragment.arguments ?: Bundle()).apply {
                putBoolean(Constants.CAN_ADD_EXERCISE_TO_TRAINING_PLAN, isOpenModeAddExercise)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercises_list)
        binding.lifecycleOwner = this

        pagerAdapter = FragmentPagerAdapter(this.supportFragmentManager, lifecycle)
        binding.viewpager.apply {
            adapter = pagerAdapter
            setPageTransformer(MarginPageTransformer(resources.getDimensionPixelOffset(R.dimen.margin_fragment)))
            offscreenPageLimit = 1
        }

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, pos ->
            tab.text = getString(fragments[pos].second)
        }.attach()

        binding.topAppBar.setNavigationOnClickListener { onActivityFinish() }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, ExerciseFormActivity::class.java))
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
        if (getOpenModeFromIntent() == ActivityOpenMode.ADD_EXERCISE_TO_TRAINING_PLAN) {
            if (exercisesToAdd.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra(Constants.EXERCISES_TO_ADD, exercisesToAdd)
                setResult(Activity.RESULT_OK, intent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
        }
        finish()
    }

    override fun onAddedExerciseToTraining(exerciseId: String, exerciseName: String) {
        Timber.d("Add: $exerciseId | $exerciseName")
        val exercise = ParcelableLinkedHashMap<String, String>().apply {
            this["id"] = exerciseId
            this["name"] = exerciseName
        }
        exercisesToAdd.add(exercise)
    }
}