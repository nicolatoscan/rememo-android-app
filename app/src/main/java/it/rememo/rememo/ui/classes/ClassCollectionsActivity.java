package it.rememo.rememo.ui.classes;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.utils.Common;

public class ClassCollectionsActivity extends ClassListActivity {

    @Override
    protected String getBtnName() {
        return "Add collections";
    }

    public void onBtnClick() {
        Collection.getMyCollections(
                colls -> {
                    colls = colls
                            .stream()
                            .filter(c -> collList.stream().noneMatch(streamC -> streamC.getId().equals(c.getId())))
                            .collect(Collectors.toList());

                    if (colls.size() > 0) {
                        new AddCollectionDialogFragment(colls,
                            selectedCollections -> {
                                stClass.addCollections(
                                        selectedCollections,
                                        s -> adapter.addAll(selectedCollections),
                                        ex -> Common.toast(this, "Couldn't add collections")
                                );
                            }
                        ).show(getSupportFragmentManager(), "PIPPO");
                    } else {
                        Common.toast(this, "No other collection to add");
                    }
                },
                ex -> Common.toast(this, "Couldn't load collections")
        );
    }

    @Override
    protected void updateList(StudentClass cl) {
        cl.getClassCollections(
                collections -> adapter.addAll(collections),
                ex -> Common.toast(this, "Couldn't load collections")
        );
    }

    @Override
    protected void removeItemHandler(FirebaseModel item) {
        ArrayList<Collection> c = new ArrayList<>();
        c.add((Collection) item);
        this.stClass.removeCollections(c,
                success -> {},
                ex -> Common.toast(getApplicationContext(), "Couldn't remove collection")
        );
    }
}