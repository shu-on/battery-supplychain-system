/*
* SPDX-License-Identifier: Apache-2.0
*/

package ;

import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;
 
@DataType()
public final class Battery {
 
    @Property()
     private final String AddDate;

    @Property()
     private final String Owner;

    @Property()
     private final String BatteryState;

    @Property()
     private final String BID;

    @Property()
     private final String CID;

    @Property()
     private final String SBID;

    @Property()
     private final String TMP;
 
    @Property()
     private final String SOH;

    @Property()
     private final String NQC;

    @Property()
     private final String Mileage;


    public String getAddDate() {
        return AddDate;
    }
    public String getOwner() {
        return Owner;
    }
    public String getBatteryState() {
        return BatteryState;
    }
    public String getBID() {
        return BID;
    }
    public String getCID() {
        return CID;
    }
    public String getSBID() {
        return SBID;
    }
    public String getTMP() {
        return TMP;
    }
    public String getSOH() {
        return SOH;
    }
    public String getNQC() {
        return NQC;
    }
    public String getMileage() {
        return Mileage;
    }
 
    public Battery(@JsonProperty("AddDate") final String AddDate, @JsonProperty("Owner") final String Owner, @JsonProperty("BatteryState") final String BatteryState, 
                    @JsonProperty("BID") final String BID, @JsonProperty("CID") final String CID, @JsonProperty("SBID") final String SBID,
                    @JsonProperty("TMP") final String TMP, @JsonProperty("SOH") final String SOH, @JsonProperty("NQC") final String NQC, @JsonProperty("Mileage") final String Mileage) {

                        this.AddDate = AddDate;
                        this.Owner = Owner;
                        this.BatteryState = BatteryState;
                        this.BID = BID;
                        this.CID = CID;
                        this.SBID = SBID;
                        this.TMP = TMP;
                        this.SOH = SOH;
                        this.NQC = NQC;
                        this.Mileage = Mileage;
    }
 
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Battery other = (Battery) obj;
 
        return Objects.deepEquals(new String[] {getAddDate(), getOwner(), getBatteryState(), getBID(), getCID(), getSBID(), getTMP(), getSOH(), getNQC(), getMileage()},
                                    new String[] {other.getAddDate(), other.getOwner(), other.getBatteryState(), other.getBID(), other.getCID(), other.getSBID(), 
                                                    other.getTMP(), other.getSOH(), other.getNQC(), other.getMileage()});
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getAddDate(), getOwner(), getBatteryState(), getBID(), getCID(), getSBID(), getTMP(), getSOH(), getNQC(), getMileage());
    }
 
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [AddDate=" + AddDate + ", Owner=" + Owner + ", BatteryState=" + BatteryState + 
                                                ", BID=" + BID + ", CID=" + CID + ", SBID=" + SBID + ", TMP=" + TMP + ", SOH=" + SOH + ", NQC=" + NQC +  ", Mileage=" + Mileage +"]";
    }
}