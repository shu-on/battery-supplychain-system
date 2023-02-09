package com.example.bcapp2.nfcread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.bcapp2.R;
import com.example.bcapp2.blockchain.TransactionBC;

//ReadTagによって生成されたタグダンプ表示編集、USASCII機能だけがほしい
public class DumpEditor extends Activity {

    //インテントの応答で新しく分割されダンプ、ヘッダー(+Sector: 1)、エラー(*)
    public final static String EXTRA_DUMP = "com.example.bcapp2.nfcread.DUMP";

    //All blocks containing valid data AND their headers (marked with "+" e.g. "+Sector: 0") as strings.
    //This will be updated with every {@link #checkDumpAndUpdateLines()} check.
    private String[] mLines;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_add);
        // Called from ReadTag (init editor by intent).
        if (getIntent().hasExtra(EXTRA_DUMP)) {
            String[] dump = getIntent().getStringArrayExtra(EXTRA_DUMP);
            Log.d("dumpeditor", "now get intent in dump");
            showAscii(dump);
        }
    }

    private void showAscii(String[] lines) {
        // Get all data blocks (skip all Access Conditions).
        int err = Common.isValidDump(lines, true);
        if (err != 0) {
            Toast.makeText(this, "editor_init_error", Toast.LENGTH_LONG).show();
            Log.d("dumpeditor", "dump1");
            finish();
            return;
        }
        ArrayList<String> tmpDump = new ArrayList<>();
        for (int i = 0; i < lines.length-1; i++) {
            if (i+1 != lines.length && !lines[i+1].startsWith("+")) {
                tmpDump.add(lines[i]);
            }
        }
        String[] dump = tmpDump.toArray(new String[0]);

        if (dump.length != 0) {
            String s = System.getProperty("line.separator");
            CharSequence ascii = "";
            Log.d("dumpeditor", "dump-->"+ dump);
            for (String line : dump) {
                if (line.startsWith("+")) {
                    // Header.
//                    String sectorNumber = line.split(": ")[1];
//                    ascii = TextUtils.concat(ascii, getString(R.string.text_sector) + ": " + sectorNumber, s);
                    ascii = TextUtils.concat(ascii,"");
                } else {
                    // Data.
                    String converted = Common.hex2Ascii(line);
                    if (converted == null) {
                        converted = getString(R.string.text_invalid_data);
                    }
                    ascii = TextUtils.concat(ascii, converted);
                }
            }
            Log.d("dumpeditor", "ascii-->"+ ascii);
            CharSequence AddDate = ascii.subSequence(57, 67);
            CharSequence Owner = ascii.subSequence(67, 78);
            CharSequence BatteryState = ascii.subSequence(105, 122);
            CharSequence BID = ascii.subSequence(122, 129);
            CharSequence CID = ascii.subSequence(130, 138);
            CharSequence SBID = ascii.subSequence(153, 160);
            CharSequence TMP = ascii.subSequence(160, 162);
            CharSequence SOH = ascii.subSequence(162, 164);
            CharSequence NQC = ascii.subSequence(164, 168);
            CharSequence Mileage = ascii.subSequence(168, 175);

            Intent intent = new Intent(this, TransactionBC.class);
            intent.putExtra("EXTRA_ADD", AddDate);
            intent.putExtra("EXTRA_OWN", Owner);
            intent.putExtra("EXTRA_BAT", BatteryState);
            intent.putExtra("EXTRA_BID", BID);
            intent.putExtra("EXTRA_CID", CID);
            intent.putExtra("EXTRA_SBI", SBID);
            intent.putExtra("EXTRA_TMP", TMP);
            intent.putExtra("EXTRA_SOH", SOH);
            intent.putExtra("EXTRA_NQC", NQC);
            intent.putExtra("EXTRA_MIL", Mileage);
            Log.d("Dump", "Intent mileage --> " + Mileage);
            startActivity(intent);

        }

    }

}
