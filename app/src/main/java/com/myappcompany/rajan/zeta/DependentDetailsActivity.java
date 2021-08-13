package com.myappcompany.rajan.zeta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.zeta.model.MyTransactionListItemAdapter;
import com.myappcompany.rajan.zeta.model.TransactionComparator;
import com.myappcompany.rajan.zeta.model.TransactionItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DependentDetailsActivity extends AppCompatActivity {

    private static final String EMAIL = "email";

    private String mEmail = null;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private ArrayList<TransactionItem> mTransactionItems;
    private TextView mNameTextView;
    private TextView mBalanceTextView;
    private Button mTopupButton;

    private Double mBalance = 0.0;
    private String mDocRef = null;

    public static Intent newIntent(Context context, String email) {
        Intent i = new Intent(context, DependentDetailsActivity.class);
        i.putExtra(EMAIL, email);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_secondary_dashboard);

        mEmail = getIntent().getStringExtra(EMAIL);
        db = FirebaseFirestore.getInstance();
        mRecyclerView = findViewById(R.id.list);
        mTransactionItems = new ArrayList<>();

        mNameTextView = findViewById(R.id.name_text_view);
        mBalanceTextView = findViewById(R.id.balance_text_view);
        mTopupButton = findViewById(R.id.topup_button);

        mTopupButton.setText("Top-up");
        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DependentDetailsActivity.this);
                dialogBuilder.setMessage("Enter the required credentials and click submit");
                dialogBuilder.setCancelable(false);

                View view = View.inflate(DependentDetailsActivity.this, R.layout.dialog_topup, null);
                final EditText amountEditText = view.findViewById(R.id.amount_edit_text);

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

                        if(TextUtils.isEmpty(amount)) {
                            Toast.makeText(DependentDetailsActivity.this, "Enter all credentials first!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        transact(amount);
                        dialog.dismiss();
                    }
                });
            }
        });

        fetchTransactions();
    }

    private void transact(String amount) {

        Map<String, Object> map = new HashMap<>();
        map.put("balance", mBalance+Double.parseDouble(amount));

        db.collection("secondary_users")
                .document(mDocRef)
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DependentDetailsActivity.this, "Transaction Successful!", Toast.LENGTH_LONG).show();
                        String id = String.valueOf(Math.round(100000000.0 + (Math.random()*99999999.0)));
                        addTransaction(id, true, amount);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(DependentDetailsActivity.this, "Transaction Failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void fetchTransactions() {

        db.collection("secondary_users")
                .whereEqualTo("email", mEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if(!task.isSuccessful()) {
                            Toast.makeText(DependentDetailsActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String name = task.getResult().getDocuments().get(0).getString("name");
                        double balance = task.getResult().getDocuments().get(0).getDouble("balance");
                        mBalance = balance;
                        mDocRef = task.getResult().getDocuments().get(0).getId();

                        mNameTextView.setText(name);
                        mBalanceTextView.setText("\u20B9 "+Double.toString(balance));

                        db.collection("secondary_users/"+task.getResult().getDocuments().get(0).getId()+"/transactions")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                                        if(task.isSuccessful()) {

                                            for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                                String id = documentSnapshot.getString("id");
                                                Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                                                boolean status = documentSnapshot.getBoolean("success");
                                                double amount = documentSnapshot.getDouble("amount");

                                                mTransactionItems.add(new TransactionItem(id, timestamp, status, amount));
                                            }

                                            Collections.sort(mTransactionItems, new TransactionComparator());
                                            mRecyclerView.setAdapter(new MyTransactionListItemAdapter(mTransactionItems, DependentDetailsActivity.this));
                                        }

                                        else {

                                            Toast.makeText(DependentDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            return;

                                        }
                                    }
                                });
                    }
                });

    }

    private void addTransaction(String id, boolean success, String amount) {

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("success", success);
        data.put("amount", (Double.parseDouble(amount)));
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
}