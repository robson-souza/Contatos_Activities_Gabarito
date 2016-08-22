package aulas.pdmi.contatos_activities_gabarito.control;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.app.ActionBar.Tab;

import java.util.List;

import aulas.pdmi.contatos_activities_gabarito.R;
import aulas.pdmi.contatos_activities_gabarito.adapter.ListAdapter;
import aulas.pdmi.contatos_activities_gabarito.model.ContatoDAO;
import aulas.pdmi.contatos_activities_gabarito.model.Contato;

/*
    Controlador da app.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, android.support.v7.app.ActionBar.TabListener {

    private static String TAG = "contatos_bd";
    private ContatoDAO contatoDAO;
    private EditText etNome;
    private EditText etSobrenome;
    private EditText etTelefone;
    private ListView lvContatos;
    private ImageView imvFoto;
    private static final String GETALL = "getAll";
    private static final String GETBYNOME = "getbynome";
    private static final String SAVE = "save";
    private static final String DELETE = "delete";
    private static Contato contato = null;
    private String nameFind = "";
    Tab tab1, tab2; //uma das abas da activity, quando em smartphone


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //obtém a instância do objeto de acesso ao banco de dados
        contatoDAO = ContatoDAO.getInstance(this);
        //constrói uma instância da classe de modelo
        contato = new Contato();

        if(isTablet(this)){
            setContentView(R.layout.activity_tablet);
            getSupportActionBar().setTitle(R.string.titulo_actionbar_list); //insere um título para a janela

            //mapeia os componentes de UI
            etNome = (EditText) findViewById(R.id.editText_nome);
            etSobrenome = (EditText) findViewById(R.id.editText_sobrenome);
            etTelefone = (EditText) findViewById(R.id.editText_telefone);
            lvContatos = (ListView) findViewById(R.id.listView);
            lvContatos.setOnItemClickListener(this); //adiciona a lista de ouvintes
            new Task().execute(GETALL); //executa a operação GET em segundo plano

        }else{
            //prepara a ActionBar
            getSupportActionBar().setTitle(R.string.titulo_actionbar_list);//insere um título para a janela
            getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_TABS);//define o modo de navegação por abas

            /*
                Cria as abas e as adiciona à ActionBar
            */
            //tab1
            tab1 = getSupportActionBar().newTab().setText("Lista de Contatos");
            tab1.setTabListener(MainActivity.this);
            getSupportActionBar().addTab(tab1);

            //tab2
            tab2 = getSupportActionBar().newTab().setText("Contato");
            tab2.setTabListener(MainActivity.this);
            getSupportActionBar().addTab(tab2);
        }

    }



    /*
        Trata eventos das Tabs
     */
    @Override
    public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
        switch (tab.getPosition()){
            case 0:{
                //mapeia os componentes da UI
                setContentView(R.layout.activity_smartphone_list);

                //insere um título na ActionBar
                getSupportActionBar().setTitle("Contatos");

                //mapeia os componentes de activity_list.xml
                lvContatos = (ListView) findViewById(R.id.listView);
                lvContatos.setOnItemClickListener(MainActivity.this); //registra o tratador de eventos para cada item da ListView

                new Task().execute(GETALL); //executa a operação GET em segundo plano

                break;
            }
            case 1:{
                //mapeia os componentes da UI
                setContentView(R.layout.activity_smartphone_inputs);

                //insere um título na ActionBar
                getSupportActionBar().setTitle("Edição");

                //mapeia os componentes de activity_inputs.xml
                etNome = (EditText) findViewById(R.id.editText_nome);
                etSobrenome = (EditText) findViewById(R.id.editText_sobrenome);
                etTelefone = (EditText) findViewById(R.id.editText_telefone);
                imvFoto = (ImageView) findViewById(R.id.imageView);

                break;
            }
        }
    }

    @Override
    public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    //infla o menu, mapeia e prepara a SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //infla o menu
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        //mapeia e insere o handler a fila de ouvintes da SearchView
        SearchView mySearchView = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();//obtem a SearchView
        mySearchView.setQueryHint("Digite um nome"); //coloca um hint na SearchView
        mySearchView.setOnQueryTextListener(MainActivity.this); //cadastra o tratador de eventos na lista de tratadores da SearchView


        return true;
    }

    //trata eventos dos itens do menu da ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuitem_salvar:
                if(!etNome.getText().toString().isEmpty() &&
                        !etSobrenome.getText().toString().isEmpty() &&
                        !etTelefone.getText().toString().isEmpty()) {
                    if(contato._id == null){ //se é uma inclusão
                        contato = new Contato(); //apaga dados antigos
                    }
                    contato.nome = etNome.getText().toString();
                    contato.sobrenome = etSobrenome.getText().toString();
                    contato.telefone = etTelefone.getText().toString();
                    Log.d(TAG, "Contato que será salvo: " + contato.toString());
                    new Task().execute(SAVE); //executa a operação CREATE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView
                }else{
                    Toast.makeText(MainActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menuitem_cancelar:
                limparFormulario();
                break;
            case R.id.menuitem_excluir:
                if(contato != null && !etNome.getText().toString().isEmpty() &&
                    !etSobrenome.getText().toString().isEmpty() &&
                    !etTelefone.getText().toString().isEmpty()){
                    new Task().execute(DELETE); //executa a operação DELETE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView
                }else{
                    Toast.makeText(MainActivity.this, "Selecione um contato na lista.", Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return true;
    }

    //trata eventos da SearchView
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //trata eventos da SearchView
    @Override
    public boolean onQueryTextChange(String newText) {
        if(!isTablet(MainActivity.this)) {
            tab1.select(); //seleciona a aba 1
            //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
        }
        if(newText.equals("")){
            new Task().execute(GETALL);
        }else{
            new Task().execute(GETBYNOME); //executa a operação GET em segundo plano para atualizar a ListView
            nameFind = newText; //armazena em uma variável global para uso na task
        }

        return true;
    }

    //limpa o formulário
    private void limparFormulario(){
        etNome.setText(null);
        etSobrenome.setText(null);
        etTelefone.setText(null);
        etNome.requestFocus();
        contato = new Contato(); //apaga dados antigos
    }

    //trata o evento onClick de cada item da lista
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!isTablet(MainActivity.this)) {
            tab2.select(); //seleciona a aba 2
            //onTabSelected(tab2, null); //chama o tratador de eventos para carregar os componentes
        }
        contato = (Contato) adapterView.getAdapter().getItem(i); //obtém o Contato
        Log.d(TAG, contato.toString());
        //carrega nos campos de input
        etNome.setText(contato.nome);
        etSobrenome.setText(contato.sobrenome);
        etTelefone.setText(contato.telefone);
        etNome.requestFocus();
    }

    /**
     * Método para carregar os dados do banco de dados para a ListView.
     */
    public void carregarListView(List<Contato> contatos){

        //cria um objeto da classe ListAdapter, um adaptador List -> ListView
        ListAdapter dadosAdapter = new ListAdapter(this, contatos);
        //associa o adaptador a ListView
        lvContatos.setAdapter(dadosAdapter);
    }

    /**
     * Detecção do tipo de screen size.
     * @param context contexto da Activity
     * @return boolean
     */
    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*
        Classe interna para realizar as transações no banco de dados
     */
    private class Task extends AsyncTask<String, Void, List<Contato>>{

        long count = 0L; //para armazenar o retorno do salvar e do excluir

        //executa a task em outra Thread
        @Override
        protected List<Contato> doInBackground(String... strings) {
            if(strings[0].equals(GETALL)){
                return contatoDAO.getAll(); //get
            }else{
                if(strings[0].equals(GETBYNOME)){
                    return contatoDAO.getByname(nameFind); //get
                }else{
                    if(strings[0].equals(SAVE)){
                        count = contatoDAO.save(MainActivity.contato); //create e update
                    }else{
                        if(strings[0].equals(DELETE)){
                            count = contatoDAO.delete(MainActivity.contato); //delete
                        }
                    }
                }
            }

            return null;
        }

        //atualiza a View
        @Override
        protected void onPostExecute(List<Contato> contatos) {
            if(contatos != null){
                carregarListView(contatos);
            }else if(count > 0){
                Toast.makeText(MainActivity.this, "Operação realizada.", Toast.LENGTH_SHORT).show();
                limparFormulario();
                if(!isTablet(MainActivity.this)) {
                    tab1.select(); //seleciona a aba 1
                    //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
                }
                Log.d(TAG, "Operação realizada.");
            }else{
                Toast.makeText(MainActivity.this, "Erro ao atualizar o contato. Contate o desenvolvedor.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
