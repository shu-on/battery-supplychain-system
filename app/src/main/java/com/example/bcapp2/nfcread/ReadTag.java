package com.example.bcapp2.nfcread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import com.example.bcapp2.R;
import com.example.bcapp2.nfcread.Common;
import com.example.bcapp2.nfcread.DumpEditor;
import com.example.bcapp2.nfcread.KeyMapCreator;
import com.example.bcapp2.nfcread.MCReader;

//キーマップを作って読み込む
public class ReadTag extends Activity {

    private final static int KEY_MAP_CREATOR = 1;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private SparseArray<String[]> mRawDump;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.bottom_sheet_layout);
        Intent intent = new Intent(this, KeyMapCreator.class);//キーマップクリエータクラスをインテントで呼ぶ
        intent.putExtra(KeyMapCreator.EXTRA_KEYS_DIR, Common.getFile(Common.KEYS_DIR).getAbsolutePath());//
        intent.putExtra(KeyMapCreator.EXTRA_BUTTON_TEXT, getString(R.string.action_create_key_map_and_read));
        startActivityForResult(intent, KEY_MAP_CREATOR);
    }

    //キーマップ処理結果チェック、成功→readTag呼ぶ
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_MAP_CREATOR) {
            if (resultCode != Activity.RESULT_OK) {
                if (resultCode == 4) {// Error.
                    Toast.makeText(this,"パスなし", Toast.LENGTH_LONG).show();//パスnull
                }
                finish();
                return;
            } else {
                readTag();// Read Tag.
            }
        }
    }

    //最初にタグ読みcreateTagDump呼ぶワーカースレッドを起動
    private void readTag() {
        final MCReader reader = Common.checkForTagAndCreateReader(this);
        if (reader == null) {
            return;
        }
        new Thread(() -> {
            Log.d("ReadTag", "now readtag ");
            mRawDump = reader.readAsMuchAsPossible(Common.getKeyMap());//グローバル変数からキーマップ取得？
            reader.close();
            mHandler.post(() -> createTagDump(mRawDump));
        }).start();
    }

    //ヘッダー(+)、エラー(*)フォーマットのタグダンプ生成
    private void createTagDump(SparseArray<String[]> rawDump) {
        ArrayList<String> tmpDump = new ArrayList<>();
        if (rawDump != null) {
            if (rawDump.size() != 0) {
                for (int i = Common.getKeyMapRangeFrom(); i <= Common.getKeyMapRangeTo(); i++) {
                    String[] val = rawDump.get(i);
                    tmpDump.add("+Sector: " + i);// Mark headers (sectors) with "+".
                    if (val != null ) {
                        Collections.addAll(tmpDump, val);
                    } else {
                        tmpDump.add("*No keys found or dead sector");// 読むことできないセクタのマーク
                    }
                }
                String[] dump = tmpDump.toArray(new String[0]);
                // Show Dump Editor Activity.
                Log.d("readtag", "go to dumpeditor");
                Intent intent = new Intent(this, DumpEditor.class);
                intent.putExtra(DumpEditor.EXTRA_DUMP, dump);
                startActivity(intent);
            } else {
                // キーマップからのキーが有効でないエラー
                Toast.makeText(this, "無効キー", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "タグを離さないでください", Toast.LENGTH_LONG).show();
        }
        finish();
    }

}
