package com.example.a05_listadecompra.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a05_listadecompra.Modelos.Producto;
import com.example.a05_listadecompra.R;

import java.text.NumberFormat;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoVH> {

    private List<Producto> objects;
    private int resource;
    private Context context;

    public ProductosAdapter(List<Producto> objects, int resource, Context context) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productoFilaView = LayoutInflater.from(context).inflate(resource,null);
        productoFilaView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ProductoVH(productoFilaView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        Producto producto = objects.get(position);
        holder.lbNombre.setText(producto.getNombre());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete(producto,holder.getAdapterPosition()).show();
            }
        });
        holder.txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int cantidad;

                try{
                    cantidad = Integer.parseInt(editable.toString());
                }catch (NumberFormatException ex){
                    cantidad = 0;
                }

                producto.setCantidad(cantidad);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProducto(producto, holder.getAdapterPosition()).show();
            }
        });
    }

    private android.app.AlertDialog updateProducto(Producto producto, int adapterPosition){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        builder.setTitle("Agregar producto");
        builder.setCancelable(false);

        View productoAlertView = LayoutInflater.from(context).inflate(R.layout.producto_model_alert,null);
        builder.setView(productoAlertView);

        EditText txtNombre = productoAlertView.findViewById(R.id.txtNombreProductoAlert);
        EditText txtCantidad = productoAlertView.findViewById(R.id.txtCantidadProductoAlert);
        EditText txtPrecio = productoAlertView.findViewById(R.id.txtPrecioProductoAlert);
        TextView lbTotal = productoAlertView.findViewById(R.id.lbTotalProductoAlert);

        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));

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

        builder.setNegativeButton("CANCELAR",null);
        builder.setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!txtNombre.getText().toString().isEmpty() && !txtCantidad.getText().toString().isEmpty() && !txtPrecio.getText().toString().isEmpty()){
                    producto.setNombre(txtNombre.getText().toString());
                    producto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                    notifyItemChanged(adapterPosition);
                }else{
                    Toast.makeText(context,"FALTAN DATOS",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

    private AlertDialog confirmDelete(Producto producto, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirma Eliminación");
        builder.setCancelable(false);
        builder.setNegativeButton("CANCELAR",null);
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                objects.remove(producto);
                notifyItemRemoved(adapterPosition);
            }
        });

        return builder.create();
    }

    /**
     * Me indica la cantidad de elementos que voy a mostrar
     * @return
     */
    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ProductoVH extends RecyclerView.ViewHolder{
        ImageButton btnEliminar;
        TextView lbNombre;
        EditText txtCantidad;

        public ProductoVH(@NonNull View itemView){
            super(itemView);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProductoCard);
            lbNombre = itemView.findViewById(R.id.lbNombreProductoCard);
            txtCantidad = itemView.findViewById(R.id.txtCantidadProductoCard);
        }
    }
}
