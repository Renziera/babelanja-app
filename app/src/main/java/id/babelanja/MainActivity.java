package id.babelanja;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    BottomNavigationView bottomNavigationView;

    TextView tv_nama;
    ImageView profilePhoto;

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

        tv_nama = navigationView.getHeaderView(0).findViewById(R.id.tv_nama);
        profilePhoto = navigationView.getHeaderView(0).findViewById(R.id.profilePhoto);

        navigationView.getHeaderView(0)
                .findViewById(R.id.button_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
            intent.putExtra("edit", true);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.button_tentang).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TentangActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("toko").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       DocumentSnapshot document = task.getResult();
                       tv_nama.setText("Hai, " + document.getString("nama").split(" ", 2)[0]);
                       Glide.with(MainActivity.this)
                               .load(document.getString("foto"))
                               .circleCrop()
                               .into(profilePhoto);
                   }else {
                       Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                   }
                });
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
                startActivity(new Intent(MainActivity.this, JualanActivity.class));
                break;
            case R.id.nav_transaksi:
                startActivity(new Intent(MainActivity.this, TransaksiActivity.class));
                break;
            case R.id.nav_pesan:
                startActivity(new Intent(MainActivity.this, PesanActivity.class));
                break;
            case R.id.nav_hadiah:
                startActivity(new Intent(MainActivity.this, HadiahActivity.class));
                break;
            case R.id.nav_bantuan:
                startActivity(new Intent(MainActivity.this, BantuanActivity.class));
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
