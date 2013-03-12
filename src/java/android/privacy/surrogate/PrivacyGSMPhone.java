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
import android.os.Binder;
import android.os.Process;
import android.os.ServiceManager;
import android.privacy.IPrivacySettingsManager;
import android.privacy.PrivacyServiceException;
import android.privacy.IPrivacySettings;
import android.privacy.PrivacySettings;
import android.privacy.PrivacySettingsManager;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.PhoneNotifier;
import com.android.internal.telephony.PhoneSubInfo;
import com.android.internal.telephony.UUSInfo;
import com.android.internal.telephony.gsm.GSMPhone;
/**
 * Provides privacy handling for phone
 * @author CollegeDev
 * {@hide}
 */
public class PrivacyGSMPhone extends GSMPhone{

    private static final String P_TAG = "PrivacyGSMPhone";
    private PrivacySettingsManager mPrvSvc;
    private Context context;

    public PrivacyGSMPhone(Context context, CommandsInterface cmdI, PhoneNotifier pN) {
        super(context, cmdI, pN);
        this.context = context;
        mPrvSvc = PrivacySettingsManager.getPrivacyService();
        Log.i(P_TAG,"Constructor ready for package: " + context.getPackageName());
    }


    @Override
    public String getDeviceSvn() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getDeviceSvn()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getDeviceIdSetting()) == IPrivacySettings.REAL) {
            output = super.getDeviceSvn();
        } else {
            output = settings.getDeviceId(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getDeviceIdSetting(), IPrivacySettings.DATA_DEVICE_ID, output);
        return output;
    }

    @Override
    public String getImei() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getImei");

        String output;		
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getDeviceIdSetting()) == IPrivacySettings.REAL) {
            output = super.getImei();
        } else {
            output = settings.getDeviceId(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getDeviceIdSetting(), IPrivacySettings.DATA_DEVICE_ID, output);
        return output;
    }

    @Override
    public String getSubscriberId() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getSubscriberId()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getSubscriberIdSetting()) == IPrivacySettings.REAL) {
            output = super.getSubscriberId();
        } else {
            output = settings.getSubscriberId(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getSubscriberIdSetting(), IPrivacySettings.DATA_SUBSCRIBER_ID, output);
        return output;
    }

    //	@Override
    //	public void notifyLocationChanged() {
    //		Log.i(P_TAG,"UID " + uid + " asked for notifyLocationChanged()");
    //		PrivacySettings settings = mPrvSvc.getSettings(context.getPackageName(), Process.myUid());
    //		if(mPrvSvc != null && settings != null && settings.getNetworkInfoSetting() != PrivacySettings.REAL){
    //			//do nothing here
    //		}
    //		else
    //			mNotifier.notifyCellLocation(this);
    //	}


    @Override
    public String getLine1AlphaTag() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getLine1AlphaTag()");
        String output;

        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getLine1NumberSetting()) == IPrivacySettings.REAL) {
            output = super.getLine1AlphaTag();
        } else {
            output = settings.getLine1Number();
        }
        mPrvSvc.notification(uid, settings.getLine1NumberSetting(), IPrivacySettings.DATA_LINE_1_NUMBER, output);
        return output;
    }


    @Override
    public String getVoiceMailAlphaTag() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getVoiceMailAlphaTag()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getLine1NumberSetting()) == IPrivacySettings.REAL) {
            output = super.getVoiceMailAlphaTag();
        } else {
            output = settings.getLine1Number(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getLine1NumberSetting(), IPrivacySettings.DATA_LINE_1_NUMBER, output);
        return output;
    }

    @Override
    public String getVoiceMailNumber(){
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getVoiceMailNumber()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getLine1NumberSetting()) == IPrivacySettings.REAL) {
            output = super.getVoiceMailNumber();
        } else {
            output = settings.getLine1Number(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getLine1NumberSetting(), IPrivacySettings.DATA_LINE_1_NUMBER, output);		            
        return output;
    }

    @Override
    public String getDeviceId() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getDeviceId()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getDeviceIdSetting()) == IPrivacySettings.REAL) {
            output = super.getDeviceId();
        } else {
            output = settings.getDeviceId(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getDeviceIdSetting(), IPrivacySettings.DATA_DEVICE_ID, output);
        return output;
    }

    @Override
    public String getMeid() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getMeid()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getDeviceIdSetting()) == IPrivacySettings.REAL) {
            output = super.getMeid();
        } else {
            output = settings.getDeviceId(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getDeviceIdSetting(), IPrivacySettings.DATA_DEVICE_ID, output);                    
        return output;
    }

    @Override
    public String getEsn() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getEsn()");

        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getDeviceIdSetting()) == IPrivacySettings.REAL) {
            output = super.getEsn();
	            
        } else {
            output = settings.getDeviceId(); // can be empty, custom or random
        }
        mPrvSvc.notification(uid, settings.getDeviceIdSetting(), IPrivacySettings.DATA_DEVICE_ID, output);
        return output;
    }

    @Override
    public String getLine1Number() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getLine1Number()");
        String output;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getLine1NumberSetting()) == IPrivacySettings.REAL) {
            output = super.getLine1Number();
        } else {
            output = settings.getLine1Number();
        }
        mPrvSvc.notification(uid, settings.getLine1NumberSetting(), IPrivacySettings.DATA_LINE_1_NUMBER, output);
        return output;
    }

    @Override
    public CellLocation getCellLocation() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"UID " + uid + " asked for getCellLocation()");

        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if ((PrivacySettings.getOutcome(settings.getLocationNetworkSetting()) == IPrivacySettings.REAL && PrivacySettings.getOutcome(settings.getLocationGpsSetting()) == IPrivacySettings.REAL)) {
            mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
            return super.getCellLocation();
        } else {
            mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
            return new GsmCellLocation();
        }
    }

    @Override
    public PhoneSubInfo getPhoneSubInfo() {
        int uid = context.getApplicationInfo().uid;
        Log.i(P_TAG,"uid " + uid + " asked for getPhoneSubInfo()");

        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getNetworkInfoSetting()) == IPrivacySettings.REAL) {
            mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
            return super.getPhoneSubInfo();
        } else {
            mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
            return null;
        }
    }

    @Override
    public ServiceState getServiceState() {
        try{
            int uid = context.getApplicationInfo().uid;
            Log.i(P_TAG,"UID " + uid + " asked for getServiceState()");
            if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
            IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
            if (PrivacySettings.getOutcome(settings.getNetworkInfoSetting()) == IPrivacySettings.REAL) {
                mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
                return super.getServiceState();
            } else {
                mPrvSvc.notification(uid, settings.getLocationNetworkSetting(), IPrivacySettings.DATA_LOCATION_NETWORK, null);
                ServiceState output = super.getServiceState();
                if (output != null) {
                    output.setOperatorName("", "", "");
                }
                return output;
            }
        } catch(Exception e) {
            Log.e(P_TAG,"We got exception in getServiceState()-> give fake state", e);
            ServiceState output = super.getServiceState();
            output.setOperatorName("", "", "");
            return output;
        }
    }

    @Override
    public Connection dial(String dialNumber) throws CallStateException{
        int uid = context.getApplicationInfo().uid;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getPhoneCallSetting()) == IPrivacySettings.REAL) {
            mPrvSvc.notification(uid, settings.getPhoneCallSetting(), IPrivacySettings.DATA_PHONE_CALL, null);
            return super.dial(dialNumber);
        } else {
            mPrvSvc.notification(uid, settings.getPhoneCallSetting(), IPrivacySettings.DATA_PHONE_CALL, null);
            throw new CallStateException();
        }
    }

    @Override
    public Connection dial (String dialNumber, UUSInfo uusInfo) throws CallStateException{
        int uid = context.getApplicationInfo().uid;
        if (mPrvSvc == null) mPrvSvc = PrivacySettingsManager.getPrivacyService();
        IPrivacySettings settings = mPrvSvc.getSettingsSafe(uid);
        if (PrivacySettings.getOutcome(settings.getPhoneCallSetting()) == IPrivacySettings.REAL) {
            mPrvSvc.notification(uid, settings.getPhoneCallSetting(), IPrivacySettings.DATA_PHONE_CALL, null);
            return super.dial(dialNumber, uusInfo);
        } else {
            mPrvSvc.notification(uid, settings.getPhoneCallSetting(), IPrivacySettings.DATA_PHONE_CALL, null);
            throw new CallStateException();
        }
    }
}
