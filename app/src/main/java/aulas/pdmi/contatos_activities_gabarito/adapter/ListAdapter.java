package aulas.pdmi.contatos_activities_gabarito.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        // popula as views
        tvNome.setText(contato.nome);
        tvSobrenome.setText(contato.sobrenome);
        tvTelefone.setText(contato.telefone);

        //mapeia a view da imagem
        ImageView imvImagem = (ImageView) convertView.findViewById(R.id.imv_item);
        if (contato.imagem != null) {
            //converte byte[] para Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(contato.imagem, 0, contato.imagem.length);
            //carrega a imagem na ImageView do item da ListView
            imvImagem.setImageBitmap(bitmap);
        }else{
            //carrega a imagem padrão (se não houver imagem no Cursor)
            imvImagem.setImageResource(R.drawable.foto_sombra);
        }
        // retorna a view do item completa para renderizar no screen
        return convertView;

    }
}
