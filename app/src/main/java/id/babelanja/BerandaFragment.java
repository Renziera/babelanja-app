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

import java.util.Objects;

import timber.log.Timber;

public class BerandaFragment extends Fragment {

    EditText tv_search;
    ImageButton button_menu;
    ImageButton button_notifikasi;

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

        tv_search = view.findViewById(R.id.tv_search);
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
