package it.rememo.rememo.ui.collections;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollectionsGroupPagerAdapter extends FragmentStateAdapter {
    ArrayList<String> collectionsGroups;
    public CollectionsGroupPagerAdapter(Fragment fa, ArrayList<String> collectionsGroups) {
        super(fa);
        this.collectionsGroups = collectionsGroups;
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
}
