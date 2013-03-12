/**
 * Copyright (C) 2012 Stefan Thiele
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */

package android.privacy.surrogate;

import android.content.Context;
import android.content.pm.IPackageManager;
import android.os.ServiceManager;
import android.privacy.IPrivacySettingsManager;
import android.privacy.PrivacyServiceException;
import android.privacy.IPrivacySettings;
import android.privacy.PrivacySettings;
import android.privacy.PrivacySettingsManager;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.os.Process;

import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneBase;
import com.android.internal.telephony.PhoneProxy;
import com.android.internal.telephony.PhoneSubInfo;
import com.android.internal.telephony.UUSInfo;

import com.android.internal.telephony.PhoneConstants;
/**
 * Provides privacy handling for phone
 * @author CollegeDev
 * @deprecated normally this class is not neeeded anymore, since we got privacy phones. The only method which is interesting is getPhoneSubInfo 
 * {@hide}
 */

public class PrivacyPhoneProxy extends PhoneProxy{

    private static final String P_TAG = "PrivacyPhoneProxy";
    private PrivacySettingsManager mPrvSvc;
    private Context mContext;
    private boolean mContextAvailable;


    public PrivacyPhoneProxy(PhoneBase mPhone, Context context) { //not sure if context is available, so test it!
        super(mPhone);
        this.mContext = context;
        mPrvSvc = PrivacySettingsManager.getPrivacyService();
        Log.i(P_TAG,"Constructor ready for UID " + context.getApplicationInfo().uid);
    }

    @Override
    public Connection dial(String dialNumber) throws CallStateException{
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getPhoneCallSetting()) == IPrivacySettings.REAL) {
            mPrvSvc.notification(uid, IPrivacySettings.REAL, IPrivacySettings.DATA_PHONE_CALL, null);
            return super.dial(dialNumber);                    
        } else {
            mPrvSvc.notification(uid, IPrivacySettings.EMPTY, IPrivacySettings.DATA_PHONE_CALL, null);
            throw new CallStateException();                    
        }
    }

    @Override
    public Connection dial (String dialNumber, UUSInfo uusInfo) throws CallStateException{
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if(PrivacySettings.getOutcome(settings.getPhoneCallSetting()) != IPrivacySettings.REAL){
            mPrvSvc.notification(uid, IPrivacySettings.EMPTY, IPrivacySettings.DATA_PHONE_CALL, null);
            throw new CallStateException();
        } else {
            mPrvSvc.notification(uid, IPrivacySettings.REAL, IPrivacySettings.DATA_PHONE_CALL, null);
            return super.dial(dialNumber, uusInfo);
        }
    }

    @Override
    public CellLocation getCellLocation() {
        int phone_type = super.getPhoneType();

        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if((PrivacySettings.getOutcome(settings.getLocationNetworkSetting()) != IPrivacySettings.REAL || PrivacySettings.getOutcome(settings.getLocationGpsSetting()) != IPrivacySettings.REAL)){
            mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getCellLocation()");
            switch(phone_type){
            case PhoneConstants.PHONE_TYPE_GSM:
                return new GsmCellLocation();
            case PhoneConstants.PHONE_TYPE_CDMA:
                return new CdmaCellLocation();
            case PhoneConstants.PHONE_TYPE_NONE:
                return null;
            case PhoneConstants.PHONE_TYPE_SIP:
                return new CdmaCellLocation();
            default: //just in case, but normally this doesn't get a call!
                return new GsmCellLocation();
            }
        } else {
            if(settings != null) {
                mPrvSvc.notification(uid, IPrivacySettings.REAL, IPrivacySettings.DATA_LOCATION_NETWORK, null);
            }
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getCellLocation()");
            return super.getCellLocation();
        }
    }

    @Override
    public PhoneConstants.DataState getDataConnectionState() {
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if(PrivacySettings.getOutcome(settings.getNetworkInfoSetting()) != IPrivacySettings.REAL){
            mPrvSvc.notification(uid, settings.getNetworkInfoSetting(), IPrivacySettings.DATA_NETWORK_INFO_CURRENT, null);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getDataConnection()");
            return PhoneConstants.DataState.CONNECTING; //it's the best way to tell system that we are connecting
        } else {
            if(settings != null)
                mPrvSvc.notification(uid, IPrivacySettings.REAL, IPrivacySettings.DATA_NETWORK_INFO_CURRENT, null);
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getDataConnection()");
            return super.getDataConnectionState();
        }
    }

    //	@Override
    //	public State getState() {
    //		State.
    //		return null;
    //	}

    //	@Override
    //	public String getPhoneName() {
    //		return null;
    //	}

    //	@Override
    //	public int getPhoneType() {
    //		return 0;
    //	}

    @Override
    public SignalStrength getSignalStrength() {
        int uid = Process.myUid();
        SignalStrength output = new SignalStrength();

        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if(PrivacySettings.getOutcome(settings.getNetworkInfoSetting()) != IPrivacySettings.REAL){
            mPrvSvc.notification(uid, settings.getNetworkInfoSetting(), IPrivacySettings.DATA_NETWORK_INFO_CURRENT, null);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getSignalStrength()");
            return output;
        } else {
            if(settings != null)
                mPrvSvc.notification(uid, IPrivacySettings.REAL, IPrivacySettings.DATA_NETWORK_INFO_CURRENT, null);
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getSignalStrength()");
            return super.getSignalStrength();
        }
    }

    //	@Override
    //	public IccCard getIccCard() {
    //		return null;
    //	}

    @Override
    public String getLine1Number() {
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings pSet = mPrvSvc.getSettingsSafe(uid);
        String output;
        if (pSet != null && PrivacySettings.getOutcome(pSet.getLine1NumberSetting()) != IPrivacySettings.REAL) {
            output = pSet.getLine1Number(); // can be empty, custom or random
            mPrvSvc.notification(uid, pSet.getLine1NumberSetting(), IPrivacySettings.DATA_LINE_1_NUMBER, output);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getLine1Number()");
        } else {
            output = super.getLine1Number();
            mPrvSvc.notification(uid, IPrivacySettings.REAL, IPrivacySettings.DATA_LINE_1_NUMBER, output);
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getLine1Number()");
        }
        return output;
    }

    /**
     * Will be handled like the Line1Number.
     */
    @Override
    public String getLine1AlphaTag() {
        return getLine1Number();
    }

    /**
     * Will be handled like the Line1Number, since voice mailbox numbers often
     * are similar to the phone number of the subscriber.
     */
    @Override
    public String getVoiceMailNumber() {
        return getLine1Number();
    }

    //will look at this later!
    //	@Override
    //	public void getNeighboringCids(Message response) {
    //		
    //	}

    @Override
    public String getDeviceId() {
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings pSet = mPrvSvc.getSettingsSafe(uid);
        String output;
        if (pSet != null && PrivacySettings.getOutcome(pSet.getDeviceIdSetting()) != PrivacySettings.REAL) {
            output = pSet.getDeviceId(); // can be empty, custom or random
            mPrvSvc.notification(uid, pSet.getDeviceIdSetting(), PrivacySettings.DATA_DEVICE_ID, output);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getDeviceId()");
        } else {
            output = super.getDeviceId();
            mPrvSvc.notification(uid, PrivacySettings.REAL, PrivacySettings.DATA_DEVICE_ID, output);
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getDeviceId()");
        }
        return output;
    }

    /**
     * Will be handled like the DeviceID.
     */
    @Override
    public String getDeviceSvn() {
        return getDeviceId();
    }

    @Override
    public String getSubscriberId() {
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings pSet = mPrvSvc.getSettingsSafe(uid);
        String output;
        if (pSet != null && PrivacySettings.getOutcome(pSet.getSubscriberIdSetting()) != PrivacySettings.REAL) {
            output = pSet.getSubscriberId(); // can be empty, custom or random
            mPrvSvc.notification(uid, pSet.getSubscriberIdSetting(), PrivacySettings.DATA_SUBSCRIBER_ID, output);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getSubscriberId()");
        } else {
            output = super.getSubscriberId();
            mPrvSvc.notification(uid, PrivacySettings.REAL, PrivacySettings.DATA_SUBSCRIBER_ID, output);   
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getSubscriberId()");
        }
        return output;
    }

    /**
     * Will be handled like the SubscriberID.
     */
    @Override
    public String getIccSerialNumber() {
        return getSubscriberId();
    }
    /**
     * Will be handled like the SubscriberID.
     */
    @Override
    public String getEsn() {
        return getSubscriberId();
    }
    /**
     * Will be handled like the SubscriberID.
     */
    @Override
    public String getMeid() {
        return getSubscriberId();
    }
    /**
     * Will be handled like the SubscriberID.
     */
    @Override
    public String getMsisdn() {
        return getSubscriberId();
    }
    /**
     * Will be handled like the DeviceID.
     */
    @Override
    public String getImei() {
        return getDeviceId();
    }

    @Override
    public PhoneSubInfo getPhoneSubInfo(){
        PhoneSubInfo output = new PhoneSubInfo(this);
        return output;
    }

    @Override
    public ServiceState getServiceState(){
        ServiceState output;
        int uid = Process.myUid();
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if(PrivacySettings.getOutcome(settings.getNetworkInfoSetting()) != PrivacySettings.REAL){
            mPrvSvc.notification(uid, settings.getNetworkInfoSetting(), PrivacySettings.DATA_NETWORK_INFO_CURRENT, null);
            Log.i(P_TAG,"UID " + uid + " BLOCKED for getServiceState()");
            output = super.getServiceState();
            if (output != null) {
                output.setOperatorName("", "", "");
            }
            //output.setRadioTechnology(-1);
            return output;
        } else {
            if(settings != null) {
                mPrvSvc.notification(uid, PrivacySettings.REAL, PrivacySettings.DATA_NETWORK_INFO_CURRENT, null);
            }
            Log.i(P_TAG,"UID " + uid + " ALLOWED for getServiceState()");
            return super.getServiceState();
        }
    }
}
