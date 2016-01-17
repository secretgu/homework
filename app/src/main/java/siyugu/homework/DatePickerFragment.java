package siyugu.homework;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
  private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd");

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the current date as the default date in the picker
    LocalDate current = new LocalDate();
    int year = current.getYear();
    int month = MonthUtil.jodaMonthToJavaMonth(current.getMonthOfYear());
    int day = current.getDayOfMonth();

    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText datePicked = (EditText) getActivity().findViewById(id);
    String dateText = datePicked.getText().toString();
    if (dateText != null && dateText.isEmpty() == false) {
      try {
        LocalDate parsedDate = DATETIME_FORMATTER.parseLocalDate(dateText);
        year = parsedDate.getYear();
        month = MonthUtil.jodaMonthToJavaMonth(parsedDate.getMonthOfYear());
        day = parsedDate.getDayOfMonth();
      } catch (IllegalArgumentException e) {
        // use default value when parse fails
      }
    }

    // Create a new instance of DatePickerDialog and return it
    return new DatePickerDialog(getActivity(), this, year, month, day);
  }

  public void onDateSet(DatePicker view, int year, int month, int day) {
    int id = getArguments().getInt(BundleKeys.VIEW_ID_KEY);
    EditText datePicked = (EditText) getActivity().findViewById(id);
    LocalDate current = new LocalDate(year, MonthUtil.javaMonthToJodaMonth(month), day);
    datePicked.setText(DATETIME_FORMATTER.print(current));
  }
}