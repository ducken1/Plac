package com.example.plac


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.plac.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), LoginFragment.OnLoginSuccessListener {

    private lateinit var binding: ActivityMainBinding

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

            drawerLayout.closeDrawer(GravityCompat.START)

            true
        }

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {

            val databaseReference = FirebaseDatabase.getInstance().reference
            val userReference = databaseReference.child("Users").child(userId)

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userName = dataSnapshot.child("name").getValue(String::class.java)
                        //Log.d("Firebase", "User Name from Database: $userName")
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