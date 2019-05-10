package id.web.babelanja.babelanja;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        FirebaseApp.initializeApp(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new BerandaFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_jualan:

                break;
            case R.id.nav_transaksi:

                break;
            case R.id.nav_pesan:

                break;
            case R.id.nav_hadiah:

                break;
            case R.id.nav_bantuan:
                Intent intent = new Intent(MainActivity.this, BantuanActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_beranda:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new BerandaFragment())
                        .commit();
                break;
            case R.id.nav_kategori:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new KategoriFragment())
                        .commit();
                break;
            case R.id.nav_forum:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new ForumFragment())
                        .commit();
                break;
            case R.id.nav_keranjang:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new KeranjangFragment())
                        .commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
