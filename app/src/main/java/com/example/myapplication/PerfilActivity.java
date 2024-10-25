package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas
        EditText pesoEditText = findViewById(R.id.et_peso);
        EditText alturaEditText = findViewById(R.id.et_altura);
        EditText edadEditText = findViewById(R.id.et_edad);
        Spinner generoSpinner = findViewById(R.id.spinner_genero);
        Spinner actividadSpinner = findViewById(R.id.spinner_actividad);
        Spinner objetivoSpinner = findViewById(R.id.spinner_objetivo);
        Button calcularButton = findViewById(R.id.btn_calcular);
        TextView caloriasTextView = findViewById(R.id.tv_calorias_recomendadas);

        calcularButton.setOnClickListener(v -> {
            double peso = Double.parseDouble(pesoEditText.getText().toString());
            double altura = Double.parseDouble(alturaEditText.getText().toString());
            int edad = Integer.parseInt(edadEditText.getText().toString());
            String genero = generoSpinner.getSelectedItem().toString();
            String actividad = actividadSpinner.getSelectedItem().toString();
            String objetivo = objetivoSpinner.getSelectedItem().toString();

            double tmb = calcularTMB(peso, altura, edad, genero);
            double nivelActividad = obtenerNivelActividad(actividad);
            double caloriasDiarias = tmb * nivelActividad;

            double caloriasObjetivo = 0;
            switch (objetivo) {
                case "Subir de peso":
                    caloriasObjetivo = caloriasDiarias + 500;
                    break;
                case "Bajar de peso":
                    caloriasObjetivo = caloriasDiarias - 300;
                    break;
                case "Mantener peso":
                    caloriasObjetivo = caloriasDiarias;
                    break;
            }

            caloriasTextView.setText("Calorías recomendadas: " + (int) caloriasObjetivo);
        });
    }

    // Fórmula Harris-Benedict
    private double calcularTMB(double peso, double altura, int edad, String genero) {
        if (genero.equals("Masculino")) {
            return 88.362 + (13.397 * peso) + (4.799 * altura) - (5.677 * edad);
        } else {
            return 447.593 + (9.247 * peso) + (3.098 * altura) - (4.330 * edad);
        }
    }

    // Nivel de actividad
    private double obtenerNivelActividad(String actividad) {
        switch (actividad) {
            case "Sedentario":
                return 1.2;
            case "Ligera actividad":
                return 1.375;
            case "Actividad moderada":
                return 1.55;
            case "Actividad intensa":
                return 1.725;
            default:
                return 1.0;
        }
    }
}
