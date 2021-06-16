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
                binding.cardView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                    // menu.setHeaderTitle("Select The Action");
                    menu.add(0, view.getId(), 0, Common.resStr(view.getContext(), R.string.basic_rename)).setOnMenuItemClickListener((mItem) -> renameCollection());
                    menu.add(0, view.getId(), 0, Common.resStr(view.getContext(), R.string.basic_delete)).setOnMenuItemClickListener((mItem) -> deleteCollection());
                });
            }
        }

        private boolean renameCollection() {
            final EditText textInput = new EditText(itemView.getContext());
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setHint(Common.resStr(itemView.getContext(), R.string.coll_name));
            if (element != null) {
                textInput.setText(element.getName());
            }

            Alerts
                .getInputTextAlert(itemView.getContext(), textInput)
                .setTitle(Common.resStr(itemView.getContext(), R.string.coll_rename))
                .setPositiveButton(Common.resStr(itemView.getContext(), R.string.basic_rename), (dialog, which) -> {
                    String title = textInput.getText().toString();

                    Map<String, Object> updateColl = new HashMap<>();
                    updateColl.put(Collection.KEY_NAME, title);
                    element.updateFirestore(updateColl,
                            x -> {
                                ((Collection) element).setName(title);
                                updateUI();
                            },
                            ex -> Common.toast(itemView.getContext(), Common.resStr(itemView.getContext(), R.string.coll_cant_rename))
                    );
                })
                .setNegativeButton(Common.resStr(itemView.getContext(), R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                .show();
            return true;
        }

        private boolean deleteCollection() {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(Common.resStr(itemView.getContext(), R.string.word_delete))
                    .setMessage(String.format(Common.resStr(itemView.getContext(), R.string.form_sure_to_delete_coll_STR_and_words), element.getName()))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(Common.resStr(itemView.getContext(), R.string.form_im_sure), (dialog, whichButton) -> {
                        element.deleteFromFirestore(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), Common.resStr(itemView.getContext(), R.string.coll_deleted)); },
                                ex -> Common.toast(itemView.getContext(), Common.resStr(itemView.getContext(), R.string.coll_cant_delete))
                        );
                    })
                    .setNegativeButton(Common.resStr(itemView.getContext(), R.string.basic_cancel), null).show();
            return true;
        }

        protected void updateUI() {
            binding.txtCollectionRow.setText(element.getName());
        }
    }

}

