package com.eighteam.ojek

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.eighteam.ojek.activities.LoginActivity
import com.eighteam.ojek.databinding.ActivityHomeBinding
import com.eighteam.ojek.ui.splashscreen.SplashScreenActivity
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        init()
    }

    @Suppress("DEPRECATION")
    private fun init() {
        navView.setNavigationItemSelectedListener {

            //when clicked on sign Out
            if (it.itemId == R.id.nav_sign_out) {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setTitle("Sign out")
                    setMessage("Do you want to sign out?")
                    setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
                    setPositiveButton("SIGN OUT") { _, _ ->
                        //FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@HomeActivity, SplashScreenActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }.setCancelable(false)

                    val dialog = builder.create()
                    dialog.setOnShowListener {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(
                                ContextCompat.getColor(
                                    this@HomeActivity,
                                    android.R.color.holo_red_dark
                                )
                            )

                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(
                                ContextCompat.getColor(
                                    this@HomeActivity,
                                    R.color.black
                                )
                            )
                    }
                    dialog.show()
                }
            }

            true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}