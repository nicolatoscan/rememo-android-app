package it.rememo.rememo.ui.collections;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.rememo.rememo.models.StudentClass;

public class CollectionsGroupPagerAdapter extends FragmentStateAdapter {
    ArrayList<StudentClass> collectionsGroups;
    public CollectionsGroupPagerAdapter(Fragment fa) {
        super(fa);
        collectionsGroups = new ArrayList<StudentClass>();
        collectionsGroups.add(null);

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
            return "Mine";
        }
        return cl.getName();
    }

}
