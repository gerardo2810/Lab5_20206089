package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MotivacionReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MOTIVACION";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Verificar permiso de notificación
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            mostrarNotificacion(context);
        } else {
            System.out.println("Permiso de notificación no otorgado.");
        }
    }

    private void mostrarNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal de Motivación";
            String descripcion = "Recordatorio de motivación para mantener el objetivo";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            channel.setDescription(descripcion);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24) // Cambia al icono que uses
                .setContentTitle("¡Sigue adelante!")
                .setContentText("Estás cerca de lograr tu objetivo. ¡No te rindas!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        try {
            notificationManager.notify(3001, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
            System.out.println("Error: No se pudieron enviar las notificaciones debido a la falta de permisos.");
        }
    }
}
