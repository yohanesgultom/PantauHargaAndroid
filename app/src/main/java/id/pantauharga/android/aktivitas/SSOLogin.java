package id.pantauharga.android.aktivitas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import id.pantauharga.android.R;
import id.pantauharga.android.databases.RMLogin;
import id.pantauharga.android.internets.Apis;
import id.pantauharga.android.internets.JacksonRequest;
import id.pantauharga.android.internets.Volleys;
import id.pantauharga.android.modelgson.User;
import id.pantauharga.android.modelgson.VerifyUser;
import id.pantauharga.android.parsers.Parseran;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Widodo Pangestu on 4/4/16.
 */
public class SSOLogin extends BaseActivityLocation implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int PROFILE_PIC_SIZE = 400;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private ActionBar aksibar;
    private User user;
    private VerifyUser verifyUser;
    private Parseran mParseran;
    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    private boolean isRunning = false;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.sign_in_button)
    SignInButton signInButton;

    @Bind(R.id.sign_out_button)
    Button signOutButton;

    @Bind(R.id.disconnect_button)
    Button disconnectButton;

    @Bind(R.id.user_info_layout)
    LinearLayout userInfoLayout;

    @Bind(R.id.sign_in_layout)
    LinearLayout signInLayout;

    @Bind(R.id.form_user_layout)
    LinearLayout formUserLayout;

    @Bind(R.id.layout_username)
    TextInputLayout layoutUsername;
    @Bind(R.id.layout_nama)
    TextInputLayout layoutNama;
    @Bind(R.id.layout_email)
    TextInputLayout layoutEmail;
    @Bind(R.id.layout_nomor_ktp)
    TextInputLayout layoutNomorKtp;
    @Bind(R.id.layout_nomor_hp)
    TextInputLayout layoutNomorHp;
    @Bind(R.id.layout_alamat)
    TextInputLayout layoutAlamat;
    @Bind(R.id.layout_kode_pos)
    TextInputLayout layoutKodePos;

    @Bind(R.id.text_username)
    EditText textUsername;
    @Bind(R.id.text_nama)
    EditText textNama;
    @Bind(R.id.text_email)
    EditText textEmail;
    @Bind(R.id.text_nomor_hp)
    EditText textNomorHp;
    @Bind(R.id.text_nomor_ktp)
    EditText textNomorKtp;
    @Bind(R.id.text_alamat)
    EditText textAlamat;
    @Bind(R.id.text_kode_pos)
    EditText textKodePos;


    @Bind(R.id.image_profile)
    ImageView imageProfile;
    @Bind(R.id.text_nama_lengkap)
    TextView textNamaLengkap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sso_login);
        ButterKnife.bind(SSOLogin.this);

        if (toolbar != null) {
            SSOLogin.this.setSupportActionBar(toolbar);
        }

        aksibar = SSOLogin.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.login_register_datapengguna);

        mRealm = Realm.getInstance(SSOLogin.this);
        mParseran = new Parseran(SSOLogin.this);
        // Button listeners
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        cekInternet();
        isRunning = true;
        // [END customize_button]
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(SSOLogin.this)) {
            EventBus.getDefault().unregister(SSOLogin.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        mRealm.close();
        mProgressDialog.dismiss();
        Volleys.getInstance(SSOLogin.this).cancelPendingRequestsNoTag();
        Volleys.getInstance(SSOLogin.this).clearVolleyCache();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog(getString(R.string.loading));
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
// [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.d(TAG, "handleSignInResult success serverCode: " + acct.getServerAuthCode());
            if(verifyUser == null){
                verifyUser = new VerifyUser();
            }
            verifyUser.setServerAuthCode(acct.getServerAuthCode());
            verifyUser.setIdToken(acct.getIdToken());
            verifyUser.setEmail(acct.getEmail());
            verifyUser.setAndroidId("pantauharga");
            jsonVerifyUser();
            updateUI(true);
        } else {
            Log.d(TAG, "handleSignInResult fail status:" + result.getStatus());

            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SSOLogin.this.getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                SSOLogin.this.finish();
                return true;
            case R.id.action_save_profile:
                saveProfile();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
// [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
// [END signIn]


    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        clearLocalData();
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
// [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        clearLocalData();
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
// [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showSnackbar(int message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).setAction("OK", null)
                .setActionTextColor(SSOLogin.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            aksibar.setTitle(R.string.login_register_datapengguna);
            signInLayout.setVisibility(View.GONE);
            userInfoLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.action_save_profile).setVisibility(View.VISIBLE);
        } else {
            aksibar.setTitle(R.string.login_login);
            signInLayout.setVisibility(View.VISIBLE);
            userInfoLayout.setVisibility(View.GONE);
            findViewById(R.id.action_save_profile).setVisibility(View.GONE);
        }
    }

    private User saveProfile() {
        clearErrorField();

        String username = textUsername.getText().toString();
        String nama = textNama.getText().toString();
        String email = textEmail.getText().toString();
        String nomorKtp = textNomorKtp.getText().toString();
        String nomorHp = textNomorHp.getText().toString();
        String alamat = textAlamat.getText().toString();
        String kodePos = textKodePos.getText().toString();

        boolean isValid = true;

        if (username.length() < 5) {
            setErrorField(textUsername, layoutUsername, R.string.err_username);
            isValid = false;
        }
        if (nama.length() < 5) {
            setErrorField(textNama, layoutNama, R.string.err_nama);
            isValid = false;
        }
        if (!mParseran.validasiEmail(email)) {
            setErrorField(textEmail, layoutEmail, R.string.err_email);
            isValid = false;
        }
        if (nomorKtp.length() < 10) {
            setErrorField(textNomorKtp, layoutNomorKtp, R.string.err_nomor_ktp);
            isValid = false;
        }
        if (nomorHp.length() < 10) {
            setErrorField(textNomorHp, layoutNomorHp, R.string.err_nomor_hp);
            isValid = false;
        }
        if (alamat.length() < 5) {
            setErrorField(textAlamat, layoutAlamat, R.string.err_alamat);
            isValid = false;
        }
        if (kodePos.length() < 5) {
            setErrorField(textKodePos, layoutKodePos, R.string.err_kode_pos);
            isValid = false;
        }

        if (user == null)
            user = new User();
        {
        }
        user.setUsername(username);
        user.setNama(nama);
        user.setEmail(email);
        user.setNohp(nomorHp);
        user.setKtp(nomorKtp);
        user.setAlamat(alamat);
        user.setKodepos(kodePos);
        if (isValid) {
            if (isInternet()) {
                jsonUpdateUser();
            } else {
                showSnackbar(R.string.toastnointernet);
            }

        }
        return user;
    }

    private void setLocalData(User user, String token) {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {
            RMLogin rmLogin = mRealmResultsLogin.first();
            mRealm.beginTransaction();
            rmLogin.setUsername(user.getUsername());
            rmLogin.setNama(user.getNama());
            rmLogin.setEmail(user.getEmail());
            rmLogin.setKtp(user.getKtp());
            rmLogin.setNohp(user.getNohp());
            rmLogin.setAlamat(user.getAlamat());
            rmLogin.setKodepos(user.getKodepos());
            rmLogin.setPhotoUrl(user.getPhotoUrl());
            if (token != null) {
                rmLogin.setToken(token);
            }
            mRealm.commitTransaction();
            updateInfo(rmLogin);
        }
    }

    private void clearLocalData() {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            RMLogin rmLogin = mRealmResultsLogin.first();

            mRealm.beginTransaction();
            rmLogin.setUsername("");
            rmLogin.setNama("");
            rmLogin.setEmail("");
            rmLogin.setKtp("");
            rmLogin.setNohp("");
            rmLogin.setAlamat("");
            rmLogin.setKodepos("");
            rmLogin.setPhotoUrl("");
            rmLogin.setId("");
            rmLogin.setToken("");
            mRealm.commitTransaction();
        }

    }


    private void updateInfo(RMLogin rmLogin) {
        if (rmLogin != null) {
            textUsername.setText(rmLogin.getUsername());
            textNama.setText(rmLogin.getNama());
            textEmail.setText(rmLogin.getEmail());
            textNomorHp.setText(rmLogin.getNohp());
            textNomorKtp.setText(rmLogin.getKtp());
            textKodePos.setText(rmLogin.getKodepos());
            textAlamat.setText(rmLogin.getAlamat());
            if(rmLogin.getPhotoUrl() != null && !rmLogin.getPhotoUrl().isEmpty())
            new LoadProfileImage(imageProfile).execute(rmLogin.getPhotoUrl() + "?sz=" + PROFILE_PIC_SIZE);
            textNamaLengkap.setText(rmLogin.getNama());
        }
    }

    //peringatkan data dan edit text
    private void setErrorField(EditText edits, TextInputLayout textInputLayout, int resId) {
        edits.requestFocus();
        textInputLayout.setError(SSOLogin.this.getResources().getString(resId));
    }

    private void clearErrorField() {
        for (int i = 0; i < formUserLayout.getChildCount(); i++) {
            if (formUserLayout.getChildAt(i) instanceof TextInputLayout) {
                TextInputLayout obj = (TextInputLayout) formUserLayout.getChildAt(i);
                obj.setError("");
            }
        }
    }

    //SUSUN JSON DATA KIRIM HARGA
    private void jsonUpdateUser() {

        //progress dialog
        showProgressDialog("Change User Profile ...");

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                return mParseran.konversiPojoUpdateUser(user);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String jsonResults = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + jsonResults);
                        sendDataServer(jsonResults);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    //KIRIM DATA KE SERVER
    private void sendDataServer(String jsonbody) {

        String urls = Apis.getLinkUpdateUser();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<User> jacksonRequest = Apis.postRequestUpdateUser(
                urls,
                headers,
                parameters,
                jsonbody,
                new Response.Listener<User>() {
                    @Override
                    public void onResponse(User response) {
                        if (isRunning) {
                            resultRespon(response);
                            showSnackbar(R.string.save_success);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (isRunning) {
                            resultRespon(null);
                            showSnackbar(R.string.save_failed);
                        }
                    }
                }
        );

        Volleys.getInstance(SSOLogin.this).addToRequestQueue(jacksonRequest);
    }

    //SUSUN JSON DATA KIRIM HARGA
    private void jsonVerifyUser() {

        //progress dialog
        showProgressDialog("Verify Google User ...");

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                return mParseran.konversiPojoVerifyUser(verifyUser);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String jsonResults = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + jsonResults);
                        sendVerifyUserServer(jsonResults);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    //KIRIM DATA KE SERVER
    private void sendVerifyUserServer(String jsonbody) {

        String urls = Apis.getLinkVerifyUser();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<User> jacksonRequest = Apis.postRequestUpdateUser(
                urls,
                headers,
                parameters,
                jsonbody,
                new Response.Listener<User>() {
                    @Override
                    public void onResponse(User response) {
                        if (isRunning) {
                            resultRespon(response);
                            showSnackbar(R.string.login_success);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (isRunning) {
                            resultRespon(null);
                            showSnackbar(R.string.save_failed);
                        }
                    }
                }
        );

        Volleys.getInstance(SSOLogin.this).addToRequestQueue(jacksonRequest);
    }

    //CEK HASIL RESPON DARI VOLLEY
    private void resultRespon(User user) {
        hideProgressDialog();
        if (user != null) {
            setLocalData(user, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
