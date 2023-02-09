package com.example.bcapp2.blockchain;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bcapp2.R;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.hyperledger.fabric.client.*;
import org.hyperledger.fabric.client.identity.*;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionBC extends AppCompatActivity {

    private static final String TAG = "Debug -->";
    private static final String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";
    public RequestQueue queue;
    Gson gson = new Gson();
    private Fragment currentTab;
    Button addButton;
    String userId;
    boolean isEnrolled;
    //RequestQueue queue;
    int tempID = 1112;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //ContractModel contract;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add);

        Intent intent = getIntent();
        String AddDate = intent.getStringExtra("EXTRA_ADD");
        String Owner = intent.getStringExtra("EXTRA_OWN");
        String BatteryState = intent.getStringExtra("EXTRA_BAT");
        String BID = intent.getStringExtra("EXTRA_BID");
        String CID = intent.getStringExtra("EXTRA_CID");
        String SBID = intent.getStringExtra("EXTRA_SBI");
        String TMP = intent.getStringExtra("EXTRA_TMP");
        String SOH = intent.getStringExtra("EXTRA_SOH");
        String NQC = intent.getStringExtra("EXTRA_NQC");
        String Mileage = intent.getStringExtra("EXTRA_MIL");
        Log.d("trasBC", "getIntent mileage --> " + Mileage);
        setIntent(null);

        TextView tx4 = findViewById(R.id.tx4);
        TextView tx3 = findViewById(R.id.tx3);
        TextView tx6 = findViewById(R.id.tx6);
        TextView tx8 = findViewById(R.id.tx8);
        TextView tx10 = findViewById(R.id.tx10);
        TextView tx12 = findViewById(R.id.tx12);
        TextView tx14 = findViewById(R.id.tx14);
        TextView tx16 = findViewById(R.id.tx16);
        TextView tx18 = findViewById(R.id.tx18);
        TextView tx20 = findViewById(R.id.tx20);
        tx4.setText(AddDate);
        tx3.setText(Owner);
        tx6.setText(BatteryState);
        tx8.setText(BID);
        tx10.setText(CID);
        tx12.setText(SBID);
        tx14.setText(TMP);
        tx16.setText(SOH);
        tx18.setText(NQC);
        tx20.setText(Mileage);
        Log.d("trasBC", "tx20 --> " + tx20);
        Toast.makeText(this, "OK?", Toast.LENGTH_LONG).show();

        addButton = findViewById(R.id.bt3);

        //TransactionBC context = this;
        // get the user id
        SharedPreferences sharedPreferences = this.getSharedPreferences("shared_preferences_conf", Context.MODE_PRIVATE);

        // Check if user is already enrolled
        if (sharedPreferences.contains("BlockchainUserId")) {
            this.userId = sharedPreferences.getString("BlockchainUserId","Something went wrong...");
            this.isEnrolled = !this.userId.equals("Something went wrong...");
            Log.d("debug", "User already registered.");
        } else {
            this.isEnrolled = false;
            // register the user
//            registerUser();
        }
        // request queue
        queue = Volley.newRequestQueue(this);
        // check if location is permitted
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("debug", "access fine location not yet granted");
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//        }

        addButton.setOnClickListener(view -> {
            try {
                JSONObject params = new JSONObject("{\"type\":\"invoke\",\"queue\":\"user_queue\",\"params\":{\"userId\":\""+tempID+"\",\"fcn\":\"addStatus\",\"args\":["+tx4.getText()+","+tx3.getText()+","+tx6.getText()+","+tx8.getText()+","+tx10.getText()+","+tx12.getText()+","+tx14.getText()+","+tx16.getText()+","+tx18.getText()+","+tx20.getText()+"]}}");
                Log.d(TAG, params.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params, response -> {
                    InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(), InitialResultFromRabbit.class);
                    if (initialResultFromRabbit.status.equals("success")) {
                        Log.d(TAG, response.toString());
                        getTransactionResult(initialResultFromRabbit.resultId,0);
                    } else {
                        Log.d(TAG, "Response is: " + response);
                    }
                }, error -> Log.d(TAG, "That didn't work!"));
                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(final String[] args) throws CommitException, GatewayException, InterruptedException {
        Reader certReader = Files.newBufferedReader(certificatePath);
        X509Certificate certificate = Identities.readX509Certificate(certReader);
        Identity identity = new X509Identity("mspId", certificate);

        Reader keyReader = Files.newBufferedReader(privateKeyPath);
        PrivateKey privateKey = Identities.readPrivateKey(keyReader);
        Signer signer = Signers.newPrivateKeySigner(privateKey);

        ManagedChannel grpcChannel = ManagedChannelBuilder.forAddress("gateway.example.org", 1337)
                .usePlaintext()
                .build();

        Gateway.Builder builder = Gateway.newInstance()
                .identity(identity)
                .signer(signer)
                .connection(grpcChannel);

        try (Gateway gateway = builder.connect()) {
            Network network = gateway.getNetwork("channelName");
            Contract contract = network.getContract("chaincodeName");

            byte[] putResult = contract.submitTransaction("put", "time", LocalDateTime.now().toString());
            System.out.println(new String(putResult, StandardCharsets.UTF_8));

            byte[] getResult = contract.evaluateTransaction("get", "time");
            System.out.println(new String(getResult, StandardCharsets.UTF_8));
        } finally {
            grpcChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public void getTransactionResult(final String resultId, final int attemptNumber) {
        if (attemptNumber < 60) {
            final TransactionBC activity = this;
            // GET THE TRANSACTION RESULT
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/api/results/" + resultId, null, response -> {
                BackendResult backendResult = gson.fromJson(response.toString(), BackendResult.class);
                // Check status of queued request
                if (backendResult.status.equals("pending")) {
                    // if it is still pending, check again
                    new Handler().postDelayed(() -> getTransactionResult(resultId,attemptNumber + 1),500);
                } else if (backendResult.status.equals("done")) {
//                    // when blockchain is done processing the request, get the contract model and start activity
//                    ResultOfMakePurchase resultOfMakePurchase = gson.fromJson(backendResult.result, ResultOfMakePurchase.class);
//                    ContractModel contractModel = gson.fromJson(resultOfMakePurchase.result.results.payload, ContractModel.class);
//                    // start activity of contract details
//                    Intent intent = new Intent(activity, ContractDetails.class);
//                    intent.putExtra("CONTRACT_JSON", new Gson().toJson(contractModel, ContractModel.class));
//                    Pair<View, String> pair1 = Pair.create(findViewById(R.id.productImageInQuantity),"productImage");
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair1);
//                    activity.startActivity(intent,options.toBundle());
                    Log.d(TAG, "Transaction result is done!: " + response);
                } else {
                    Log.d(TAG, "Response is: " + response);
                }
            }, error -> Log.d(TAG, "That didn't work!"));
            queue.add(jsonObjectRequest);
        }
    }

//    public void registerUser() {
//        try {
//            JSONObject params = new JSONObject("{\"type\":\"enroll\",\"queue\":\"user_queue\",\"params\":{}}");
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BACKEND_URL + "/api/execute", params, response -> {
//                InitialResultFromRabbit initialResultFromRabbit = gson.fromJson(response.toString(),InitialResultFromRabbit.class);
//                if (initialResultFromRabbit.status.equals("success")) {
//                    getResultFromResultId("enrollment", initialResultFromRabbit.resultId, 0);
//                } else {
//                    Log.d("debug", "Response is: " + response);
//                }
//            }, error -> Log.d("debug", "That didn't work!"));
//            this.queue.add(jsonObjectRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    public void getResultFromResultId(final String initialRequestType, final String resultId, final int attemptNumber) {
//        // Limit to 60 attempts
//        if (attemptNumber < 60) {
//            if (initialRequestType.equals("enrollment")) {
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BACKEND_URL + "/api/results/" + resultId, null, response -> {
//                    BackendResult backendResult = gson.fromJson(response.toString(), BackendResult.class);
//                    if (backendResult.status.equals("pending")) {
//                        new Handler().postDelayed((Runnable) () -> getResultFromResultId(initialRequestType,resultId,attemptNumber + 1),3000);
//                    } else if (backendResult.status.equals("done")) {
//                        Log.d("debug", "saveuser!");
//                        //saveUser(backendResult.result);
//                    } else {
//                        Log.d("debug", "Response is: " + response);
//                    }
//                }, error -> Log.d("debug", "That didn't work!"));
//                this.queue.add(jsonObjectRequest);
//            }
//        } else {
//            Log.d("debug", "No results after 60 times...");
//        }
//    }
}

//class ContractModel {
//
//    @SerializedName("adddate")
//    String AddDate;
//    @SerializedName("owner")
//    String Owner;
//    @SerializedName("bstate")
//    String BatteryState;
//    @SerializedName("bid")
//    String BID;
//    @SerializedName("cid")
//    String CID;
//    @SerializedName("sbid")
//    String SBID;
//    @SerializedName("tmp")
//    String TMP;
//    @SerializedName("soh")
//    String SOH;
//    @SerializedName("nqc")
//    String NQC;
//    @SerializedName("mileage")
//    String Mileage;
//
//    public ContractModel(String AddDate, String Owner, String BatteryState, String BID, String CID, String SBID, String TMP, String SOH, String NQC, String Mileage) {
//        this.AddDate = AddDate;
//        this.Owner = Owner;
//        this.BatteryState = BatteryState;
//        this.BID = BID;
//        this.CID = CID;
//        this.SBID = SBID;
//        this.TMP = TMP;
//        this.SOH = SOH;
//        this.NQC = NQC;
//        this.Mileage = Mileage;
//    }
//
//    public String getAddDate() {
//        return AddDate;
//    }
//    public String getOwner() {
//        return Owner;
//    }
//    public String getBatteryState() {
//        return BatteryState;
//    }
//    public String getBID() {
//        return BID;
//    }
//    public String getCID() {
//        return CID;
//    }
//    public String getSBID() {
//        return SBID;
//    }
//    public String getTMP() {
//        return TMP;
//    }
//    public String getSOH() {
//        return SOH;
//    }
//    public String getNQC() {
//        return NQC;
//    }
//    public String getMileage() {
//        return Mileage;
//    }
//}

class InitialResultFromRabbit {
    String status;
    String resultId;
}

class BackendResult {
    String status;
    String result;
}
class ResultOfEnroll {
    String message;
    EnrollFinalResult result;
}

class EnrollFinalResult {
    String user;
    String txId;
    String error;
}

class ResultOfBackendResult {
    String message;
    String result;
    String error;
}

class ResultOfTransactionResult {
    int status;
    String message;
    String payload;
}

class TransactionResult {
    String txId;
    ResultOfTransactionResult results;
}

class ResultOfMakePurchase {
    String message;
    TransactionResult result;
    String error;
}
