package com.kwasowski.sportslife.ui.trainingPlans.list.fragment.own

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
import com.kwasowski.sportslife.databinding.FragmentOwnTrainingPlansBinding
import com.kwasowski.sportslife.ui.trainingPlans.form.TrainingPlanFormActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class OwnTrainingPlansFragment : Fragment() {
    private val viewModel: OwnTrainingPlansViewModel by viewModel()
    private lateinit var binding: FragmentOwnTrainingPlansBinding

    private lateinit var adapter: OwnTrainingPlansAdapter
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
    private fun canAddTrainingToCalendarDay() =
        arguments?.getBoolean(Constants.CAN_ADD_TRAINING_TO_CALENDAR_DAY) ?: false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
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

        adapter = OwnTrainingPlansAdapter(
            context = requireContext(),
            canAddTrainingToCalendarDay = canAddTrainingToCalendarDay(),
            onMenuItemSelected = { trainingPlan, menuItemId ->
                onTrainingPlanMenuItemSelected(
                    trainingPlan,
                    menuItemId
                )
            },
            onItemClick = { trainingPlan ->
                onItemClick(trainingPlan)
            },
            onScheduleButtonClicked = { trainingPlan ->
                dataPassListener?.onAddedTrainingToCalendarDay(
                    trainingPlanId = trainingPlan.id,
                    trainingPlanName = trainingPlan.name,
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
                    OwnTrainingPlansState.Default -> Unit
                    OwnTrainingPlansState.OnFailure -> {
                        binding.emptyListInfo.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                        showToast(R.string.network_connection_error_please_try_again_later)
                    }

                    OwnTrainingPlansState.OnSuccessGetEmptyList -> {
                        binding.emptyListInfo.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                    }

                    OwnTrainingPlansState.OnSuccessGetTrainingPlans -> {
                        binding.emptyListInfo.visibility = View.GONE
                        binding.progress.visibility = View.GONE
                        viewModel.filterTrainingPlans(queryText)
                    }

                    is OwnTrainingPlansState.OnFilteredTrainingPlans -> {
                        adapter.updateList(it.filteredList)
                    }

                    OwnTrainingPlansState.OnSuccessDeleteTrainingPlan -> {
                        showToast(R.string.correctly_deleted_training_plan)
                        viewModel.getTrainingPlans()
                    }

                    OwnTrainingPlansState.OnSuccessShareTrainingPlan -> showToast(R.string.training_plan_successfully_shared)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTrainingPlans()
    }

    private fun onTrainingPlanMenuItemSelected(trainingPlan: TrainingPlanDto, menuItemId: Int) {
        Timber.d("$menuItemId | $trainingPlan")

        when (menuItemId) {
            R.id.edit -> {
                val intent = Intent(requireContext(), TrainingPlanFormActivity::class.java)
                intent.putExtra(Constants.TRAINING_PLAN_ID_INTENT, trainingPlan.id)
                startActivity(intent)
            }

            R.id.delete -> {
                Timber.d("Delete")
                viewModel.deleteTrainingPlan(trainingPlan)
            }

            R.id.share -> {
                Timber.d("Share")
                viewModel.shareTrainingPlan(trainingPlan)
            }
        }
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
        )
    }
}