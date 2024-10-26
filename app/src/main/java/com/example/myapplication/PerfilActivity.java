package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {
    private int caloriasObjetivo; // Variable para almacenar las calorías recomendadas
    private boolean caloriasCalculadas = false; // Variable para indicar si se ha calculado

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

        EditText pesoEditText = findViewById(R.id.et_peso);
        EditText alturaEditText = findViewById(R.id.et_altura);
        EditText edadEditText = findViewById(R.id.et_edad);
        Spinner generoSpinner = findViewById(R.id.spinner_genero);
        Spinner actividadSpinner = findViewById(R.id.spinner_actividad);
        Spinner objetivoSpinner = findViewById(R.id.spinner_objetivo);
        Button btnIrCalorias = findViewById(R.id.btn_ir_calorias); // Nuevo botón
        Button calcularButton = findViewById(R.id.btn_calcular);
        TextView caloriasTextView = findViewById(R.id.tv_calorias_recomendadas);
        EditText intervaloNotificacionEditText = findViewById(R.id.et_intervalo_notificacion); // Nuevo EditText
        Button activarNotificacionButton = findViewById(R.id.btn_activar_notificacion); // Nuevo botón

        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(this,
                R.array.genero_options, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generoSpinner.setAdapter(generoAdapter);

        ArrayAdapter<CharSequence> actividadAdapter = ArrayAdapter.createFromResource(this,
                R.array.actividad_options, android.R.layout.simple_spinner_item);
        actividadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actividadSpinner.setAdapter(actividadAdapter);

        ArrayAdapter<CharSequence> objetivoAdapter = ArrayAdapter.createFromResource(this,
                R.array.objetivo_options, android.R.layout.simple_spinner_item);
        objetivoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objetivoSpinner.setAdapter(objetivoAdapter);

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

            caloriasObjetivo = (int) caloriasDiarias;
            switch (objetivo) {
                case "Subir de peso":
                    caloriasObjetivo += 500;
                    break;
                case "Bajar de peso":
                    caloriasObjetivo -= 300;
                    break;
            }

            caloriasTextView.setText("Calorías recomendadas: " + caloriasObjetivo);
            caloriasCalculadas = true;
        });

        btnIrCalorias.setOnClickListener(v -> {
            if (caloriasCalculadas) {
                Intent intent = new Intent(PerfilActivity.this, ComidasActivity.class);
                intent.putExtra("CALORIAS_RECOMENDADAS", caloriasObjetivo);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Primero calcula las calorías recomendadas.", Toast.LENGTH_SHORT).show();
            }
        });

        activarNotificacionButton.setOnClickListener(v -> {
            String intervaloStr = intervaloNotificacionEditText.getText().toString();
            if (!intervaloStr.isEmpty()) {
                int intervaloMinutos = Integer.parseInt(intervaloStr);
                programarNotificacionMotivacional(intervaloMinutos);
                Toast.makeText(this, "Notificación de motivación activada cada " + intervaloMinutos + " minutos.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor, ingresa un intervalo en minutos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void programarNotificacionMotivacional(int intervaloEnMinutos) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MotivacionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long intervaloEnMilisegundos = intervaloEnMinutos * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervaloEnMilisegundos, pendingIntent);
    }

    private double calcularTMB(double peso, double altura, int edad, String genero) {
        if (genero.equals("Masculino")) {
            return 88.362 + (13.397 * peso) + (4.799 * altura) - (5.677 * edad);
        } else {
            return 447.593 + (9.247 * peso) + (3.098 * altura) - (4.330 * edad);
        }
    }

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
