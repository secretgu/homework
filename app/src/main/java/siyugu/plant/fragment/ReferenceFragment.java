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
      "Homestead High School: Home",
      "School Loop",
      "Naviance",
      "Infinite Campus",
      "Epitaph",
      "Calendar",
      "Staff Directory",
      "Guidance Counselor",
      "Guidance Events",
      "Bell Schedule",
      "Attendance",
      "Graduation and College Entrance Requirement",
      "College & Career Center",
      "Academics",
      "Library Catalog",
      "Student Life",
      "Athletics",
      "Final Grade Calculator: RogerHub",
      "Slader",
  };
  static final String[] HOMESTEAD_REFERENCE_VALUES = {
      "http://www.hhs.fuhsd.org",
      "https://homestead.schoolloop.com/portal/login?d=x&amp;return_url=1455934104872",
      "https://connection.naviance.com/family-connection/auth/login/?hsid=homestead",
      "https://campus.fuhsd.org/campus/portal/fremont.jsp?",
      "http://hhsepitaph.com",
      "http://www.hhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1232963517201",
      "http://www.hhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1231078951610",
      "http://www.hhs.fuhsd.org/guidance",
      "http://www.hhs.fuhsd.org/guidanceevents",
      "http://www.hhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1226557283831",
      "https://homestead.schoolloop.com/attendance?no_controls=t",
      "https://homestead.schoolloop.com/cms/page_view?d=x&piid=&vpid=1229223006083",
      "http://www.hhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1234018621243",
      "https://homestead.schoolloop.com/academics",
      "https://homestead.schoolloop.com/library?no_controls=t",
      "https://homestead.schoolloop.com/cms/page_view?d=x&piid=&vpid=1232370417797",
      "https://homestead.schoolloop.com/athleticshome",
      "https://rogerhub.com/final-grade-calculator/",
      "https://www.slader.com",
  };
  static final String[] FREMONT_REFERENCE_NAMES = {
      "Fremont High School: Home",
      "School Loop",
      "Naviance",
      "Infinite Campus",
      "The Phoenix",
      "Calendar",
      "Staff Directory",
      "Guidance Counselor",
      "Bell Schedule",
      "Attendance",
      "Graduation and College Entrance Requirement",
      "College & Career Center",
      "Academics",
      "Library Catalog",
      "Student Life",
      "Athletics",
      "Final Grade Calculator: RogerHub",
      "Slader",
  };
  static final String[] FREMONT_REFERENCE_VALUES = {
      "http://www.fhs.fuhsd.org/",
      "https://fremonths.schoolloop.com/portal/login?d=x&return_url=1460763635005",
      "https://connection.naviance.com/family-connection/auth/login/?hsid=fremont",
      "https://campus.fuhsd.org/campus/portal/fremont.jsp",
      "http://www.fhsphoenix.com/",
      "http://www.fhs.fuhsd.org/calendar",
      "http://www.fhs.fuhsd.org/staffdirectory",
      "http://www.fhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1227680294609",
      "http://www.fhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1226557378808",
      "http://www.fhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1220711230607",
      "http://www.fhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1228579259924",
      "http://www.fhs.fuhsd.org/cms/page_view?d=x&piid=&vpid=1222235297683",
      "http://www.fhs.fuhsd.org/cms/nothing?d=x&group_id=1211910079783",
      "http://www.fhs.fuhsd.org/library",
      "http://www.fhs.fuhsd.org/cms/nothing?d=x&group_id=1228578927828",
      "http://www.fhs.fuhsd.org/athletics",
      "https://rogerhub.com/final-grade-calculator/",
      "https://www.slader.com",
  };
  static final String[] PIONEER_REFERENCE_NAMES = {
      "Pioneer High School: Home",
      "Infinite Campus",
      "Naviance",
      "Pony Express",
      "Calendar",
      "Staff Directory",
      "Bell Schedule",
      "Attendance",
      "Guidance Counselor",
      "Graduation and College Entrance Requirement",
      "Academics",
      "Athletics",
      "School Year Snapshot 2015-2016",
      "Final Grade Calculator: RogerHub",
      "Slader",
  };
  static final String[] PIONEER_REFERENCE_VALUES = {
      "http://pioneerhigh.org",
      "https://sanjoseca.infinitecampus.org/campus/portal/sanjose.jsp",
      "https://connection.naviance.com/family-connection/auth/login/?hsid=pioneerhigh",
      "http://pioneerponyexpress.weebly.com",
      "https://calendar.google.com/calendar/embed?showTitle=0&height=600&wkst=1&bgcolor=%23FFFFFF&src=pioneerhighsanjose@gmail.com&color=%23182C57&ctz=America/Los_Angeles%22+style%3D%22+border-width:0+%22+width%3D%22800%22+height%3D%22600%22+frameborder%3D%220%22+scrolling%3D%22no%22",
      "http://pioneerhigh.org/staff-directory/",
      "http://pioneerhigh.org/docs/1516_BellSchedules.pdf",
      "http://pioneerhigh.org/school-information/contact-us/",
      "http://pioneerhigh.org/academics-activities/college-and-career-center/",
      "http://pioneerhigh.org/docs/UCCSUSJUSDRequirements_(1)_PDF.pdf",
      "http://pioneerhigh.org/academics-activities/academics/",
      "http://pioneerhigh.org/academics-activities/athletics/",
      "http://www.sjusd.org/pdf/districtinformation/2015-2016_instructional_calendar.pdf",
      "https://rogerhub.com/final-grade-calculator/",
      "https://www.slader.com",
  };

  static enum School {
    HOMESTEAD("Homestead High School", HOMESTEAD_REFERENCE_NAMES, HOMESTEAD_REFERENCE_VALUES),
    FREMONT("Fremont High School", FREMONT_REFERENCE_NAMES, FREMONT_REFERENCE_VALUES),
    PIONEER("Pioneer High School", PIONEER_REFERENCE_NAMES, PIONEER_REFERENCE_VALUES);

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
