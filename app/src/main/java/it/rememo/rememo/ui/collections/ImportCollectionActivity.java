package it.rememo.rememo.ui.collections;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import it.rememo.rememo.MainActivity;
import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityImportCollectionBinding;
import it.rememo.rememo.models.Collection;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.ui.login.LoginActivity;
import it.rememo.rememo.utils.Common;


// Page to import a collection
public class ImportCollectionActivity extends AppCompatActivity {

    ActivityImportCollectionBinding binding;
    Collection collection;
    // If collection is mine i can't import it
    boolean cantImport = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImportCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnImportCollection.setEnabled(false);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        binding.btnImportCollection.setOnClickListener(v -> importCollection());

        // Hide before loading collection
        binding.btnImportCollection.setVisibility(View.GONE);
        binding.txtCollectionName.setVisibility(View.GONE);
        binding.txtTitle.setVisibility(View.GONE);

        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
            String url = appLinkIntent.getData().toString();
            String[] urlParts = url.split("/");
            String id = urlParts[urlParts.length - 1];

            // Get collection
            Collection.getCollectionById(
                id,
                this::updateUI,
                ex -> {
                    // The url is probably broken, exit app
                    Common.toast(this, getString(R.string.class_not_found));
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            );

        } else {
            // This page is used only with an intent
            finish();
        }
    }

    // Update UI with collection data
    void updateUI(Collection collection) {
        if (collection.getOwnerId().equals(Common.getUserId())) { // Can't import my collections
            binding.txtTitle.setText(getString(R.string.coll_import_title_owner_error));
            binding.btnImportCollection.setText(getString(R.string.login_go_to_rememo));
        } else {
            cantImport = false;
        }

        binding.txtCollectionName.setText(collection.getName());

        binding.btnImportCollection.setVisibility(View.VISIBLE);
        binding.txtCollectionName.setVisibility(View.VISIBLE);
        binding.txtTitle.setVisibility(View.VISIBLE);


        this.collection = collection;
        binding.btnImportCollection.setEnabled(true);
    }

    // Clone collection to my collections
    void importCollection() {
        if (collection == null)
            return;

        if (cantImport) { // Is my collection?
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        binding.btnImportCollection.setEnabled(false);
        binding.progressImport.setVisibility(View.VISIBLE);

        Collection.importCollection(
            collection,
            x -> {
                Common.toast(this, Common.resStr(this, R.string.colls_imported));
                startActivity(new Intent(this, MainActivity.class));
            },
            ex -> Common.toast(this, Common.resStr(this, R.string.colls_imported_failed))
        );

    }
}