package com.dicoding.consumerapp.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.consumerapp.R
import com.dicoding.consumerapp.data.Fav
import com.dicoding.consumerapp.data.User
import com.dicoding.consumerapp.databinding.ActivityDetailBinding
import com.dicoding.githubuser.adapter.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ActivityDetailBinding
    private lateinit var avatarStr: String

    companion object {
        const val EXTRA_FAV = "extra_fav"
        var IS_FAV = 0
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

        if(IS_FAV == 0) {
            setDataFromFav()
        }else{
            setDataFromUser()
        }

        btn_back.setOnClickListener(this)

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

    private fun setDataFromUser() {
        val user = intent.getParcelableExtra<Fav>(EXTRA_FAV) as User

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

    override fun onClick(v: View?) {
        if(v?.id == R.id.btn_back){
            onBackPressed()
        }
    }
}