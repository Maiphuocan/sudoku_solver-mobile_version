package maiphuocan.swinburne.utilities;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DayCountFragment extends android.support.v4.app.Fragment {


    public DayCountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_count, container, false);
    }

    public static DayCountFragment newInstance()
    {
        DayCountFragment fragment = new DayCountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Button calculate = (Button)getView().findViewById(R.id.buttonGenerate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Get controls*/
                TextView noOfDay = (TextView)getView().findViewById(R.id.textViewNoOfDay);
                TextView noOfMonth = (TextView)getView().findViewById(R.id.textViewNoOfMonth);
                TextView noOfYear = (TextView)getView().findViewById(R.id.textViewNoOfYear);
                DatePicker dpFrom = (DatePicker)getView().findViewById(R.id.datePickerFrom);
                DatePicker dpTo = (DatePicker)getView().findViewById(R.id.datePickerTo);

                /*Get input*/
                int fromYear = dpFrom.getYear();
                int fromMonth = dpFrom.getMonth();
                int fromDay = dpFrom.getDayOfMonth();
                int toYear = dpTo.getYear();
                int toMonth = dpTo.getMonth();
                int toDay = dpTo.getDayOfMonth();

                /*Calculate for NoOfDay*/
                Calendar c = Calendar.getInstance();
                c.set(fromYear, fromMonth - 1, fromDay);
                long fromDate = c.getTimeInMillis();
                c.set(toYear, toMonth - 1, toDay);
                long toDate = c.getTimeInMillis();
                float noOfDays = (toDate - fromDate) / (3600000*24.0f);

                /*Swap two dates in case toDate is erlier than fromDate*/
                if (noOfDays < 0) {
                    int tmpInt = toYear;
                    toYear = fromYear;
                    fromYear = tmpInt;
                    tmpInt = toMonth;
                    toMonth = fromMonth;
                    fromMonth = tmpInt;
                    tmpInt = toDay;
                    toDay = fromDay;
                    fromDay = tmpInt;
                    long tmpLong = toDate;
                    toDate = fromDate;
                    fromDate = tmpLong;
                    noOfDays = (toDate - fromDate) / (3600000*24.0f);
                }
                noOfDay.setText("Equal to: " + (int)Math.floor(noOfDays) + " days");

                /*Calculate NoOfMonth*/
                int noOfMonths;
                int tmpDay;
                if (toDay >= fromDay) {
                    noOfMonths = (toYear - fromYear) * 12 + (toMonth - fromMonth);
                    tmpDay = toDay - fromDay;
                } else {
                    noOfMonths = (toYear - fromYear) * 12 + (toMonth - fromMonth) - 1;
                    c.set(toYear, toMonth - 2, fromDay);
                    long tmpDate = c.getTimeInMillis();
                    tmpDay = (int)Math.floor((toDate - tmpDate) / (3600000*24.0f));
                }
                noOfMonth.setText("Equal to : " + noOfMonths + " months, " + tmpDay + " days");
                int noOfYears = (int)Math.floor(noOfMonths / 12.0f);
                noOfMonths = noOfMonths - noOfYears * 12;
                noOfYear.setText("Equal to : " + noOfYears + " years, " + noOfMonths + " months, " + tmpDay + " days");
            }
        });
    }
}
