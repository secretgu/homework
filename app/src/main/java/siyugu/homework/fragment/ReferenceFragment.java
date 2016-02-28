package siyugu.homework.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import siyugu.homework.BuildConfig;
import siyugu.homework.R;

public class ReferenceFragment extends ListFragment {
  private static final String TAG = "ReferenceFragment";
  private ArrayAdapter<CharSequence> mAdapter;
  private String[] mUrls;

  @Override
  public void onActivityCreated(Bundle savedInstanceBundle) {
    super.onActivityCreated(savedInstanceBundle);

    mAdapter = ArrayAdapter.createFromResource(
        getActivity(),
        R.array.reference_links_name,
        android.R.layout.simple_list_item_1);
    setListAdapter(mAdapter);

    mUrls = getActivity().getResources().getStringArray(R.array.reference_links_value);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    String url = mUrls[position];
    String canonicalUrl = URLUtil.guessUrl(url);
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(canonicalUrl));
    if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivity(browserIntent);
    } else {
      if (BuildConfig.DEBUG) {
        Log.i(TAG, "intent to open url in browser can not be handled");
      }
    }
  }
}
