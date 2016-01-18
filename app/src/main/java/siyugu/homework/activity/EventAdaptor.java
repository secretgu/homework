package siyugu.homework.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import siyugu.homework.R;
import siyugu.homework.event.Event;
import siyugu.homework.util.TimeUtil;

public class EventAdaptor extends ArrayAdapter<Event> {
  private final Context context;
  private final int layoutResourceId;
  private final Event[] data;

  public EventAdaptor(Context context, int layoutResourceId, Event[] data) {
    super(context, layoutResourceId, data);

    this.context = context;
    this.layoutResourceId = layoutResourceId;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Log.e("EventAdaptor", "getView called for position " + position);
    View view = convertView;

    if (view == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      view = inflater.inflate(layoutResourceId, parent, false);

      ViewHolder viewHolder = new ViewHolder();
      viewHolder.mEventTypeText = (TextView) view.findViewById(R.id.event_type_item);
      viewHolder.mDescriptionText = (CheckedTextView) view.findViewById(R.id.event_description_item);
      viewHolder.mStartTimeText = (TextView) view.findViewById(R.id.start_time_text_item);
      viewHolder.mTimePermittedText = (TextView) view.findViewById(R.id.time_permitted_text_item);
      view.setTag(viewHolder);
    }

    // fill data
    ViewHolder holder = (ViewHolder) view.getTag();
    Event e = data[position];
    holder.mEventTypeText.setText(e.getTypeOfWork().toString());
    holder.mDescriptionText.setText(e.getDescription());
    holder.mDescriptionText.setChecked(e.getCompleted());
    holder.mTimePermittedText.setText(
        String.format("%d hr %d min",
            e.getPermittedTime().getHours(),
            e.getPermittedTime().getMinutes()));
    holder.mStartTimeText.setText(TimeUtil.LOCALTIME_FORMATTER.print(e.getStartTime()));

    return view;
  }

  static class ViewHolder {
    public TextView mEventTypeText;
    public CheckedTextView mDescriptionText;
    public TextView mStartTimeText;
    public TextView mTimePermittedText;
  }
}
