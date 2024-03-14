import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.pesapal.sdk.BuildConfig
import com.pesapal.sdk.utils.sec.BasicSecurity.verifyBasicSecurity
import com.pesapal.sdk.utils.sec.SecurityLevel
import com.pesapal.sdk.utils.sec.device.fingerprinting.DeviceFingerPrintModel

class DeviceFingerprint(private val context: Context) {
    private fun getIMEI(): String? {
        // Implement your logic to get the IMEI here
        // Requires READ_PHONE_STATE permission
        return null
    }

    @SuppressLint("HardwareIds")
    fun getAndroidID(): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun deviceSecurity(): String {
        val deviceSecurity = verifyBasicSecurity(context, true)
        return deviceSecurity.toString();
    }

    private fun getDeviceBuildInfo(): String {
        val buildInfo = StringBuilder()
        buildInfo.append("Manufacturer: ${Build.MANUFACTURER}\n")
        buildInfo.append("Model: ${Build.MODEL}\n")
        buildInfo.append("Device: ${Build.DEVICE}\n")
        buildInfo.append("Product: ${Build.PRODUCT}\n")
        return buildInfo.toString()
    }

    // Create a fingerprint using the collected data
    fun createFingerprint(): DeviceFingerPrintModel {
        val manufacturer = Build.MANUFACTURER ?: " "
        val imei = getIMEI() ?: ""
        val androidId = getAndroidID() ?: ""

        val deviceSecurity: String = if (BuildConfig.BUILD_TYPE.contains("release"))
            deviceSecurity()
        else
            SecurityLevel.ACCEPTABLE.toString()

        val deviceBuildInfo = getDeviceBuildInfo()

        return DeviceFingerPrintModel(
            manufacturer = manufacturer,
            imei = imei,
            device_id = androidId,
            device_security = deviceSecurity,
            deviceInfo = deviceBuildInfo,
            channel = 4
        );

    }

}
