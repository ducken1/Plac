package com.example.plac

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.plac.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Math.abs
import java.lang.Math.max

class MainActivity : AppCompatActivity(), LoginFragment.OnLoginSuccessListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var  databaseReference: DatabaseReference

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkUserLoginStatus()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.drawer_layout)

//        appBarConfiguration = AppBarConfiguration(setOf(R.id.loginFragment, R.id.registerFragment, R.id.homeFragment, R.id.oglasFragment), drawerLayout)
//
//        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController) //appbarconfig.. ampak pol se mapa zjebe

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.loginFragment -> {
                    navController.navigate(R.id.loginFragment)
                }
                R.id.registerFragment -> {
                    navController.navigate(R.id.registerFragment)
                }
                R.id.oglasFragment -> {
                    navController.navigate(R.id.oglasFragment)
                }

            }

            // Close the drawer if you have a drawerLayout
            drawerLayout?.closeDrawer(GravityCompat.START)

            // Return true to indicate that the item click has been handled
            true
        }


//        binding.navView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.homeFragment -> {
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(R.id.homeFragment, false) // Clear the back stack to ensure HomeFragment is recreated
//                        .build()
//                    navController.navigate(R.id.action_homeFragment_self, null, navOptions)
//                }
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }


        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        //Log.d("MyTag", "User ID: $userId")

        if (userId != null) {

            val databaseReference = FirebaseDatabase.getInstance().reference
            val userReference = databaseReference.child("Users").child(userId) // Replace 'users' with your database structure

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userName = dataSnapshot.child("name").getValue(String::class.java)
                        Log.d("Firebase", "User Name from Database: $userName")
                        updateUserNameInNavHeader(userName)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.navView.getHeaderView(0).findViewById<Button>(R.id.logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            updateUserNameInNavHeader("Mr. XYZ")
            Toast.makeText(this@MainActivity, "Successfully logged out", Toast.LENGTH_SHORT).show()
            binding.navView.findViewById<Button>(R.id.logout)?.visibility = View.GONE
            binding.navView.menu.findItem(R.id.loginFragment)?.isVisible = true
            binding.navView.menu.findItem(R.id.registerFragment)?.isVisible = true
            binding.navView.menu.findItem(R.id.oglasFragment)?.isVisible = false
        }

    }
    override fun onLoginSuccess(userName: String?) {
        updateUserNameInNavHeader(userName)

        binding.navView.findViewById<Button>(R.id.logout)?.visibility = View.VISIBLE
        binding.navView.menu.findItem(R.id.loginFragment)?.isVisible = false
        binding.navView.menu.findItem(R.id.registerFragment)?.isVisible = false
        binding.navView.menu.findItem(R.id.oglasFragment)?.isVisible = true
    }
    fun updateUserNameInNavHeader(userName: String?) {
        val userNameTextView = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.user_name)

        userNameTextView?.text = userName
    }
    private fun checkUserLoginStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.navView.getHeaderView(0).findViewById<Button>(R.id.logout)?.visibility = View.VISIBLE
            binding.navView.menu.findItem(R.id.loginFragment)?.isVisible = false
            binding.navView.menu.findItem(R.id.registerFragment)?.isVisible = false
            binding.navView.menu.findItem(R.id.oglasFragment)?.isVisible = true
        } else {
            binding.navView.getHeaderView(0).findViewById<Button>(R.id.logout)?.visibility = View.GONE
            binding.navView.menu.findItem(R.id.loginFragment)?.isVisible = true
            binding.navView.menu.findItem(R.id.registerFragment)?.isVisible = true
            binding.navView.menu.findItem(R.id.oglasFragment)?.isVisible = false
        }
    }
}