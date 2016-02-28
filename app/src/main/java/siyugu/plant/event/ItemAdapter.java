package siyugu.plant.event;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import siyugu.plant.R;
import siyugu.plant.util.TimeUtil;

public class ItemAdapter extends ArrayAdapter<ItemAdapter.Item> {
  private final Context context;
  private final int entryLayoutResourceId;
  private final int sectionLayoutResourceId;
  private final List<Item> data;
  private final EventToggleCompleteListener listener;

  public ItemAdapter(Context context,
                     int entryLayoutResourceId,
                     int sectionLayoutResourceId,
                     List<Item> data,
                     EventToggleCompleteListener listener) {
    super(context, 0, data);

    this.context = context;
    this.entryLayoutResourceId = entryLayoutResourceId;
    this.sectionLayoutResourceId = sectionLayoutResourceId;
    this.data = data;
    this.listener = listener;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    Item item = data.get(position);

    if (item.isSection()) {
      return getSectionView((SectionItem) item, parent, view);
    } else {
      return getEntryView((EntryItem) item, parent, view);
    }
  }

  private View getSectionView(SectionItem item, ViewGroup parent, View view) {
    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    view = inflater.inflate(sectionLayoutResourceId, parent, false);
    TextView header = (TextView) view.findViewById(R.id.txtHeader);
    header.setText(item.getHeaderText());
    return view;
  }

  private View getEntryView(EntryItem item, ViewGroup parent, View view) {
    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    view = inflater.inflate(entryLayoutResourceId, parent, false);

    TextView mEventTypeText = (TextView) view.findViewById(R.id.event_type_item);
    CheckBox mTitleText = (CheckBox) view.findViewById(R.id.event_title_item);
    TextView mStartTimeText = (TextView) view.findViewById(R.id.start_time_text_item);
    TextView mTimePermittedText = (TextView) view.findViewById(R.id.time_permitted_text_item);

    final Event e = item.getEvent();
    mEventTypeText.setText(e.getTypeOfWork().toString());
    mTitleText.setText(e.getTitle());
    mTitleText.setChecked(e.getCompleted());
    mTitleText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.eventToggleComplete(e);
      }
    });
    mTimePermittedText.setText(
        String.format("%d hr %d min",
            e.getPermittedTime().getHours(),
            e.getPermittedTime().getMinutes()));
    mStartTimeText.setText(TimeUtil.LOCALTIME_FORMATTER.print(e.getStartTime()));
    return view;
  }

  public interface Item {
    boolean isSection();
  }

  public static class SectionItem implements Item {
    private String headerText;

    public SectionItem(String header) {
      this.headerText = header;
    }

    public String getHeaderText() {
      return headerText;
    }

    @Override
    public boolean isSection() {
      return true;
    }
  }

  public static class EntryItem implements Item {
    private Event event;

    public EntryItem(Event event) {
      this.event = event;
    }

    public Event getEvent() {
      return event;
    }

    @Override
    public boolean isSection() {
      return false;
    }
  }

  public interface EventToggleCompleteListener {
    void eventToggleComplete(Event e);
  }
}
