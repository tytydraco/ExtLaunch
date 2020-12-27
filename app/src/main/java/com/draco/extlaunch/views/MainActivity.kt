package com.draco.extlaunch.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.extlaunch.R
import com.draco.extlaunch.recyclers.LauncherRecyclerAdapter
import com.draco.extlaunch.recyclers.RecyclerEdgeEffectFactory
import com.draco.extlaunch.viewmodels.MainActivityViewModel

class MainActivity: AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var recyclerAdapter: LauncherRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.getAppList().observe(this) {
            recyclerAdapter.appList = viewModel.getAppList().value!!
            recyclerAdapter.notifyDataSetChanged()
        }

        recyclerAdapter = LauncherRecyclerAdapter(this, emptyArray()).apply {
            setHasStableIds(true)
        }

        with (findViewById<RecyclerView>(R.id.recycler)) {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
            edgeEffectFactory = RecyclerEdgeEffectFactory()
            setItemViewCacheSize(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateList()
    }

    override fun onBackPressed() {}
}