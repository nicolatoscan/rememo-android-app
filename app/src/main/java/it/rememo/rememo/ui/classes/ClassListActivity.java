package it.rememo.rememo.ui.classes;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityListWithAddBinding;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.ListWithRemoveAdapter;
import it.rememo.rememo.utils.Common;

// abstract activity class to create student and collection list for class
// The two pages are similar and share several method and properties
public abstract class ClassListActivity extends AppCompatActivity {
    public final static String ARG_CLASS_ID = "classId";
    ActivityListWithAddBinding binding;
    // Current class
    StudentClass stClass = null;
    // Student or collection list
    ArrayList<FirebaseModel> elementList;
    ListWithRemoveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListWithAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getPageTitle());

        binding.addBtn.setText(getBtnName());

        String classId = getIntent().getStringExtra(ARG_CLASS_ID);
        elementList = new ArrayList<>();
        adapter = new ListWithRemoveAdapter(this, elementList);
        adapter.setDeleteClickListener((v, i) -> removeItem(i, elementList.get(i) ));
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);

        // Get current class
        StudentClass.getClassById(
                classId,
                (cl) -> {
                    stClass = cl;
                    // Each page should implement it's UI update
                    this.updateList(cl);
                },
                (ex) -> Common.toast(this, getString(R.string.colls_cant_load))
        );

        // Add button implemented by each class
        binding.addBtn.setOnClickListener(v -> onBtnClick() );
    }

    // Remove dialog on
    private void removeItem(int position, FirebaseModel item) {
        new AlertDialog.Builder(this)
                .setTitle(String.format(getString(R.string.class_name).toString(), item.getName()))
                .setMessage(String.format(getString(R.string.form_sure_to_delete_STR), item.getName()))
                .setPositiveButton(R.string.form_im_sure, (dialog, whichButton) -> {
                    elementList.remove(position);
                    adapter.notifyItemRemoved(position);
                    removeItemHandler(item);
                })
                .setNegativeButton(R.string.basic_cancel, null).show();
    }

    protected abstract void  onBtnClick();
    protected abstract String getBtnName();
    protected abstract void updateList(StudentClass cl);
    protected abstract void removeItemHandler(FirebaseModel item);
    protected abstract String getPageTitle();

}