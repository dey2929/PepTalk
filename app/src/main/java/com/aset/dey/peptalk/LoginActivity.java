package com.aset.dey.peptalk;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.*;



public class LoginActivity extends ActionBarActivity {


    protected EditText mUsername;
    protected EditText mPassword;
    protected Button LoginButton;

    protected TextView mSignUpTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
        mSignUpTextView = (TextView)findViewById(R.id.signuptext);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {//what happens when we click sign up text
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        mUsername = (EditText)findViewById(R.id.LoginUsername);
        mPassword = (EditText)findViewById(R.id.LoginPassword);

        LoginButton = (Button)findViewById(R.id.Loginbutton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Username = mUsername.getText().toString();
                String Password = mPassword.getText().toString();//as the text is editable which is a different data type we change it to a string

                Username = Username.trim();//trim gets rid of spaces added by mistake
                Password = Password.trim();

                if (Username.isEmpty() || Password.isEmpty()) {//checks if the Username or password is empty or not
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(R.string.LoginErrorMessage);//Error for Wrong input
                        builder.setTitle(R.string.LoginErrorTitle);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog Dialog = builder.create();
                        Dialog.show();//Gives an alert dialog with ok as an option
                    }
                } else {//login
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(Username, Password, new LogInCallback() {
                        @Override
                        public void done(ParseUser User, ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (User != null) {
                                //Success in logging in (null means we have a valid user)
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//these flags are used for the user not to return to sigupActivity when pressed back, it clears the task stack
                                startActivity(intent);
                            } else {//login failed,signup failed
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle(R.string.LoginErrorTitle);
                                builder.setPositiveButton(android.R.string.ok, null);
                                AlertDialog Dialog = builder.create();
                                Dialog.show();

                            }
                        }
                    });


                    //Login user
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
