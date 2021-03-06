package com.santiago.ws_colorapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.santiago.ws_colorapp.R;
import com.santiago.ws_colorapp.datos.DatosBD;
import com.santiago.ws_colorapp.modelo.Juego;

import java.util.Locale;
import java.util.Random;

public class JuegoActivity extends AppCompatActivity {
    Button pause;
    TextView tiempo,movimientos,reaccion,palabra;
    FloatingActionButton fab1,fab2,fab3,fab4;
    long tiempoTotal=30000,tiempoPalabra=3000,tiempo_restante;
    int mil=1000,incorrectas=0,correctas=0,totalPalabaras=0,posicionPalabra;
    String colores[]={"AMARILLO","ROJO","AZUL","VERDE"};
    Random random= new Random();
    boolean seleccion=false;
    float porReaccion;
    boolean estado=true;
    int count=0;
    private CountDownTimer contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        inicializar();
        play();
        reaccion.setText("0%");

    }


    private void iniciarJuego() {
        contador = new CountDownTimer(tiempoTotal,mil) {
            @Override
            public void onTick(long l) {
                int time= (int) (l/mil);
                tiempoTotal=l;
                actualizarTextoTiempo();
                tiempo.setText(time +"''");
                if ((JuegoActivity.this.isFinishing())){
                    finish();
                    cancel();
                }else if (incorrectas==3) {
                    tiempo_restante=l/1000;
                    totalPalabaras = correctas + incorrectas;
                    onFinish();
                    cancel();

                }
                if (count ==4){
                    pause.setBackgroundResource(R.drawable.play_desactivado);
                    pause.setEnabled(false);
                }

            }

            @Override
            public void onFinish() {

                tiempo.setText("0''");
                DatosBD bd=new DatosBD(JuegoActivity.this);
                Juego juego= new Juego();
                juego.setReaccion(porReaccion);
                if (bd.guardarPuntaje(juego)){
                    Toast.makeText(JuegoActivity.this, "guardo", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(JuegoActivity.this, "no guardo", Toast.LENGTH_SHORT).show();
                }

                AlertDialog.Builder builder= new AlertDialog.Builder(JuegoActivity.this);
                builder.setTitle("Resultados");
                builder.setMessage("Total de palabras: "+ totalPalabaras+"\n"+
                        "Correctas: "+correctas+"\n"+
                        "Incorrectas: "+incorrectas+"\n"+
                        "Reaccion: "+reaccion.getText().toString()+"\n"+
                        "Tiempo restante: "+tiempo_restante+"''");
                builder.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT,"RESULTADOS: \n"+
                                "Total de palabras: "+ totalPalabaras+"\n"+
                                "Correctas: "+correctas+"\n"+
                                "Incorrectas: "+incorrectas+"\n"+
                                "Reaccion: "+reaccion.getText().toString()+"\n"+
                                "Tiempo restante: "+tiempo_restante+"''");
                        intent.setType("text/plain");
                        startActivity(intent);
                        onBackPressed();
                    }
                });
                builder.setNegativeButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).setCancelable(false);
                AlertDialog dialog=builder.create();
                dialog.show();

            }
        }.start();

    }
    private void cambiarPalabra() {
        //habilitamos los botones
        habilitarBotones();
        //aumentamos en una la camtidad de palabras
        totalPalabaras++;
        //le determinamos a movimientos el valor de total_palabras
        movimientos.setText(totalPalabaras+"");
        // generamos un random para las palabras
        posicionPalabra=random.nextInt(4);
        //determinamos una de las palabras del array dependiendo del random dado
        palabra.setText(colores[posicionPalabra]);

        //generamos en contador de tiempo para las palabras
        new CountDownTimer(tiempoPalabra,mil) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                //definimos que pasaria si no se acaba el tiempo y no se selecciona un boton
                if (!tiempo.getText().toString().equalsIgnoreCase("0''")){
                    if (seleccion==false){
                        porReaccion=((float) correctas/(float) totalPalabaras)*100;
                        reaccion.setText((int) porReaccion+"%");
                        incorrectas++;
                    }

                    //llamamos de nuevo los metodos para cambiarColorPalabra, cambiarPalabra, cambiarColorBoton
                    cambiarColorPalabra();
                    cambiarPalabra();
                    cambiarColorBoton();
                }
            }
        }.start();

    }

    //se crean variables enteras que representan el color de cada boton
    int bColor1=0,bColor2=0,bColor3=0,bColor4=0;
    private void cambiarColorBoton() {
        //pasamos seleccion a ser falsa
        seleccion=false;
        //creamos unas variables booleanas que representan a cada boton y se inicializar en falso
        boolean btn1=false,btn2=false,btn3=false,btn4=false;
        //se crean variables entera que representan los 4 colores
        int color1=0,color2=0,color3=0,color4=0;
        //mientra uno de los botones este en falso
        while (btn1==false || btn2==false || btn3==false || btn4==false){
            //se crea una variable entera que guarda un numero entero aleatorio
            int azar=random.nextInt(4);
            //se hace un switch dependiendo del numero
            switch (azar){
                //en case de 0
                case 0:
                    //si el boton 1 esta falso
                    if (btn1==false){
                        //si alguno de los colores esta en 0
                        if (color1==0){
                            //al boton f_1 se le da el color correspondiente
                            fab1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b7b716")));
                            //al color del boton se le da un valor dependiendo de la posicion
                            bColor1=0;
                            //la variable del color se cambia por 1
                            color1=1;
                            //a la variable que representa el color de le asigna true
                            btn1=true;
                            //se finailza en caso
                            break;
                        }
                        if (color2==0){
                            fab1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0a7300")));
                            bColor1=1;
                            color2=1;
                            btn1=true;
                            break;
                        }
                        if (color3==0){
                            fab1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#090f89")));
                            bColor1=2;
                            color3=1;
                            btn1=true;
                            break;
                        }
                        if (color4==0){
                            fab1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#c71f1e")));
                            bColor1=3;
                            color4=1;
                            btn1=true;
                            break;
                        }
                    }
                case 1:
                    if (btn2==false){
                        if (color1==0){
                            fab2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b7b716")));
                            bColor2=0;
                            color1=1;
                            btn2=true;
                            break;
                        }
                        if (color2==0){
                            fab2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0a7300")));
                            bColor2=1;
                            color2=1;
                            btn2=true;
                            break;
                        }
                        if (color3==0){
                            fab2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#090f89")));
                            bColor2=2;
                            color3=1;
                            btn2=true;
                            break;
                        }
                        if (color4==0){
                            fab2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#c71f1e")));
                            bColor2=3;
                            color4=1;
                            btn2=true;
                            break;
                        }
                    }
                case 2:
                    if (btn3==false){
                        if (color1==0){
                            fab3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b7b716")));
                            bColor3=0;
                            color1=1;
                            btn3=true;
                            break;
                        }
                        if (color2==0){
                            fab3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0a7300")));
                            bColor3=1;
                            color2=1;
                            btn3=true;
                            break;
                        }
                        if (color3==0){
                            fab3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#090f89")));
                            bColor3=2;
                            color3=1;
                            btn3=true;
                            break;
                        }
                        if (color4==0){
                            fab3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#c71f1e")));
                            bColor3=3;
                            color4=1;
                            btn3=true;
                            break;
                        }
                    }
                case 3:
                    if (btn4==false){
                        if (color1==0){
                            fab4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b7b716")));
                            bColor4=0;
                            color1=1;
                            btn4=true;
                            break;
                        }
                        if (color2==0){
                            fab4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0a7300")));
                            bColor4=1;
                            color2=1;
                            btn4=true;
                            break;
                        }
                        if (color3==0){
                            fab4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#090f89")));
                            bColor4=2;
                            color3=1;
                            btn4=true;
                            break;
                        }
                        if (color4==0){
                            fab4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#c71f1e")));
                            bColor4=3;
                            color4=1;
                            btn4=true;
                            break;
                        }
                    }


            }
        }
    }

    //creo una variable entera
    int color=0;
    private void cambiarColorPalabra() {
        //genero un numero aleatio en la variable creada
        color=random.nextInt(4);
        //hago un switch dependiendo del numero aleatorio
        switch (color){
            //dependiendo del caso se determina un color al texto
            case 0: palabra.setTextColor(Color.parseColor("#b7b716"));
                break;
            case 1: palabra.setTextColor(Color.parseColor("#0a7300"));
                break;
            case 2: palabra.setTextColor(Color.parseColor("#090f89"));
                break;
            case 3: palabra.setTextColor(Color.parseColor("#c71f1e"));
                break;
        }
//        new CountDownTimer(tiempoPalabra,mil) {
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                if (!tiempo.getText().toString().equalsIgnoreCase("0''")){
//                    cambiarColorPalabra();
//                }
//
//            }
//        }.start();

    }




    public void pausarJuego(View view) {
        //aumenta el count en uno
        count++;
        //dependiendo de si el estado es uno verdadero ejecute un metodo
        if (estado){
            //llama a play
            play();
        }else {
            //llamar a pause
            pausar();
            //se desabilita  la palabra y los botones
            palabra.setEnabled(false);
            desabilitarBotones();
        }
    }
    private void play() {
        iniciarJuego();
        cambiarColorPalabra();
        cambiarColorBoton();
        cambiarPalabra();
        estado=false;
        pause.setBackgroundResource(R.drawable.pause);

    }
    private void pausar() {
        //mermamos en una el totalPalabras
        totalPalabaras--;
        //el tiempo lo ponemos en 0
        tiempo.setText("0''");
        //cancelamos el contador
        contador.cancel();
        //cambiarmos el estado a true
        estado=true;
        //cambiamos el backgroup del boton
        pause.setBackgroundResource(R.drawable.play);
    }
    private void actualizarTextoTiempo() {
        //creo una variable entera
        int t= (int) (tiempoTotal/mil);
        //creo una variable string y le determino en formaato para mostrar el valor
        String tt= String.format( Locale.getDefault(),"%02d", t);
        //ese valor se lo paso al tiempo
        tiempo.setText(tt);

    }



    public void verificarRespuesta(View view) {
        //paso a seleccion a ser true
        seleccion=true;
        //desabilita los botones
        desabilitarBotones();

        //si el id que trae es igual al id de uno de los botones
        if (view.getId()==fab1.getId()){
            //si el color que biene de la palabra es igual en que biene del boton
            if (color==bColor1){
                //aumente en una las correactas
                correctas++;
            }
        }
        if (view.getId()==fab2.getId()){
            if (color==bColor2){
                correctas++;
            }
        }
        if (view.getId()==fab3.getId()){
            if (color==bColor3){
                correctas++;
            }
        }
        if (view.getId()==fab4.getId()){
            if (color==bColor4){
                correctas++;
            }
        }

        porReaccion=((float) correctas/(float) totalPalabaras)*100;
        reaccion.setText((int) porReaccion+"%");
        incorrectas=totalPalabaras-correctas;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void inicializar() {
        tiempo=findViewById(R.id.txtTiempo);
        movimientos=findViewById(R.id.txtmovimientos);
        reaccion=findViewById(R.id.txtReaccion);
        palabra=findViewById(R.id.txtPalabra);
        fab1=findViewById(R.id.fab1);
        fab2=findViewById(R.id.fab2);
        fab3=findViewById(R.id.fab3);
        fab4=findViewById(R.id.fab4);
        pause = findViewById(R.id.button_pausar);
    }
    private void desabilitarBotones(){
        fab1.setEnabled(false);
        fab2.setEnabled(false);
        fab3.setEnabled(false);
        fab4.setEnabled(false);
    }
    private void habilitarBotones(){
        fab1.setEnabled(true);
        fab2.setEnabled(true);
        fab3.setEnabled(true);
        fab4.setEnabled(true);
    }
}
