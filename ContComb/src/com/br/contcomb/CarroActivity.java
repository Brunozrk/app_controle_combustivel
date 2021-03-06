package com.br.contcomb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.br.banco.BancoDeDados;
import com.br.econocomb.R;
import com.br.uteis.Pages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public class CarroActivity extends BaseActivity {
	
	BancoDeDados banco_de_dados;
	Button btnFormCarro, btnGravaCarro;

	EditText etMarca, etFiltro;
	
	ListView listContentCarros;

	int idCarro = 0;
	int pagina_atual = 0;
 	
	Cursor cursor = null;
	CursorAdapter dataSource;

	Uteis util = new Uteis();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(CarroActivity.this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		chamaListaCarros();
		menuEsquerda();
	}
	
	/**
	 * Chama o lista de carros
	 */
	public void chamaListaCarros(){
		try {
			pagina_atual = Pages.LISTAGEM_CARROS;
			supportInvalidateOptionsMenu();
			carregaListaCarros();
			
		} catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_LISTAR + e.getMessage(), CarroActivity.this);
		}

	}

	/**
	 * Carrega lista de carros
	 */
	@SuppressWarnings("deprecation")
	public void carregaListaCarros(){
		setContentView(R.layout.listagem_carros);
		inicializaDados();
		etFiltro.requestFocus();
		cursor = banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO);
		dataSource = new SimpleCursorAdapter(CarroActivity.this, 
											 R.layout.item_list_carro, 
											 cursor, 
											 Variaveis.CAMPOS_CARRO, 
											 new int[] { R.id.tvMarca});
		
		idCarro = 0;
		
		listContentCarros.setAdapter(dataSource);
		
		listContentCarros.setTextFilterEnabled(true);
		listContentCarros.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    		chamaEdicaoCarro(position);
			    }
		});
		
//		((SimpleCursorAdapter) dataSource).setViewBinder(binder);
		
		listContentCarros.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long id) {
    			cursor.moveToPosition(position);
    			idCarro = cursor.getInt(cursor.getColumnIndex("_id"));

				util.confirm(CarroActivity.this, 
						 CONFIRMACAO,
						 CONFIRMA_EXCLUSAO + AVISO_ABASTECIMENTOS_DO_CARRO, 
						 SIM, 
						 NAO,
						 new Runnable() {
							public void run() {
								Boolean sucesso = banco_de_dados.excluirCarroQuery(CarroActivity.this, idCarro);
								if (sucesso){
									util.mostraToast(SUCESSO_EXCLUSAO, CarroActivity.this);
									util.animation_slide_out_right(CarroActivity.this, view, new Runnable() { 
																												public void run() { 
																													carregaListaCarros();
																													menuEsquerda();
																												}
																											});
								}
							}
						}, null);
    
                return true;
            }
        });
		
		etFiltro.addTextChangedListener(new TextWatcher() {
             
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            }
             
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                 
            }
             
            @Override
            public void afterTextChanged(Editable s) {
            	dataSource = (SimpleCursorAdapter)listContentCarros.getAdapter();
            	dataSource.getFilter().filter(s.toString());                
            }
            
        });
		dataSource.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence filtro) {
				cursor = banco_de_dados.filtraCarroQuery(filtro, Variaveis.CAMPOS_CARRO);
				return cursor;
			}
		});
	}
	
	/**
	 * Chama tela de cadastro
	 */
	public void chamaCadastroCarro() {
		pagina_atual = Pages.FORM_CARRO;
		supportInvalidateOptionsMenu();
		setContentView(R.layout.form_carro);
		inicializaDados();
		etMarca.requestFocus();
		desabilitaDrawer();
	}
	
	/**
	 * Grava carro
	 */
	public void gravaCarro(){
		String marca = etMarca.getText().toString().trim();
		if (marca.equals("")){
			util.mostraMensagem(CAMPO_OBRIGATORIO_TITULO, CAMPO_OBRIGATORIO, CarroActivity.this);
		}
		else{
			int retorno = banco_de_dados.gravarCarroQuery(CarroActivity.this, marca, idCarro);
			if (retorno == 1){
				util.mostraToast(SUCESSO_CADASTRO, CarroActivity.this);
			}else{
				util.mostraToast(SUCESSO_EDICAO, CarroActivity.this);
			}
			
			InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			chamaListaCarros();
			habilitaDrawer();
			menuEsquerda();
		}
	}
	/**
	 * Chama tela de edi��o
	 * @param position
	 */
	public void chamaEdicaoCarro(int position) {

		try {
			cursor.moveToPosition(position);
			idCarro = cursor.getInt(cursor.getColumnIndex("_id"));

			chamaCadastroCarro();

			etMarca.setText(cursor.getString(cursor.getColumnIndex("marca")));

		} catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_CARREGAR_REGISTRO + e.getMessage(), CarroActivity.this);
		}
	}
	
	
	/**
	 * Carrega MainActivity
	 */
	public void chamaTelaInicial() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        this.finish();
	}
	
	/**
	 * Inicializa dados (bot�es, edit text, etc)
	 */
	public void inicializaDados() {
		// Edit Text
		etFiltro= (EditText) findViewById(R.id.etFiltro);
		etMarca = (EditText) findViewById(R.id.etMarca);
		
		// ListView
		listContentCarros = (ListView) findViewById(R.id.listViewCarros);
	}
	
	/**********************************************************************************
	 * ACTION BAR - Aqui est�o os m�todos que fazem o controle da action bar
	 **********************************************************************************/
	
	/**
	 * Cria��o do menu no action bar
	 * @param menu
	 * @return
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Prepara��o do menu no action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
		menu_novo = menu.findItem(R.id.menu_novo); 
		menu_grava = menu.findItem(R.id.menu_grava); 
		menu_envia = menu.findItem(R.id.menu_envia); 
		
		menu_envia.setVisible(false);
		switch (pagina_atual) {
			case Pages.LISTAGEM_CARROS:
				menu_novo.setVisible(true);
				menu_grava.setVisible(false);
				break;
			case Pages.FORM_CARRO:
				menu_novo.setVisible(false);
				menu_grava.setVisible(true);
				break;
			default:
				menu_novo.setVisible(true);
				menu_grava.setVisible(false);
				break;
		}
		return true;
    }
	
	/**
	 * A��es dos bot�es na action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case android.R.id.home:
	    		if (pagina_atual == Pages.LISTAGEM_CARROS){
	    			return(super.onOptionsItemSelected(item));
	    		}
	        	redirecionaVoltar();
	            return(true);
	        case R.id.menu_novo:
	            chamaCadastroCarro();
	            return true;
		    case R.id.menu_grava:
		    	gravaCarro();
		        return true;
		}
	    return(super.onOptionsItemSelected(item));
	}
	
	/**
	 * Sobrescreve m�todo do bot�o voltar do android
	 */
	@Override
    public void onBackPressed() {
		redirecionaVoltar();
    }
	
	/**
	 * Identifica tela atual para enviar para tela correta ao pressionar o bot�o 'voltar' do android
	 */
	public void redirecionaVoltar(){
		switch (pagina_atual) {
			case Pages.LISTAGEM_CARROS:
				chamaTelaInicial();
				break;
			case Pages.FORM_CARRO:
				chamaListaCarros();
				habilitaDrawer();
				menuEsquerda();
				break;
			default:
				banco_de_dados.fechaBancoDeDados(CarroActivity.this);
				finish();
				break;
		}
	}
}
