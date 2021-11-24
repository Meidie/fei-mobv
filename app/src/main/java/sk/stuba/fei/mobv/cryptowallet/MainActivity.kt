package sk.stuba.fei.mobv.cryptowallet

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import sk.stuba.fei.mobv.cryptowallet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.transactionListFragment, R.id.contactListFragment,
            R.id.loginFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNavigation.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}