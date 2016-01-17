package siyugu.homework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;

public class TestListView extends AppCompatActivity {
  private static final String TAG = "TestListView";

  private static final int CHOOSE_IMAGE_REQUEST = 1;
  private static final int CAMERA_REQUEST = 2;

  private Spinner mTypeOfWorkSpinner;
  private Spinner mWarningTimeSpinner;
  private Spinner mRepeatSpinner;
  private NumberPicker mHourPermittedPicker;
  private NumberPicker mMinutePermittedPicker;
  private ImageButton mInsertPhotoBtn;
  private ImageView mPhotoAdded;
  private EditText mDescriptionText;
  private EditText mDueDateText;
  private EditText mDoDateText;
  private EditText mStartTimeText;

  private EventDB events;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_list_view);

    events = EventDB.getInstance();

    initializeUiFields();
    initializeSpinners();
    initializeNumberPicker();
  }

  // Call before other initializing methods
  private void initializeUiFields() {
    mTypeOfWorkSpinner = (Spinner) findViewById(R.id.type_of_work_selector);
    mWarningTimeSpinner = (Spinner) findViewById(R.id.warning_time_selector);
    mRepeatSpinner = (Spinner) findViewById(R.id.repeat_selector);
    mHourPermittedPicker = (NumberPicker) findViewById(R.id.hour_permitted_picker);
    mMinutePermittedPicker = (NumberPicker) findViewById(R.id.minute_permitted_picker);
    mPhotoAdded = (ImageView) findViewById(R.id.photo_added);
    mInsertPhotoBtn = (ImageButton) findViewById(R.id.add_photo_btn);
    mDescriptionText = (EditText) findViewById(R.id.editDescription);
    mDueDateText = (EditText) findViewById(R.id.edit_due_date);
    mDoDateText = (EditText) findViewById(R.id.edit_do_date);
    mStartTimeText = (EditText) findViewById(R.id.edit_start_time);
  }

  private void initializeSpinners() {
    ArrayAdapter<Event.TypeOfWork> typeOfWorkAdaptor =
        new ArrayAdapter<Event.TypeOfWork>(
            this,
            android.R.layout.simple_spinner_item,
            Event.TypeOfWork.values());
    typeOfWorkAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mTypeOfWorkSpinner.setAdapter(typeOfWorkAdaptor);

    ArrayAdapter<Event.WarningTime> warningTimeAdaptor =
        new ArrayAdapter<Event.WarningTime>(
            this,
            android.R.layout.simple_spinner_item,
            Event.WarningTime.values());
    warningTimeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mWarningTimeSpinner.setAdapter(warningTimeAdaptor);

    ArrayAdapter<Event.RepeatPattern> repeatAdaptor =
        new ArrayAdapter<Event.RepeatPattern>(
            this,
            android.R.layout.simple_spinner_item,
            Event.RepeatPattern.values());
    repeatAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mRepeatSpinner.setAdapter(repeatAdaptor);
  }

  private void initializeNumberPicker() {
    int min = 0;
    int max = 24;
    mHourPermittedPicker.setMinValue(min);
    mHourPermittedPicker.setMaxValue(max);
    String[] values = new String[max - min + 1];
    for (int i = min; i <= max; ++i) {
      values[i - min] = (i - min) + " hr";
    }
    mHourPermittedPicker.setDisplayedValues(values);

    min = 0;
    max = 59;
    mMinutePermittedPicker.setMinValue(min);
    mMinutePermittedPicker.setMaxValue(max);
    values = new String[max - min + 1];
    for (int i = min; i <= max; ++i) {
      values[i - min] = (i - min) + " min";
    }
    mMinutePermittedPicker.setDisplayedValues(values);
  }

  public void selectImage(View view) {
    final CharSequence[] items = getResources().getStringArray(R.array.insert_photo_menus);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.insert_photo_btn);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        if (items[item].equals(getResources().getString(R.string.take_photo_menuitem))) {
          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          startActivityForResult(intent, CAMERA_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.choose_from_libray_menuitem))) {
          Intent intent = new Intent(
              Intent.ACTION_PICK,
              android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          intent.setType("image/*");
          startActivityForResult(
              Intent.createChooser(intent, "Select File"),
              CHOOSE_IMAGE_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.cancel_menuitem))) {
          dialog.dismiss();
        }
      }
    });
    builder.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == CHOOSE_IMAGE_REQUEST) {
        onSelectFromGalleryResult(data);
      } else if (requestCode == CAMERA_REQUEST) {
        onCaptureImageResult(data);
      }
    }
  }

  private void onCaptureImageResult(Intent data) {
    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

    File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        System.currentTimeMillis() + ".jpg");
    Log.d(TAG, "photo saved to: " + destination);

    FileOutputStream fo;
    try {
      destination.createNewFile();
      fo = new FileOutputStream(destination);
      fo.write(bytes.toByteArray());
      fo.close();

      // Tell the media scanner about the new file so that it is
      // immediately available to the user.
      MediaScannerConnection.scanFile(this,
          new String[]{destination.toString()}, null,
          new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
              Log.i("ExternalStorage", "Scanned " + path + ":");
              Log.i("ExternalStorage", "-> uri=" + uri);
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }

    mPhotoAdded.setImageBitmap(thumbnail);
  }

  private void onSelectFromGalleryResult(Intent data) {
    Uri selectedImageUri = data.getData();
    String[] projection = {MediaStore.MediaColumns.DATA};
    Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
        null);
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
    cursor.moveToFirst();

    String selectedImagePath = cursor.getString(column_index);
    Log.d(TAG, "photo selected: " + selectedImagePath);

    Bitmap bm;
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(selectedImagePath, options);
    final int REQUIRED_SIZE = 200;
    int scale = 1;
    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
      scale *= 2;
    options.inSampleSize = scale;
    options.inJustDecodeBounds = false;
    bm = BitmapFactory.decodeFile(selectedImagePath, options);

    mPhotoAdded.setImageBitmap(bm);
  }

  public void showDateTimePickerDialog(View view) {
    DialogFragment newFragment = null;
    Bundle fragmentData = new Bundle();
    fragmentData.putInt(BundleKeys.VIEW_ID_KEY, view.getId());

    switch (view.getId()) {
      case R.id.edit_start_time: {
        newFragment = new TimePickerFragment();
        fragmentData.putString(BundleKeys.FRAGMENT_TAG, "startTimePicker");
        break;
      }
      case R.id.edit_do_date: {
        newFragment = new DatePickerFragment();
        fragmentData.putString(BundleKeys.FRAGMENT_TAG, "doDatePicker");
        break;
      }
      case R.id.edit_due_date: {
        newFragment = new DatePickerFragment();
        fragmentData.putString(BundleKeys.FRAGMENT_TAG, "dueDatePicker");
        break;
      }
      default: {
        Log.e(TAG, "unknown view id");
        break;
      }
    }
    if (newFragment == null) {
      Log.e(TAG, "failed to create a new fragment");
      return;
    }
    newFragment.setArguments(fragmentData);
    newFragment.show(getFragmentManager(), fragmentData.getString(BundleKeys.FRAGMENT_TAG));
  }

  public void onAddEventBtnClick(View view) {
    Event e = new Event(
        (Event.TypeOfWork) mTypeOfWorkSpinner.getSelectedItem(),
        mDescriptionText.getText().toString(),
        mDueDateText.getText().toString(),
        mDoDateText.getText().toString(),
        "filepath",  // TODO: get file path
        mStartTimeText.getText().toString(),
        mHourPermittedPicker.getValue(),
        mMinutePermittedPicker.getValue(),
        (Event.WarningTime) mWarningTimeSpinner.getSelectedItem(),
        (Event.RepeatPattern) mRepeatSpinner.getSelectedItem()
    );
    events.addEvent(e);
  }

  public void onCancelBtnClick(View view) {
    events.printAllEvents();
  }
}
