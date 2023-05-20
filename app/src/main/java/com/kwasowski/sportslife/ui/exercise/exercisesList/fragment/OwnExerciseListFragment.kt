package com.kwasowski.sportslife.ui.exercise.exercisesList.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.FragmentOwnExerciseListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class OwnExerciseListFragment : Fragment() {
    private val viewModel: OwnExerciseListViewModel by viewModel()
    private lateinit var binding: FragmentOwnExerciseListBinding

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
    }
}