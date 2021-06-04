package it.rememo.rememo.ui.collections;

import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
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
                for (StudentClass cl : classes) {
                    collectionsGroups.add(cl);
                }
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
