package siyugu.homework.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Map;

import hirondelle.date4j.DateTime;
import siyugu.homework.R;

public class CaldroidSimpleCustomFragment extends CaldroidFragment {
  @Override
  public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
    return new CaldroidSampleCustomAdapter(getActivity(), month, year,
        getCaldroidData(), extraData);
  }

  public static class CaldroidSampleCustomAdapter extends CaldroidGridAdapter {

    public CaldroidSampleCustomAdapter(Context context, int month, int year,
                                       Map<String, Object> caldroidData,
                                       Map<String, Object> extraData) {
      super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View cellView = convertView;

      // For reuse
      if (convertView == null) {
        cellView = inflater.inflate(R.layout.custom_cell, null);
      }

      int topPadding = cellView.getPaddingTop();
      int leftPadding = cellView.getPaddingLeft();
      int bottomPadding = cellView.getPaddingBottom();
      int rightPadding = cellView.getPaddingRight();

      TextView dateInMonth = (TextView) cellView.findViewById(R.id.day_in_month_text);

      dateInMonth.setTextColor(Color.BLACK);

      // Get dateTime of this cell
      DateTime dateTime = this.datetimeList.get(position);
      Resources resources = context.getResources();

      // Set color of the dates in previous / next month
      if (dateTime.getMonth() != month) {
        dateInMonth.setTextColor(resources
            .getColor(com.caldroid.R.color.caldroid_darker_gray));
      }

      boolean shouldResetSelectedView = false;

      // Customize for selected dates
      if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
        cellView.setBackgroundColor(resources
            .getColor(com.caldroid.R.color.caldroid_sky_blue));

        dateInMonth.setTextColor(Color.BLACK);
      } else {
        shouldResetSelectedView = true;
      }

      if (shouldResetSelectedView) {
        // Customize for today
        if (dateTime.equals(getToday())) {
          cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
        } else {
          cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
        }
      }

      dateInMonth.setText(String.valueOf(dateTime.getDay()));

      // Somehow after setBackgroundResource, the padding collapse.
      // This is to recover the padding
      cellView.setPadding(leftPadding, topPadding, rightPadding,
          bottomPadding);

      // Set custom color if required
      setCustomResources(dateTime, cellView, dateInMonth);

      return cellView;
    }
  }
}
