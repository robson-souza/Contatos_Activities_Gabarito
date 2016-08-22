package aulas.pdmi.contatos_activities_gabarito.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import aulas.pdmi.contatos_activities_gabarito.R;
import aulas.pdmi.contatos_activities_gabarito.model.Contato;

/**
 * Created by vagner on 21/08/16.
 */
public class ListAdapter extends ArrayAdapter<Contato> {

    public ListAdapter(Context context, List<Contato> contatos) {
        super(context, 0, contatos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Lê os dados da posição que o usuário clicou
        Contato contato = getItem(position);

        // Checa de está utilizando o padrão ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_layout, parent, false);
        }
        // mapeia as views do layout do adaptador
        TextView tvNome = (TextView) convertView.findViewById(R.id.tv_item_nome);
        TextView tvSobrenome = (TextView) convertView.findViewById(R.id.tv_item_sobrenome);
        TextView tvTelefone = (TextView) convertView.findViewById(R.id.tv_item_telefone);
        // Populate the data into the template view using the data object
        tvNome.setText(contato.nome);
        tvSobrenome.setText(contato.sobrenome);
        tvTelefone.setText(contato.telefone);
        // retorna a view do item completa para renderizar no screen
        return convertView;

    }
}
