package gss.com.bsell;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SellFragment extends Fragment {

    private TabLayout tabLayout;
    //    private ViewPager viewPager;
    private MyViewPager viewPager;
    private Fragment fragment;

    public SellFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_sell_fragment, container, false);

        viewPager = (MyViewPager) v.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {

       ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());


        fragment = new Addsell();

        Bundle bundle = new Bundle();
        bundle.putString("sub_cat", getActivity().getIntent().getStringExtra("sub_cat"));
        bundle.putString("sub_catid", getActivity().getIntent().getStringExtra("sub_catid"));
        bundle.putString("category", getActivity().getIntent().getStringExtra("category"));
        bundle.putString("cat_id", getActivity().getIntent().getStringExtra("cat_id"));
        bundle.putString("sub_cat2_id", getActivity().getIntent().getStringExtra("sub_cat_2_id"));
        bundle.putString("sub_cat2", getActivity().getIntent().getStringExtra("sub_cat2"));
        bundle.putString("sub_cat3_id", getActivity().getIntent().getStringExtra("sub_cat3_id"));
        bundle.putString("sub_cat3", getActivity().getIntent().getStringExtra("sub_cat3"));

        adapter.addFragment(fragment, "Add Sell");
          adapter.addFragment(new SellList(), "My Sell");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList =   new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


}


