package it.rememo.rememo.ui.shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.rememo.rememo.databinding.RowCollectionItemBinding;
import it.rememo.rememo.models.FirebaseModel;

// Abstract recycled view used both by collections and classes
public abstract class GroupRecyclerViewAdapter<T extends FirebaseModel, T1 extends  GroupRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<T1> {

    private final List<T> list;
    final protected LayoutInflater mInflater;
    protected ItemClickListener mClickListener;

    public GroupRecyclerViewAdapter(Context context, List<T> collections) {
        this.mInflater = LayoutInflater.from(context);
        this.list = collections;
    }

    protected abstract RecyclerView.ViewHolder getViewHolder(RowCollectionItemBinding binding, GroupRecyclerViewAdapter adapter);

    @NotNull
    @Override
    public T1 onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RowCollectionItemBinding binding = RowCollectionItemBinding.inflate(mInflater, parent, false);
        return (T1) getViewHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(GroupRecyclerViewAdapter.ViewHolder holder, int position) {
        T c = list.get(position);
        holder.setCollection(c);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // --- Edit data in list ---

    public void removeAt(int i) {
        list.remove(i);
        notifyItemRemoved(i);
    }

    public void resetAll(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void add(T c) {
        list.add(c);
        notifyItemInserted(list.size() - 1);
    }

    // --- ---- ---

    public T getItem(int id) {
        return list.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        protected RowCollectionItemBinding binding;
        protected T element = null;
        protected GroupRecyclerViewAdapter adapterReference;

        public ViewHolder(RowCollectionItemBinding binding, GroupRecyclerViewAdapter adapterReference) {
            super(binding.getRoot());
            this.adapterReference = adapterReference;
            this.binding = binding;

            binding.cardView.setOnClickListener(view -> {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            });
        }

        public void setCollection(T c) {
            element = c;
            updateUI();
        }

        protected abstract void updateUI();

    }

}

