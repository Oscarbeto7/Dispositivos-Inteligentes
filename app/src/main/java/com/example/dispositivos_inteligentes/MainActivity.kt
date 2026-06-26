package com.example.dispositivos_inteligentes

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    CoroutineScope by MainScope(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var activityContext: Context? = null
    private var deviceConnected: Boolean = false
    private val PAYLOAD_PATH = "/APP_OPEN"
    lateinit var nodeID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        activityContext = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // 1. Encontrar los elementos de la pantalla usando sus IDs
        val cajaTexto = findViewById<EditText>(R.id.etEntradaTexto)
        val boton = findViewById<Button>(R.id.btnAceptar)
        val label = findViewById<TextView>(R.id.tvResultado)

        // 2. Darle la acción al botón para cuando sea presionado
        boton.setOnClickListener {

            // 3. Obtener el texto que escribiste en la caja de texto
            val textoIngresado = cajaTexto.text.toString()
            if (!deviceConnected) {
                val tempAct: android.app.Activity = activityContext as MainActivity
                getNodes(tempAct)
            } else {
                sendMessage() // ¡Aquí llamamos a nuestra nueva función!
            }

            // 4. Pasar ese texto al label
            label.text = textoIngresado

            // 5. Crear y mostrar la alerta tipo pop-up
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¡Éxito!")
            builder.setMessage("El texto se ha pasado correctamente.")
            builder.setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss() // Esto cierra la alerta al presionar "Aceptar"
            }

            // Mostrar la alerta
            val alerta = builder.create()
            alerta.show()

            // --- NUEVOS BOTONES PARA HTTP ---
            val btnGet = findViewById<Button>(R.id.btnGet)
            val btnPost = findViewById<Button>(R.id.btnPost)

            btnGet.setOnClickListener {
                // Hacemos un GET a la API de prueba
                hacerGet("https://jsonplaceholder.typicode.com/posts/1")
            }

            btnPost.setOnClickListener {
                // Hacemos un POST simulando que guardamos información desde el celular
                val jsonParaGuardar = """
                {
                  "title": "Prueba de App",
                  "body": "Guardando información desde mi celular",
                  "userId": 100
                }
            """.trimIndent()

                hacerPost("https://jsonplaceholder.typicode.com/posts", jsonParaGuardar)
            }
        }
    }


    private fun getNodes(context: Context) {
        launch(Dispatchers.Default) {
            try {
                // Busca los nodos (relojes) conectados
                val nodeList = com.google.android.gms.tasks.Tasks.await(
                    com.google.android.gms.wearable.Wearable.getNodeClient(context).connectedNodes
                )

                for (node in nodeList) {
                    android.util.Log.d("NODO", node.toString())
                    android.util.Log.d("NODO", "El id del nodo es: ${node.id}")
                    nodeID = node.id
                    deviceConnected = true
                }
            } catch (exception: Exception) {
                android.util.Log.d("Error en el nodo", exception.toString())
            }
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

    private fun sendMessage() {
        val sendMessage = com.google.android.gms.wearable.Wearable.getMessageClient(activityContext!!)
            .sendMessage(nodeID, PAYLOAD_PATH, "HOLA K ASE".toByteArray())
            .addOnSuccessListener {
                android.util.Log.d("sendMessage", "Mensaje enviado correctamente")
            }
            .addOnFailureListener { e ->
                android.util.Log.d("sendMessage", "Error al enviar mensaje ${e.message}")
            }
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(ME: MessageEvent) {
        android.util.Log.d("onMessageReceived", ME.toString())
        android.util.Log.d("onMessageReceived", "ID del nodo ${ME.sourceNodeId}")
        android.util.Log.d("onMessageReceived", "Payload: ${ME.path}")
        val message = String(ME.data, java.nio.charset.StandardCharsets.UTF_8)
        android.util.Log.d("onMessageReceived", "Mensaje: $message")
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
        TODO("Not yet implemented")

    }
    // --- FUNCIONES HTTP (Páginas 34 y 35) ---
    private fun hacerGet(url: String) {
        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                android.util.Log.d("FETCH", "Error GET: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        android.util.Log.d("FETCH", "Error en la respuesta GET: ${response.code}")
                    } else {
                        val responseData = response.body?.string()
                        android.util.Log.d("FETCH", "Respuesta GET exitosa: $responseData")
                    }
                }
            }
        })
    }

    private fun hacerPost(url: String, jsonString: String) {
        val client = okhttp3.OkHttpClient()
        val JSON = okhttp3.MediaType.parse("application/json; charset=utf-8")
        val body = okhttp3.RequestBody.create(JSON, jsonString)

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                android.util.Log.d("FETCH", "Error POST: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        android.util.Log.d("FETCH", "Error en la respuesta POST: ${response.code}")
                    } else {
                        val responseData = response.body?.string()
                        android.util.Log.d("FETCH", "Respuesta POST exitosa: $responseData")
                    }
                }
            }
        })
    }
}

