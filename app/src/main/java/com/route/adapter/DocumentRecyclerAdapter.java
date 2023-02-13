package com.route.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.route.modal.RoutesBean;
import com.route.modal.RoutesDocuments;
import com.route.routeme.R;
import com.route.routeme.RouteArPath_v2;
import com.route.routeme.RouteDetail;

import java.util.ArrayList;
import java.util.List;

public class DocumentRecyclerAdapter extends RecyclerView.Adapter<DocumentRecyclerAdapter.MyViewHolder> {

    private List<RoutesBean> documentList;

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
        RoutesBean documents = documentList.get(position);
        holder.title.setText(documents.getName());
//        holder.subtitle.setText(documents.loc);
//        holder.pts.setText(documents.pts.toString());

        holder.itemView.setOnClickListener(click->{
            //Toast.makeText(holder.itemView.getContext(), "mkdkdkkd", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(holder.itemView.getContext(), RouteDetail.class);
            Intent intent = new Intent(holder.itemView.getContext(), RouteArPath_v2.class);
            intent.putExtra("id", documents.getId());
            holder.itemView.getContext().startActivity(intent);


        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void setDocumentList(List<RoutesBean> documentList) {
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
