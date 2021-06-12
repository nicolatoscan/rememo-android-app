package it.rememo.rememo.ui.classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.RowCollectionItemBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.GroupRecyclerViewAdapter;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.ShareUrls;

public class ClassesRecyclerViewAdapter extends GroupRecyclerViewAdapter<Collection, ClassesRecyclerViewAdapter.ViewHolder> {


    private boolean isCreated;
    Context context;

    ClassesRecyclerViewAdapter(Context context, List<Collection> collections, boolean isCreated) {
        super(context, collections);
        this.context = context;
        this.isCreated = isCreated;
    }

    protected RecyclerView.ViewHolder getViewHolder(RowCollectionItemBinding binding, GroupRecyclerViewAdapter adapter) {
        return new ClassesRecyclerViewAdapter.ViewHolder(binding, this, context);
    }

    public class ViewHolder extends GroupRecyclerViewAdapter.ViewHolder {
        Context context;

        ViewHolder(RowCollectionItemBinding binding, ClassesRecyclerViewAdapter adapterReference, Context context) {
            super(binding, adapterReference);
            this.context = context;
            if (isCreated) {
                itemView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                    // menu.setHeaderTitle("Select The Action");
                    menu.add(0, view.getId(), 0, Common.resStr(context, R.string.basic_share)).setOnMenuItemClickListener((mItem) -> shareClass());
                    menu.add(0, view.getId(), 0, Common.resStr(context, R.string.basic_rename)).setOnMenuItemClickListener((mItem) -> renameClasses());
                    menu.add(0, view.getId(), 0, Common.resStr(context, R.string.basic_delete)).setOnMenuItemClickListener((mItem) -> deleteCollection());
                });
            }
        }

        private boolean shareClass() {
            ShareUrls.shareClass(context, element.getId());
            return true;
        }

        private boolean renameClasses() {
            final EditText textInput = new EditText(itemView.getContext());
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setHint(Common.resStr(context, R.string.class_name));
            if (element != null) {
                textInput.setText(element.getName());
            }

            Alerts
                    .getInputTextAlert(itemView.getContext(), textInput)
                    .setTitle(Common.resStr(context, R.string.class_rename))
                    .setPositiveButton(Common.resStr(context, R.string.basic_rename), (dialog, which) -> {
                        String title = textInput.getText().toString();

                        Map<String, Object> updateColl = new HashMap<>();
                        updateColl.put(Collection.KEY_NAME, title);
                        element.updateFirestore(updateColl,
                                x -> {
                                    ((StudentClass) element).setName(title);
                                    updateUI();
                                },
                                ex -> Common.toast(itemView.getContext(), Common.resStr(context, R.string.class_cant_rename))
                        );
                    })
                    .setNegativeButton(Common.resStr(context, R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                    .show();
            return true;
        }

        private boolean deleteCollection() {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(Common.resStr(context, R.string.class_delete))
                    .setMessage(String.format(Common.resStr(context, R.string.form_sure_to_delete_STR), element.getName()))
                    .setPositiveButton(Common.resStr(context, R.string.form_im_sure), (dialog, whichButton) -> {
                        element.deleteFromFirestore(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), Common.resStr(context, R.string.class_deleted)); },
                                ex -> Common.toast(itemView.getContext(), Common.resStr(context, R.string.class_cant_delete))
                        );
                    })
                    .setNegativeButton(Common.resStr(context, R.string.basic_cancel), null).show();
            return true;
        }

        protected void updateUI() {
            binding.txtCollectionRow.setText(element.getName());
        }
    }

}

