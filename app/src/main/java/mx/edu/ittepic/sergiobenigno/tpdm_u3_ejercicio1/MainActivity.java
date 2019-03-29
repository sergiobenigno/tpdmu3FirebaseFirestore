package mx.edu.ittepic.sergiobenigno.tpdm_u3_ejercicio1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button autenticar, crear;
    EditText login, password;
    FirebaseAuth servicioAutenticacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticar = findViewById(R.id.autenticar);
        crear = findViewById(R.id.crear);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);

        servicioAutenticacion = FirebaseAuth.getInstance();

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servicioAutenticacion.createUserWithEmailAndPassword(login.getText().toString(),
                          password.getText().toString())
                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task) {
                                  if(task.isSuccessful()){
                                      //SI SE CREO!!!!
                                      Toast.makeText(MainActivity.this, "EXITO! se creo!", Toast.LENGTH_SHORT)
                                                .show();
                                      login.setText("");password.setText("");
                                      servicioAutenticacion.getCurrentUser().sendEmailVerification();
                                  } else {
                                      //NO SE CREO!!!
                                      Toast.makeText(MainActivity.this, "ERROR! no se pudo crear",
                                                Toast.LENGTH_SHORT).show();
                                  }
                              }
                          }); //Se cierra todo
            }
        });

        autenticar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                servicioAutenticacion.signInWithEmailAndPassword(login.getText().toString(),
                          password.getText().toString())
                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser usuario = servicioAutenticacion.getCurrentUser();

                                    /*if(!usuario.isEmailVerified()){
                                        Toast.makeText(MainActivity.this, "DEBES VERIFICAR",
                                                  Toast.LENGTH_SHORT).show();
                                        usuario.sendEmailVerification();
                                        servicioAutenticacion.signOut();
                                        return;
                                    }*/

                                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                                    finish();

                                } else {
                                    Toast.makeText(MainActivity.this,
                                              "ERROR EN USUARIO y/o CONTRASEÑA", Toast.LENGTH_SHORT).show();
                                }
                              }
                          });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            FirebaseUser usuario = servicioAutenticacion.getCurrentUser();
            setTitle(usuario.getEmail());
            startActivity(new Intent(MainActivity.this, Main2Activity.class));
            finish();
        }catch (Exception e){

        }
    }
}
