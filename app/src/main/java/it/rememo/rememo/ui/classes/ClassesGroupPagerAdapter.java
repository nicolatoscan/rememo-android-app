package it.rememo.rememo.ui.classes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.rememo.rememo.ui.collections.CollectionsGroupFragment;

public class ClassesGroupPagerAdapter extends FragmentStateAdapter {
    ArrayList<String> classTypes;
    public ClassesGroupPagerAdapter(Fragment fa) {
        super(fa);
        this.classTypes = new ArrayList<>();
        this.classTypes.add("Joined");
        this.classTypes.add("Created");
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
        return classTypes.size();
    }

    public String getTitle(int pos) {
        return classTypes.get(pos);
    }
}
