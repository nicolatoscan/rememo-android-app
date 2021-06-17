package it.rememo.rememo.ui.classes;

import android.view.View;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.models.Username;
import it.rememo.rememo.utils.Common;
import it.rememo.rememo.utils.ShareUrls;

public class ClassStudentActivity extends ClassListActivity {

    @Override
    protected String getBtnName() {
        return getString(R.string.class_share);
    }
    @Override
    protected String getPageTitle() {
        return getString(R.string.title_students);
    }

    @Override
    protected void  onBtnClick() {
        ShareUrls.shareClass(this, this.stClass.getId(), this.stClass.getName());
    }

    @Override
    protected void updateList(StudentClass cl) {
        cl.getClassStudents(
                students -> {
                    if (students.size() <= 0)
                        binding.txtLoading.setText(ClassStudentActivity.this.getString(R.string.basic_no_students));
                    else
                        binding.txtLoading.setVisibility(View.GONE);
                    adapter.addAll(students);
                },
                ex -> Common.toast(this, getString(R.string.class_cant_load_usernames))
        );
    }

    @Override
    protected void removeItemHandler(FirebaseModel item) {
        ArrayList<Username> u = new ArrayList<>();
        u.add((Username) item);
        this.stClass.removeStudents(u,
                success -> {},
                ex -> Common.toast(getApplicationContext(), getString(R.string.coll_cant_remove))
        );
    }
}