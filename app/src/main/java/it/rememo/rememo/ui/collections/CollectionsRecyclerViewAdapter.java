package it.rememo.rememo.ui.collections;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowCollectionItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.ui.shared.GroupRecyclerViewAdapter;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.ShareUrls;

// Recycler View that holds collections names
public class CollectionsRecyclerViewAdapter extends GroupRecyclerViewAdapter<Collection, CollectionsRecyclerViewAdapter.ViewHolder> {

    // Is tab mine or in a class
    private final boolean isMine;

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
                // Add context menu if click and hold
                binding.cardView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                    menu.add(0, view.getId(), 0, view.getContext().getString(R.string.basic_share)).setOnMenuItemClickListener((mItem) -> shareCollection());
                    menu.add(0, view.getId(), 0, view.getContext().getString(R.string.basic_rename)).setOnMenuItemClickListener((mItem) -> renameCollection());
                    menu.add(0, view.getId(), 0, view.getContext().getString(R.string.basic_delete)).setOnMenuItemClickListener((mItem) -> deleteCollection());
                });
            }
        }

        // Rename a collection
        private boolean renameCollection() {
            final EditText textInput = new EditText(itemView.getContext());
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setHint(itemView.getContext().getString(R.string.coll_name));
            if (element != null) {
                textInput.setText(element.getName());
            }

            // Ask for new name
            Alerts
                .getInputTextAlert(itemView.getContext(), textInput)
                .setTitle(itemView.getContext().getString(R.string.coll_rename))
                .setPositiveButton(itemView.getContext().getString(R.string.basic_rename), (dialog, which) -> {
                    String title = textInput.getText().toString();

                    Map<String, Object> updateColl = new HashMap<>();
                    updateColl.put(Collection.KEY_NAME, title);
                    // Save in DB
                    element.updateFirestore(updateColl,
                            x -> {
                                ((Collection) element).setName(title);
                                updateUI();
                            },
                            ex -> Common.toast(itemView.getContext(), itemView.getContext().getString(R.string.coll_cant_rename))
                    );
                })
                .setNegativeButton(itemView.getContext().getString(R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                .show();
            return true;
        }

        // Remove a collection
        private boolean deleteCollection() {
            // Ask if sure
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(itemView.getContext().getString(R.string.word_delete))
                    .setMessage(String.format(itemView.getContext().getString(R.string.form_sure_to_delete_coll_STR_and_words), element.getName()))
                    .setPositiveButton(itemView.getContext().getString(R.string.form_im_sure), (dialog, whichButton) ->
                        element.deleteFromFirestore(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), itemView.getContext().getString(R.string.coll_deleted)); },
                                ex -> Common.toast(itemView.getContext(), itemView.getContext().getString(R.string.coll_cant_delete))
                        )
                    )
                    .setNegativeButton(itemView.getContext().getString(R.string.basic_cancel), null).show();
            return true;
        }

        // Open share prompt
        private boolean shareCollection() {
            ShareUrls.shareCollection(itemView.getContext(), element.getId(), element.getName());
            return true;
        }

        // Reload view holder
        protected void updateUI() {
            binding.txtCollectionRow.setText(element.getName());
        }
    }

}

