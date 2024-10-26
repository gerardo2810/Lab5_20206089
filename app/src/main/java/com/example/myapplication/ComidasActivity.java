package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class ComidasActivity extends AppCompatActivity {

    private double caloriasConsumidas = 0.0;
    private double caloriasRecomendadas = 0.0;
    private double caloriasQuemadas = 0.0;

    private TextView caloriasRestantesTextView;
    private TextView caloriasRecomendadasTextView;
    private TextView caloriasConsumidasTextView;
    private TextView caloriasQuemadasTextView;

    private Map<String, Integer> alimentosCatalogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        // Obtener el valor de calorías recomendadas del intent
        caloriasRecomendadas = getIntent().getIntExtra("CALORIAS_RECOMENDADAS", 0);

        // Inicializar vistas
        caloriasRecomendadasTextView = findViewById(R.id.tv_calorias_recomendadas);
        caloriasRestantesTextView = findViewById(R.id.tv_calorias_restantes);
        caloriasConsumidasTextView = findViewById(R.id.tv_calorias_consumidas);
        caloriasQuemadasTextView = findViewById(R.id.tv_calorias_quemadas);

        // Mostrar calorías recomendadas al iniciar la actividad
        caloriasRecomendadasTextView.setText("Calorías recomendadas: " + (int) caloriasRecomendadas);
        actualizarCaloriasRestantes();

        // Inicialización de vistas adicionales
        EditText nombreComidaEditText = findViewById(R.id.et_nombre_comida);
        EditText caloriasComidaEditText = findViewById(R.id.et_calorias_comida);
        Button añadirComidaButton = findViewById(R.id.btn_añadir_comida);
        Spinner spinnerAlimentos = findViewById(R.id.spinner_alimentos);
        // Llama al método para configurar los recordatorios
        configurarRecordatoriosDiarios();
        alimentosCatalogo = new HashMap<>();
        alimentosCatalogo.put("Manzana", 95);
        alimentosCatalogo.put("Plátano", 105);
        alimentosCatalogo.put("Pan Integral", 75);
        alimentosCatalogo.put("Pollo a la plancha", 165);
        alimentosCatalogo.put("Arroz blanco", 206);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alimentosCatalogo.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlimentos.setAdapter(adapter);

        spinnerAlimentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String alimentoSeleccionado = parentView.getItemAtPosition(position).toString();
                nombreComidaEditText.setText(alimentoSeleccionado);
                caloriasComidaEditText.setText(String.valueOf(alimentosCatalogo.get(alimentoSeleccionado)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hacer nada si no se selecciona un alimento
            }
        });

        añadirComidaButton.setOnClickListener(v -> {
            String nombreComida = nombreComidaEditText.getText().toString();
            String caloriasComidaString = caloriasComidaEditText.getText().toString();

            if (!caloriasComidaString.isEmpty()) {
                double caloriasComida = Double.parseDouble(caloriasComidaString);

                caloriasConsumidas += caloriasComida;
                caloriasConsumidasTextView.setText("Calorías consumidas hoy: " + (int) caloriasConsumidas);
                actualizarCaloriasRestantes();

                if (caloriasConsumidas > caloriasRecomendadas) {
                    mostrarNotificacionExcesoCalorias();
                }

                nombreComidaEditText.setText("");
                caloriasComidaEditText.setText("");
            }
        });

        Button añadirActividadButton = findViewById(R.id.btn_añadir_actividad);
        EditText actividadFisicaEditText = findViewById(R.id.et_actividad_fisica);
        EditText caloriasQuemadasEditText = findViewById(R.id.et_calorias_quemadas);

        añadirActividadButton.setOnClickListener(v -> {
            String caloriasQuemadasString = caloriasQuemadasEditText.getText().toString();

            if (!caloriasQuemadasString.isEmpty()) {
                double caloriasQuemadasActividad = Double.parseDouble(caloriasQuemadasString);

                caloriasQuemadas += caloriasQuemadasActividad;
                caloriasQuemadasTextView.setText("Calorías quemadas: " + (int) caloriasQuemadas);

                caloriasRecomendadas += caloriasQuemadasActividad;
                actualizarCaloriasRestantes();

                actividadFisicaEditText.setText("");
                caloriasQuemadasEditText.setText("");
            }
        });
    }

    private void mostrarNotificacionExcesoCalorias() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal de Exceso de Calorías";
            String descripcion = "Notificación de exceso de calorías";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("EXCESO_CALORIAS", nombre, importancia);
            channel.setDescription(descripcion);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, ComidasActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EXCESO_CALORIAS")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle("¡Has excedido las calorías recomendadas!")
                .setContentText("Considera hacer ejercicio o reducir calorías en la próxima comida.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, builder.build());
    }

    private void actualizarCaloriasRestantes() {
        double caloriasRestantes = caloriasRecomendadas - caloriasConsumidas;

        if (caloriasRestantes > 0) {
            caloriasRestantesTextView.setText("Te faltan " + (int) caloriasRestantes + " calorías para alcanzar tu meta diaria.");
        } else {
            caloriasRestantesTextView.setText("¡Has excedido tu meta diaria por " + Math.abs((int) caloriasRestantes) + " calorías!");
        }
    }
    private void configurarRecordatoriosDiarios() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Configuración de recordatorio para desayuno (8:00 AM)
        Calendar desayuno = Calendar.getInstance();
        desayuno.set(Calendar.HOUR_OF_DAY, 8);
        desayuno.set(Calendar.MINUTE, 0);
        desayuno.set(Calendar.SECOND, 0);
        programarAlarma(alarmManager, desayuno, "Es hora de registrar tu desayuno", 1001);

        // Configuración de recordatorio para almuerzo (12:00 PM)
        Calendar almuerzo = Calendar.getInstance();
        almuerzo.set(Calendar.HOUR_OF_DAY, 12);
        almuerzo.set(Calendar.MINUTE, 0);
        almuerzo.set(Calendar.SECOND, 0);
        programarAlarma(alarmManager, almuerzo, "Es hora de registrar tu almuerzo", 1002);

        // Configuración de recordatorio para cena (7:00 PM)
        Calendar cena = Calendar.getInstance();
        cena.set(Calendar.HOUR_OF_DAY, 19);
        cena.set(Calendar.MINUTE, 0);
        cena.set(Calendar.SECOND, 0);
        programarAlarma(alarmManager, cena, "Es hora de registrar tu cena", 1003);
    }

    private void programarAlarma(AlarmManager alarmManager, Calendar hora, String mensaje, int requestCode) {
        Intent intent = new Intent(this, RecordatorioReceiver.class);
        intent.putExtra("mensaje", mensaje);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Configurar la alarma para que se repita todos los días a la hora indicada
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, hora.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
