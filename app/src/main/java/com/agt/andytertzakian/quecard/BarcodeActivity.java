package com.agt.andytertzakian.quecard;

import java.util.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import net.glxn.qrgen.android.QRCode;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarcodeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    //class variables
    private ImageButton SignOutButton;
    private ImageView DisplayBCode;
    private TextView BarcodeContent;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "BarcodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        SignOutButton = (ImageButton) findViewById(R.id.Settings);
        SignOutButton.setOnClickListener(this);
        DisplayBCode = (ImageView) findViewById(R.id.BCode);
        BarcodeContent = (TextView) findViewById(R.id.BarcodeContent);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mFirebaseUser = user;
                    updloadUser();
                    initUI();

                    //FOR GENERATING RANDOM USERS
                    try {
                        RandomDataGenerator.generateRandomUsers();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "onAuthStateChanged_Barcode:signed_in:" + mFirebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged_Barcode:signed_out");
                }
            }
        };
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Settings:
                signOut();
                break;
        }
    }

    private void signOut() {

        mAuth.signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast toast = Toast.makeText(BarcodeActivity.this,
                                "Signed Out Successfully",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        Intent i = new Intent(BarcodeActivity.this, SignInActivity.class);
                        BarcodeActivity.this.startActivity(i);
                        BarcodeActivity.this.finish();
                    }
                });
    }

    protected void updloadUser(){
        User user = new User();
        user.upload();
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void initUI(){
        //Intent intent = getIntent();
        //String userName = intent.getStringExtra(SignInActivity.USERNAME);

        //Set the welcome text for the user
        TextView labelWelcome = (TextView) findViewById(R.id.LabelWelcome);
        labelWelcome.setText(mFirebaseUser.getDisplayName().toUpperCase());

        //Create the
        Bitmap userQRCode = QRCode.from(mFirebaseUser.getUid()).bitmap();
        ImageView displayQRCode = (ImageView) findViewById(R.id.QRCode);
        displayQRCode.setImageBitmap(userQRCode);

        Bitmap bitmap = null;
        //Create the barcode to be viewedx
        try {
            bitmap = encodeAsBitmap(mFirebaseUser.getUid(),
                    BarcodeFormat.CODE_128,
                    DisplayBCode.getWidth(),
                    DisplayBCode.getHeight());
            BarcodeContent.setText(mFirebaseUser.getUid());
            DisplayBCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //////////////////////////////////////////////////////////////////////
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {

            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }


}