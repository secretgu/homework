package siyugu.plant.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import siyugu.plant.BuildConfig;
import siyugu.plant.R;

public class ReferenceFragment extends Fragment implements AdapterView.OnItemSelectedListener {
  private static final String TAG = "ReferenceFragment";
  private Spinner mTypeOfWorkSpinner;
  private ListView mReferenceListView;
  private String[] mUrls;

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.reference_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mTypeOfWorkSpinner = (Spinner) view.findViewById(R.id.school_selector);
    mReferenceListView = (ListView) view.findViewById(R.id.reference_link_listview);
    mReferenceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mUrls == null) {
          return;
        }
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
    });

    ArrayAdapter<School> schoolAdaptor =
        new ArrayAdapter<School>(
            getActivity(),
            android.R.layout.simple_spinner_item,
            School.values());
    schoolAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mTypeOfWorkSpinner.setAdapter(schoolAdaptor);

    mTypeOfWorkSpinner.setOnItemSelectedListener(this);
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    Log.i(TAG, "position: " + position);

    School s = School.values()[position];
    mUrls = s.getReferenceLinks();
    ArrayAdapter<String> referenceAdaptor = new ArrayAdapter<String>(
        getActivity(),
        android.R.layout.simple_list_item_1,
        s.getReferenceDisplayNames());
    mReferenceListView.setAdapter(referenceAdaptor);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
  }

  static final String[] HOMESTEAD_REFERENCE_NAMES = {
      "Homestead High School: Home Page",
      "School Loop",
      "Final Grade Calculator: RogerHub",
      "Slader",
  };
  static final String[] HOMESTEAD_REFERENCE_VALUES = {
      "http://www.hhs.fuhsd.org",
      "https://homestead.schoolloop.com/portal/login?d=x&amp;return_url=1455934104872",
      "https://rogerhub.com/final-grade-calculator/",
      "https://www.slader.com",
  };
  static final String[] SAN_JOSE_REFERENCE_NAMES = {
      "facebook",
      "google",
  };
  static final String[] SAN_JOSE_REFERENCE_VALUES = {
      "http://www.facebook.com",
      "http://www.google.com",
  };

  static enum School {
    HOMESTEAD("Homestead", HOMESTEAD_REFERENCE_NAMES, HOMESTEAD_REFERENCE_VALUES),
    SAN_JOSE("San Jose", SAN_JOSE_REFERENCE_NAMES, SAN_JOSE_REFERENCE_VALUES);

    School(String displayName, String[] referenceDisplayNames,
           String[] referenceLinks) {
      this.displayName = displayName;
      this.referenceDisplayNames = referenceDisplayNames;
      this.referenceLinks = referenceLinks;
    }

    String[] getReferenceDisplayNames() {
      return referenceDisplayNames;
    }

    String[] getReferenceLinks() {
      return referenceLinks;
    }

    private final String displayName;
    private final String[] referenceDisplayNames;
    private final String[] referenceLinks;

    @Override
    public String toString() {
      return displayName;
    }
  }
}
