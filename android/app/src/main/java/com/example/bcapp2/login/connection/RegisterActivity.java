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

import com.example.bcapp2.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    EditText idtxt2,owner2,passtxt2;
    Button registerbt3;
    RadioGroup belong2;
    RadioButton radioButton;
    Connection con;
    Statement state;
    int radioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idtxt2 = findViewById(R.id.idtxt2);
        owner2 = findViewById(R.id.owner2);
        passtxt2 = findViewById(R.id.passtxt2);
        registerbt3 = findViewById(R.id.registerbt3);
        belong2 = findViewById(R.id.belong2);

        registerbt3.setOnClickListener(v -> new backtask().execute(""));
    }

    class backtask {
        String txt = "";
        boolean isSuccess = false;
        private class TaskRun implements Runnable {
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
            executorService.submit(new TaskRun());
        }
        void onPreExecute() {
            Log.d("debug", "onPreExecute()");
        }
        String doInBackground() {
            radioId = belong2.getCheckedRadioButtonId();
            radioButton = findViewById(radioId);
            try{
                con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip, ConnectionClass.port);
                if(con == null){
                    txt = "Check Your Internet Connection";
                } else{
                    String sql2 = "SET IDENTITY_INSERT dbo.userTB ON";
                    String sql = "INSERT INTO dbo.userTB (userid,owner,belong,password) VALUES ('"+idtxt2.getText()+"','"+owner2.getText()+"','"+radioButton.getText()+"','"+passtxt2.getText()+"')";
                    state = con.createStatement();
                    state.executeUpdate(sql2);
                    state.executeUpdate(sql);
                    Log.d("debug", "sql2-->"+ sql2);
                    Log.d("debug", "sql-->"+ sql);
                }
            }catch(Exception e){
                isSuccess = false;
                txt = e.getMessage();
            }
            Log.d("debug", "doInBackground() txt-->"+ txt);
            return txt;
        }
        void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "登録しました！", Toast.LENGTH_LONG).show();
            idtxt2.setText("");
            owner2.setText("");
            passtxt2.setText("");
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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
//            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(url,user,password);
        }catch (ClassNotFoundException e) {
            Log.e("SQL Connection Error1 : ", e.getMessage());
        } catch(SQLException e){
            Log.e("SQL Connection Error : ", e.getMessage());
            Log.e("SQL Connection Error : ", "connectionURL-->"+url);
            Log.e("SQL Connection Error : ", "connection-->"+connection);
        }
        return connection;
    }
}