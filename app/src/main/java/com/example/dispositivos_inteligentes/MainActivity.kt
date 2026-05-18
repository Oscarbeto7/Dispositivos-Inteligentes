package com.example.dispositivos_inteligentes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
        }
    }
}