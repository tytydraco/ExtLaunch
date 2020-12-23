package com.draco.extlaunch.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.extlaunch.R
import com.draco.extlaunch.recyclers.LauncherRecyclerAdapter
import com.draco.extlaunch.recyclers.RecyclerEdgeEffectFactory

class MainActivity: AppCompatActivity() {
    private lateinit var recyclerAdapter: LauncherRecyclerAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerAdapter = LauncherRecyclerAdapter(this).apply {
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
        recyclerAdapter.updateList()
    }

    override fun onBackPressed() {}
}