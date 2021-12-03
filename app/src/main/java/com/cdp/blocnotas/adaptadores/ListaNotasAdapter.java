package com.cdp.blocnotas.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cdp.blocnotas.R;
import com.cdp.blocnotas.VerActivity;
import com.cdp.blocnotas.entidades.Notas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaNotasAdapter extends RecyclerView.Adapter<ListaNotasAdapter.NotaViewHolder> {

    ArrayList<Notas> listaNotas;
    ArrayList<Notas> listaOriginal;

    public ListaNotasAdapter(ArrayList<Notas> listaNotas) {
        this.listaNotas = listaNotas;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaNotas);
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_nota, null, false);
        return new NotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        holder.viewTipo.setText(listaNotas.get(position).getTipo());
        holder.viewTitulo.setText(listaNotas.get(position).getTitulo());
        holder.viewContenido.setText(listaNotas.get(position).getContenido());

    }

    public void filtrado(final String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaNotas.clear();
            listaNotas.addAll(listaOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Notas> collecion = listaNotas.stream()
                        .filter(i -> i.getTipo().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaNotas.clear();
                listaNotas.addAll(collecion);
            } else {
                for (Notas c : listaOriginal) {
                    if (c.getTipo().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaNotas.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public class NotaViewHolder extends RecyclerView.ViewHolder {

        TextView viewTipo, viewTitulo, viewContenido;
        ImageView viewImage;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);

            viewTipo = itemView.findViewById(R.id.viewTipo);
            viewTitulo = itemView.findViewById(R.id.viewTitulo);
            viewContenido = itemView.findViewById(R.id.viewContenido);
            

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, VerActivity.class);
                    intent.putExtra("ID", listaNotas.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
