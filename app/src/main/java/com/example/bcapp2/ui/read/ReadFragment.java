package com.example.bcapp2.ui.read;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bcapp2.nfcread.Common;
import com.example.bcapp2.R;
import com.example.bcapp2.databinding.FragmentReadBinding;

public class ReadFragment extends Fragment {

    private FragmentReadBinding binding;
    // 変数を用意
    private MyListener mListener;
    private static Button mReadTag;
    private static Boolean nobt = false;

    // FragmentがActivityに追加されたら呼ばれるメソッド
    @Override
    public void onAttach(@NonNull Context context) {
        // contextクラスがMyListenerを実装しているかチェック
        super.onAttach(context);
        if (context instanceof MyListener) {
            // リスナーをセット
            mListener = (MyListener) context;
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReadViewModel readViewModel = new ViewModelProvider(this).get(ReadViewModel.class);
        binding = FragmentReadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textDashboard;
        readViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mReadTag = root.findViewById(R.id.rbtn);
        Log.e("ReadFragment", "w2  " + nobt);
        if(nobt){
            mReadTag.setEnabled(false);
            Log.e("ReadFragment", "w4");
        }
        return root;
    }

    public static void useAsEditorOnly(boolean useAsEditorOnly) {
        Common.setUseAsEditorOnly(useAsEditorOnly);
        Log.e("ReadFragment", "12  " + useAsEditorOnly);
        if(useAsEditorOnly){
            nobt = true;
            Log.e("ReadFragment", "w1 ");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("ReadFragment", "w3");
        view.findViewById(R.id.rbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickButton();
                }
            }
        });
    }
     //FragmentがActivityから離れたら呼ばれるメソッド
    @Override
    public void onDetach() {
        super.onDetach();
        // 画面からFragmentが離れたあとに処理が呼ばれることを避けるためにNullで初期化しておく
        mListener = null;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface MyListener {
        void onClickButton();
    }

}