package com.br.contcomb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.banco.BancoDeDados;
import com.br.econocomb.R;
import com.br.uteis.Messages;
import com.br.uteis.Pages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public class AbastecimentoActivity extends BaseActivity {

	// Date Dialog
	int dpAno;
	int dpMes;
	int dpDia;
	TextView tvData, tvMediaTotal, tvAbastecimentos;
	ImageButton btnDatePicker;
	StringBuilder data;
	final int DATE_DIALOG_ID = 0;
	
	BancoDeDados banco_de_dados;

	EditText etLitros, etOdometro, etObs;
	
	String dataFiltro = "";
	int idCarro = 0;
	int idAbastecimento = 0;
	int pagina_atual = 0;
	
	// Abastecimentos
	ListView listContentAbastecimentos;

	// Spinners
 	Spinner spCarros;
 	Spinner spDatas;
 	Cursor cursorSpinnerCarro = null;
 	Cursor cursorSpinnerData = null;
 	
 	Cursor cursor = null;
	CursorAdapter dataSource;
	
	Uteis util = new Uteis();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(AbastecimentoActivity.this);
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		chamaListaAbastecimentos();
		menuEsquerda();
	}
	
	/**
	 * Chama lista de abastecimento
	 */
	public void chamaListaAbastecimentos(){
		try {
			pagina_atual = Pages.LISTAGEM_ABASTECIMENTOS;
			supportInvalidateOptionsMenu();
			if (getCurrentFocus() != null){
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			}
			carregaListaAbastecimentos();
			
		} catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_LISTAR + e.getMessage(), AbastecimentoActivity.this);
		}

	}
	
	/**
	 * Carrega Lista de abastecimentos
	 */
	public void carregaListaAbastecimentos(){
		setContentView(R.layout.listagem_abastecimentos);
		inicializaDados();
		carregaSpinnerCarro();
		carregaSpinnerData();
		idAbastecimento = 0;
		
		// Captura e filtra
		capturaItemSelecionadoSpinnerParaFiltrar();
		
		// Edi��o click
		listContentAbastecimentos.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    		chamaEdicaoAbastecimento(position);
			    }
		});
		
		// Excluir click
		listContentAbastecimentos.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long id) {
    			cursor.moveToPosition(position);
    			idAbastecimento = cursor.getInt(cursor.getColumnIndex("_id"));
				util.confirm(AbastecimentoActivity.this, 
						 CONFIRMACAO,
						 CONFIRMA_EXCLUSAO, 
						 SIM, 
						 NAO,
						 new Runnable() {
							public void run() {
								Boolean sucesso = banco_de_dados.excluirAbastecimentoQuery(AbastecimentoActivity.this, idAbastecimento);
								if (sucesso){
									util.mostraToast(SUCESSO_EXCLUSAO, AbastecimentoActivity.this);
									util.animation_slide_out_right(AbastecimentoActivity.this, view, new Runnable() { 
																														public void run() { 
																															carregaListaAbastecimentos();
																															menuEsquerda();
																														}
																													});
									
								}
							}
						}, null);
    
                return true;
            }
        });
	}
	
	/**
	 *  Chama tela de cadastro
	 */
	public void chamaCadastroAbastecimento() {
		pagina_atual = Pages.FORM_ABASTECIMENTO;
		supportInvalidateOptionsMenu();
		setContentView(R.layout.form_abastecimento);
		inicializaDados();
		carregaSpinnerCarro();
		capturaItemSelecionadoSpinnerParaGravar();

        // Adiciona listener para o botao do datepicker
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        atualizaTvData();
        desabilitaDrawer();
	}
	
	/**
	 * Grava abastecimento
	 */
	public void gravaAbastecimento(){
		try {
			String litros = etLitros.getText().toString().trim();  
			String odometro = etOdometro.getText().toString().trim();  
			String obs = etObs.getText().toString().trim();
			if (litros.equals("") || odometro.equals("")){
				util.mostraMensagem(CAMPO_OBRIGATORIO_TITULO, CAMPO_OBRIGATORIO, AbastecimentoActivity.this);
			}
			else if(util.convertStringParaDouble(litros) == 0 || util.convertStringParaDouble(odometro) == 0){
				util.mostraMensagem(VALOR_INVALIDO_TITULO, CAMPO_NAO_PODE_SER_ZERO, AbastecimentoActivity.this);
			}
				else{
					int retorno = banco_de_dados.gravarAbastecimentoQuery(AbastecimentoActivity.this, util.convertStringParaDouble(odometro), util.convertStringParaDouble(litros), obs, data, idCarro, idAbastecimento);
					if (retorno == 1){
						util.mostraToast(SUCESSO_CADASTRO, AbastecimentoActivity.this);
					}else{
						util.mostraToast(SUCESSO_EDICAO, AbastecimentoActivity.this);
					}
					chamaListaAbastecimentos();
					habilitaDrawer();
					menuEsquerda();
				}
		}  catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_GRAVAR_REGISTRO + e.getMessage(), AbastecimentoActivity.this);
			capturaItemSelecionadoSpinnerParaGravar();
		}
	}
	
	/**
	 * Atualiza a textview tvData e a variavel data 
	 */
    private void atualizaTvData() {
    	data =  new StringBuilder().append(dpDia).append("/")
							       .append(dpMes + 1).append("/") // Month is 0 based so add 1
							       .append(dpAno);
    	
        tvData.setText(data);
    }

    /**
     * Callback quando o usu�rio escolhe uma data no datepickerdialog
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                	atualizaValoresData(dia, mes, ano);
                    atualizaTvData();
                }
            };
	
	/**
	 * Chama tela de edi��o
	 * @param position
	 */
	public void chamaEdicaoAbastecimento(int position) {

		try {
			cursor.moveToPosition(position);
			idAbastecimento = cursor.getInt(cursor.getColumnIndex("_id"));
			int postionCursorSpinnerCarro = cursorSpinnerCarro.getPosition();
			
			// Atualiza data para a data do registro a ser editado
			String dataColumn = cursor.getString(cursor.getColumnIndex("strftime('%d/%m/%Y',date)"));
			atualizaValoresData(util.convertStringParaInt(dataColumn.split("/")[0]), util.convertStringParaInt(dataColumn.split("/")[1]) - 1, util.convertStringParaInt(dataColumn.split("/")[2]));
			Calendar c = Calendar.getInstance();
			c.set(dpAno, dpMes, dpDia);
			
			// CARREGA CADASTRO
			chamaCadastroAbastecimento();
			
			etOdometro.setText(util.convertDoubleParaString(cursor.getDouble(cursor.getColumnIndex("odometro"))));
			etLitros.setText(util.convertDoubleParaString(cursor.getDouble(cursor.getColumnIndex("litros"))));
			etObs.setText(cursor.getString(cursor.getColumnIndex("obs")));
			spCarros.setSelection(postionCursorSpinnerCarro);
			
		} catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_CARREGAR_REGISTRO + e.getMessage(), AbastecimentoActivity.this);
		}
	}
	
	/**
	 * Carrega spinner com os carros cadastrados
	 */
	public void carregaSpinnerCarro(){
		cursorSpinnerCarro = banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO);
		List<String> nomes = new ArrayList<String>();
		cursorSpinnerCarro.moveToFirst();
		idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
		while(!cursorSpinnerCarro.isAfterLast()){
			nomes.add(cursorSpinnerCarro.getString(cursorSpinnerCarro.getColumnIndex("marca")));
			cursorSpinnerCarro.moveToNext();
		}
		
		//Cria um ArrayAdapter usando um padr�o de layout da classe R do android, passando o ArrayList nomes
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomes);
		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spCarros.setAdapter(spinnerArrayAdapter);
	}
	
	/**
	 * Carrega spinner com datas do abastecimento
	 */
	public void carregaSpinnerData(){
		cursorSpinnerData = banco_de_dados.buscaDatasAbastecimentoQuery(idCarro);
		List<String> datas = new ArrayList<String>();
		cursorSpinnerData.moveToFirst();
		datas.add("Todos");
		while(!cursorSpinnerData.isAfterLast()){
			datas.add(cursorSpinnerData.getString(cursorSpinnerData.getColumnIndex("strftime('%m/%Y',date)")));
			cursorSpinnerData.moveToNext();
		}
		
		//Cria um ArrayAdapter usando um padr�o de layout da classe R do android, passando o ArrayList nomes
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, datas);
		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spDatas.setAdapter(spinnerArrayAdapter);
	}
	
	/**
	 * M�todo do Spinner para capturar o item selecionado 
	 */
	public void capturaItemSelecionadoSpinnerParaGravar(){
		spCarros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
 
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				cursorSpinnerCarro.moveToPosition(position);
    			idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
			}
 
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
 
			}
		});
	}
	
	/**
	 * Captura item selecionado do spinner e filtra a listagem
	 */
	public void capturaItemSelecionadoSpinnerParaFiltrar(){
		spCarros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				cursorSpinnerCarro.moveToPosition(position);
				idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
				filtraListaAbastecimentos();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		spDatas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				position--;
				if (position < 0){
					dataFiltro = "";
				}else{
					cursorSpinnerData.moveToPosition(position);
					dataFiltro = cursorSpinnerData.getString(cursorSpinnerData.getColumnIndex("strftime('%m/%Y',date)"));
				}
				filtraListaAbastecimentos();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public void filtraListaAbastecimentos(){
		cursor = banco_de_dados.filtraAbastecimentoPorCarroDataQuery(idCarro, dataFiltro, AbastecimentoActivity.this);
		dataSource = new SimpleCursorAdapter(AbastecimentoActivity.this, 
											R.layout.item_list_abastecimento, 
											cursor, 
											Variaveis.CAMPOS_ABASTECIMENTO, 
											new int[] { R.id.tvData, R.id.tvOdometro, R.id.tvLitros, R.id.tvMedia, R.id.tvObs});
		carregaResumo();
		listContentAbastecimentos.setAdapter(dataSource);
//		((SimpleCursorAdapter) dataSource).setViewBinder(binder);
	}
	
	/**
	 * Carrega resummo (Abastecimentos e M�dia Total)
	 */
	public void carregaResumo(){
		Double somaMedia = banco_de_dados.somaMediaAbastecimento(idCarro, dataFiltro, AbastecimentoActivity.this);
		String mediaTotal = "0";
		if (cursor.getCount() != 0){
			mediaTotal = util.tresCasasDecimais(somaMedia/cursor.getCount());
		}
		tvMediaTotal.setText(mediaTotal + " Km/L");
		tvAbastecimentos.setText(util.convertIntParaString(cursor.getCount()));
	}
	
    /**
     * Cria datepicker com a data que esta na variavel 'data'
     */
    @Override
    protected Dialog onCreateDialog(int id) {
	    switch (id) {
		    case DATE_DIALOG_ID:
		    	return new DatePickerDialog(this,
						                     mDateSetListener,
						                     dpAno, dpMes, dpDia);
	    }
	    return null;
	}
    
    /**
     * Atualiza datepicker com a data que esta na variavel 'data'
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch (id) {
	    	case DATE_DIALOG_ID:
	    		((DatePickerDialog) dialog).updateDate(dpAno,
									    	           dpMes,
								    	           dpDia);
    	} 	
    }
	
	/**
	 * Atualiza vari�veis referente a data
	 * @param dia
	 * @param mes
	 * @param ano
	 */
	public void atualizaValoresData(int dia, int mes, int ano){
	    dpDia = dia;
	    dpMes = mes;
	    dpAno = ano;
	}
	
	/**
	 * Carrega tela inicial
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
		
		// Bot�es
		btnDatePicker = (ImageButton) findViewById(R.id.btnDatePicker);
		
		// Edit Text
		etLitros = (EditText) findViewById(R.id.etLitros);
		etOdometro = (EditText) findViewById(R.id.etOdometro);
		etObs = (EditText) findViewById(R.id.etObs);

		// Text View
        tvData = (TextView) findViewById(R.id.tvData);
        tvMediaTotal = (TextView) findViewById(R.id.tvMediaTotal);
        tvAbastecimentos = (TextView) findViewById(R.id.tvAbastecimentos);
		
		// ListView
		listContentAbastecimentos = (ListView) findViewById(R.id.listViewAbastecimentos);
		
		spCarros = (Spinner) findViewById(R.id.spCarros);
		spDatas = (Spinner) findViewById(R.id.spDatas);
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
			case Pages.LISTAGEM_ABASTECIMENTOS:
				menu_novo.setVisible(true);
				menu_grava.setVisible(false);
				break;
			case Pages.FORM_ABASTECIMENTO:
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
	    		if (pagina_atual == Pages.LISTAGEM_ABASTECIMENTOS){
	    			return(super.onOptionsItemSelected(item));
	    		}
	        	redirecionaVoltar();
	            return(true);
	        case R.id.menu_novo:
				Calendar c = Calendar.getInstance();
				atualizaValoresData(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
	            chamaCadastroAbastecimento();
	            return true;
		    case R.id.menu_grava:
		    	gravaAbastecimento();
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
			case Pages.LISTAGEM_ABASTECIMENTOS:
				chamaTelaInicial();
				break;
			case Pages.FORM_ABASTECIMENTO:
				chamaListaAbastecimentos();
				habilitaDrawer();
				menuEsquerda();
				break;
			default:
				banco_de_dados.fechaBancoDeDados(AbastecimentoActivity.this);
				finish();
				break;
		}
	}
}
