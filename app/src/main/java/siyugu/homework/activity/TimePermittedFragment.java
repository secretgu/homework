package siyugu.homework.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import siyugu.homework.R;

public class TimePermittedFragment extends DialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_time_permitted, null);
    final NumberPicker hourPicker = (NumberPicker) dialogView
        .findViewById(R.id.hour_permitted_picker);
    final NumberPicker minutePicker = (NumberPicker) dialogView
        .findViewById(R.id.minute_permitted_picker);
    initializeNumberPicker(hourPicker, minutePicker);

    builder.setView(dialogView)
        .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            int hour = hourPicker.getValue();
            int minute = minutePicker.getValue();
//            EditText timePermittedEdit = (EditText) getActivity()
//                .findViewById(R.id.edit_time_permitted);
//            timePermittedEdit.setText(String.format("%d hr %d min", hour, minute));
          }
        })
        .setNegativeButton(R.string.cancel_menuitem, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            TimePermittedFragment.this.getDialog().cancel();
          }
        });
    return builder.create();
  }

  private void initializeNumberPicker(NumberPicker hourPicker, NumberPicker minutePicker) {
    int min = 0;
    int max = 24;
    hourPicker.setMinValue(min);
    hourPicker.setMaxValue(max);
    String[] values = new String[max - min + 1];
    for (int i = min; i <= max; ++i) {
      values[i - min] = (i - min) + " hr";
    }
    hourPicker.setDisplayedValues(values);

    min = 0;
    max = 59;
    minutePicker.setMinValue(min);
    minutePicker.setMaxValue(max);
    values = new String[max - min + 1];
    for (int i = min; i <= max; ++i) {
      values[i - min] = (i - min) + " min";
    }
    minutePicker.setDisplayedValues(values);
  }
}
