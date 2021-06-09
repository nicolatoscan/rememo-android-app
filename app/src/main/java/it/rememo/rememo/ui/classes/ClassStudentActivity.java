package it.rememo.rememo.ui.classes;

import java.util.ArrayList;

import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.models.Username;
import it.rememo.rememo.utils.Common;

public class ClassStudentActivity extends ClassListActivity {

    @Override
    protected String getBtnName() {
        return "Share class";
    }

    @Override
    protected void  onBtnClick() {

    }

    @Override
    protected void updateList(StudentClass cl) {
        cl.getClassStudents(
                students -> adapter.addAll(students),
                ex -> Common.toast(this, "Couldn't load Usernames")
        );
    }

    @Override
    protected void removeItemHandler(FirebaseModel item) {
        ArrayList<Username> u = new ArrayList<>();
        u.add((Username) item);
        this.stClass.removeStudents(u,
                success -> {},
                ex -> Common.toast(getApplicationContext(), "Couldn't remove collection")
        );
    }
}