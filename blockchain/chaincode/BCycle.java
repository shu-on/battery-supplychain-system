/*
 * SPDX-License-Identifier: Apache-2.0
 */

package ;

import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;
 

@Contract(
        name = "BCycle",
        info = @Info(
                title = "BatteryCycle contract",
                description = "The Battery Supply Chain System contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        name = "shu-on",
                        url = "https://github.com/shu-on")))
@Default
public final class BCycle implements ContractInterface {

    private final Genson genson = new Genson();

    private enum BcycleErrors {
        BATTERY_NOT_FOUND,
        BATTERY_ALREADY_EXISTS
    }

    /**
     * 台帳からキーを特定してバッテリーを扱う
    * @param ctx the transaction context
    * @param key the key
    * @return 台帳でバッテリー発見したら値を返す
    */
    @Transaction()
    public Battery queryBattery(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String batteryState = stub.getStringState(key);

        if (batteryState.isEmpty()) {
            String errorMessage = String.format("Battery %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BCycleErrors.BATTERY_NOT_FOUND.toString());
        }

        Battery battery = genson.deserialize(batteryState, Battery.class);

        return battery;
    }

    /**
     * 初期バッテリー情報を台帳に作成
    * @param ctx the transaction context
    */
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] batteryData = {
                "{ \"AddDate\": \"2022-12-21\", \"Owner\": \"-------taro\", \"BatteryState\": \"------NeedRecycle\", \"BID\": \"0000001\", \"CID\": \"-y0000001\", \"SBID\": \"hx00001\", \"TMP\": \"40\", \"SOH\": \"70\", \"NQC\": \"-200\", \"Mileage\": \"-200800\" }",
                "{ \"AddDate\": \"2022-12-28\", \"Owner\": \"-----hanako\", \"BatteryState\": \"----------UsedCar\", \"BID\": \"0000002\", \"CID\": \"-x0000001\", \"SBID\": \"-------\", \"TMP\": \"30\", \"SOH\": \"85\", \"NQC\": \"--90\", \"Mileage\": \"--70040\" }",
                "{ \"AddDate\": \"2023-01-12\", \"Owner\": \"-------goku\", \"BatteryState\": \"StationaryBattery\", \"BID\": \"0000003\", \"CID\": \"-y0000002\", \"SBID\": \"-------\", \"TMP\": \"35\", \"SOH\": \"80\", \"NQC\": \"-100\", \"Mileage\": \"--65000\" }"
        };

        for (int i = 0; i < batteryData.length; i++) {
            String key = String.format("BATTERY%d", i);

            Battery battery = genson.deserialize(batteryData[i], Battery.class);
            String batteryState = genson.serialize(battery);
            stub.putStringState(key, batteryState);
        }
    }

    /**
     * 新しくバッテリーを台帳に追加
    * @param ctx the transaction context
    * @param key the key for the new battery
    * @param AddDate the Add Date of the new battery
    * @param Owner the Owner of the new battery
    * @param BatteryState the Battery State of the new battery
    * @param BID the Battey ID of the new battery
    * @param CID the Car ID of the new battery
    * @param SBID the Stationary Battery of the new battery
    * @param TMP the Temperature of the new battery
    * @param SOH the State Of Health of the new battery
    * @param NQC the Number of Quick Charging of the new battery
    * @param Mileage the Mileage of the new battery
    * @return the created battery
    */
    @Transaction()
    public Battery createBattery(final Context ctx, final String key, final String AddDate, final String Owner, final String BatteryState, final String BID, final String CID, final String SBID, 
                                    final String TMP, final String SOH, final String NQC, final String Mileage) {
        ChaincodeStub stub = ctx.getStub();

        String batteryState = stub.getStringState(key);
        if (!batteryState.isEmpty()) {
            String errorMessage = String.format("Battery %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BCycleErrors.BATTERY_ALREADY_EXISTS.toString());
        }
        Battery battery = new Battery(AddDate, Owner, BatteryState, BID, CID, SBID, TMP, SOH, NQC, Mileage);
        batteryState = genson.serialize(battery);
        stub.putStringState(key, batteryState);

        return battery;
    }

    /**
    * すべてのバッテリー情報取得
    * @param ctx the transaction context
    * @return array of Battery found on the ledger
    */
    @Transaction()
    public String queryAllBattry(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "BATTERY1";
        final String endKey = "BATTERY99";
        List<BQuery> queryResults = new ArrayList<BQuery>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Battery battery = genson.deserialize(result.getStringValue(), Battery.class);
            queryResults.add(new BQuery(result.getKey(), battery));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
    * 台帳を更新
    * @param ctx the transaction context
    * @param key the key
    * @param newAddDate the new AddDate
    * @param newOwner the new owner
    * @param newBatteryState the new BatteryState
    * @param newSBID the new SBID
    * @param newTMP the new TMP
    * @param newSOH the new SOH
    * @param newNQC the new NQC
    * @param newMileage the new Mileage
    * @return the updated Battery
    */
    @Transaction()
    public Battery changeBatteryProperty(final Context ctx, final String key, final String newAddDate, final String newOwner, final String newBatteryState, 
                                            final String newSBID, final String newTMP, final String newSOH, final String newNQC, final String newMileage) {
        ChaincodeStub stub = ctx.getStub();

        String batteryState = stub.getStringState(key);

        if (batteryState.isEmpty()) {
            String errorMessage = String.format("battery %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BCycleErrors.BATTERY_NOT_FOUND.toString());
        }

        Battery battery = genson.deserialize(batteryState, Battery.class);

        Battery newBattery = new battery(newAddDate, newOwner, newBatteryState, battery.getBID(), battery.getCID(), newSBID, newTMP, newSOH, newNQC, newMileage);
        String newBatteryResult = genson.serialize(newBattery);
        stub.putStringState(key, newBatteryResult);

        return newBattery;
    }
}