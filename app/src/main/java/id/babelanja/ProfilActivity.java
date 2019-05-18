package id.babelanja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class ProfilActivity extends AppCompatActivity {

    DocumentReference docRef;

    Button button_simpan;
    ImageButton button_foto;
    EditText tv_nama;
    TextView tv_email;
    TextView tv_nomor;
    Button button_ubah;
    RadioGroup gender;
    Spinner spinnerKabupaten;
    Spinner spinnerKecamatan;
    EditText tv_kelurahan;
    EditText tv_alamat;

    String imagePath = null;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        if(getIntent().getBooleanExtra("edit", false)){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("Edit Profil");

        docRef = FirebaseFirestore.getInstance().collection("toko")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        button_simpan = findViewById(R.id.button_simpan);
        button_foto = findViewById(R.id.button_foto);
        tv_nama = findViewById(R.id.tv_nama);
        tv_email = findViewById(R.id.tv_email);
        tv_nomor = findViewById(R.id.tv_nomor);
        button_ubah = findViewById(R.id.button_ubah);
        gender = findViewById(R.id.rg_gender);
        spinnerKabupaten = findViewById(R.id.spinnerKabupaten);
        spinnerKecamatan = findViewById(R.id.spinnerKecamatan);
        tv_kelurahan = findViewById(R.id.tv_kelurahan);
        tv_alamat = findViewById(R.id.tv_alamat);

        button_ubah.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhoneAuthActivity.class);
            intent.putExtra("edit", true);
            startActivity(intent);
        });

        spinnerKabupaten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> adapter = null;
                switch (position){
                    case 0:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.bangka, android.R.layout.simple_spinner_item);
                        break;
                    case 1:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.barat, android.R.layout.simple_spinner_item);
                        break;
                    case 2:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.selatan, android.R.layout.simple_spinner_item);
                        break;
                    case 3:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.tengah, android.R.layout.simple_spinner_item);
                        break;
                    case 4:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.belitung, android.R.layout.simple_spinner_item);
                        break;
                    case 5:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.timur, android.R.layout.simple_spinner_item);
                        break;
                    case 6:
                        adapter = ArrayAdapter.createFromResource(
                                ProfilActivity.this, R.array.pinang, android.R.layout.simple_spinner_item);
                        break;
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKecamatan.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button_foto.setOnClickListener(v -> {
            ImagePicker.create(this)
                    .returnMode(ReturnMode.ALL)
                    .single()
                    .theme(R.style.AppThemeNoActionBar)
                    .toolbarImageTitle("Pilih foto profil anda")
                    .start();
        });

        button_simpan.setOnClickListener(v -> {
            Toast.makeText(this, "Sedang proses", Toast.LENGTH_SHORT).show();
            button_simpan.setEnabled(false);
            if(imagePath == null){

                Map<String, Object> map = new HashMap<>();
                map.put("nama", tv_nama.getText().toString());
                if(gender.getCheckedRadioButtonId() == R.id.rb_pria){
                    map.put("pria", true);
                }else if(gender.getCheckedRadioButtonId() == R.id.rb_wanita){
                    map.put("pria", false);
                }
                map.put("kabupaten", spinnerKabupaten.getSelectedItem().toString());
                map.put("kecamatan", spinnerKecamatan.getSelectedItem().toString());
                if(!tv_kelurahan.getText().toString().isEmpty()){
                    map.put("kelurahan", tv_kelurahan.getText().toString());
                }
                if(!tv_alamat.getText().toString().isEmpty()){
                    map.put("alamat", tv_alamat.getText().toString());
                }
                map.put("timestamp_update_profile", FieldValue.serverTimestamp());

                docRef.update(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Berhasil update profil", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                        button_simpan.setEnabled(true);
                    }
                });

            }else{
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference ref = storage
                        .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(UUID.randomUUID().toString());

                ref.putFile(Uri.fromFile(new File(imagePath))).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                ref.getDownloadUrl().addOnCompleteListener(task1 -> {
                                   if(task1.isSuccessful()){

                                       Uri downloadUri = task1.getResult();
                                       Timber.d("URL: %s", downloadUri.toString());

                                       Map<String, Object> map = new HashMap<>();
                                       map.put("foto", downloadUri.toString());
                                       map.put("nama", tv_nama.getText().toString());
                                       if(gender.getCheckedRadioButtonId() == R.id.rb_pria){
                                           map.put("pria", true);
                                       }else if(gender.getCheckedRadioButtonId() == R.id.rb_wanita){
                                           map.put("pria", false);
                                       }
                                       map.put("kabupaten", spinnerKabupaten.getSelectedItem().toString());
                                       map.put("kecamatan", spinnerKecamatan.getSelectedItem().toString());
                                       if(!tv_kelurahan.getText().toString().isEmpty()){
                                           map.put("kelurahan", tv_kelurahan.getText().toString());
                                       }
                                       if(!tv_alamat.getText().toString().isEmpty()){
                                           map.put("alamat", tv_alamat.getText().toString());
                                       }
                                       map.put("timestamp_update_profile", FieldValue.serverTimestamp());

                                       docRef.update(map).addOnCompleteListener(task2 -> {
                                          if(task2.isSuccessful()){
                                              Toast.makeText(this, "Berhasil update profil", Toast.LENGTH_SHORT).show();
                                              finish();
                                          }else {
                                              ref.delete();
                                              Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                                              button_simpan.setEnabled(true);
                                          }
                                       });

                                   }else {
                                       ref.delete();
                                       Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                                       button_simpan.setEnabled(true);
                                   }
                                });
                            }else{
                                Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                                button_simpan.setEnabled(true);
                            }
                        });

                try{
                    storage.getReferenceFromUrl(imageUrl).delete();
                }catch (Exception e){
                    Timber.d(e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();

                imageUrl = document.getString("foto");

                if(imagePath == null){
                    Glide.with(ProfilActivity.this)
                            .load(document.getString("foto"))
                            .circleCrop()
                            .into(button_foto);
                }

                tv_nama.setText(document.getString("nama"));
                tv_email.setText(document.getString("email"));
                tv_nomor.setText(document.getString("telepon"));

                if(document.getBoolean("pria") != null){
                    if(document.getBoolean("pria")){
                        gender.check(R.id.rb_pria);
                    }else{
                        gender.check(R.id.rb_wanita);
                    }
                }

                String kabupaten = document.getString("kabupaten");
                String kecamatan = document.getString("kecamatan");
                String kelurahan = document.getString("kelurahan");
                String alamat = document.getString("alamat");

                if(kabupaten != null){
                    spinnerKabupaten.setSelection(Arrays.asList(getResources().getStringArray(R.array.kabupaten)).indexOf(kabupaten));
                    if(kecamatan != null){
                        for (int i = 0; i < spinnerKecamatan.getAdapter().getCount(); i++) {
                            if(spinnerKecamatan.getAdapter().getItem(i).equals(kecamatan)){
                                spinnerKecamatan.setSelection(i);
                                break;
                            }
                        }
                    }
                }

                tv_kelurahan.setText(kelurahan);
                tv_alamat.setText(alamat);

            }else{
                Toast.makeText(this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            Image image = ImagePicker.getFirstImageOrNull(data);
            imagePath = image.getPath();
            Glide.with(this)
                    .load(new File(imagePath))
                    .circleCrop()
                    .into(button_foto);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
