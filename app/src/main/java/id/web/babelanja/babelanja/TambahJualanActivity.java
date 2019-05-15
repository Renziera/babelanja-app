package id.web.babelanja.babelanja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class TambahJualanActivity extends AppCompatActivity {

    boolean edit;
    String uid;
    DocumentReference docRef;

    ImageButton button_pick;
    ImageView[] foto = new ImageView[7];
    List<Image> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jualan);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        edit = getIntent().getBooleanExtra("edit", false);

        if(edit){
            getSupportActionBar().setTitle("Edit Jualan");
        }else{
            getSupportActionBar().setTitle("Tambah Jualan");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_pick = findViewById(R.id.button_pick);
        foto[0] = findViewById(R.id.foto_1);
        foto[1] = findViewById(R.id.foto_2);
        foto[2] = findViewById(R.id.foto_3);
        foto[3] = findViewById(R.id.foto_4);
        foto[4] = findViewById(R.id.foto_5);
        foto[5] = findViewById(R.id.foto_6);
        foto[6] = findViewById(R.id.foto_7);

        Glide.with(this)
                .load(new ColorDrawable(getResources().getColor(R.color.hijauMuda)))
                .transform(new RoundedCorners(64))
                .into(button_pick);

        button_pick.setOnClickListener(v -> {
            ImagePicker.create(this)
                    .returnMode(ReturnMode.NONE)
                    .limit(7)
                    .theme(R.style.AppThemeNoActionBar)
                    .toolbarDoneButtonText("OK")
                    .toolbarImageTitle("Pilih foto")
                    .start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            for (ImageView imgview : foto) {
                imgview.setVisibility(View.GONE);
            }

            imageList = ImagePicker.getImages(data);

            for (int i = 0; i < imageList.size(); i++) {
                foto[i].setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageList.get(i).getPath())
                        .transform(new CenterCrop(), new RoundedCorners(64))
                        .into(foto[i]);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
