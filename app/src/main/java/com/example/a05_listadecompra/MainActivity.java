package com.example.a05_listadecompra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.a05_listadecompra.Adapters.ProductosAdapter;
import com.example.a05_listadecompra.Modelos.Producto;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.a05_listadecompra.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static ArrayList<Producto> productosList;
    public static SharedPreferences sharedPreferences;

    // Recycler
    private static ProductosAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        productosList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(Constantes.DATOS, MODE_PRIVATE);

        int columnas;
        // Horizontal -> 2
        // Vertical -> 1
        // Desde las configuraciones de la actividad -> orientation // portrait(Vertical) / landscape(Horizontal)
        columnas = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;

        adapter = new ProductosAdapter(productosList, R.layout.producto_model_card,this);
        layoutManager = new GridLayoutManager(this,columnas);
        binding.contentMain.contenedor.setAdapter(adapter);
        binding.contentMain.contenedor.setLayoutManager(layoutManager);
        cargarDatos();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProducto().show();
            }
        });
    }

    public static void guardarEnSP(){
        String contactosSTR = new Gson().toJson(productosList);
        Log.d("JSON",contactosSTR);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constantes.COMPRA, contactosSTR);
        editor.apply();
    }

    public void cargarDatos(){
        if (sharedPreferences.contains(Constantes.COMPRA) && !sharedPreferences.getString(Constantes.COMPRA,"").isEmpty()){
            String contactosSTR = sharedPreferences.getString(Constantes.COMPRA,"");
            Type tipo = new TypeToken< ArrayList<Producto> >(){}.getType();
            List<Producto> temp = new Gson().fromJson(contactosSTR, tipo);
            productosList.clear();
            productosList.addAll(temp);
            adapter.notifyItemRangeInserted(0,productosList.size());
            Toast.makeText(MainActivity.this, "Elementos cargados", Toast.LENGTH_SHORT).show();
        }
    }


    private AlertDialog createProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.alert_title_crear));
        builder.setCancelable(false);

        View productoAlertView = LayoutInflater.from(this).inflate(R.layout.producto_model_alert,null);
        builder.setView(productoAlertView);

        EditText txtNombre = productoAlertView.findViewById(R.id.txtNombreProductoAlert);
        EditText txtCantidad = productoAlertView.findViewById(R.id.txtCantidadProductoAlert);
        EditText txtPrecio = productoAlertView.findViewById(R.id.txtPrecioProductoAlert);
        TextView lbTotal = productoAlertView.findViewById(R.id.lbTotalProductoAlert);

        TextWatcher textWatcher = new TextWatcher() {
            /**
             *
             * @param charSequence -> Envia el contenido que había antes del cambio
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             *
             * @param charSequence -> Envia el texto actual despues de la modificacion
             * @param i
             * @param i1
             * @param i2
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * Se dispara al terminar la modificación
             * @param editable -> envia el contenido final del cuadro de texto
             */
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                    lbTotal.setText(numberFormat.format(cantidad * precio));
                }catch (NumberFormatException ex){
                    ex.printStackTrace();
                }
            }
        };

        txtCantidad.addTextChangedListener(textWatcher);
        txtPrecio.addTextChangedListener(textWatcher);

        builder.setNegativeButton(getString(R.string.btn_alert_cancel),null);
        builder.setPositiveButton(getString(R.string.btn_alert_crear), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!txtNombre.getText().toString().isEmpty() && !txtCantidad.getText().toString().isEmpty() && !txtPrecio.getText().toString().isEmpty()){
                    Producto producto = new Producto(txtNombre.getText().toString(),Integer.parseInt(txtCantidad.getText().toString()),Float.parseFloat(txtPrecio.getText().toString()));
                    productosList.add(0, producto);
                    adapter.notifyItemInserted(0);
                    guardarEnSP();
                }else{
                    Toast.makeText(MainActivity.this,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

    /**
     * Se dispara ANTES de que se elimine la actividad
     * @param outState -> guardo datos
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("LISTA",productosList);
    }

    /**
     * Se dispara DESPUÉS de crear la actividad de nuevo
     * @param savedInstanceState -> recupero datos
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Producto> temp = (ArrayList<Producto>) savedInstanceState.getSerializable("LISTA");
        productosList.addAll(temp);
        adapter.notifyItemRangeInserted(0,productosList.size());
    }
}