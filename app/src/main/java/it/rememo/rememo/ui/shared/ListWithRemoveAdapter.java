package it.rememo.rememo.ui.shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.databinding.RowCollectionItemBinding;
import it.rememo.rememo.databinding.RowDeletableItemBinding;
import it.rememo.rememo.models.FirebaseModel;

public class ListWithRemoveAdapter extends RecyclerView.Adapter<ListWithRemoveAdapter.ViewHolder> {

    private ArrayList<FirebaseModel> list;
    protected LayoutInflater mInflater;
    GroupRecyclerViewAdapter.ItemClickListener mClickListener;

    public ListWithRemoveAdapter(Context context, ArrayList<FirebaseModel> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowDeletableItemBinding binding = RowDeletableItemBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(list.get(position).getName());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setDeleteClickListener(GroupRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void removeAt(int i) {
        list.remove(i);
        notifyItemRemoved(i);
    }

    public void resetAll(List<FirebaseModel> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void add(FirebaseModel c) {
        list.add(c);
        notifyItemInserted(list.size() - 1);
    }

    public void addAll(List<? extends FirebaseModel> c) {
        int sizeBefore = list.size();
        list.addAll(c);
        notifyItemRangeInserted(sizeBefore, c.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowDeletableItemBinding binding;

        public ViewHolder(RowDeletableItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnDelete.setOnClickListener(view -> {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            });
        }

        public TextView getTextView() {
            return this.binding.txtCollectionRow;
        }
    }
}

