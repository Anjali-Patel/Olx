package gss.com.bsell;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gss.com.bsell.CheckNetworkSpeed.CheckInternetSpeed;

public class ChatFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ChatFragment() {
    }
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_chat_fragment, container, false);

            try {
                CheckInternetSpeed internet = new CheckInternetSpeed();
                int s = Integer.parseInt(internet.ConnectionQuality(getActivity()));
                if (s < 3){
                    Toast.makeText(getActivity(), "Your internet speed is low", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e){

            }
            viewPager = (ViewPager) v.findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) v.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        return v;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new AllChatFragment(), "All");
        adapter.addFragment(new MyChatsFragment(), "BUYING");
        adapter.addFragment(new MySellingChat(), "SELLING");
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

