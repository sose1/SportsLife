package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_own_exercise_list,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
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

        searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    queryText = newText
                    adapter.updateList(viewModel.filterExercises(queryText))
                }
                return false
            }
        })
    }

    private fun onViewStateChanged() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect{
                when(it) {
                    OwnExerciseListState.Default -> Unit
                    is OwnExerciseListState.OnSuccessGetExerciseList -> adapter.updateList(it.exercises)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getExerciseList(queryText)
    }
}