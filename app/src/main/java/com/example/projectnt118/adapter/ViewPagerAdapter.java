package com.example.projectnt118.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectnt118.fragment.DashboardFragment;
import com.example.projectnt118.fragment.MapFragment;
import com.example.projectnt118.fragment.ProfileFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }




    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new DashboardFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new MapFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
