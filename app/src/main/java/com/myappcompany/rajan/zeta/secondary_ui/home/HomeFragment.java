package com.myappcompany.rajan.zeta.secondary_ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.zeta.CodeScannerActivity;
import com.myappcompany.rajan.zeta.MainActivity;
import com.myappcompany.rajan.zeta.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ImageView mScanQrImage;
    private RadioGroup mRadioGroup;
    private RadioButton mUpiRadioButton;
    private RadioButton mAccountRadioButton;
    private EditText mCredentialsEditTextOne;
    private EditText mCredentialsEditTextTwo;
    private Button mSendButton;
    private TextInputLayout mIfscParent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String mDocRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_secondary_home, container, false);

        mScanQrImage = v.findViewById(R.id.scan_qr_image);
        mRadioGroup = v.findViewById(R.id.radio_button_group);
        mUpiRadioButton = v.findViewById(R.id.upi_radio_button);
        mAccountRadioButton = v.findViewById(R.id.account_radio_button);
        mCredentialsEditTextOne = v.findViewById(R.id.credentials_edit_text_1);
        mCredentialsEditTextTwo = v.findViewById(R.id.credentials_edit_text_2);
        mSendButton = v.findViewById(R.id.send_button);
        mIfscParent = v.findViewById(R.id.ifsc_parent);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mScanQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = CodeScannerActivity.newIntent(getActivity());
                startActivity(i);
            }
        });

        mUpiRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    mIfscParent.setVisibility(View.GONE);
                    mCredentialsEditTextOne.setHint("UPI ID");
                }
            }
        });

        mAccountRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    mIfscParent.setVisibility(View.VISIBLE);
                    mCredentialsEditTextOne.setHint("Account Number");
                    mCredentialsEditTextOne.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {

                if(mUpiRadioButton.isChecked()) {

                    String upi = mCredentialsEditTextOne.getText().toString();

                    if(TextUtils.isEmpty(upi)) {
                        Toast.makeText(getActivity(), "Enter required credentials first!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else {

                    String account = mCredentialsEditTextOne.getText().toString();
                    String ifsc = mCredentialsEditTextTwo.getText().toString();

                    if(TextUtils.isEmpty(account) || TextUtils.isEmpty(ifsc)) {
                        Toast.makeText(getActivity(), "Enter required credentials first!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setMessage("Enter the required credentials and click submit");
                dialogBuilder.setCancelable(false);

                View view = View.inflate(getActivity(), R.layout.dialog_enter_pin, null);
                final EditText amountEditText = view.findViewById(R.id.amount_edit_text);
                final EditText pinEditText = view.findViewById(R.id.pin_edit_text);


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

                        String amount = amountEditText.getText().toString();
                        String pin = pinEditText.getText().toString();

                        if(TextUtils.isEmpty(amount) || TextUtils.isEmpty(pin)) {
                            Toast.makeText(getActivity(), "Enter all credentials first!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(!MainActivity.isPinFormatted(pin)) {
                            Toast.makeText(getActivity(), "Incorrect PIN format", Toast.LENGTH_LONG).show();
                            return;
                        }

                        transact(pin, amount);
                        dialog.dismiss();
                    }
                });
            }
        });

        return v;
    }

    private void transact(String pin, String amount) {

        String id = String.valueOf(Math.round(100000000.0 + (Math.random()*99999999.0)));

        db.collection("secondary_users")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()) {
                            return;
                        }

                        if(task.getResult().getDocuments().get(0).getString("pin").equals("NA")) {
                            Toast.makeText(getActivity(), "Create your PIN first from Account menu!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mDocRef = task.getResult().getDocuments().get(0).getId();
                        final double balance = task.getResult().getDocuments().get(0).getDouble("balance");

                        if(task.getResult().getDocuments().get(0).getString("pin").equals(pin) && Double.parseDouble(amount)<=balance) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("balance", balance-Double.parseDouble(amount));

                            db.collection("secondary_users")
                                    .document(mDocRef)
                                    .update(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            addTransaction(id, true, amount);
                                            Toast.makeText(getActivity(), "Transaction Successful!", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            addTransaction(id, false, amount);
                                            Toast.makeText(getActivity(), "Transaction Failed!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else {
                            addTransaction(id, false, amount);
                            Toast.makeText(getActivity(), "Transaction Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addTransaction(String id, boolean success, String amount) {

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("success", success);
        data.put("amount", -(Double.parseDouble(amount)));
        data.put("timestamp", FieldValue.serverTimestamp());

        db.collection("secondary_users/"+mDocRef+"/transactions/")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}