package it.rememo.rememo.ui.classes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.rememo.rememo.R;
import it.rememo.rememo.utils.Common;

// Tab group of classes
public class ClassesGroupPagerAdapter extends FragmentStateAdapter {
    final ArrayList<String> classTypes;
    public ClassesGroupPagerAdapter(Fragment fa) {
        super(fa);
        // Tabs
        this.classTypes = new ArrayList<>();
        this.classTypes.add(fa.getContext().getString(R.string.classes_joined));
        this.classTypes.add(fa.getContext().getString(R.string.classes_created));
    }

    @NotNull
    @Override
    public Fragment createFragment(int i) {
        Fragment fragment = new ClassesGroupFragment();
        Bundle args = new Bundle();
        args.putInt(ClassesGroupFragment.ARG_POSITION, i);
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
