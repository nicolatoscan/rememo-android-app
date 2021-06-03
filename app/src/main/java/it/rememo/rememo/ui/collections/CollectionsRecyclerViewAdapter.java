package it.rememo.rememo.ui.collections;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowCollectionItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.CollectionWord;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

public class CollectionsRecyclerViewAdapter extends RecyclerView.Adapter<CollectionsRecyclerViewAdapter.ViewHolder> {

    private List<Collection> collections;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    CollectionsRecyclerViewAdapter(Context context, List<Collection> collections) {
        this.mInflater = LayoutInflater.from(context);
        this.collections = collections;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCollectionItemBinding binding = RowCollectionItemBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Collection c = collections.get(position);
        holder.setCollection(c);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public void removeAt(int i) {
        collections.remove(i);
        notifyItemRemoved(i);
    }

    public void resetAll(List<Collection> list) {
        collections.clear();
        collections.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Collection c) {
        collections.add(c);
        notifyItemInserted(collections.size() - 1);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowCollectionItemBinding binding;
        private Collection collection = null;
        private CollectionsRecyclerViewAdapter adapterReference;

        ViewHolder(RowCollectionItemBinding binding, CollectionsRecyclerViewAdapter adapterReference) {
            super(binding.getRoot());
            this.adapterReference = adapterReference;

            this.binding = binding;

            itemView.setOnClickListener(view -> {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            });

            itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                // menu.setHeaderTitle("Select The Action");
                menu.add(0, view.getId(), 0, "Rename").setOnMenuItemClickListener((mItem) -> renameCollection());
                menu.add(0, view.getId(), 0, "Delete").setOnMenuItemClickListener((mItem) -> deleteCollection());
            });
        }

        private boolean renameCollection() {
            final EditText textInput = new EditText(itemView.getContext());
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setHint("Collection name");
            if (collection != null) {
                textInput.setText(collection.getName());
            }

            Alerts
                .getInputTextAlert(itemView.getContext(), textInput)
                .setTitle("Rename collection")
                .setPositiveButton("Rename", (dialog, which) -> {
                    String title = textInput.getText().toString();

                    Map<String, Object> updateColl = new HashMap<>();
                    updateColl.put(Collection.KEY_NAME, title);
                    collection.updateFirestore(updateColl,
                            x -> {
                                collection.setName(title);
                                updateUI();
                            },
                            ex -> Common.toast(itemView.getContext(), "Couldn't rename collection")
                    );
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
            return true;
        }

        private boolean deleteCollection() {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete word")
                    .setMessage("Are you sure you want to delete " + collection.getName() + " and all it's words?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("I'm sure", (dialog, whichButton) -> {
                        collection.deleteFromFirestore(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), "Collection deleted"); },
                                ex -> Common.toast(itemView.getContext(), "Couldn't delete this collection")
                        );
                    })
                    .setNegativeButton("Cancel", null).show();
            return true;
        }
        public void setCollection(Collection c) {
            collection = c;
            updateUI();
        }

        private void updateUI() {
            binding.txtCollectionRow.setText(collection.getName());
        }
    }

}

