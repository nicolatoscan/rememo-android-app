package it.rememo.rememo.ui.collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.rememo.rememo.R;
import it.rememo.rememo.models.Collection;

public class CollectionRecyclerViewAdapter extends RecyclerView.Adapter<CollectionRecyclerViewAdapter.ViewHolder> {

    private List<Collection> collections;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    CollectionRecyclerViewAdapter(Context context, List<Collection> collections) {
        this.mInflater = LayoutInflater.from(context);
        this.collections = collections;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_collection_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Collection c = collections.get(position);
        holder.myTextView.setText(c.getName());
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public void clear() {
        collections.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Collection> list) {
        collections.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Collection c) {
        collections.add(c);
        notifyItemInserted(collections.size() - 1);
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.txtCollectionRow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Collection getItem(int id) {
        return collections.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

