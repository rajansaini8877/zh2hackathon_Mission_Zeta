package com.myappcompany.rajan.zeta.primary_ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.myappcompany.rajan.zeta.MainActivity;
import com.myappcompany.rajan.zeta.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeFragment extends Fragment {

    private TextView mBalanceTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_primary_home, container, false);

        mBalanceTextView = root.findViewById(R.id.balance_text_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchData();
        return root;
    }

    private class FetchTask extends AsyncTask<String, Void, String> {

        private String result = null;

        @Override
        protected String doInBackground(String... accountId) {

            //String s = null;

            String response = fetchBalance(accountId[0]);

            result = new String("***");

            try {
                JSONObject obj = new JSONObject(response);
                double balance = obj.getLong("balance")/100.0;
                result = Double.toString(balance);
            }
            catch(Exception e) {
                Log.d("###########", e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            mBalanceTextView.setText("\u20B9 " + s);
        }
    }

    private void fetchData() {

        db.collection("primary_users")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {

                            String accountId = task.getResult().getDocuments().get(0).getString("account_id");

                            new FetchTask().execute(accountId);
                        }
                    }
                });

    }

    private String fetchBalance(String accountId) {

        String response = null;
        ByteArrayOutputStream out = null;
        URL url = null;
        HttpURLConnection http = null;

        try {
            out = new ByteArrayOutputStream();
            url = new URL("https://fusion.preprod.zeta.in/api/v1/ifi/" + MainActivity.IFI_ID+ "/accounts/" + accountId + "/balance");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("X-Zeta-AuthToken", MainActivity.AUTH_TOKEN);

            InputStream in = http.getInputStream();

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            String temp = new String(out.toByteArray());

            if(http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Toast.makeText(getActivity(), "Some error occured!", Toast.LENGTH_LONG).show();
            }

            response = (temp);

        }
        catch (Exception e) {
            Log.d("######################", e.getMessage());
        }
        finally {

            http.disconnect();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}