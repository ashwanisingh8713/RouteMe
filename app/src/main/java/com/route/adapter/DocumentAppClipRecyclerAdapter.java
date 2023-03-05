package com.route.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.route.modal.RoutesBean;
import com.route.modal.RoutesDocuments;
import com.route.routeme.R;
import com.route.routeme.RouteArPath_v2;

import java.util.ArrayList;
import java.util.List;

public class DocumentAppClipRecyclerAdapter extends RecyclerView.Adapter<DocumentAppClipRecyclerAdapter.MyViewHolder> {

    private List<RoutesDocuments> documentList;

    public DocumentAppClipRecyclerAdapter() {
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
        RoutesDocuments documents = documentList.get(position);
        holder.title.setText(documents.cat);
//        holder.subtitle.setText(documents.loc);
//        holder.pts.setText(documents.pts.toString());

        holder.itemView.setOnClickListener(click->{
            //Toast.makeText(holder.itemView.getContext(), "mkdkdkkd", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(holder.itemView.getContext(), RouteDetail.class);
            Intent intent = new Intent(holder.itemView.getContext(), RouteArPath_v2.class);
            intent.putExtra("id", documents.id);
            holder.itemView.getContext().startActivity(intent);


        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void setDocumentList(List<RoutesDocuments> documentList) {
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
