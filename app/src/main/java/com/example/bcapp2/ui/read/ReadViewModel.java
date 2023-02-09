package com.example.bcapp2.ui.read;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReadViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReadViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("バッテリー情報の読み込みとブロックチェーンへの追加を行います。\n" +
                "1.バッテリー情報を取得済みのNFCカードを用意します。\n" +
                "2.NFCカードをスマートフォンの裏面5cm以内に配置します。\n" +
                "3.下の「Start Read」ボタンを選択します。\n" +
                "4.画面が切り替わったら「Read Tag」ボタンを選択します。\n" +
                "5.画面が切り替わり読み取った情報を確認したら「Add」ボタンを選択します。");
    }
    public LiveData<String> getText() {
        return mText;
    }


}