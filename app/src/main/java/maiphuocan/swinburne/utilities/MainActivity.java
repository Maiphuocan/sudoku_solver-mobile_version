package maiphuocan.swinburne.utilities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Using Actionbar*/
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.addTab(actionBar.newTab().setText("Day Calculator").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Sudoku Solver").setTabListener(this));

        /*set OnPageChangeListener to actionBar*/
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // show the given tab
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // probably ignore this event
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        SparseArray<Fragment> fragmentArray = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);

        }

        public Fragment getFragment(int position)
        {
            return fragmentArray.get(position);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment;
            if (position == 0) {
                fragment = DayCountFragment.newInstance();
            } else {
                fragment = SudokuSolverFragment.newInstance();
            }

            fragmentArray.put(position, fragment); // store for use later
            return fragment;
        }

        @Override
        public int getCount() {
            return 2; // 2 fragments
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "FIRST";
                case 1:
                    return "SECOND";
            }
            return null;
        }

        public void destroyItem(ViewGroup container, int position, Object object)
        {
            super.destroyItem(container, position, object);
            fragmentArray.remove(position);
        }
    }
}