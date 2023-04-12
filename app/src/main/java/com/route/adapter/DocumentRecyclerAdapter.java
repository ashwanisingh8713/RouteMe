package com.route.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.ar.navigation.ArRenderingActivity;
import com.route.modal.ktM2.Anchor;
import com.route.modal.ktM2.Route;
import com.route.routeme.R;
import com.route.routeme.RouteArPath_v2;

import java.util.ArrayList;
import java.util.List;

public class DocumentRecyclerAdapter extends RecyclerView.Adapter<DocumentRecyclerAdapter.MyViewHolder> {

    private List<Anchor> documentList;

    public DocumentRecyclerAdapter() {
        documentList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_recycler, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,int position) {
        Anchor documents = documentList.get(position);
        Route route = documents.getRoutes().get(0);
        holder.title.setText(route.getName());

        holder.itemView.setOnClickListener(click->{
//            Intent intent = new Intent(holder.itemView.getContext(), RouteArPath_v2.class);
            Intent intent = new Intent(holder.itemView.getContext(), ArRenderingActivity.class);
            intent.putExtra("id", route.getId());
            holder.itemView.getContext().startActivity(intent);


        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void setDocumentList(List<Anchor> documentList) {
        this.documentList = documentList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle,pts;
        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            pts = view.findViewById(R.id.pts);
        }
    }

}
