package com.kwasowski.sportslife.ui.trainingPlans.list

import android.content.Intent
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
import com.kwasowski.sportslife.databinding.ActivityTrainingPlansBinding
import com.kwasowski.sportslife.ui.trainingPlans.form.TrainingPlanFormActivity
import com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities.CommunitiesTrainingPlansFragment
import com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own.OwnTrainingPlansFragment

class TrainingPlansActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingPlansBinding
    private lateinit var pageAdapter: FragmentStateAdapter

    val fragments = listOf(
        { OwnTrainingPlansFragment() } to R.string.own,
        { CommunitiesTrainingPlansFragment() } to R.string.communities
    )

    private inner class FragmentPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position].first.invoke()
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

        binding.topAppBar.setNavigationOnClickListener { finish() }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, TrainingPlanFormActivity::class.java))
        }
    }
}