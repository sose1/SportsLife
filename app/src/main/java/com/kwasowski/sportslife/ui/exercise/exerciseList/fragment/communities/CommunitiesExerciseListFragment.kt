package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.communities

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
import com.kwasowski.sportslife.data.exercise.ExerciseDto
import com.kwasowski.sportslife.databinding.FragmentCommunitiesExerciseListBinding
import com.kwasowski.sportslife.ui.exercise.details.ExerciseDetailsActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CommunitiesExerciseListFragment : Fragment() {
    private val viewModel: CommunitiesExerciseListViewModel by viewModel()
    private lateinit var binding: FragmentCommunitiesExerciseListBinding

    private lateinit var adapter: CommunitiesExercisesAdapter
    private var queryText: String = ""

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_communities_exercise_list,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.progress.visibility = View.VISIBLE
        onViewStateChanged()

        adapter = CommunitiesExercisesAdapter(
            context = requireContext(),
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

    override fun onResume() {
        super.onResume()
        viewModel.getExerciseList()
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    CommunitiesExerciseState.Default -> Unit
                    CommunitiesExerciseState.OnFailure -> {
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                        showToast(R.string.network_connection_error_please_try_again_later)
                    }

                    CommunitiesExerciseState.OnSuccessGetExerciseList -> {
                        binding.progress.visibility = View.GONE
                        viewModel.filterExercises(queryText)
                    }

                    is CommunitiesExerciseState.OnFilteredExercises -> adapter.updateList(it.filteredList)
                    CommunitiesExerciseState.OnSuccessCopy -> showToast(R.string.success_copy_to_own)
                }
            }
        }
    }


    private fun onItemClick(exercise: ExerciseDto) {
        val intent = Intent(requireContext(), ExerciseDetailsActivity::class.java)
        intent.putExtra(Constants.EXERCISE_ID_INTENT, exercise.id)
        intent.putExtra(Constants.IS_COMMUNITY_INTENT, true)
        startActivity(intent)
    }

    private fun onExerciseMenuItemSelected(exercise: ExerciseDto, menuItemId: Int) {
        Timber.d("$menuItemId | $exercise")
        when (menuItemId) {
            R.id.add_to_training -> {
                Timber.d("Add to training")
            }

            R.id.add_to_fav -> {
                Timber.d("Add to fav")
            }

            R.id.copy_to_own -> {
                viewModel.copyToOwn(exercise)
            }
        }
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
    }
}