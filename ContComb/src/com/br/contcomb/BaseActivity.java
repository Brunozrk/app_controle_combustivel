package com.br.contcomb;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.br.banco.BancoDeDados;
import com.br.econocomb.R;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public abstract class BaseActivity extends ActionBarActivity {
	
	String SIM;
	String NAO;

	String CONFIRMACAO;
	
	String SELECIONE_PROVEDOR;

	String CAMPO_OBRIGATORIO_TITULO;
	String VALOR_INVALIDO_TITULO;
	String ETANOL_OU_GASOLINA_TITULO;

	String ETANOL;
	String GASOLINA;

	String SUCESSO_CADASTRO;
	String SUCESSO_EDICAO;
	String SUCESSO_EXCLUSAO;

	String CAMPO_OBRIGATORIO;
	String CAMPO_NAO_PODE_SER_ZERO;
	
	String CONFIRMA_EXCLUSAO;
	String AVISO_ABASTECIMENTOS_DO_CARRO;
	
	String NAO_HA_CARRO_CADASTRADO;

	String ERRO;
	String ERRO_LISTAR;
	String ERRO_CARREGAR_REGISTRO;
	String ERRO_GRAVAR_REGISTRO;
	
	BancoDeDados banco_de_dados;
	
	private String[] drawerListViewItems;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Uteis util = new Uteis();
	
    MenuItem menu_novo, menu_grava, menu_envia;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inicializaMensagens();
        banco_de_dados = new BancoDeDados(BaseActivity.this);
    }
    
    /**
     * Classe para adaptar o array e colocar ícones nos itens do menu
     *
     */
    private class MyArrayAdapter extends ArrayAdapter<String>{

        public MyArrayAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup  parent) {
        	LayoutInflater inflater = getLayoutInflater();
        	View row = inflater.inflate(R.layout.drawer_listview_item, parent, false);
        	TextView text = (TextView)row.findViewById(R.id.text1);
        	
        	switch (position) {
				case 0: montaItemMenu(text, drawerListViewItems[0], R.drawable.ic_action_home);
						break;
				case 1: montaItemMenu(text, drawerListViewItems[1], R.drawable.ic_action_car);
						break;
				case 2: montaItemMenu(text, drawerListViewItems[2], R.drawable.ic_action_refuelling);
						break;
				case 3: montaItemMenu(text, drawerListViewItems[3], R.drawable.ic_action_email);
						break;
				case 4: montaItemMenu(text, drawerListViewItems[4], R.drawable.ic_action_help_menu);
						break;
				case 5: montaItemMenu(text, drawerListViewItems[5], R.drawable.ic_action_cancel);
						break;
				default:break;
			}
        	return row;
        }
    }
    
    /**
     * Monta item do menu
     * @param text
     * @param titulo
     * @param drawable
     */
    public void montaItemMenu(TextView text, String titulo, int drawable){
    	text.setText(titulo);
		text.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }
    
	/**
	 * Carrega menu esquerda
	 */
	public void menuEsquerda(){
		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.items);

		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
		drawerListView.setAdapter(new MyArrayAdapter(this,
                R.layout.drawer_listview_item, drawerListViewItems));
      
		// 2. App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// 2.1 create ActionBarDrawerToggle
		actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                );
		// 2.2 Set actionBarDrawerToggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        
        // 2.3 enable and show "up" arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); 
        
        // just styling option
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		drawerListView.setOnItemClickListener(new DrawerItemClickListener());
	}

	public void desabilitaDrawer(){
		actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
	}
	
	public void habilitaDrawer(){
		actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
         actionBarDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		 // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Controle do menu da esquerda (Drawer) 
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	    	
	    	switch (position) {
	    	// Página Inicial
			case 0:	Intent iMain = new Intent(getBaseContext(), MainActivity.class);
		            startActivity(iMain);
		            finish();
		            break;
		    // Carros
			case 1: Intent iCarro = new Intent(getBaseContext(), CarroActivity.class);
		            startActivity(iCarro);
		            finish();
		            break;
		    // Abastecimentos
			case 2: if (banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO).getCount() == 0){
						util.mostraToast(NAO_HA_CARRO_CADASTRADO, BaseActivity.this);
					}else{
						Intent iAbastecimento = new Intent(getBaseContext(), AbastecimentoActivity.class);
						startActivity(iAbastecimento);
						finish();
					}
					break;
			// Contato
			case 3: Intent iContato = new Intent(getBaseContext(), ContatoActivity.class);
		            startActivity(iContato);
		            finish();
		            break;					
            // Ajuda
			case 4: Intent iAjuda = new Intent(getBaseContext(), AjudaActivity.class);
					startActivity(iAjuda);
					finish();
					break;					
			// Sair
			case 5: finish();
					break;
			// Nenhum dos outros
			default: finish();
					 break;
			}
	    	drawerLayout.closeDrawer(drawerListView);
	    }
	}
	/**
	 * Inicializa variáveis com mensagens do string.xml
	 * Isso é feito para internacionalização do aplicativo
	 */
	public void inicializaMensagens(){
		
		SIM = getResources().getString(R.string.sim);
		NAO = getResources().getString(R.string.nao);

		CONFIRMACAO = getResources().getString(R.string.confirmacao);

		SELECIONE_PROVEDOR = getResources().getString(R.string.selecione_provedor);

		CAMPO_OBRIGATORIO_TITULO = getResources().getString(R.string.campo_obrigatorio_titulo);
		VALOR_INVALIDO_TITULO = getResources().getString(R.string.valor_invalido_titulo);
		ETANOL_OU_GASOLINA_TITULO = getResources().getString(R.string.etanol_ou_gasolina_titulo);
		
		ETANOL = getResources().getString(R.string.etanol);
		GASOLINA = getResources().getString(R.string.gasolina);
		
		SUCESSO_CADASTRO = getResources().getString(R.string.sucesso_cadastro);
		SUCESSO_EDICAO = getResources().getString(R.string.sucesso_edicao);
		SUCESSO_EXCLUSAO = getResources().getString(R.string.sucesso_exclusao);;

		CAMPO_OBRIGATORIO = getResources().getString(R.string.campo_obrigatorio);
		CAMPO_NAO_PODE_SER_ZERO = getResources().getString(R.string.campo_nao_pode_ser_zero);
		
		CONFIRMA_EXCLUSAO = getResources().getString(R.string.confirma_exclusao);
		AVISO_ABASTECIMENTOS_DO_CARRO = getResources().getString(R.string.aviso_abastecimentos_do_carro);
		
		NAO_HA_CARRO_CADASTRADO = getResources().getString(R.string.nao_ha_carro_cadastrado);

		ERRO = getResources().getString(R.string.erro);
		ERRO_LISTAR = getResources().getString(R.string.erro_listar);
		ERRO_CARREGAR_REGISTRO = getResources().getString(R.string.erro_carregar_registro);
		ERRO_GRAVAR_REGISTRO = getResources().getString(R.string.erro_gravar_registro);
	}
	
//	SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
//	    @Override
//	    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//	    	int cont = cursor.getPosition();
//	    	if ((cont) % 2 != 1) {
//	    	    view.setBackgroundColor(Color.parseColor("#80f9f9f9"));
//	    	} 
//	        return false;
//	    }
//	};
}