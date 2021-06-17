package it.rememo.rememo.ui.collections;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.models.StudentClass;
import it.rememo.rememo.utils.Common;

// Tabs adapter for collections
public class CollectionsGroupPagerAdapter extends FragmentStateAdapter {
    final ArrayList<StudentClass> collectionsGroups;
    final Fragment fragment;

    public CollectionsGroupPagerAdapter(Fragment fa) {
        super(fa);
        fragment = fa;

        collectionsGroups = new ArrayList<>();
        // Used to create mine tab
        collectionsGroups.add(null);

        // get all classes
        StudentClass.getClasses(false,
            (classes) -> {
                collectionsGroups.addAll(classes);
                notifyDataSetChanged();
            },
            (ex) -> { }
        );
    }

    @NotNull
    @Override
    public Fragment createFragment(int i) {
        Fragment fragment = new CollectionsGroupFragment();
        Bundle args = new Bundle();
        args.putInt(CollectionsGroupFragment.ARG_POSITION, i);
        args.putSerializable(CollectionsGroupFragment.ARG_CLASS, collectionsGroups.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return collectionsGroups.size();
    }

    public String getTitle(int pos) {
        StudentClass cl = collectionsGroups.get(pos);
        if (cl == null) {
            return fragment.getContext().getString(R.string.colls_mine);
        }
        return cl.getName();
    }

}
