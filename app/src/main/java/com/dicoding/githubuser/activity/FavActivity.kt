package com.dicoding.githubuser.activity

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.ListFavAdapter
import com.dicoding.githubuser.data.Fav
import com.dicoding.githubuser.databinding.ActivityFavBinding
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.dicoding.githubuser.db.FavHelper
import com.dicoding.mynotesapp.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavActivity : AppCompatActivity() {
    private lateinit var adapter: ListFavAdapter
    private lateinit var binding: ActivityFavBinding
    private lateinit var rvInfos: RecyclerView

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvInfos = findViewById(R.id.rv_infos)
        rvInfos.setHasFixedSize(false)

        showRecyclerList()

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                loadNotesAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Fav>(EXTRA_STATE)
            if (list != null) {
                adapter.listFav = list
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFav)
    }

    private fun setActionBarTitle(title: CharSequence) {
        supportActionBar?.title = title
    }

    private fun showRecyclerList() {
        rvInfos.layoutManager = LinearLayoutManager(this)
        adapter = ListFavAdapter(this)
        rvInfos.adapter = adapter

        adapter.setOnItemClickCallback(object : ListFavAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Fav) {
                showSelectedInfo(data)
            }
        })
    }

    private fun showSelectedInfo(fav: Fav) {
        val detailActivity = Intent(this@FavActivity, DetailActivity::class.java)
        detailActivity.putExtra(DetailActivity.EXTRA_FAV, fav)
        startActivity(detailActivity)
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val favHelper = FavHelper.getInstance(applicationContext)
            favHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            binding.progressBar.visibility = View.INVISIBLE
            val data = deferredNotes.await()
            if (data.size > 0) {
                adapter.listFav = data
            } else {
                adapter.listFav = ArrayList()
            }
            favHelper.close()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotesAsync()
    }
}