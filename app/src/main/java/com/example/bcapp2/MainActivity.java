package com.example.bcapp2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.bcapp2.nfcread.Common;
import com.example.bcapp2.nfcread.ReadTag;
import com.example.bcapp2.ui.read.ReadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bcapp2.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements ReadFragment.MyListener{

    private ActivityMainBinding binding;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mHasNoNfc = false;
//    private Button mReadTag;

    /**
     * Nodes (stats) MCT passes through during its startup.
     */
    private enum StartUpNode {
        HasNfc, HasMifareClassicSupport, HasNfcEnabled
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Log.e(LOG_TAG, "8");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_read, R.id.navigation_maypage).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Restore state.
        if (savedInstanceState != null) {
            mHasNoNfc = savedInstanceState.getBoolean("has_no_nfc");
            Log.e(LOG_TAG, "8");
        }
        // Bind main layout buttons.
//        mReadTag = findViewById(R.id.rbtn);
        Log.e(LOG_TAG, "9");
        initFolders();
        Log.e(LOG_TAG, "10");
        copyStdKeysFiles();
        Log.e(LOG_TAG, "7");
    }

    // interface内のメソッドを実装します。
    @Override
    public void onClickButton( ) {
        Intent intent = new Intent(this, ReadTag.class);
        startActivity(intent);
        Toast.makeText(this, "MainFragmentからクリックされました!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(LOG_TAG, "17");
        outState.putBoolean("has_no_nfc", mHasNoNfc);
        Log.e(LOG_TAG, "6");
    }


    private void runStartUpNode(StartUpNode startUpNode) {
        Log.e(LOG_TAG, "5");
        switch (startUpNode) {
            case HasNfc:
                Common.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
                if (Common.getNfcAdapter() == null) {
                    mHasNoNfc = true;
                    runStartUpNode(StartUpNode.HasNfcEnabled);
                } else {
                    runStartUpNode(StartUpNode.HasMifareClassicSupport);
                }
                break;
            case HasMifareClassicSupport:
                if (!Common.hasMifareClassicSupport() && !Common.useAsEditorOnly()) {
                    AlertDialog ad = createHasNoMifareClassicSupportDialog();
                    ad.show();
                } else {
                    runStartUpNode(StartUpNode.HasNfcEnabled);
                }
                break;
            case HasNfcEnabled:
                Common.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
                if (!Common.getNfcAdapter().isEnabled()) {
                    if (!Common.useAsEditorOnly()) {
                        createNfcEnableDialog().show();
                    }
                } else {
                    // Use MCT with internal NFC controller.
                    Log.e(LOG_TAG, "18");
                    ReadFragment.useAsEditorOnly(false);
                    Common.enableNfcForegroundDispatch(this);
                    //runStartUpNode(StartUpNode.HasNfcEnabled);
                }
                break;
        }
    }

    /**
     * Set whether to use the app in editor only mode or not.
     * @param useAsEditorOnly True if the app should be used in editor
     * only mode.
     */
//    private void useAsEditorOnly(boolean useAsEditorOnly) {
//        Log.e(LOG_TAG, "11");
//        Common.setUseAsEditorOnly(useAsEditorOnly);
//        Log.e(LOG_TAG, "12  " + useAsEditorOnly);
//        if(useAsEditorOnly){
//            mReadTag.setEnabled(false);
//            Log.e(LOG_TAG, "13");
//        }
//    }

    /**
     * Create the dialog which is displayed if the device does not have
     * MIFARE classic support. After showing the dialog,
     * {@link #runStartUpNode(StartUpNode)} with {}
     * will be called or the app will be exited.
     * @return The created alert dialog.
     * @see #runStartUpNode(StartUpNode)
     */
    private AlertDialog createHasNoMifareClassicSupportDialog() {
        CharSequence styledText = HtmlCompat.fromHtml(
                getString(R.string.dialog_no_mfc_support_device),
                HtmlCompat.FROM_HTML_MODE_LEGACY);
        return new AlertDialog.Builder(this)
                .setTitle("Not Support MFC")
                .setMessage(styledText)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton("Exit",
                        (dialog, id) -> {
                            // Exit the App.
                            finish();
                        })
                .setOnCancelListener(
                        dialog -> finish())
                .create();
    }

    /**
     * Create a dialog that send user to NFC settings if NFC is off.
     * Alternatively the user can chose to use the App in editor only
     * mode or exit the App.
     * @return The created alert dialog.
     * @see #runStartUpNode(StartUpNode)
     */
    private AlertDialog createNfcEnableDialog() {
        return new AlertDialog.Builder(this)
                .setTitle("No NFC Enable!")
                .setMessage("please Setting!")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("GoSetting", (dialog, which) -> {
                            // Goto NFC Settings.
                            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                        })
                .setNeutralButton("UseOnly", (dialog, which) -> {
                            Log.e(LOG_TAG, "16");
                            // Only use Editor.
                            ReadFragment.useAsEditorOnly(true);
                        })
                .setNegativeButton("Exit", (dialog, id) -> {
                            // Exit the App.
                            finish();
                        })
                .setOnCancelListener(dialog -> finish())
                .create();
    }

    /**
     * Create the directories needed by MCT and clean out the tmp folder.
     */
    @SuppressLint("ApplySharedPref")
    private void initFolders() {
        // Create keys directory.
        Log.e(LOG_TAG, "14");
        File path = Common.getFile(Common.KEYS_DIR);
        Log.e(LOG_TAG, "15");
        if (!path.exists() && !path.mkdirs()) {
            // Could not create directory.
            Log.e(LOG_TAG, "Error while creating '" + Common.HOME_DIR + "/" + Common.KEYS_DIR + "' directory.");
            return;
        }
        // Create dumps directory.
        path = Common.getFile(Common.DUMPS_DIR);
        if (!path.exists() && !path.mkdirs()) {
            // Could not create directory.
            Log.e(LOG_TAG, "Error while creating '" + Common.HOME_DIR + "/" + Common.DUMPS_DIR + "' directory.");
            return;
        }
        // Create tmp directory.
        path = Common.getFile(Common.TMP_DIR);
        if (!path.exists() && !path.mkdirs()) {
            // Could not create directory.
            Log.e(LOG_TAG, "Error while creating '" + Common.HOME_DIR + Common.TMP_DIR + "' directory.");
            return;
        }
        // Try to clean up tmp directory.
        File[] tmpFiles = path.listFiles();
        Log.e(LOG_TAG, "4");
        if (tmpFiles != null) {
            for (File file : tmpFiles) {
                Log.e(LOG_TAG, "3");
                file.delete();
            }
        }
    }

    /**
     * Resume by triggering MCT's startup system
     * ({@link #runStartUpNode(StartUpNode)}).
     * @see #runStartUpNode(StartUpNode)
     */
    @Override
    public void onResume() {
        super.onResume();
        ReadFragment.useAsEditorOnly(Common.useAsEditorOnly());
        // The start up nodes will also enable the NFC foreground dispatch if all
        // conditions are met (has NFC & NFC enabled).
        runStartUpNode(StartUpNode.HasNfcEnabled);
    }

    /**
     * Disable NFC foreground dispatch system.
     * @see Common#disableNfcForegroundDispatch(Activity)
     */
    @Override
    public void onPause() {
        super.onPause();
        Common.disableNfcForegroundDispatch(this);
    }


    /**
     * Copy the standard key files ({@link Common#STD_KEYS} and
     * {@link Common#STD_KEYS_EXTENDED}) form assets to {@link Common#KEYS_DIR}.
     * @see Common#KEYS_DIR
     * @see Common#HOME_DIR
     * @see Common#copyFile(InputStream, OutputStream)
     */
    private void copyStdKeysFiles() {
        File std = Common.getFile(Common.KEYS_DIR + "/" + Common.STD_KEYS);
        Log.e(LOG_TAG, "start copy");
//        File extended = Common.getFile(Common.KEYS_DIR + "/" + Common.STD_KEYS_EXTENDED);
        AssetManager assetManager = getAssets();
        Log.e(LOG_TAG, "assets -->" + assetManager);
        // Copy std.keys.
        try {
            InputStream in = assetManager.open(Common.KEYS_DIR + "/" + Common.STD_KEYS);
            OutputStream out = new FileOutputStream(std);
            Common.copyFile(in, out);
            Log.e(LOG_TAG, "in -->" + in);
            in.close();
            Log.e(LOG_TAG, "copy done -->" + out);
            out.flush();
            out.close();
        } catch(IOException e) {
            Log.e(LOG_TAG, "Error while copying 'std.keys' from assets " + "to internal storage.");
        }
        File std2 = Common.getFile(Common.KEYS_DIR + "/" + Common.STD_KEYS);
        Log.d(LOG_TAG, "std2--> " + std2 + std2.exists());

        // Copy extended-std.keys.
//        try {
//            InputStream in = assetManager.open(Common.KEYS_DIR + "/" + Common.STD_KEYS_EXTENDED);
//            OutputStream out = new FileOutputStream(extended);
//            Common.copyFile(in, out);
//            in.close();
//            out.flush();
//            out.close();
//        } catch(IOException e) {
//            Log.e(LOG_TAG, "Error while copying 'extended-std.keys' " + "from assets to internal storage.");
//        }

    }

}