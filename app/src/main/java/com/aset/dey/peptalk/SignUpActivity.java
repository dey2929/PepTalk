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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends ActionBarActivity {
    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);
        mUsername = (EditText)findViewById(R.id.SignUpUsernameField);
        mPassword = (EditText)findViewById(R.id.SignUpPasswordField);
        mEmail= (EditText)findViewById(R.id.SignUpEmailField);
        mSignupButton = (Button)findViewById(R.id.SignupButton);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Username = mUsername.getText().toString();
                String Password = mPassword.getText().toString();
                String Email = mEmail.getText().toString();
                Username = Username.trim();
                Password = Password.trim();
                Email = Email.trim();
                if (Username.isEmpty() || Password.isEmpty() || Email.isEmpty()) {//refer to Login Activity comments for this section
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.SignUpErrorMessage);
                    builder.setTitle(R.string.SignUpErrorTitle);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog Dialog = builder.create();
                    Dialog.show();

                } else {
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();//using parse api
                    newUser.setUsername(Username);
                    newUser.setPassword(Password);
                    newUser.setEmail(Email);
                    newUser.signUpInBackground(new SignUpCallback() {//callback is used for the backend to ping us when it has processed the data
                        @Override
                        public void done(ParseException e) {//done method refers to when the data is processed by the backend
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                //success!
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//these flags are used for the user not to return to sigupActivity when pressed back, it clears the task stack
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle(R.string.SignUpErrorTitle);
                                builder.setPositiveButton(android.R.string.ok, null);
                                AlertDialog Dialog = builder.create();
                                Dialog.show();
                            }
                        }
                    });

                    //Create user
                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;
    }
}
