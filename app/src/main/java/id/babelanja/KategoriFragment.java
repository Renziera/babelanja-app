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
import android.widget.TableLayout;
import android.widget.ToggleButton;

import java.util.Objects;

import timber.log.Timber;

public class KategoriFragment extends Fragment {


    public KategoriFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kategori, container, false);

        TableLayout table_barang = view.findViewById(R.id.table_barang);
        TableLayout table_jasa = view.findViewById(R.id.table_jasa);
        table_jasa.setVisibility(View.GONE);

        ToggleButton toogle = view.findViewById(R.id.toggle);
        toogle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                table_jasa.setVisibility(View.VISIBLE);
                table_barang.setVisibility(View.GONE);
            }else {
                table_barang.setVisibility(View.VISIBLE);
                table_jasa.setVisibility(View.GONE);
            }
        });

        ImageButton button_menu = view.findViewById(R.id.button_menu);
        button_menu.setOnClickListener(v -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).drawer.openDrawer(GravityCompat.START);
        });

        ImageButton button_notifikasi = view.findViewById(R.id.button_notifikasi);
        button_notifikasi.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotifikasiActivity.class);
            startActivity(intent);
        });

        EditText tv_search = view.findViewById(R.id.tv_search);
        tv_search.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                Timber.d("Search called");
                return true;
            }
            return false;
        });

        return view;
    }

}
