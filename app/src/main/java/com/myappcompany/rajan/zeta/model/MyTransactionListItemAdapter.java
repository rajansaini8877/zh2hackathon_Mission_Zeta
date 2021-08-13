package com.myappcompany.rajan.zeta.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myappcompany.rajan.zeta.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTransactionListItemAdapter extends RecyclerView.Adapter<MyTransactionListItemAdapter.MyViewHolder> {

    private final List<TransactionItem> mValues;
    private Context mContext;
    private FirebaseFirestore db;

    public MyTransactionListItemAdapter(List<TransactionItem> items, Context context) {
        mValues = items;
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_transaction_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdTextView.setText(mValues.get(position).getId());
        holder.mTimestampTextView.setText(mValues.get(position).getTimestamp().toDate().toString());
        holder.mStatusTextView.setText(mValues.get(position).getStatus());
        holder.mAmountTextView.setText(Double.toString(mValues.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdTextView;
        public final TextView mTimestampTextView;
        public final TextView mStatusTextView;
        public final TextView mAmountTextView;
        public TransactionItem mItem;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mIdTextView = view.findViewById(R.id.id_text_view);
            mTimestampTextView = view.findViewById(R.id.timestamp_text_view);
            mStatusTextView = view.findViewById(R.id.status_text_view);
            mAmountTextView = view.findViewById(R.id.amount_text_view);
        }
    }
}
