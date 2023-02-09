package com.example.bcapp2.login.connection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bcapp2.MainActivity;
import com.example.bcapp2.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    EditText idtxt,owner,passtxt;
    Button loginbt,register;
    RadioGroup belong;
    RadioButton radioButton2;
    Connection con2;
    int radioId2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idtxt = findViewById(R.id.idtxt);
        owner = findViewById(R.id.owner);
        passtxt = findViewById(R.id.passtxt);
        loginbt = findViewById(R.id.loginbt);
        register = findViewById(R.id.register);
        belong = findViewById(R.id.belong);
//        radioId2 = belong.getCheckedRadioButtonId();
//        radioButton2 = findViewById(radioId2);

        loginbt.setOnClickListener(view -> new checkLogin().execute(""));
        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    class checkLogin {
        String txt2 = "";
        boolean isSuccess = false;
        private class TaskRun2 implements Runnable {
            private String result;
            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                onPreExecute();
                result = doInBackground();
                handler.post(() -> onPostExecute(result));
            }
        }
        public void execute(String s) {
            ExecutorService executorService  = Executors.newSingleThreadExecutor();
            executorService.submit(new LoginActivity.checkLogin.TaskRun2());
        }
        void onPreExecute() {
            Log.d("debug", "onPreExecute()2");
        }
        String doInBackground() {
            radioId2 = belong.getCheckedRadioButtonId();
            radioButton2 = findViewById(radioId2);
            con2 = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip, ConnectionClass.port);
            if(con2 == null){
                runOnUiThread(() -> Toast.makeText(LoginActivity.this,"Check Internet Connection",Toast.LENGTH_LONG).show());
                txt2 = "On Internet Connection";
            } else {
                try {
                    String sql = "SELECT * FROM dbo.userTB WHERE userid = '"+idtxt.getText()+"' AND owner = '"+owner.getText()+"' AND belong = '"+radioButton2.getText()+"' AND password = '"+passtxt.getText()+"' ";
                    Statement state2 = con2.createStatement();
                    ResultSet rs = state2.executeQuery(sql);
                    if (rs.next()) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "ログイン成功！", Toast.LENGTH_LONG).show());
                        txt2 = "Success";
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "もう一度、正しく入力してください。", Toast.LENGTH_LONG).show());
                    }
                } catch (Exception e) {
                    isSuccess = false;
                    Log.e("SQL Error : ", e.getMessage());
                }
            }
            return txt2;
        }
        void onPostExecute(String result) {
            Log.e(" onPostExecute() ", "result-->"+result);
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String ip, String port){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
//        String connectionURL = null;
        String url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" +  database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(url,user,password);
        }catch (Exception e){
            Log.e("SQL Connection Error : ", e.getMessage());
        }
        return connection;
    }
}