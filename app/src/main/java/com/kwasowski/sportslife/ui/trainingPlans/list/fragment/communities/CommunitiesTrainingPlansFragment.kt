package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.communities

import android.content.Context
import android.content.Intent
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
import com.kwasowski.sportslife.data.trainingPlan.TrainingPlanDto
import com.kwasowski.sportslife.databinding.FragmentCommunitiesTrainingPlansBinding
import com.kwasowski.sportslife.ui.trainingPlans.form.TrainingPlanFormActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommunitiesTrainingPlansFragment : Fragment() {
    private val viewModel: CommunitiesTrainingPlansViewModel by viewModel()
    private lateinit var binding: FragmentCommunitiesTrainingPlansBinding

    private lateinit var adapter: CommunitiesTrainingPlansAdapter
    private var queryText: String = ""

    private var dataPassListener: DataPassListener? = null

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            newText?.let {
                queryText = newText
                viewModel.filterTrainingPlans(queryText)
            }
            return false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DataPassListener) {
            dataPassListener = context
        } else {
            throw ClassCastException("$context must implement SendDataToActivity")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_communities_training_plans,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.progress.visibility = View.VISIBLE
        onViewStateChanged()

        adapter = CommunitiesTrainingPlansAdapter(
            onItemClick = { trainingPlan -> onItemClick(trainingPlan) },
            onScheduleButtonClicked = { trainingPlan ->
                dataPassListener?.onAddedTrainingToCalendarDay(
                    trainingPlanId = trainingPlan.id,
                    trainingPlanName = trainingPlan.name,
                    numberOfExercises = trainingPlan.exercisesSeries.size
                )
                showToast(R.string.scheduled_on_the_calendar)
            }
        )

        binding.trainingPlansList.setHasFixedSize(true)
        binding.trainingPlansList.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchInput = binding.searchInput
        searchInput.setOnClickListener {
            searchInput.isIconified = !searchInput.isIconified
        }
        searchInput.setOnQueryTextListener(onQueryTextListener)
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    CommunitiesTrainingPlansState.Default -> Unit
                    CommunitiesTrainingPlansState.OnFailure -> {
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                        showToast(R.string.network_connection_error_please_try_again_later)
                    }

                    CommunitiesTrainingPlansState.OnSuccessGetTrainingPlans -> {
                        binding.progress.visibility = View.GONE
                        viewModel.filterTrainingPlans(queryText)
                    }

                    is CommunitiesTrainingPlansState.OnFilteredTrainingPlans -> {
                        adapter.updateList(it.filteredList)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTrainingPlans()
    }

    private fun onItemClick(trainingPlan: TrainingPlanDto) {
        val intent = Intent(requireContext(), TrainingPlanFormActivity::class.java)
        intent.putExtra(Constants.TRAINING_PLAN_ID_INTENT, trainingPlan.id)
        intent.putExtra(Constants.TRAINING_PLAN_IS_DETAILS_VIEW, true)
        startActivity(intent)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
    }

    interface DataPassListener {
        fun onAddedTrainingToCalendarDay(
            trainingPlanId: String,
            trainingPlanName: String,
            numberOfExercises: Int,
        )
    }
}