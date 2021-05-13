package com.dicoding.githubuser.activity

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.SectionsPagerAdapter
import com.dicoding.githubuser.data.Fav
import com.dicoding.githubuser.data.User
import com.dicoding.githubuser.databinding.ActivityDetailBinding
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.AVATAR
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.COMPANY
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.FAVORITE
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.FOLLOWERS
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.FOLLOWING
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.LOCATION
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.NAME
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.REPOSITORY
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.USERNAME
import com.dicoding.githubuser.db.FavHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ActivityDetailBinding
    private lateinit var avatarStr: String
    private lateinit var tempRepository: String
    private lateinit var tempCompany: String
    private lateinit var tempFollowers: String
    private lateinit var tempFollowing: String
    private lateinit var tempLocation: String
    private var fav: Fav? = null
    private lateinit var favHelper: FavHelper
    private var isFavorite = false

    companion object {
        const val EXTRA_USER = "extra_user"
        const val EXTRA_FAV = "extra_fav"
        var EXTRA_USERNAME = "extra_username"
        @StringRes
        private val TAB_TITLES = intArrayOf(
                R.string.follower_tab,
                R.string.following_tab
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favHelper = FavHelper.getInstance(applicationContext)
        favHelper.open()

        fav = intent.getParcelableExtra(EXTRA_FAV)

        if (fav != null) {
            setDataFromFav()
            isFavorite = true
            val fill: Int = R.drawable.ic_fav_fill_24
            btn_favorite.setImageResource(fill)
        } else {
            setDataFromMain()
        }

        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener(this)

        btn_favorite.setOnClickListener(this)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }

    private fun setDataFromFav() {
        val user = intent.getParcelableExtra<Fav>(EXTRA_FAV) as Fav

        EXTRA_USERNAME = user.username

        name_detail.text = user.name
        username_detail.text = user.username
        company_detail.text = "${getString(R.string.company)} ${user.company}"
        location_detail.text = "${getString(R.string.location)} ${user.location}"
        repository_detail.text = "${getString(R.string.repository)} ${user.repository}"
        followers_detail.text = "${getString(R.string.followers)} ${user.followers}"
        following_detail.text = "${getString(R.string.following)} ${user.following}"
        Glide.with(this)
                .load(user.avatar)
                .into(avatar_detail)
        avatarStr = user.avatar
    }

    private fun setDataFromMain() {
        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User

        EXTRA_USERNAME = user.username

        tempCompany = user.company
        tempFollowers = user.followers
        tempFollowing = user.following
        tempRepository = user.repository
        tempLocation = user.location

        name_detail.text = user.name
        username_detail.text = user.username
        company_detail.text = "${getString(R.string.company)} ${user.company}"
        location_detail.text = "${getString(R.string.location)} ${user.location}"
        repository_detail.text = "${getString(R.string.repository)} ${user.repository}"
        followers_detail.text = "${getString(R.string.followers)} ${user.followers}"
        following_detail.text = "${getString(R.string.following)} ${user.following}"
        Glide.with(this)
                .load(user.avatar)
                .into(avatar_detail)
        avatarStr = user.avatar
    }

    override fun onDestroy() {
        super.onDestroy()
        favHelper.close()
    }

    override fun onClick(v: View?) {
        val fill: Int = R.drawable.ic_fav_fill_24
        val border: Int = R.drawable.ic_fav_border_24

        when(v?.id){
            R.id.btn_back -> {
                onBackPressed()
            }

            R.id.btn_favorite -> {
                if (isFavorite) {
                    favHelper.deleteById(fav?.username.toString())
                    //Toast.makeText(this, getString(R.string.delete_favorite), Toast.LENGTH_SHORT).show()
                    btn_favorite.setImageResource(border)
                    isFavorite = false
                } else {
                    val username = username_detail.text.toString()
                    val name = name_detail.text.toString()
                    val avatar = avatarStr
                    val company = tempCompany
                    val location = tempLocation
                    val repository = tempRepository
                    val followers =  tempFollowers
                    val following = tempFollowing
                    val favorite = "1"

                    val values = ContentValues()
                    values.put(USERNAME, username)
                    values.put(NAME, name)
                    values.put(AVATAR, avatar)
                    values.put(COMPANY, company)
                    values.put(LOCATION, location)
                    values.put(REPOSITORY, repository)
                    values.put(FOLLOWING, following)
                    values.put(FOLLOWERS, followers)
                    values.put(FAVORITE, favorite)

                    isFavorite = true
                    contentResolver.insert(CONTENT_URI, values)
                    //Toast.makeText(this, getString(R.string.add_favorite), Toast.LENGTH_SHORT).show()
                    btn_favorite.setImageResource(fill)
                }
            }
        }
    }
}