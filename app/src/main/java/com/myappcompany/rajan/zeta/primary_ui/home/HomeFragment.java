package com.myappcompany.rajan.zeta.primary_ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myappcompany.rajan.zeta.R;

public class HomeFragment extends Fragment {

    private TextView mHomeTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_primary_home, container, false);

        mHomeTextView = root.findViewById(R.id.text_home);
        mHomeTextView.setText("Conventional Mobile banking can be added here..");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}