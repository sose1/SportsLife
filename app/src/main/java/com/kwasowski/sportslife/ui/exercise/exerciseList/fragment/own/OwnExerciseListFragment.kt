package com.kwasowski.sportslife.ui.exercise.exerciseList.fragment.own

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
import com.kwasowski.sportslife.databinding.FragmentOwnExerciseListBinding
import com.kwasowski.sportslife.ui.exercise.details.ExerciseDetailsActivity
import com.kwasowski.sportslife.ui.exercise.form.ExerciseFormActivity
import com.kwasowski.sportslife.utils.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class OwnExerciseListFragment : Fragment() {
    private val viewModel: OwnExerciseListViewModel by viewModel()
    private lateinit var binding: FragmentOwnExerciseListBinding

    private lateinit var adapter: OwnExercisesAdapter
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
            R.layout.fragment_own_exercise_list,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.progress.visibility = View.VISIBLE
        onViewStateChanged()

        adapter = OwnExercisesAdapter(
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

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect {
                when (it) {
                    OwnExerciseListState.Default -> Unit
                    OwnExerciseListState.OnFailure -> {
                        binding.emptyListInfo.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                        showToast(R.string.network_connection_error_please_try_again_later)
                    }

                    OwnExerciseListState.OnSuccessGetEmptyList -> {
                        binding.emptyListInfo.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        adapter.updateList(emptyList())
                    }

                    OwnExerciseListState.OnSuccessGetExerciseList -> {
                        binding.emptyListInfo.visibility = View.GONE
                        binding.progress.visibility = View.GONE
                        viewModel.filterExercises(queryText)
                    }

                    OwnExerciseListState.OnSuccessSharedExercise ->
                        showToast(R.string.exercise_was_successfully_shared)


                    OwnExerciseListState.OnSuccessDeletingExercise -> {
                        showToast(R.string.correctly_deleted_exercise)
                        viewModel.getExerciseList()
                    }

                    is OwnExerciseListState.OnFilteredExercises -> adapter.updateList(it.filteredList)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getExerciseList()
    }

    private fun onItemClick(exercise: ExerciseDto) {
        val intent = Intent(requireContext(), ExerciseDetailsActivity::class.java)
        intent.putExtra(Constants.EXERCISE_ID_INTENT, exercise.id)
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

            R.id.edit -> {
                Timber.d("Edit")
                val intent = Intent(requireContext(), ExerciseFormActivity::class.java)
                intent.putExtra(Constants.EXERCISE_ID_INTENT, exercise.id)
                startActivity(intent)
            }

            R.id.delete -> {
                Timber.d("Delete")
                viewModel.deleteExercise(exercise)
            }

            R.id.share -> {
                Timber.d("Share")
                viewModel.shareExercise(exercise)
            }
        }
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
    }
}