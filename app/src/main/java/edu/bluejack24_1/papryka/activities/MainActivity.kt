package edu.bluejack24_1.papryka.activities

import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}