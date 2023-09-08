package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.FragmentOwnTrainingPlansBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class OwnTrainingPlansFragment : Fragment() {
    private val viewModel: OwnTrainingPlansViewModel by viewModel()
    private lateinit var binding: FragmentOwnTrainingPlansBinding

    //    private lateinit var adapter: OwnTrainingPlansAdapter todo
    private var queryText: String = ""

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            newText?.let {
                queryText = newText
//                viewModel.filterTrainingPlans(queryText) todo
            }
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_own_training_plans,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.progress.visibility = View.VISIBLE
        onViewStateChanged()
        return binding.root
    }

    private fun onViewStateChanged() = lifecycleScope.launch {

        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.trainingPlans.collect {
//                when(it) {
//                    todo
//                }
//            }
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.getTrainingPlans() todo
    }

//    private fun onItemClick(trainingPlan: TrainingPlanDto) {
//        val intent = Intent(requireContext(), TrainingPlanFormActivity::class.java)
//        intent.putExtra(Constants.EXERCISE_ID_INTENT, trainingPlan.id)
//        startActivity(intent)
//   todo }

    private fun showToast(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
    }
}