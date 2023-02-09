package com.example.bcapp2.nfcread;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.bcapp2.nfcread.Common;

/**
 * An Activity implementing the NFC foreground dispatch system overwriting
 * onResume() and onPause(). New Intents will be treated as new Tags.
 *  Common#enableNfcForegroundDispatch(Activity)
 *  Common#disableNfcForegroundDispatch(Activity)
 *  Common#treatAsNewTag(Intent, android.content.Context)
 * @author Gerhard Klostermeier
 *
 */
public abstract class BasicActivity extends Activity {

    /**
     * Enable NFC foreground dispatch system.
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        Common.setPendingComponentName(this.getComponentName());
        Common.enableNfcForegroundDispatch(this);
        Toast.makeText(this, "利用可NFCディスパッチシステム起動", Toast.LENGTH_LONG).show();
    }

    /**
     * Disable NFC foreground dispatch system.
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        Common.disableNfcForegroundDispatch(this);

        Toast.makeText(this, "利用不可NFCディスパッチシステム起動", Toast.LENGTH_LONG).show();
    }

    /**
     * Handle new Intent as a new tag Intent and if the tag/device does not
     * support MIFARE Classic, then run {@link }.
     * @see Common#treatAsNewTag(Intent, android.content.Context)
     */
    @Override
    public void onNewIntent(Intent intent) {
        int typeCheck = Common.treatAsNewTag(intent, this);
        if (typeCheck == -1 || typeCheck == -2) {
            // Device or tag does not support MIFARE Classic.
            // Run the only thing that is possible: The tag info tool.
            //Intent i = new Intent(this, TagInfoTool.class);
            //startActivity(i);
            Toast.makeText(this, "TagtoolInfoインテント起動", Toast.LENGTH_LONG).show();
        }
    }
}