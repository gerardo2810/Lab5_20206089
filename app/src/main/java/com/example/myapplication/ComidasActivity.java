package com.example.myapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ComidasActivity extends AppCompatActivity {

    private double caloriasConsumidas = 0.0; // Variable para acumular las calorías
    private double caloriasRecomendadas = 2000; // Puedes cambiar esto según el cálculo real
    private TextView caloriasRestantesTextView;  // Nueva vista para mostrar calorías restantes
    private Map<String, Integer> alimentosCatalogo;  // Catálogo de alimentos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        // Verificar permisos de notificaciones
        checkNotificationPermission();

        // Configurar recordatorios personalizados (desayuno, almuerzo, cena)
        configurarRecordatorios();

        // Inicialización de vistas
        EditText nombreComidaEditText = findViewById(R.id.et_nombre_comida);
        EditText caloriasComidaEditText = findViewById(R.id.et_calorias_comida);
        Button añadirComidaButton = findViewById(R.id.btn_añadir_comida);
        caloriasRestantesTextView = findViewById(R.id.tv_calorias_restantes);  // TextView para calorías restantes
        Spinner spinnerAlimentos = findViewById(R.id.spinner_alimentos);  // Spinner para alimentos

        // Inicializar catálogo de alimentos comunes
        alimentosCatalogo = new HashMap<>();
        alimentosCatalogo.put("Manzana", 95);
        alimentosCatalogo.put("Plátano", 105);
        alimentosCatalogo.put("Pan Integral", 75);
        alimentosCatalogo.put("Pollo a la plancha", 165);
        alimentosCatalogo.put("Arroz blanco", 206);

        // Configurar el Spinner de alimentos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alimentosCatalogo.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlimentos.setAdapter(adapter);

        // Evento al seleccionar un alimento del Spinner
        spinnerAlimentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String alimentoSeleccionado = parentView.getItemAtPosition(position).toString();
                // Establecer el valor calórico del alimento en el campo de calorías
                caloriasComidaEditText.setText(String.valueOf(alimentosCatalogo.get(alimentoSeleccionado)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hacer nada si no se selecciona un alimento
            }
        });

        // Configurar el botón para añadir comida
        añadirComidaButton.setOnClickListener(v -> {
            String nombreComida = nombreComidaEditText.getText().toString();
            String caloriasComidaString = caloriasComidaEditText.getText().toString();

            if (!caloriasComidaString.isEmpty()) {
                double caloriasComida = Double.parseDouble(caloriasComidaString);

                // Aumentar el total de calorías consumidas
                caloriasConsumidas += caloriasComida;
                actualizarCaloriasRestantes();

                // Verificar si se exceden las calorías recomendadas
                if (caloriasConsumidas > caloriasRecomendadas) {
                    mostrarNotificacionExcesoCalorias();
                }

                // Limpiar los campos después de añadir la comida
                nombreComidaEditText.setText("");
                caloriasComidaEditText.setText("");
            }
        });

        // Configurar el botón para añadir actividad física
        Button añadirActividadButton = findViewById(R.id.btn_añadir_actividad);
        EditText actividadFisicaEditText = findViewById(R.id.et_actividad_fisica);
        EditText caloriasQuemadasEditText = findViewById(R.id.et_calorias_quemadas);

        añadirActividadButton.setOnClickListener(v -> {
            String caloriasQuemadasString = caloriasQuemadasEditText.getText().toString();

            if (!caloriasQuemadasString.isEmpty()) {
                double caloriasQuemadas = Double.parseDouble(caloriasQuemadasString);

                // Restar calorías quemadas de la actividad física del total de calorías consumidas
                caloriasConsumidas -= caloriasQuemadas;
                actualizarCaloriasRestantes();

                // Limpiar los campos después de añadir la actividad
                actividadFisicaEditText.setText("");
                caloriasQuemadasEditText.setText("");
            }
        });
    }

    // Método para mostrar notificación cuando se exceden las calorías
    private void mostrarNotificacionExcesoCalorias() {
        // Crear el canal de notificación para Android 8.0 y versiones superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal de Exceso de Calorías";
            String descripcion = "Notificación de exceso de calorías";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("EXCESO_CALORIAS", nombre, importancia);
            channel.setDescription(descripcion);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent para abrir la actividad cuando se toque la notificación
        Intent intent = new Intent(this, ComidasActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EXCESO_CALORIAS")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle("¡Has excedido las calorías recomendadas!")
                .setContentText("Considera hacer ejercicio o reducir calorías en la próxima comida.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, builder.build());
    }

    // Método para actualizar las calorías restantes
    private void actualizarCaloriasRestantes() {
        double caloriasRestantes = caloriasRecomendadas - caloriasConsumidas;

        if (caloriasRestantes > 0) {
            caloriasRestantesTextView.setText("Te faltan " + (int) caloriasRestantes + " calorías para alcanzar tu meta diaria.");
        } else {
            caloriasRestantesTextView.setText("¡Has excedido tu meta diaria por " + Math.abs((int) caloriasRestantes) + " calorías!");
        }
    }

    // Método para verificar permisos de notificaciones
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1002);
            }
        }
    }

    // Configurar recordatorios para desayuno, almuerzo y cena
    private void configurarRecordatorios() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Recordatorio para desayuno (8:00 AM)
        Calendar desayuno = Calendar.getInstance();
        desayuno.set(Calendar.HOUR_OF_DAY, 8);
        desayuno.set(Calendar.MINUTE, 0);
        desayuno.set(Calendar.SECOND, 0);

        Intent desayunoIntent = new Intent(this, RecordatorioReceiver.class);
        desayunoIntent.putExtra("mensaje", "Recuerda registrar tu desayuno");
        PendingIntent desayunoPendingIntent = PendingIntent.getBroadcast(this, 1, desayunoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, desayuno.getTimeInMillis(), desayunoPendingIntent);

        // Recordatorio para almuerzo (12:00 PM)
        Calendar almuerzo = Calendar.getInstance();
        almuerzo.set(Calendar.HOUR_OF_DAY, 12);
        almuerzo.set(Calendar.MINUTE, 0);
        almuerzo.set(Calendar.SECOND, 0);

        Intent almuerzoIntent = new Intent(this, RecordatorioReceiver.class);
        almuerzoIntent.putExtra("mensaje", "Recuerda registrar tu almuerzo");
        PendingIntent almuerzoPendingIntent = PendingIntent.getBroadcast(this, 2, almuerzoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, almuerzo.getTimeInMillis(), almuerzoPendingIntent);

        // Recordatorio para cena (7:00 PM)
        Calendar cena = Calendar.getInstance();
        cena.set(Calendar.HOUR_OF_DAY, 19);
        cena.set(Calendar.MINUTE, 0);
        cena.set(Calendar.SECOND, 0);

        Intent cenaIntent = new Intent(this, RecordatorioReceiver.class);
        cenaIntent.putExtra("mensaje", "Recuerda registrar tu cena");
        PendingIntent cenaPendingIntent = PendingIntent.getBroadcast(this, 3, cenaIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cena.getTimeInMillis(), cenaPendingIntent);
    }

    // Configurar intervalo para la notificación de motivación
    public void programarNotificacionMotivacional(int intervaloEnMinutos) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long intervaloEnMilisegundos = intervaloEnMinutos * 60 * 1000; // Convertir minutos a milisegundos
        Intent intent = new Intent(this, MotivacionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervaloEnMilisegundos, pendingIntent);
    }
}
