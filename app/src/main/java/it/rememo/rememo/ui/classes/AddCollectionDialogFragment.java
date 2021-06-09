package it.rememo.rememo.ui.classes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.models.Collection;

public class AddCollectionDialogFragment extends DialogFragment {

    List<Collection> collections;
    String[] items;
    boolean[] itemChecked;
    @NonNull OnSuccessListener<? super ArrayList<Collection>> positiveResponse;


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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Title")
                .setMultiChoiceItems(items, itemChecked,
                        (DialogInterface.OnMultiChoiceClickListener) (dialog, which, isChecked) -> { }
                )
                .setPositiveButton("Add", (dialog, id) -> {
                    ArrayList<Collection> res = new ArrayList<>();
                    for (int i = 0; i < itemChecked.length; i++) {
                        if (itemChecked[i]) {
                            res.add(collections.get(i));
                        }
                    }
                    positiveResponse.onSuccess(res);
                })
                .setNegativeButton("Cancel", (dialog, id) -> { });

        return builder.create();
    }
}
