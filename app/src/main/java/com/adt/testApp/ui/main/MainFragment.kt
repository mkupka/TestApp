package com.adt.testApp.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adt.testApp.R
import com.adt.testApp.databinding.MainFragmentBinding

class MainFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    companion object {
        fun newInstance() = MainFragment()
    }

    //private lateinit var viewModel: MainViewModel
    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: MainFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        recyclerView = binding.recyclerViewCharacter
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        setRecyclerViewScrollListener()
        val characterActorAdapter =
            CharacterActorListAdapter(CharacterActorListener { characterActorId ->

                // item is clicked, show dialog

            })
        recyclerView.adapter = characterActorAdapter

        this.viewModel.vendorCharacterActorList.observe(
            viewLifecycleOwner,
            Observer { customerList ->
                characterActorAdapter.addHeaderAndSubmitList(customerList)
            })


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = linearLayoutManager.childCount
                val firstVisibleItemPosition: Int =
                    linearLayoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = firstVisibleItemPosition + visibleItemCount
                viewModel.setRecyclerPosition(firstVisibleItemPosition, lastVisibleItemPosition)
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
    }

}