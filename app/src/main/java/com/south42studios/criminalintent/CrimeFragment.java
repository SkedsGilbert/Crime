package com.south42studios.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Jsin on 8/27/2015.
 */
public class CrimeFragment extends Fragment {

    private static final String TAG = "com.south42studios";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_POSITION = "position";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private Crime mCrime;
    private File mPhotoFile;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSendReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private CheckBox mSolvedCheckBox;
    private String contactID;


    public static CrimeFragment newInstance(UUID crimeId, int position) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        args.putInt(ARG_POSITION, position);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrimes(crimeID);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });
        updateTime();


        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mSendReportButton = (Button) v.findViewById(R.id.crime_report_button);
        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());
                builder.setType("text/plain");
                builder.setSubject(getString(R.string.crime_report_subject));
                builder.setText(getCrimeReport());
                builder.startChooser();

            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect_button);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getActivity().getPackageManager();
                if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                    String toastMessage = "You have no contacts app";
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                }
            }
        });

        // Need to add PackageManager to check if dialer is available!

        mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect_button);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + mCrime.getPhoneNumber());
                final Intent callSuspect = new Intent(Intent.ACTION_DIAL, number);
                startActivityForResult(callSuspect, REQUEST_CONTACT);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v;
    }

    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
    }

    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport() {
        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
        String dateString = dateFormat.format(mCrime.getDate());


        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;

            case REQUEST_TIME:
                Date dateTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE);
                mCrime.setDate(dateTime);
                updateTime();
                break;

            case REQUEST_CONTACT:
                if (data != null) {
                    Uri contactUri = data.getData();
                    String[] queryFields = new String[]{
                            ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID
                    };

                    Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

                    try {
                        if (c.getCount() == 0) {
                            return;
                        }

                        c.moveToFirst();
                        String suspect = c.getString(0);
                        contactID = c.getString(1);
                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(suspect);
                    } finally {
                        c.close();
                    }


                    Cursor phoneCursor =
                            getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                                            + contactID,
                                    null,
                                    null
                            );
                    try {
                        if (phoneCursor.getCount() == 0) {
                            return;
                        }

                        phoneCursor.moveToFirst();
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER));
                        mCrime.setPhoneNumber(phoneNumber);
                    } finally {
                        phoneCursor.close();
                    }
                }

                break;

            case REQUEST_PHOTO:
                updatePhotoView();

            default:
                break;

        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);

                Intent intent = new Intent(CrimeFragment.this.getActivity(), CrimeListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
