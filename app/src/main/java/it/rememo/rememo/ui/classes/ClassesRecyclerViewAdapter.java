package it.rememo.rememo.ui.classes;

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
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.GroupRecyclerViewAdapter;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.ShareUrls;


// Main class list
public class ClassesRecyclerViewAdapter extends GroupRecyclerViewAdapter<Collection, ClassesRecyclerViewAdapter.ViewHolder> {

    private final boolean isCreated;
    final Context context;

    ClassesRecyclerViewAdapter(Context context, List<Collection> collections, boolean isCreated) {
        super(context, collections);
        this.context = context;
        // Type of class (created or joined)
        this.isCreated = isCreated;
    }

    protected RecyclerView.ViewHolder getViewHolder(RowCollectionItemBinding binding, GroupRecyclerViewAdapter adapter) {
        return new ClassesRecyclerViewAdapter.ViewHolder(binding, this, context);
    }

    public class ViewHolder extends GroupRecyclerViewAdapter.ViewHolder {
        final Context context;

        ViewHolder(RowCollectionItemBinding binding, ClassesRecyclerViewAdapter adapterReference, Context context) {
            super(binding, adapterReference);
            this.context = context;
            if (isCreated) {
                // Menu for created classes
                binding.cardView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                    // menu.setHeaderTitle("Select The Action");
                    menu.add(0, view.getId(), 0, context.getString(R.string.basic_share)).setOnMenuItemClickListener((mItem) -> shareClass());
                    menu.add(0, view.getId(), 0, context.getString(R.string.basic_rename)).setOnMenuItemClickListener((mItem) -> renameClasses());
                    menu.add(0, view.getId(), 0, context.getString(R.string.basic_delete)).setOnMenuItemClickListener((mItem) -> deleteClass());
                });
            } else {
                // Menu for joined classes
                binding.cardView.setOnCreateContextMenuListener((menu, view, menuInfo) -> {
                    // menu.setHeaderTitle("Select The Action");
                    menu.add(0, view.getId(), 0, context.getString(R.string.basic_leave)).setOnMenuItemClickListener((mItem) -> leaveClass());
                });
            }

        }

        // User leaves class
        private boolean leaveClass() {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.class_leave)
                    .setMessage(R.string.dialog_sure_leave_class)
                    .setPositiveButton(context.getString(R.string.form_im_sure), (dialog, whichButton) ->
                        ((StudentClass) element).leaveClass(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), context.getString(R.string.class_left)); },
                                ex -> Common.toast(itemView.getContext(), context.getString(R.string.class_couldnt_leave))
                        )
                    )
                    .setNegativeButton(context.getString(R.string.basic_cancel), null).show();
            return true;
        }

        // Open share dialog
        private boolean shareClass() {
            ShareUrls.shareClass(context, element.getId(), element.getName());
            return true;
        }

        // Rename a class with dialog
        private boolean renameClasses() {
            final EditText textInput = new EditText(itemView.getContext());
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
            textInput.setHint(context.getString(R.string.class_name));
            if (element != null) {
                textInput.setText(element.getName());
            }

            Alerts
                .getInputTextAlert(itemView.getContext(), textInput)
                .setTitle(context.getString(R.string.class_rename))
                .setPositiveButton(context.getString(R.string.basic_rename), (dialog, which) -> {
                    String title = textInput.getText().toString();

                    Map<String, Object> updateColl = new HashMap<>();
                    updateColl.put(Collection.KEY_NAME, title);
                    element.updateFirestore(updateColl,
                            x -> {
                                ((StudentClass) element).setName(title);
                                updateUI();
                            },
                            ex -> Common.toast(itemView.getContext(), context.getString(R.string.class_cant_rename))
                    );
                })
                .setNegativeButton(context.getString(R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                .show();
            return true;
        }

        // Delete a class
        private boolean deleteClass() {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(context.getString(R.string.class_delete))
                    .setMessage(String.format(context.getString(R.string.form_sure_to_delete_STR), element.getName()))
                    .setPositiveButton(context.getString(R.string.form_im_sure), (dialog, whichButton) ->
                        element.deleteFromFirestore(
                                x -> { removeAt(getAdapterPosition()); Common.toast(itemView.getContext(), context.getString(R.string.class_deleted)); },
                                ex -> Common.toast(itemView.getContext(), context.getString(R.string.class_cant_delete))
                        )
                    )
                    .setNegativeButton(context.getString(R.string.basic_cancel), null).show();
            return true;
        }

        protected void updateUI() {
            binding.txtCollectionRow.setText(element.getName());
        }
    }

}

