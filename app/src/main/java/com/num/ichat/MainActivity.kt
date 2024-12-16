package com.num.ichat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.num.ichat.activity.NumberActivity
import com.num.ichat.adapter.ViewPagerAdapter
import com.num.ichat.databinding.ActivityMainBinding
import com.num.ichat.ui.ChatFragment
import com.num.ichat.ui.SearchFragment
import com.num.ichat.ui.SettingFragment

class   MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()

        // Check if user is authenticated
        if (auth.currentUser == null) {
            startActivity(Intent(this, NumberActivity::class.java))
            finish()
            return
        }

        // Initialize fragments
        val fragmentArrayList = arrayListOf<Fragment>(
            ChatFragment(),
            SearchFragment(),
            SettingFragment()
        )

        // Set up ViewPager with adapter
        val adapter = ViewPagerAdapter(this, supportFragmentManager, fragmentArrayList)
        binding?.viewPager?.adapter = adapter
        binding?.tabs?.setupWithViewPager(binding?.viewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
