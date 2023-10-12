package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.fav

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.databinding.FragmentFavExerciseListBinding
import com.kwasowski.sportslife.ui.exercise.details.ExerciseDetailsActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavExerciseListFragment : Fragment() {
    private val viewModel: FavExerciseListViewModel by viewModel()
    private lateinit var binding: FragmentFavExerciseListBinding

    private lateinit var adapter: FavExercisesAdapter
    private var queryText: String = ""

    private var dataPassListener: DataPassListener? = null

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            newText?.let {
                queryText = newText
                viewModel.filterExercises(queryText)
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
            R.layout.fragment_fav_exercise_list,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.progress.visibility = View.VISIBLE
        onViewStateChanged()

        adapter = FavExercisesAdapter(
            context = requireContext(),
            addToTrainingIsVisible = canAddExerciseToTrainingPlan(),
            onMenuItemSelected = { exercise, menuItemId ->
                onExerciseMenuItemSelected(exercise, menuItemId)
            },
            onItemClick = { exercise ->
                onItemClick(exercise)
            }
        )

        binding.exerciseList.setHasFixedSize(true)
        binding.exerciseList.adapter = adapter

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
                    FavExerciseListState.Default -> Unit
                    FavExerciseListState.OnFailure -> {
                        binding.emptyListInfo.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                        showToast(R.string.network_connection_error_please_try_again_later)
                    }

                    is FavExerciseListState.OnFilteredExercises -> adapter.updateList(it.filteredList)
                    FavExerciseListState.OnSuccessGetEmptyList -> {
                        binding.emptyListInfo.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                    }

                    FavExerciseListState.OnSuccessGetExerciseList -> {
                        binding.emptyListInfo.visibility = View.GONE
                        binding.progress.visibility = View.GONE
                        viewModel.filterExercises(queryText)
                    }

                    FavExerciseListState.OnSuccessDeletingExercise -> {
                        showToast(R.string.removed_exercise_from_favorites)
                        viewModel.getExerciseList()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getExerciseList()
    }

    private fun canAddExerciseToTrainingPlan() =
        arguments?.getBoolean(Constants.CAN_ADD_EXERCISE_TO_TRAINING_PLAN) ?: false

    private fun onExerciseMenuItemSelected(exercise: ExerciseDto, menuItemId: Int) {
        Timber.d("$menuItemId | $exercise")
        when (menuItemId) {
            R.id.add_to_training -> {
                Timber.d("Add to training")
                showToast(R.string.added_to_training)
                dataPassListener?.onAddedExerciseToTraining(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    exerciseUnit = exercise.units
                )
            }

            R.id.remove_from_likes -> {
                Timber.d("Remove from fav")
                viewModel.removeFromLikes(exercise)
            }
        }
    }

    private fun onItemClick(exercise: ExerciseDto) {
        val intent = Intent(requireContext(), ExerciseDetailsActivity::class.java)
        intent.putExtra(Constants.EXERCISE_ID_INTENT, exercise.id)
        if (exercise.ownerId != Firebase.auth.currentUser?.uid)
            intent.putExtra(Constants.IS_COMMUNITY_INTENT, true)
        startActivity(intent)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
    }

    interface DataPassListener {
        fun onAddedExerciseToTraining(
            exerciseId: String,
            exerciseName: String,
            exerciseUnit: String,
        )
    }
}