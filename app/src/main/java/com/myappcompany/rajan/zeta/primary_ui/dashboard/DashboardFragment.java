package com.myappcompany.rajan.zeta.primary_ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.zeta.EntryActivity;
import com.myappcompany.rajan.zeta.MainActivity;
import com.myappcompany.rajan.zeta.R;
import com.myappcompany.rajan.zeta.model.DependentItem;
import com.myappcompany.rajan.zeta.model.MyDependentListItemAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<DependentItem> mDependentItems;

    private FloatingActionButton mFab;
    private String mDocRef = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_primary_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mRecyclerView = v.findViewById(R.id.list);
        mDependentItems = new ArrayList<>();

        mFab = v.findViewById(R.id.add_fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setMessage("Enter the required credentials and click submit");
                dialogBuilder.setCancelable(false);

                View view = View.inflate(getActivity(), R.layout.dialog_add_dependent, null);
                final EditText nameEditText = view.findViewById(R.id.name_edit_text);
                final EditText mobileEditText = view.findViewById(R.id.mobile_edit_text);
                final EditText emailEditText = view.findViewById(R.id.email_edit_text);
                final EditText passwordEditText = view.findViewById(R.id.password_edit_text);


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

                        String name = nameEditText.getText().toString();
                        String mobile = mobileEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                            Toast.makeText(getActivity(), "Enter all credentials first!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        authenticate(name, mobile, email, password);
                        dialog.dismiss();
                    }
                });
            }
        });

        fetchDependents();

        return v;
    }

    public void fetchDependents() {

        db.collection("primary_users")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if(!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mDocRef = task.getResult().getDocuments().get(0).getId();

                        db.collection("primary_users/"+mDocRef+"/dependents")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                                        if(task.isSuccessful()) {

                                            for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                                String name = documentSnapshot.getString("name");
                                                String email = documentSnapshot.getString("email");

                                                mDependentItems.add(new DependentItem(name, email));
                                            }

                                            mRecyclerView.setAdapter(new MyDependentListItemAdapter(mDependentItems, getActivity()));
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

    private void authenticate(String name, String mobile, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            Map<String, Object> map = new HashMap<>();
                            map.put("name", name);
                            map.put("email", email);
                            map.put("mobile_number", mobile);
                            map.put("password", password);
                            map.put("pin", "NA");
                            map.put("balance", 0.0);

                            db.collection("secondary_users")
                                    .add(map)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {

                                            if(task.isSuccessful()) {

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("name", name);
                                                map.put("email", email);

                                                db.collection("primary_users/"+mDocRef+"/dependents")
                                                        .add(map)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {

                                                                if(task.isSuccessful()) {

                                                                    Toast.makeText(getActivity(), "User created Successfully!", Toast.LENGTH_LONG).show();
                                                                    mAuth.signOut();
                                                                    Intent intent = new Intent(getActivity(), EntryActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intent);
                                                                    getActivity().finish();

                                                                }
                                                                else {
                                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else {
                                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}