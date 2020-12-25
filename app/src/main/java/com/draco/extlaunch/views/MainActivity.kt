package com.draco.extlaunch.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.extlaunch.R
import com.draco.extlaunch.recyclers.LauncherRecyclerAdapter
import com.draco.extlaunch.recyclers.RecyclerEdgeEffectFactory
import com.draco.extlaunch.viewmodels.MainActivityViewModel

class MainActivity: AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel

    private lateinit var recyclerAdapter: LauncherRecyclerAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        recycler = findViewById(R.id.recycler)

        setupRecyclerView()

        viewModel.getAppList().observe(this) {
            recyclerAdapter.appList = viewModel.getAppList().value!!
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun setupRecyclerView() {
        recyclerAdapter = LauncherRecyclerAdapter(this, viewModel.getAppList().value!!).apply {
            setHasStableIds(true)
        }

        with (recycler) {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
            edgeEffectFactory = RecyclerEdgeEffectFactory()

            setItemViewCacheSize(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.updateList())
            recyclerAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {}
}