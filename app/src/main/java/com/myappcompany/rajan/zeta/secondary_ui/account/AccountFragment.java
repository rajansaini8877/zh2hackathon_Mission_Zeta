package com.myappcompany.rajan.zeta.secondary_ui.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.zeta.EntryActivity;
import com.myappcompany.rajan.zeta.MainActivity;
import com.myappcompany.rajan.zeta.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    private TextView mEmailTextView;
    private Button mPinButton;
    private Button mLogoutButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String mPin = null;
    private String mDocRef = null;

    private boolean isPinCreated = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_secondary_account, container, false);

        mEmailTextView = v.findViewById(R.id.email_text_view);
        mPinButton = v.findViewById(R.id.pin_button);
        mLogoutButton = v.findViewById(R.id.logout_button);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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

    private void fetchCredentials() {

        mEmailTextView.setText(mAuth.getCurrentUser().getEmail());

        db.collection("secondary_users")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                        if(error != null) {
                            return;
                        }

                        mPin = value.getDocuments().get(0).getString("pin");
                        mDocRef = value.getDocuments().get(0).getId();

                        if(mPin.equals("NA")) {
                            isPinCreated = false;
                            mPinButton.setText("CREATE PIN");
                            mPinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setCreatePinListener();
                                }
                            });

                        }
                        else {
                            isPinCreated = true;
                            mPinButton.setText("CHANGE PIN");
                            mPinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setChangePinListener();
                                }
                            });

                        }
                    }
                });

    }

    private void setCreatePinListener() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Enter the required credentials and click submit");
        dialogBuilder.setCancelable(false);

        View view = View.inflate(getActivity(), R.layout.dialog_create_pin, null);
        final EditText pinEditText = view.findViewById(R.id.pin_edit_text);
        final EditText confirmPinEditText = view.findViewById(R.id.confirm_pin_edit_text);

        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton("Submit", null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pin = pinEditText.getText().toString();
                String confirmPin = pinEditText.getText().toString();

                if(!pin.equals(confirmPin)) {
                    Toast.makeText(getActivity(), "PIN doesn't match!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!MainActivity.isPinFormatted(pin)) {
                    Toast.makeText(getActivity(), "PIN must be of 6 digit only", Toast.LENGTH_LONG).show();
                    return;
                }

                updatePin(pin);
            }
        });

    }

    private void setChangePinListener() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Enter the required credentials and click submit");
        dialogBuilder.setCancelable(false);

        View view = View.inflate(getActivity(), R.layout.dialog_change_pin, null);
        final EditText currentPinEditText = view.findViewById(R.id.current_pin_edit_text);
        final EditText newPinEditText = view.findViewById(R.id.new_pin_edit_text);
        final EditText confirmPinEditText = view.findViewById(R.id.confirm_pin_edit_text);

        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton("Submit", null);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentPin = currentPinEditText.getText().toString();
                String newPin = newPinEditText.getText().toString();
                String confirmPin = confirmPinEditText.getText().toString();

                if(!mPin.equals(currentPin) || !newPin.equals(confirmPin)) {
                    Toast.makeText(getActivity(), "PIN doesn't match!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!MainActivity.isPinFormatted(newPin)) {
                    Toast.makeText(getActivity(), "PIN must be of 6 digit only", Toast.LENGTH_LONG).show();
                    return;
                }

                updatePin(newPin);
            }
        });

    }

    public void updatePin(String newPin) {

        db.collection("secondary_users")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()) {
                            return;
                        }

                        Map<String, Object> map = new HashMap<>();
                        map.put("pin", newPin);

                        db.collection("secondary_users")
                                .document(mDocRef)
                                .update(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "PIN updated successfully!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCredentials();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}