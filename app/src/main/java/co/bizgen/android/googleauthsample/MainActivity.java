package co.bizgen.android.googleauthsample;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener {

    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private TextView mStatusTextView;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusTextView = (TextView) findViewById(R.id.mStatusTextView);
        avatar = (ImageView) findViewById(R.id.avatar);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this ,
                        this )  // Listener for fail
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signIn (View v) {
        Log.d("PELLODEBUG","Trying to sign in");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Log.d("PELLODEBUG","Trying to sign out");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d("PELLODEBUG", "Logged out");
                        mStatusTextView.setText("Logged out");
                    }
                });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * handler when sign in is successful
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("PELLODEBUG", "handleSignInResult:" + result.isSuccess());
        String info = "";
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            info = "Signed in: "  + acct.getDisplayName();
            info += "UserId: " + acct.getId();
            mStatusTextView.setText(info);
            avatar.setImageURI(acct.getPhotoUrl());
            Log.d("PELLODEBUG", "Sign in SUCCESS: " + info +". Avatar: "+ acct.getPhotoUrl());
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            mStatusTextView.setText("Signed out");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("PELLODEBUG", "Connection failed");
        mStatusTextView.setText("Connections failed");
    }


}
