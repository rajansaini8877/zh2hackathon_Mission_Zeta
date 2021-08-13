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
import com.myappcompany.rajan.zeta.DependentDetailsActivity;
import com.myappcompany.rajan.zeta.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDependentListItemAdapter extends RecyclerView.Adapter<MyDependentListItemAdapter.MyViewHolder> {

    private final List<DependentItem> mValues;
    private Context mContext;
    private FirebaseFirestore db;

    public MyDependentListItemAdapter(List<DependentItem> items, Context context) {
        mValues = items;
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_dependent_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameTextView.setText(mValues.get(position).getName());
        holder.mEmailTextView.setText(mValues.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mNameTextView;
        public final TextView mEmailTextView;
        public DependentItem mItem;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mNameTextView = view.findViewById(R.id.name_text_view);
            mEmailTextView = view.findViewById(R.id.email_text_view);
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = DependentDetailsActivity.newIntent(mContext, mEmailTextView.getText().toString());
            mContext.startActivity(i);
        }
    }
}
