package com.kenetic.blockchainvs.fingerprint

import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi

class FingerPrintAuthentication(
    private val context: Context,
    private val taskEnum: FingerPrintTaskEnum,
    private val task: () -> Unit
) {
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context,
                        "Authentication failed, ${taskEnum.name} Cannot Be Executed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    task()
                }
            }

    @RequiresApi(Build.VERSION_CODES.P)
    fun verifyBiometrics() {
        val biometricPrompt = BiometricPrompt.Builder(context)
            .setTitle(taskEnum.name)
            .setSubtitle("Authentication Required To Proceed")
            .setDescription(
                when (taskEnum) {
                    FingerPrintTaskEnum.LOGIN -> "Login To Account Through Fingerprint"
                    FingerPrintTaskEnum.TRANSACTION -> "Scan Your Fingerprint To Authenticate Transaction"
                }
            )
            .setNegativeButton("Cancel", context.mainExecutor) { _: DialogInterface, _: Int -> }
            .build()
        biometricPrompt.authenticate(
            getCancellationSignal(),
            context.mainExecutor,
            authenticationCallback
        )
    }

    private fun getCancellationSignal(): CancellationSignal {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            Toast.makeText(
                context, "Fingerprint Authentication Cancelled", Toast.LENGTH_SHORT
            ).show()
        }
        return cancellationSignal
    }
}