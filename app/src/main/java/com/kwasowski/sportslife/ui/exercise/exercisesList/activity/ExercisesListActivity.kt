package com.kwasowski.sportslife.ui.exercise.exercisesList.activity

import android.os.Bundle
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
import com.kwasowski.sportslife.ui.exercise.exercisesList.fragment.CommunitiesExercisesListFragment
import com.kwasowski.sportslife.ui.exercise.exercisesList.fragment.OwnExerciseListFragment

class ExercisesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExercisesListBinding
    private lateinit var pagerAdapter: FragmentStateAdapter

    val fragments = listOf(
        { OwnExerciseListFragment() } to R.string.own,
        { CommunitiesExercisesListFragment() } to R.string.communities,
    )


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

        binding.topAppBar.setNavigationOnClickListener { finish() }

//        val searchInput = binding.searchInput
//        searchInput.setOnClickListener {
//            searchInput.isIconified = !searchInput.isIconified
//        }
    }

    private inner class FragmentPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position].first.invoke()
    }
}