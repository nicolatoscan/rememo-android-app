package it.rememo.rememo.ui.classes;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import it.rememo.rememo.R;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.GroupFragment;
import it.rememo.rememo.utils.Alerts;
import it.rememo.rememo.utils.Common;

public class ClassesGroupFragment extends GroupFragment<Collection> {

    @Override
    protected void parseArgs(Bundle args) { }

    // Visible if is classes created create
    protected boolean isFloatingAddVisible(int index) {
        return index == 1;
    }

    protected void setUp() {
        adapter = new ClassesRecyclerViewAdapter(getContext(), list, position == 1);
        adapter.setClickListener((v, i) -> {
            Intent intent = new Intent(getContext(), ClassDetailsActivity.class);
            StudentClass c = (StudentClass) adapter.getItem(i);
            intent.putExtra(ClassDetailsActivity.ARG_CLASS, c);
            intent.putExtra(ClassDetailsActivity.ARG_IS_CREATED, position == 1);
            startActivity(intent);
        });
        binding.collectionRecyclerView.setAdapter(adapter);
        binding.txtLoading.setText(getContext().getString(R.string.basic_no_classes));
    }

    // get all classes
    protected void updateList() {
        if (!(position == 0 || position == 1)) {
            return;
        }
        binding.txtLoading.setVisibility(View.GONE);

        // Choose classes created or joined
        StudentClass.getClasses(position == 1,
            (updatedClasses) -> {
                if (updatedClasses.size() == 0) {
                    binding.txtLoading.setVisibility(View.VISIBLE);
                }
                adapter.resetAll(updatedClasses);
                binding.collectionSwipeContainer.setRefreshing(false);
            },
            (ex) -> {
                Common.toast(getContext(), getContext().getString(R.string.colls_cant_update));
                binding.collectionSwipeContainer.setRefreshing(false);
            });
    }

    //Create a new class
    protected void onAddClicked() {
        final EditText textInput = new EditText(getContext());
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint(getString(R.string.class_name));

        Alerts
                .getInputTextAlert(getContext(), textInput)
                .setTitle(getContext().getString(R.string.class_create_new))
                .setPositiveButton(getContext().getString(R.string.basic_create), (dialog, which) -> {
                    String title = textInput.getText().toString();
                    if (title.length() > 0) {
                        createClass(title);
                    }
                })
                .setNegativeButton(getContext().getString(R.string.basic_cancel), (dialog, which) -> dialog.cancel())
                .show();
    }

    // Add class to Firestore
    private void createClass(String name) {
        StudentClass cl = new StudentClass(name);
        cl.addToFirestore(
                doc -> {
                    // Add class to list
                    adapter.add(cl);
                    binding.collectionRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    binding.txtLoading.setVisibility(View.GONE);
                },
                ex -> Common.toast(getContext(), getContext().getString(R.string.coll_err_creating_retry))
        );
    }
}
