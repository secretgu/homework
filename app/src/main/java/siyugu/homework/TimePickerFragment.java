package siyugu.homework;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by siyugu on 1/10/16.
 */
public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the current time as the default values for the picker
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);

    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText timePicked = (EditText) getActivity().findViewById(id);
    String timeText = timePicked.getText().toString();
    if (timeText != null && timeText.isEmpty() == false) {
      try {
        Date parsedDate = TIME_FORMAT.parse(timeText);
        hour = parsedDate.getHours();
        minute = parsedDate.getMinutes();
      } catch (ParseException pe) {
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
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    cal.set(Calendar.MINUTE, minute);
    timePicked.setText(TIME_FORMAT.format(cal.getTime()));
  }
}
