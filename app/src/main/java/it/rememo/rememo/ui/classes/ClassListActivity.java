package it.rememo.rememo.ui.classes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.databinding.ActivityListWithAddBinding;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.ListWithRemoveAdapter;
import it.rememo.rememo.utils.Common;

public abstract class ClassListActivity extends AppCompatActivity {
    public final static String ARG_CLASS_ID = "classId";
    ActivityListWithAddBinding binding;
    StudentClass stClass = null;
    ArrayList<FirebaseModel> collList;
    ListWithRemoveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListWithAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getPageTitle());

        binding.addBtn.setText(getBtnName());

        String classId = getIntent().getStringExtra(ARG_CLASS_ID);
        collList = new ArrayList<>();
        adapter = new ListWithRemoveAdapter(this, collList);
        adapter.setDeleteClickListener((v, i) -> removeItem(i, collList.get(i) ));
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);

        StudentClass.getClassById(
                classId,
                (cl) -> {
                    stClass = cl;
                    this.updateList(cl);
                },
                (ex) -> Common.toast(this, "Couldn't load collections")
        );

        binding.addBtn.setOnClickListener(v -> onBtnClick() );
    }

    private void removeItem(int position, FirebaseModel item) {
        new AlertDialog.Builder(this)
                .setTitle("Remove " + item.getName())
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("I'm sure", (dialog, whichButton) -> {
                    collList.remove(position);
                    adapter.notifyItemRemoved(position);
                    removeItemHandler(item);
                })
                .setNegativeButton("Cancel", null).show();
    }

    protected abstract void  onBtnClick();
    protected abstract String getBtnName();
    protected abstract void updateList(StudentClass cl);
    protected abstract void removeItemHandler(FirebaseModel item);
    protected abstract String getPageTitle();

}