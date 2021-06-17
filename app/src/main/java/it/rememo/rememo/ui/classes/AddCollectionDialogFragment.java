package it.rememo.rememo.ui.classes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.R;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.utils.Common;


// Dialog add collection to class
// List of collections with checkboxes
public class AddCollectionDialogFragment extends DialogFragment {

    final List<Collection> collections;
    final String[] items;
    final boolean[] itemChecked;
    final @NonNull OnSuccessListener<? super ArrayList<Collection>> positiveResponse;


    public AddCollectionDialogFragment(
            List<Collection> collections,
            @NonNull OnSuccessListener<? super ArrayList<Collection>> positiveResponse
    ) {
        super();
        this.collections = collections;
        this.positiveResponse = positiveResponse;
        itemChecked = new boolean[collections.size()];
        items = new String[collections.size()];
        for (int i = 0; i < collections.size(); i++) {
            items[i] = collections.get(i).getName();
        }
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Common.resStr(getContext(), R.string.coll_title))
                .setMultiChoiceItems(items, itemChecked, (dialog, which, isChecked) -> { })
                .setPositiveButton(Common.resStr(getContext(), R.string.coll_add), (dialog, id) -> {
                    // Return selected items
                    ArrayList<Collection> res = new ArrayList<>();
                    for (int i = 0; i < itemChecked.length; i++) {
                        if (itemChecked[i]) {
                            res.add(collections.get(i));
                        }
                    }
                    positiveResponse.onSuccess(res);
                })
                .setNegativeButton(Common.resStr(getContext(), R.string.basic_cancel), (dialog, id) -> { });
        return builder.create();
    }
}
