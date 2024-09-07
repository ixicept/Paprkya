package edu.bluejack24_1.papryka.activities

import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import edu.bluejack24_1.papryka.R
import edu.bluejack24_1.papryka.databinding.ActivityMainBinding
import edu.bluejack24_1.papryka.fragments.HomeFragment
import edu.bluejack24_1.papryka.fragments.JobListFragment
import edu.bluejack24_1.papryka.fragments.RoomFragment
import edu.bluejack24_1.papryka.fragments.ScheduleFragment
import edu.bluejack24_1.papryka.fragments.SettingsFragment
import edu.bluejack24_1.papryka.utils.ProgressBarUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)
        replaceFragment(HomeFragment())

        binding.botNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    replaceFragment(HomeFragment())
                }

                R.id.menuJob -> {
                    replaceFragment(JobListFragment())
                }

                R.id.menuSchedule -> {
                    replaceFragment(ScheduleFragment())
                }

                R.id.menuRoom -> {
                    replaceFragment(RoomFragment())
                }

                R.id.menuSettings -> {
                    replaceFragment(SettingsFragment())
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        hideProgressBar()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun showProgressBar() {
        ProgressBarUtils.show(progressBar)
    }

    fun hideProgressBar() {
        ProgressBarUtils.hide(progressBar)
    }
}