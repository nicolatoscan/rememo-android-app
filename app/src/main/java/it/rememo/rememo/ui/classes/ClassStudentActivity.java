package it.rememo.rememo.ui.classes;

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
        return Common.resStr(this, R.string.class_share);
    }
    @Override
    protected String getPageTitle() {
        return Common.resStr(this, R.string.title_students);
    }

    @Override
    protected void  onBtnClick() {
        ShareUrls.shareClass(this, this.stClass.getId(), this.stClass.getName());
    }

    @Override
    protected void updateList(StudentClass cl) {
        cl.getClassStudents(
                students -> adapter.addAll(students),
                ex -> Common.toast(this, Common.resStr(this, R.string.class_cant_load_usernames))
        );
    }

    @Override
    protected void removeItemHandler(FirebaseModel item) {
        ArrayList<Username> u = new ArrayList<>();
        u.add((Username) item);
        this.stClass.removeStudents(u,
                success -> {},
                ex -> Common.toast(getApplicationContext(), Common.resStr(this, R.string.coll_cant_remove))
        );
    }
}