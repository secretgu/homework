package siyugu.homework.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import siyugu.homework.util.BundleKeys;
import siyugu.homework.util.TimeUtil;

public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the current time as the default values for the picker
    LocalTime current = new LocalTime();
    int hour = current.getHourOfDay();
    int minute = current.getMinuteOfHour();

    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText timePicked = (EditText) getActivity().findViewById(id);
    String timeText = timePicked.getText().toString();
    if (timeText != null && timeText.isEmpty() == false) {
      try {
        LocalTime parsedTime = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime(timeText);
        hour = parsedTime.getHourOfDay();
        minute = parsedTime.getMinuteOfHour();
      } catch (IllegalArgumentException e) {
        // use default value when parse fails
      }
    }

    // Create a new instance of TimePickerDialog and return it
    return new TimePickerDialog(getActivity(), this, hour, minute,
        DateFormat.is24HourFormat(getActivity()));
  }

  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText timePicked = (EditText) getActivity().findViewById(id);
    LocalTime current = new LocalTime(hourOfDay, minute);
    timePicked.setText(TimeUtil.LOCALTIME_FORMATTER.print(current));
  }
}
