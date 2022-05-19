package es.upm.roombasic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import es.upm.roombasic.utils.CifradoCesar;

public class NuevoUsuarioActivity extends AppCompatActivity {

    static String LOG_TAG = "UPM";
    public static final String PARAM_NOMBRE = "es.upm.roombasic.nombre";
    public static final String PARAM_PASSWORD = "es.upm.roombasic.password";
    public static final String PARAM_ROL = "es.upm.roombasic.rol";

    private EditText etName, etPass;
    private RatingBar rbRol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        Intent myIntent = getIntent();
        String s1 = myIntent.getStringExtra("clave1");
        String s2= myIntent.getStringExtra("clave2");
        Log.i(LOG_TAG, "params=" + s1 + " | "+ s2);


        etName = findViewById(R.id.etNombre);
        rbRol = findViewById(R.id.ratingBar);
        etPass = findViewById(R.id.etPassword);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(etName.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    CifradoCesar cc = new CifradoCesar();
                    String sNombre = etName.getText().toString();
                    String sPass = etPass.getText().toString();
                    float fRol = rbRol.getRating();
                    replyIntent.putExtra(PARAM_NOMBRE, sNombre);
                    replyIntent.putExtra(PARAM_PASSWORD, cc.cifrar(sPass, CifradoCesar.CAESARCODE_ROT));
                    replyIntent.putExtra(PARAM_ROL, fRol);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

}
