package com.aset.dey.peptalk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT=1024*1024*10;//Converting bytes to MB

    protected Uri mMediaUri; //Uri=uniform resource identifier-used to identify file types in storage

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which)
            {
                case 0://take picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri==null)
                    {
                        //display error
                        Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);//putting image in mMediaUri
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                        break;
                case 1://take Video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri==null)
                    {
                        //display error
                        Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();
                    }
                    else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);//0 is low quality and 1 is high
                        startActivityForResult(videoIntent,TAKE_VIDEO_REQUEST);
                    }
                    break;
                case 2://Choose pic
                    Intent ChoosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    ChoosePhotoIntent.setType("image/*");
                    startActivityForResult(ChoosePhotoIntent,PICK_PHOTO_REQUEST);
                    break;
                case 3://choose video
                    Intent ChooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    ChooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this,R.string.VideoError,Toast.LENGTH_LONG).show();
                    startActivityForResult(ChooseVideoIntent, PICK_PHOTO_REQUEST);
                    break;
            }
        }
    };

    private Uri getOutputMediaFileUri(int mediaType) {
            //To be safe ,we should check that the SDCard or ExternalStorage is mounted
            //using Environment.getExternalStorageState() before doing this
        if(isExternalStorageAvailable())
        {   String appName =MainActivity.this.getString(R.string.app_name);
            //get the URi
            //1.get the external storage directory
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);
            //2.Create own sub directory of our app name
            if(!mediaStorageDir.exists()){
                if(mediaStorageDir.mkdirs())
                {
                    Log.e(TAG,"Failed to create directory");
                    return null;
                }
            }
            //3.Create the filename
            //4.Create the file
            File mediaFile;
            Date now=new Date();//date is default constructor
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(now);
            String path = mediaStorageDir.getPath()+File.separator;
            if(mediaType == MEDIA_TYPE_IMAGE)
            {
                mediaFile=new File(path+"IMG_"+timestamp+".jpg");
            }
            else if (mediaType == MEDIA_TYPE_VIDEO)
            {
                mediaFile=new File(path+"VID_"+timestamp+".mp4");
            }
            else
            {
                return null;
            }
            Log.d(TAG,"File:"+ Uri.fromFile(mediaFile));

            //5.Return the file's URI
            return Uri.fromFile(mediaFile);

        }
        else
        {
            return null;
        }

    }
    private boolean isExternalStorageAvailable(){
        String state= Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED) )
        {
            return true;
        }
        else
        {
            return false;

        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpened(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();

        }
        else {
            Log.i(TAG, currentUser.getUsername());
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//after a photo or video is selected we end up in this activity

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==PICK_PHOTO_REQUEST||requestCode==PICK_VIDEO_REQUEST)
            {
                if(data==null)
                {
                    Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                }
                else
                {
                    mMediaUri=data.getData();
                }
                Log.i(TAG,"MediaUri"+mMediaUri);
                if (requestCode==PICK_VIDEO_REQUEST)
                {//make sure fill is less than 10mb
                    int filesize=0;
                    InputStream inputStream=null;
                    try {


                        inputStream=getContentResolver().openInputStream(mMediaUri);
                        filesize = inputStream.available();
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(this,R.string.errorOpeningFile,Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e)
                    {   Toast.makeText(this,R.string.errorOpeningFile,Toast.LENGTH_LONG).show();
                        return;

                    }
                    finally
                    {
                        try
                        {inputStream.close();
                        }
                        catch (IOException e)
                        {

                        }
                    }
                    if(filesize>=FILE_SIZE_LIMIT)
                    {
                        Toast.makeText(this,R.string.FileTooLong,Toast.LENGTH_LONG).show();
                        return;
                    }

                }

            }
            else
            {
                Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScan.setData(mMediaUri);
                sendBroadcast(mediaScan);//asking gallery to add the file by broadcasting
                //add to gallery
            }
            Intent RecipientsIntent = new Intent(this,RecipientsActivity.class);
            RecipientsIntent.setData(mMediaUri);//uri is passed with the intent
            String fileType;
            if(requestCode==PICK_PHOTO_REQUEST||requestCode==PICK_VIDEO_REQUEST)
            {
                fileType = ParseConstants.TYPE_IMAGE;
            }
            else
            {
                fileType = ParseConstants.TYPE_VIDEO;
            }
            RecipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);
            startActivity(RecipientsIntent);
        }
        else if(resultCode!=RESULT_CANCELED)
        {
            Toast.makeText(this,R.string.general_error,Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        switch(itemId)
        {
            case R.id.action_logout:

                ParseUser.logOut();
                navigateToLogin();
                break;

            case R.id.action_edit_friends:

                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera://options when we tap on camera
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);//mDialogListener is the method above
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }





}
