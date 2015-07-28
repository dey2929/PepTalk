package com.aset.dey.peptalk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ListActivity {
    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFiletype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);


        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mMediaUri = getIntent().getData();
        mFiletype=getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
    }
    public void onResume()
    {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query=mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e)//getting list of friends
            {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];//creating an array to put list of users
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, usernames);//cannot use fragment name as fragment cannot extend activity so we get context by getting list view
                    setListAdapter(adapter);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.ErrorTitle);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog Dialog = builder.create();
                    Dialog.show();
                    Log.e(TAG, e.getMessage());//logging the error message for the developer

                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
        case R.id.action_send:
            ParseObject Message = createMessage();//Parse object is the base class for all the Parse classes we have been using.....ParseUser is a child and ParseRelation is a collection of all ParseObjects
            if(Message==null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.error_selecting_file);
                builder.setTitle(R.string.error_selecting_file_title);
                builder.setPositiveButton(android.R.string.ok,null);
                AlertDialog dialog =builder.create();
                dialog.show();

                //error
            }
            else {
                send(Message);//above we created a new class in the backend
                finish();
            }
                return true;
    }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(l.getCheckedItemCount()>0)
        {
            mSendMenuItem.setVisible(true);//when item is clicked the send button is displayed
        }
        else
        {
            mSendMenuItem.setVisible(false);
        }
    }
    protected ParseObject createMessage()//for a message we need - RecipiendID,SenderID,SenderName,File,Filetype.
    {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_REPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFiletype);
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMediaUri);
        if(fileBytes==null)
        {
            return null;
        }
        else
        {
            if(mFiletype.equals(ParseConstants.TYPE_IMAGE))
            {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);//reduces image size to less than 10 mb
            }
            String fileName = FileHelper.getFileName(this,mMediaUri,mFiletype);
            ParseFile file = new ParseFile(fileName,fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
        }
        return message;
    }
    protected ArrayList<String> getRecipientIds()//used to get friends to whom the msg is to be sent
    {
        ArrayList<String> recipientIds = new ArrayList<String>();//this will hold all friends to whom the message is to be sent
        for(int i=0;i<getListView().getCount();i++)
        {

            if (getListView().isItemChecked(i))
            {
                recipientIds.add(mFriends.get(i).getObjectId());

            }
        }
        return recipientIds;
    }
    protected void send(ParseObject message)
    {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                {
                    Toast.makeText(RecipientsActivity.this,R.string.success_message,Toast.LENGTH_LONG).show();
                    //success
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_sending_message);
                    builder.setTitle(R.string.error_selecting_file_title);
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog =builder.create();
                    dialog.show();

                }
            }
        });
    }

}

