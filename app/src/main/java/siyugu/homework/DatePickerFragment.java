package siyugu.homework;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by siyugu on 1/10/16.
 */
public class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the current date as the default date in the picker
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText datePicked = (EditText) getActivity().findViewById(id);
    String dateText = datePicked.getText().toString();
    if (dateText != null && dateText.isEmpty() == false) {
      try {
        Date parsedDate = DATE_FORMAT.parse(dateText);
        year = parsedDate.getYear() + 1900;  // FIXME(siyugu): don't use java.util.Date
        month = parsedDate.getMonth();
        day = parsedDate.getDate();
      } catch (ParseException pe) {
        // use default value when parse fails
      }
    }

    // Create a new instance of DatePickerDialog and return it
    return new DatePickerDialog(getActivity(), this, year, month, day);
  }

  public void onDateSet(DatePicker view, int year, int month, int day) {
    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText datePicked = (EditText) getActivity().findViewById(id);
    Calendar cal = Calendar.getInstance();
    cal.set(year, month, day);
    datePicked.setText(DATE_FORMAT.format(cal.getTime()));
  }
}