package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String emailUsuario;
    ListView listViewTareas;
    List<String> listaTareas = new ArrayList<>();
    List<String> listaIdTareas = new ArrayList<>();
    ArrayAdapter<String> mAdapterTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        emailUsuario = mAuth.getCurrentUser().getEmail();
        listViewTareas = findViewById(R.id.ListView);

        // update UI
        actualizarUI();

    }

    private void actualizarUI() {
        db.collection("Tareas")
                .whereEqualTo("emailUsuario", emailUsuario)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,@Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        listaTareas.clear();
                        listaIdTareas.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            listaIdTareas.add(doc.getId());
                            listaTareas.add(doc.getString("nombreTarea"));
                        }

                        if (listaTareas.size() == 0) {
                            listViewTareas.setAdapter(null);
                        } else {
                            mAdapterTareas = new ArrayAdapter<>(MainActivity.this, R.layout.item_tarea, R.id.nombreTarea, listaTareas);
                            listViewTareas.setAdapter(mAdapterTareas);
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                //activar cuadro diálogo para añadir

                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Nueva Tarea")
                        .setMessage("¿Que quiere hacer a continuación?")
                        .setView(taskEditText)
                        .setPositiveButton("añadir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Añadir tarea a la base de datos

                                String miTarea = taskEditText.getText().toString();

                                Map<String, Object> tarea = new HashMap<>();
                                tarea.put("nombreTarea", miTarea);
                                tarea.put("emailUsuario", emailUsuario);

                                db.collection("Tareas").add(tarea);

                                Toast toast = new Toast(getApplicationContext());
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.lytLayout));
                                TextView txtMsg = (TextView) layout.findViewById(R.id.txtMensaje);
                                txtMsg.setText("Tarea añadida");
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();
                return true;

            case R.id.logout:
                //cierre de sesión
                mAuth.signOut();
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void borrarTarea(View view) {
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.nombreTarea);
        String tarea = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tarea);

        db.collection("Tareas").document(listaIdTareas.get(posicion)).delete();
        Toast toast = new Toast(getApplicationContext());
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.lytLayout));
        TextView txtMsg = (TextView) layout.findViewById(R.id.txtMensaje);
        txtMsg.setText("Tarea eliminada");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void editarTarea(View view) {
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.nombreTarea);
        String tarea = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tarea);
        final EditText taskEditText = new EditText(this);
        taskEditText.setText(tarea);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Editar Tarea")
                .setMessage("¿Que quiere hacer a continuación?")
                .setView(taskEditText)
                .setPositiveButton("editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String miTareaEditada = taskEditText.getText().toString();

                        db.collection("Tareas").document(listaIdTareas.get(posicion)).update("nombreTarea",miTareaEditada);

                        Toast toast = new Toast(getApplicationContext());
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.lytLayout));
                        TextView txtMsg = (TextView) layout.findViewById(R.id.txtMensaje);
                        txtMsg.setText("Tarea editada");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }
}