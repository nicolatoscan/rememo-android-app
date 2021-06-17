package it.rememo.rememo.ui.classes;

import android.view.View;

import java.util.ArrayList;
import java.util.stream.Collectors;

import it.rememo.rememo.R;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.utils.Common;

// Shows the collections shared with a class
public class ClassCollectionsActivity extends ClassListActivity {

    @Override
    protected String getBtnName() {
        return getString(R.string.colls_add);
    }
    @Override
    protected String getPageTitle() {
        return getString(R.string.colls_collections);
    }

    // Open add collection dialog
    public void onBtnClick() {
        Collection.getMyCollections(
                colls -> {
                    // Filter collections already in class
                    colls = colls
                            .stream()
                            .filter(c -> elementList.stream().noneMatch(streamC -> streamC.getId().equals(c.getId())))
                            .collect(Collectors.toList());

                    if (colls.size() > 0) {
                        // Open dialog
                        new AddCollectionDialogFragment(colls,
                            selectedCollections ->
                                stClass.addCollections(
                                        selectedCollections,
                                        s -> {
                                            // Remove txt loading
                                            if (selectedCollections.size() > 0)
                                                binding.txtLoading.setVisibility(View.GONE);
                                            // Add to list
                                            adapter.addAll(selectedCollections);
                                        },
                                        ex -> Common.toast(this, getString(R.string.colls_cant_add))
                                )
                        ).show(getSupportFragmentManager(), "");
                    } else {
                        Common.toast(this, getString(R.string.coll_no_other_add));
                    }
                },
                ex -> Common.toast(this, getString(R.string.colls_cant_load))
        );
    }

    // Update list on refresh
    @Override
    protected void updateList(StudentClass cl) {
        cl.getClassCollections(
                collections -> {
                    if (collections.size() <= 0)
                        binding.txtLoading.setText(getString(R.string.basic_no_collections));
                    else
                        binding.txtLoading.setVisibility(View.GONE);
                    adapter.addAll(collections);
                },
                ex -> Common.toast(this, getString(R.string.colls_cant_load))
        );
    }

    // Remove an item
    @Override
    protected void removeItemHandler(FirebaseModel item) {
        ArrayList<Collection> c = new ArrayList<>();
        c.add((Collection) item);
        this.stClass.removeCollections(c,
                success -> {},
                ex -> Common.toast(getApplicationContext(), getString(R.string.coll_cant_remove))
        );
    }
}