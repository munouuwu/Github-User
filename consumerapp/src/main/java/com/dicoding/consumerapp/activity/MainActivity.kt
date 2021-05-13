package com.dicoding.consumerapp.activity

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.consumerapp.R
import com.dicoding.consumerapp.adapter.ListFavAdapter
import com.dicoding.consumerapp.data.Fav
import com.dicoding.consumerapp.databinding.ActivityMainBinding
import com.dicoding.consumerapp.db.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.dicoding.mynotesapp.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var rvInfos: RecyclerView
    private lateinit var adapter: ListFavAdapter
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBarTitle(getString(R.string.app_name))

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
            val list = savedInstanceState.getParcelableArrayList<Fav>(EXTRA_DATA)
            if (list != null) {
                adapter.listFav = list
            }
        }
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val data = deferredNotes.await()
            binding.progressBar.visibility = View.INVISIBLE
            if (data.size > 0) {
                adapter.listFav = data
            } else {
                adapter.listFav = ArrayList()
                showSnackbarMessage()
            }
        }
    }

    private fun showSnackbarMessage() {
        Toast.makeText(this, getString(R.string.fav_empty), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_change_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }

            R.id.action_notification_settings -> {
                val mIntent = Intent(this, NotificationActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setActionBarTitle(title: CharSequence) {
        supportActionBar?.title = title
    }

    override fun onResume() {
        super.onResume()
        loadNotesAsync()
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

    private fun showSelectedInfo(user: Fav) {
        val detailActivity = Intent(this@MainActivity, DetailActivity::class.java)
        detailActivity.putExtra(DetailActivity.EXTRA_FAV, user)
        DetailActivity.IS_FAV = 0
        startActivity(detailActivity)
    }
}