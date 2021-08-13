package com.myappcompany.rajan.zeta.secondary_ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.zeta.R;
import com.myappcompany.rajan.zeta.model.DependentItem;
import com.myappcompany.rajan.zeta.model.MyDependentListItemAdapter;
import com.myappcompany.rajan.zeta.model.MyTransactionListItemAdapter;
import com.myappcompany.rajan.zeta.model.TransactionItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class DashboardFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<TransactionItem> mTransactionItems;
    private TextView mNameTextView;
    private TextView mBalanceTextView;
    private Button mTopupButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_secondary_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mRecyclerView = v.findViewById(R.id.list);
        mTransactionItems = new ArrayList<>();

        mNameTextView = v.findViewById(R.id.name_text_view);
        mBalanceTextView = v.findViewById(R.id.balance_text_view);
        mTopupButton = v.findViewById(R.id.topup_button);
        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Top-up requested successfully!", Toast.LENGTH_LONG).show();
            }
        });

        fetchTransactions();

        return v;
    }

    public void fetchTransactions() {

        db.collection("secondary_users")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                        if(error != null) {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String name = value.getDocuments().get(0).getString("name");
                        double balance = value.getDocuments().get(0).getDouble("balance");

                        mNameTextView.setText(name);
                        mBalanceTextView.setText("\u20B9 "+Double.toString(balance));

                        db.collection("secondary_users/"+value.getDocuments().get(0).getId()+"/transactions")
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

                                            mRecyclerView.setAdapter(new MyTransactionListItemAdapter(mTransactionItems, getActivity()));
                                        }

                                        else {

                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            return;

                                        }
                                    }
                                });
                    }
                });

    }

}