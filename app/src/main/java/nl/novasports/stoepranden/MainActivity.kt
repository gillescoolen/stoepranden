package nl.novasports.stoepranden

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import nl.novasports.stoepranden.fragment.GamesFragment
import nl.novasports.stoepranden.fragment.MyAccountFragment
import nl.novasports.stoepranden.fragment.PeopleFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(PeopleFragment())

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_people -> {
                    replaceFragment(PeopleFragment())
                    true
                }
                R.id.navigation_games -> {
                    replaceFragment(GamesFragment())
                    true
                }
                R.id.navigation_my_account -> {
                    replaceFragment(MyAccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rules_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_jan -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jantjebeton.nl/"))
                startActivity(browserIntent)
                return true
            }
            R.id.navigation_nova -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://novasports.nl/webshop/"))
                startActivity(browserIntent)
                return true
            }
            R.id.navigation_sport -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sportenco.nl/"))
                startActivity(browserIntent)
                return true
            }
            R.id.navigation_rules -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.novasports.nl/app/download/11191505421/Spelopzet,%20spelregels%20en%20puntentelling%20-%202017%2005%2030.pdf?t=1525167518"))
                startActivity(browserIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit()
    }
}
