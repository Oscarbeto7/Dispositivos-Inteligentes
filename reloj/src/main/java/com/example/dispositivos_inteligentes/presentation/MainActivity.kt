/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.dispositivos_inteligentes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.dispositivos_inteligentes.R
import com.example.dispositivos_inteligentes.presentation.theme.Dispositivos_InteligentesTheme
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class MainActivity : ComponentActivity(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var activityContext: android.content.Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        activityContext = this

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            com.google.android.gms.wearable.Wearable.getDataClient(activityContext!!).removeListener(this)
            com.google.android.gms.wearable.Wearable.getMessageClient(activityContext!!).removeListener(this)
            com.google.android.gms.wearable.Wearable.getCapabilityClient(activityContext!!).removeListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            com.google.android.gms.wearable.Wearable.getDataClient(activityContext!!).addListener(this)
            com.google.android.gms.wearable.Wearable.getMessageClient(activityContext!!).addListener(this)
            com.google.android.gms.wearable.Wearable.getCapabilityClient(activityContext!!).addListener(
                this,
                android.net.Uri.parse("wear://"),
                com.google.android.gms.wearable.CapabilityClient.FILTER_REACHABLE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(ME: com.google.android.gms.wearable.MessageEvent) {
        android.util.Log.d("onMessageReceived", ME.toString())
        android.util.Log.d("onMessageReceived", "ID del nodo ${ME.sourceNodeId}")
        android.util.Log.d("onMessageReceived", "Payload: ${ME.path}")
        val message = String(ME.data, java.nio.charset.StandardCharsets.UTF_8)
        android.util.Log.d("onMessageReceived", "Mensaje recibido desde el celular: $message")
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
        TODO("Not yet implemented")
    }
}

@Composable
fun WearApp(greetingName: String) {
    Dispositivos_InteligentesTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}