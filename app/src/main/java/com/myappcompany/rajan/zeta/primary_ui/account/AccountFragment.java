package com.myappcompany.rajan.zeta.primary_ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.myappcompany.rajan.zeta.EntryActivity;
import com.myappcompany.rajan.zeta.R;

public class AccountFragment extends Fragment {

    private TextView mEmailTextView;
    private Button mLogoutButton;

    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_primary_account, container, false);

        mEmailTextView = v.findViewById(R.id.email_text_view);
        mLogoutButton = v.findViewById(R.id.logout_button);
        mAuth = FirebaseAuth.getInstance();

        mEmailTextView.setText(mAuth.getCurrentUser().getEmail());
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), EntryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}