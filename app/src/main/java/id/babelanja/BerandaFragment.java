package id.babelanja;


import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;

import java.util.Objects;

import timber.log.Timber;

public class BerandaFragment extends Fragment {

    EditText tv_search;
    ImageButton button_menu;
    ImageButton button_notifikasi;

    CarouselView carouselView;

    public BerandaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        button_menu = view.findViewById(R.id.button_menu);
        button_menu.setOnClickListener(v -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).drawer.openDrawer(GravityCompat.START);
        });
        //TODO https://github.com/nex3z/NotificationBadge
        button_notifikasi = view.findViewById(R.id.button_notifikasi);
        button_notifikasi.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotifikasiActivity.class);
            startActivity(intent);
        });

        int[] sample = {R.drawable.sample1, R.drawable.sample2, R.drawable.sample3, R.drawable.sample4};

        //TODO https://github.com/sayyam/carouselview
        carouselView = view.findViewById(R.id.carousel);
        carouselView.setPageCount(sample.length);
        carouselView.setImageListener((position, imageView) -> imageView.setImageResource(sample[position]));

        view.findViewById(R.id.button_pasang_iklan)
                .setOnClickListener(v -> Toast.makeText(getActivity(), "Belum tersedia", Toast.LENGTH_SHORT).show());

        tv_search = view.findViewById(R.id.tv_search);
        tv_search.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                Timber.d("Search called");
                return true;
            }
            return false;
        });
        //TODO icon pada home adalah yang paling sering diakses kalo pertama buka dan belom akses apapun iconnya random aja
        return view;
    }

}
