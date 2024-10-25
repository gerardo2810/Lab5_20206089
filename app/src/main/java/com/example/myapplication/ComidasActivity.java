package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ComidasActivity extends AppCompatActivity {

    private double caloriasConsumidas = 0.0; // Variable para acumular las calorías

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        // Inicialización de vistas
        EditText nombreComidaEditText = findViewById(R.id.et_nombre_comida);
        EditText caloriasComidaEditText = findViewById(R.id.et_calorias_comida);
        Button añadirComidaButton = findViewById(R.id.btn_añadir_comida);
        TextView caloriasConsumidasTextView = findViewById(R.id.tv_calorias_consumidas);

        // Configurar el botón para añadir comida
        añadirComidaButton.setOnClickListener(v -> {
            // Obtener los valores ingresados
            String nombreComida = nombreComidaEditText.getText().toString();
            String caloriasComidaString = caloriasComidaEditText.getText().toString();

            if (!caloriasComidaString.isEmpty()) {
                double caloriasComida = Double.parseDouble(caloriasComidaString);

                // Aumentar el total de calorías consumidas
                caloriasConsumidas += caloriasComida;
                caloriasConsumidasTextView.setText("Calorías consumidas hoy: " + (int) caloriasConsumidas);

                // Limpiar los campos después de añadir la comida
                nombreComidaEditText.setText("");
                caloriasComidaEditText.setText("");
            }
        });
    }
}
