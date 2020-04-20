package com.raising.app.fragments.leads;

import android.os.Bundle;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.raising.app.R;
import com.raising.app.fragments.RaisingFragment;
import com.raising.app.util.HandshakePageAdapter;

public class LeadsContainerFragment extends RaisingFragment {
    private TabLayout tabLayout;
    private TabItem yourTurn, pending, closed;
    private ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_handshakes, container, false);

        customizeAppBar(getString(R.string.toolbar_title_handshakes), false);

        tabLayout = view.findViewById(R.id.handshake_tab_layout);
        yourTurn = view.findViewById(R.id.tab_your_turn);
        pending = view.findViewById(R.id.tab_pending);
        closed = view.findViewById(R.id.tab_closed);

        viewPager = view.findViewById(R.id.handshake_view_pager);

        HandshakePageAdapter pagerAdapter = new HandshakePageAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

        return view;
    }
}
