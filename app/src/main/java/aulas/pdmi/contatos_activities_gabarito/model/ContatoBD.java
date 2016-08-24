package aulas.pdmi.contatos_activities_gabarito.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vagner on 21/08/16.
 */
public class ContatoBD extends SQLiteOpenHelper{

    private static String TAG = "contatos_bd";
    private static final String NOME_BD = "contatos.sqlite";
    private static final int VERSAO = 1;
    private static ContatoBD contatoBD = null; //Singleton

    private ContatoBD(Context context) {
        // context, nome do banco, factory, versão
        super(context, NOME_BD, null, VERSAO);
    }

    public static ContatoBD getInstance(Context context){
        if(contatoBD == null){
            contatoBD = new ContatoBD(context);
            return contatoBD;
        }else{
            return contatoBD;
        }
    }

    /*
        Métodos do ciclo de vida do banco de dados.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists contato" +
                "( _id integer primary key autoincrement, " +
                " nome text, " +
                " sobrenome text, " +
                " telefone text, " +
                " imagem blob);";
        Log.d(TAG, "Criando a tabela contato. Aguarde ...");
        sqLiteDatabase.execSQL(sql);
        Log.d(TAG, "Tabela contatos criada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Caso mude a versão do banco de dados, podemos executar um SQL aqui
        // exemplo:
        Log.d("aulas", "Upgrade da versão " + oldVersion + " para "
                + newVersion + ", destruindo tudo.");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contato");
        onCreate(sqLiteDatabase); // chama onCreate e recria o banco de dados
        Log.i("aulas", "Executou o script de upgrade da tabela contatos.");
    }

    /*
        Métodos CRUD.
     */
    public long save(Contato contato){

        SQLiteDatabase db = getWritableDatabase(); //abre a conexão com o banco

        try{
            //tupla com: chave, valor
            ContentValues values = new ContentValues();
            values.put("_id", contato._id);
            values.put("nome", contato.nome);
            values.put("sobrenome", contato.sobrenome);
            values.put("telefone", contato.telefone);
            values.put("imagem", contato.imagem); //insere o valor (a imagem) na tupla)

            //realiza a operação
            if(contato._id == null){
                //insere no banco de dados
                return db.insert("contato", null, values);
            }else{
                //altera no banco de dados
                values.put("_id", contato._id);
                return db.update("contato", values, "_id=" + contato._id, null);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            db.close(); //não esquecer de liberar o recurso
        }

        return 0; //caso não realize as operações
    }

    //deleta um contato
    public long delete(Contato contato){
        SQLiteDatabase db = getWritableDatabase(); //abre a conexão com o banco
        try{
            return db.delete("contato", "_id=?", new String[]{String.valueOf(contato._id)});
        }
        finally {
            db.close(); //não esquecer de liberar o recurso
        }
    }

    //retorna a lista de contatos
    public List<Contato> getAll(){
        SQLiteDatabase db = getReadableDatabase();
        try {
            //retorna uma List para os registros contidos no banco de dados
            // select * from carro
            return toList(db.rawQuery("SELECT  * FROM contato", null));
        } finally {
            db.close();
        }
    }

    public List<Contato> getByname(String nome){
        SQLiteDatabase db = getReadableDatabase();
        try {
            //retorna uma List para os registros contidos no banco de dados
            // select * from carro
            return toList(db.rawQuery("SELECT  * FROM contato where nome LIKE'" + nome + "%'", null));
        } finally {
            db.close();
        }
    }

    //converte de Cursor para List
    private List<Contato> toList(Cursor c) {
        List<Contato> contatos = new ArrayList<Contato>();

        if (c.moveToFirst()) {
            do {
                Contato contato = new Contato();

                // recupera os atributos de carro
                contato._id = c.getLong(c.getColumnIndex("_id"));
                contato.nome = c.getString(c.getColumnIndex("nome"));
                contato.sobrenome = c.getString(c.getColumnIndex("sobrenome"));
                contato.telefone = c.getString(c.getColumnIndex("telefone"));
                contato.imagem = c.getBlob(c.getColumnIndex("imagem"));

                contatos.add(contato);

            } while (c.moveToNext());
        }

        return contatos;
    }

}