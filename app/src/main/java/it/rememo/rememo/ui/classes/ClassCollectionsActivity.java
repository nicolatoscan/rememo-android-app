package it.rememo.rememo.ui.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.rememo.rememo.databinding.ActivityClassDetailsBinding;
import it.rememo.rememo.databinding.ActivityListWithAddBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.FirebaseModel;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.shared.ListWithRemoveAdapter;
import it.rememo.rememo.utils.Common;

public class ClassCollectionsActivity extends AppCompatActivity {
    public static String ARG_COLLECTIONS;
    ActivityListWithAddBinding binding;
    StudentClass stClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListWithAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.addBtn.setText("Add collection");
        String classId = getIntent().getStringExtra(ARG_COLLECTIONS);
        ArrayList<FirebaseModel> collList = new ArrayList<>();

        ListWithRemoveAdapter adapter = new ListWithRemoveAdapter(this, collList);
        adapter.setDeleteClickListener((v, i) -> {

        });
        binding.list.setAdapter(adapter);


        FirebaseFirestore.getInstance().collection(StudentClass.COLLECTION_NAME)
            .document(classId)
            .get()
            .addOnSuccessListener((doc) -> {
                stClass = new StudentClass(doc);
                List<String> collIds = stClass.getCollectionIds();

                if (collIds.size() > 0) {
                    FirebaseFirestore.getInstance()
                            .collection(Collection.COLLECTION_NAME)
                            .whereIn(FieldPath.documentId(), stClass.getCollectionIds())
                            .get()
                            .addOnSuccessListener((docs) -> {
                                for (QueryDocumentSnapshot d : docs) {
                                    collList.add(new Collection(d));
                                }
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener((ex) -> {
                                Common.toast(this, "Couldn't load collections");
                            });
                }

            })
            .addOnFailureListener((ex) -> {
                Common.toast(this, "Couldn't load collections");
            });
    }
}