package mx.edu.ittepic.sergiobenigno.tpdm_u3_ejercicio1;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    EditText nombre, domicilio, telefono;
    Button insertar, eliminar, consultar;
    ListView listado;
    String con;
    FirebaseFirestore servicioBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nombre = findViewById(R.id.editText);
        domicilio = findViewById(R.id.editText2);
        telefono= findViewById(R.id.editText3);

        insertar = findViewById(R.id.button);
        eliminar= findViewById(R.id.button2);
        consultar= findViewById(R.id.button3);

        listado = findViewById(R.id.listado);
        con = "";
        servicioBaseDatos = FirebaseFirestore.getInstance();

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insertarAlumnoAutoID();
                insertarAlumnoTelefonoID();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarAlumno();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consultarTodos();
                consultarPorTelefono();
            }
        });
    }

    private void consultarPorTelefono(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText porTelefono = new EditText(this);
        porTelefono.setInputType(InputType.TYPE_CLASS_PHONE);
        porTelefono.setHint("SIN O CON GUIONES");

        alerta.setTitle("BUSQUEDA").setMessage("ESCRIBA EL NUM TELEFONICO")
                  .setView(porTelefono)
                  .setPositiveButton("buscar", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                            if(porTelefono.getText().toString().isEmpty()){
                                Toast.makeText(Main2Activity.this,
                                          "DEBES PONER UN TELEFON", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            consultarPorTelefono(porTelefono.getText().toString());
                      }
                  })
                  .setNegativeButton("cancelar",null)
                  .show();
    }

    private void consultarPorTelefono(String telefonoABuscar){
        servicioBaseDatos.collection("alumnos")
                  .whereEqualTo("telefono",telefonoABuscar)
                  .addSnapshotListener(new EventListener<QuerySnapshot>() {
                      @Override
                      public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                          @Nullable FirebaseFirestoreException e) {
                          Query q = queryDocumentSnapshots.getQuery();

                          q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                              @Override
                              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                  if(task.isSuccessful()){
                                      for (QueryDocumentSnapshot registro : task.getResult()){
                                          Map<String, Object> dato = registro.getData();

                                          nombre.setText(dato.get("nombre").toString());
                                          domicilio.setText(dato.get("domicilio").toString());
                                          telefono.setText(dato.get("telefono").toString());
                                      }
                                  }
                                  //hacer else de tarea
                              }
                          });

                      }
                  });
    }

    private void consultarTodos(){
        servicioBaseDatos.collection("alumnos")
                  .get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          con = "";
                          if(task.isSuccessful()){
                              for(QueryDocumentSnapshot registro : task.getResult()){
                                  Map<String, Object> datos = registro.getData();

                                  con+=" -- "+datos.get("nombre").toString();

                              }
                          } else {
                              Toast.makeText(Main2Activity.this,
                                        "NO DATOS", Toast.LENGTH_SHORT).show();
                          }
                          Toast.makeText(Main2Activity.this,
                                    ""+con, Toast.LENGTH_SHORT).show();
                      }
                  });
    }

    private void eliminarAlumno(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText idEliminar = new EditText(this);
        idEliminar.setHint("NO DEBE QUEDAR VACIO");

        alerta.setTitle("ATENCION").setMessage("ESCRIBA EL ID:")
                  .setView(idEliminar)
                  .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          if(idEliminar.getText().toString().isEmpty()){
                              Toast.makeText(Main2Activity.this, "EL ID ESTA VACIO",
                                        Toast.LENGTH_SHORT).show();
                              return;
                          }
                          eliminarAlumnos2(idEliminar.getText().toString());
                      }
                  })
                  .setNegativeButton("Cancelar",null)
                  .show();
    }

    private void eliminarAlumnos2(String idEliminar){
        servicioBaseDatos.collection("alumnos")
                  .document(idEliminar)
                  .delete()
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          Toast.makeText(Main2Activity.this,
                                    "SE ELIMINO!", Toast.LENGTH_SHORT).show();
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(Main2Activity.this,
                                    "NO SE ENCONTRO COINCIDENCIA!",
                                    Toast.LENGTH_SHORT).show();
                      }
                  });
    }


    private void insertarAlumnoAutoID(){
        Alumno alu = new Alumno(nombre.getText().toString(),
                  domicilio.getText().toString(), telefono.getText().toString());

        servicioBaseDatos.collection("alumnos")
                  .add(alu)
                  .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                      @Override
                      public void onSuccess(DocumentReference documentReference) {
                          Toast.makeText(Main2Activity.this, "SE INSERTO",
                                    Toast.LENGTH_SHORT).show();
                          nombre.setText("");domicilio.setText("");telefono.setText("");
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(Main2Activity.this, "ERROR! NO INSERTO",
                                    Toast.LENGTH_SHORT).show();
                      }
                  });
    }
    private void insertarAlumnoTelefonoID(){
        Alumno alu = new Alumno(nombre.getText().toString(), domicilio.getText().toString(),
                  telefono.getText().toString());

        servicioBaseDatos.collection("alumnos")
                  .document(telefono.getText().toString())
                  .set(alu)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          Toast.makeText(Main2Activity.this, "SE INSERTO CORRECTAMENTE",
                                    Toast.LENGTH_SHORT).show();
                          nombre.setText("");domicilio.setText("");telefono.setText("");
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(Main2Activity.this,
                                    "ERROR NO SE PUDO INSERTAR", Toast.LENGTH_SHORT).show();
                      }
                  });

    }
}
