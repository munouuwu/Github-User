package com.dicoding.githubuser.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.User
import com.dicoding.githubuser.activity.DetailActivity
import com.dicoding.githubuser.adapter.ListUserAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_following.*
import org.json.JSONArray
import org.json.JSONObject

class FollowingFragment : Fragment() {
    private var list: ArrayList<User> = arrayListOf()

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = DetailActivity.EXTRA_USERNAME

        list.clear()

        getUserFollowing(user)
        showRecyclerList()
    }

    private fun showRecyclerList() {
        rv_infos.layoutManager = LinearLayoutManager(activity)
        val listUserAdapter = ListUserAdapter(list)
        rv_infos.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedInfo(data)
            }
        })
    }

    private fun showSelectedInfo(user: User) {
        val detailActivity = Intent(activity, DetailActivity::class.java)
        detailActivity.putExtra(DetailActivity.EXTRA_USER, user)
        startActivity(detailActivity)
    }

    private fun getUserFollowing(uName: String?) {
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$uName/following"
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseArray = JSONArray(result)
                    for(i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)
                        val username: String = responseObject.getString("login")
                        getUserDetail(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserDetail(uName: String){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://api.github.com/users/$uName"
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val name: String = responseObject.getString("name")
                    val username: String = responseObject.getString("login")
                    val avatar: String = responseObject.getString("avatar_url")
                    val location: String = responseObject.getString("location")
                    val repository: String = responseObject.getString("public_repos")
                    val company: String = responseObject.getString("company")
                    val followers: String = responseObject.getString("followers")
                    val following: String = responseObject.getString("following")

                    list.add(
                            User(
                                    name,
                                    username,
                                    location,
                                    repository,
                                    company,
                                    followers,
                                    following,
                                    avatar
                            )
                    )
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}