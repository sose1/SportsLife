package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

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
import com.kwasowski.sportslife.databinding.FragmentOwnExerciseListBinding
import com.kwasowski.sportslife.ui.exercise.exercisesList.fragment.adapter.OwnExercisesAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OwnExerciseListFragment : Fragment() {
    private val viewModel: OwnExerciseListViewModel by viewModel()
    private lateinit var binding: FragmentOwnExerciseListBinding

    private val adapter = OwnExercisesAdapter()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = binding.searchInput
        searchInput.setOnClickListener {
            searchInput.isIconified = !searchInput.isIconified
        }

        binding.exerciseList.setHasFixedSize(true)
        binding.exerciseList.adapter = adapter

        onViewStateChanged()

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
                        Toast.makeText(
                            context,
                            R.string.network_connection_error_please_try_again_later,
                            Toast.LENGTH_LONG
                        ).show()
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

                    is OwnExerciseListState.OnFilteredExercises -> adapter.updateList(it.filteredList)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getExerciseList()
    }
}