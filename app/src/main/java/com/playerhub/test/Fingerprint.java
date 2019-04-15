package com.playerhub.test;

import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.Nullable;

public interface Fingerprint {

    void callFingerprint();

    void createKey(String keyName, boolean invalidatedByBiometricEnrollment);

    void onPurchased(boolean withFingerprint,
                     @Nullable FingerprintManager.CryptoObject cryptoObject);
}
