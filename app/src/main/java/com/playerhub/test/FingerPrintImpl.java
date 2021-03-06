package com.playerhub.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.widget.Toast;


import com.playerhub.ui.login.FingerprintAuthenticationDialogFragment;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrintImpl implements Fingerprint {

    private static final String TAG = "FingerPrintImpl";

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";

    private Context mContext;
    private FragmentManager fragmentManager;
    private FingerprintCallback fingerprintCallback;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher defaultCipher;
    private Cipher cipherNotInvalidated;


    public FingerPrintImpl(Context context, FragmentManager fragmentManager, FingerprintCallback fingerprintCallback) {

        this.fragmentManager = fragmentManager;
        this.mContext = context;
        this.fingerprintCallback = fingerprintCallback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (isSensorAvialable(this.mContext)) {

                KeyguardManager keyguardManager = this.mContext.getSystemService(KeyguardManager.class);
                FingerprintManager fingerprintManager = this.mContext.getSystemService(FingerprintManager.class);


                // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
                // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
                // The line below prevents the false positive inspection from Android Studio
                // noinspection ResourceType

                try {

                    if (keyguardManager != null) {
                        if (!keyguardManager.isKeyguardSecure()) {
                            // Show a message that the user hasn't set up a fingerprint or lock screen.
                            Toast.makeText(this.mContext,
                                    "Secure lock screen hasn't set up.\n"
                                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                                    Toast.LENGTH_LONG).show();
                            //                return;
                        } else {


                            if (fingerprintManager != null) {
                                if (!fingerprintManager.hasEnrolledFingerprints()) {
                                    // This happens when no fingerprints are registered.
                                    Toast.makeText(this.mContext,
                                            "Go to 'Settings -> Security -> Fingerprint' and register at least one" +
                                                    " fingerprint",
                                            Toast.LENGTH_LONG).show();
                                    //                return;
                                } else {


                                    try {
                                        mKeyStore = KeyStore.getInstance("AndroidKeyStore");
                                    } catch (KeyStoreException e) {
                                        //            throw new RuntimeException("Failed to get an instance of KeyStore", e);
                                    }
                                    try {
                                        mKeyGenerator = KeyGenerator
                                                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                                    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                                        //            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
                                    }

                                    try {
                                        defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
                                        cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
                                    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                                        //            throw new RuntimeException("Failed to get an instance of Cipher", e);
                                    }

                                    createKey(DEFAULT_KEY_NAME, true);
                                    createKey(KEY_NAME_NOT_INVALIDATED, false);
                                }
                            } else {

                                Toast.makeText(this.mContext,
                                        "Go to 'Settings -> Security -> Fingerprint' and register at least one" +
                                                " fingerprint",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    }


                } catch (NullPointerException e) {

                    Log.e(TAG, "onCreate: " + e.getMessage());

                }


            }
        } else {

            Toast.makeText(this.mContext,
                    "There is no finger print",
                    Toast.LENGTH_LONG).show();
        }


    }


    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void callFingerprint() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            FingerprintManager fingerprintManager = mContext.getSystemService(FingerprintManager.class);

            try {

                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {

                    Cipher mCipher = defaultCipher;
                    String mKeyName = DEFAULT_KEY_NAME;

                    if (initCipher(mCipher, mKeyName)) {

                        // Show the fingerprint dialog. The user has the option to use the fingerprint with
                        // crypto, or you can fall back to using a server-side verified password.
                        FingerprintAuthenticationDialogFragment fragment
                                = new FingerprintAuthenticationDialogFragment();
                        fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));

                        fragment.setStage(
                                FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);

                        fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);


                    } else {

                        // This happens if the lock screen has been disabled or or a fingerprint got
                        // enrolled. Thus show the dialog to authenticate with their password first
                        // and ask the user if they want to authenticate with fingerprints in the
                        // future
                        FingerprintAuthenticationDialogFragment fragment
                                = new FingerprintAuthenticationDialogFragment();
                        fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                        fragment.setStage(
                                FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                        fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);


                    }
                } else {

                    if (fingerprintCallback != null) fingerprintCallback.onFingerPrintSuccess();
//                    startActivity(new Intent(this, DashBoardActivity.class));
//                    finish();
                }

            } catch (NullPointerException e) {
//                startActivity(new Intent(this, DashBoardActivity.class));
//                finish();

                if (fingerprintCallback != null) fingerprintCallback.onFingerPrintSuccess();

            }

        } else {
            if (fingerprintCallback != null) fingerprintCallback.onFingerPrintSuccess();
//            startActivity(new Intent(this, DashBoardActivity.class));
//            finish();

        }


    }


    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String, boolean)} method.
     *
     * @param keyName the key name to init the cipher
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
//            throw new RuntimeException("Failed to init Cipher", e);
            return false;
        }
    }

    /**
     * Proceed the purchase operation
     *
     * @param withFingerprint {@code true} if the purchase was made by using a fingerprint
     * @param cryptoObject    the Crypto object
     */
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            showConfirmation(null);
        }
    }

    // Show confirmation, if fingerprint was used show crypto information.
    private void showConfirmation(byte[] encrypted) {

        if (encrypted != null) {

            if (fingerprintCallback != null) fingerprintCallback.onFingerPrintSuccess();

        } else {

            if (fingerprintCallback != null)
                fingerprintCallback.onFingerPrintFailer("Fingerprint something went wrong, try again later");

        }
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt(Cipher cipher) {
        try {
            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
            showConfirmation(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(mContext, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName                          the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     */
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
//            throw new RuntimeException(e);
        }
    }

    private boolean isSensorAvialable(Context AppContext) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ActivityCompat.checkSelfPermission(AppContext, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED &&
                        AppContext.getSystemService(FingerprintManager.class).isHardwareDetected();
            } else {
                return FingerprintManagerCompat.from(AppContext).isHardwareDetected();
            }
        } catch (NullPointerException e) {

            return false;
        } catch (RuntimeException e) {

            return false;
        }

    }


    public interface FingerprintCallback {


        void onFingerPrintSuccess();

        void onFingerPrintFailer(String s);

    }

}
