/*
 * SPDX-License-Identifier: Apache-2.0
 */

package ;//github path

import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;
 
//クエリーの結果を操作するためにこのクラスは使われる
@DataType()
public final class BQuery {
    
    @Property()
    private final String key;

    @Property()
    private final Battery record;
 
    public BQuery(@JsonProperty("Key") final String key, @JsonProperty("Record") final Battery record) {
        this.key = key;
        this.record = record;
    }
    public String getKey() {
        return key;
    }
    public Battery getRecord() {
        return record;
    }
 
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        BQuery other = (BQuery) obj;
        Boolean recordsAreEquals = this.getRecord().equals(other.getRecord());
        Boolean keysAreEquals = this.getKey().equals(other.getKey());
 
        return recordsAreEquals && keysAreEquals;
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(this.getKey(), this.getRecord());
    }
 
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [key=" + key + ", record=" + record + "]";
    }
}