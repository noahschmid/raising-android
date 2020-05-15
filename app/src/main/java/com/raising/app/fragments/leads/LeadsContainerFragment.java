package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.util.HandshakePageAdapter;
import com.raising.app.util.TabOrigin;
import com.raising.app.viewModels.TabViewModel;

public class LeadsContainerFragment extends RaisingFragment {
    private TabLayout tabLayout;
    private TabItem yourTurn, pending, closed;
    private ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leads_container, container, false);

        customizeAppBar(getString(R.string.toolbar_title_leads), false);

        setBase(TabOrigin.LEADS);

        tabViewModel = ViewModelProviders.of(getActivity())
                .get(TabViewModel.class);

        if(tabViewModel.getCurrentLeadsFragment() != null) {
            changeFragment(tabViewModel.getCurrentLeadsFragment());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find all views
        tabLayout = view.findViewById(R.id.leads_tab_layout);
        yourTurn = view.findViewById(R.id.tab_your_turn);
        pending = view.findViewById(R.id.tab_pending);
        closed = view.findViewById(R.id.tab_closed);

        viewPager = view.findViewById(R.id.leads_view_pager);

        // initialize pager adapter for view pager
        HandshakePageAdapter pagerAdapter = new HandshakePageAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabViewModel.setCurrentLeadsTab(tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setCurrentItem(tabViewModel.getCurrentLeadsTab());
    }
}
