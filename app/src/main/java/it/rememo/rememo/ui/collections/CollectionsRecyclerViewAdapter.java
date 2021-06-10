package it.rememo.rememo.ui.collections;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.databinding.RowCollectionItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.ui.shared.GroupRecyclerViewAdapter;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

public class CollectionsRecyclerViewAdapter extends GroupRecyclerViewAdapter<Collection, CollectionsRecyclerViewAdapter.ViewHolder> {

    private boolean isMine;

    CollectionsRecyclerViewAdapter(Context context, List<Collection> collections, boolean isMine) {
        super(context, collections);
        this.isMine = isMine;
    }

    protected RecyclerView.ViewHolder getViewHolder(RowCollectionItemBinding binding, GroupRecyclerViewAdapter adapter) {
        return new ViewHolder(binding, this);
    }

    public class ViewHolder extends GroupRecyclerViewAdapter.ViewHolder {

        ViewHolder(RowCollectionItemBinding binding, CollectionsRecyclerViewAdapter adapterReference) {
            super(binding, adapterReference);
            if (isMine) {
                itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                    // menu.setHeaderTitle("Select The Action");
                    menu.add(0, view.getId(), 0, "Rename").setOnMenuItemClickListener((mItem) -> renameCollection());
                    menu.add(0, view.getId(), 0, "Delete").setOnMenuItemClickListener((mItem) -> deleteCollection());
                });
            }
        }

        private boolean renameCollection() {
            final EditText textInput = new EditText(itemView.getContext());
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setHint("Collection name");
            if (element != null) {
                textInput.setText(element.getName());
            }

            Alerts
                .getInputTextAlert(itemView.getContext(), textInput)
                .setTitle("Rename collection")
                .setPositiveButton("Rename", (dialog, which) -> {
                    String title = textInput.getText().toString();

                    Map<String, Object> updateColl = new HashMap<>();
                    updateColl.put(Collection.KEY_NAME, title);
                    element.updateFirestore(updateColl,
                            x -> {
                                ((Collection) element).setName(title);
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
                    .setMessage("Are you sure you want to delete " + element.getName() + " and all it's words?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("I'm sure", (dialog, whichButton) -> {
                        element.deleteFromFirestore(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), "Collection deleted"); },
                                ex -> Common.toast(itemView.getContext(), "Couldn't delete this collection")
                        );
                    })
                    .setNegativeButton("Cancel", null).show();
            return true;
        }

        protected void updateUI() {
            binding.txtCollectionRow.setText(element.getName());
        }
    }

}

