package com.br.econocomb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.banco.BancoDeDados;
import com.br.uteis.Messages;
import com.br.uteis.Pages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public class AbastecimentoActivity extends Activity {

	// Date Dialog
	int dpAno;
	int dpMes;
	int dpDia;
	TextView tvData, tvMediaTotal, tvAbastecimentos;
	Button btnDatePicker;
	StringBuilder data;
	final int DATE_DIALOG_ID = 0;
	
	BancoDeDados banco_de_dados;

	EditText etLitros, etOdometro, etObs;
	
	MenuItem menu_novo, menu_grava;
	
	int idCarro = 0;
	int idAbastecimento = 0;
	int pagina_atual = 0;
	
	// Abastecimentos
	ListView listContentAbastecimentos;

	// Spinner carros
 	Spinner spCarros;
 	Cursor cursorSpinnerCarro = null;
 	
 	Cursor cursor = null;
	CursorAdapter dataSource;
	Uteis util = new Uteis();

	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(AbastecimentoActivity.this);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		chamaListaAbastecimentos();
	}
	
	/**
	 * Chama lista de abastecimento
	 */
	public void chamaListaAbastecimentos(){
		try {
			pagina_atual = Pages.LISTAGEM_ABASTECIMENTOS;
			invalidateOptionsMenu();
			if (getCurrentFocus() != null){
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			}
			carregaListaAbastecimentos();
			
		} catch (Exception e) {
			util.mostraMensagem("Erro", Messages.ERRO_LISTAR + e.getMessage(), AbastecimentoActivity.this);
		}

	}
	
	/**
	 * Carrega Lista de abastecimentos
	 */
	public void carregaListaAbastecimentos(){
		setContentView(R.layout.listagem_abastecimentos);
		inicializaDados();
		carregaSpinnerCarro();
		
		idAbastecimento = 0;
		
		// Captura e filtra
		capturaItemSelecionadoSpinnerParaFiltrar();
		
		listContentAbastecimentos.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    		chamaEdicaoAbastecimento(position);
			    }
		});
		
		listContentAbastecimentos.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long id) {
    			cursor.moveToPosition(position);
    			idAbastecimento = cursor.getInt(cursor.getColumnIndex("_id"));
				util.confirm(AbastecimentoActivity.this, 
						 "Confirmação",
						 Messages.CONFIRMA_EXCLUSAO, 
						 "Sim", 
						 "Não",
						 new Runnable() {
							public void run() {
								banco_de_dados.excluirAbastecimentoQuery(AbastecimentoActivity.this, idAbastecimento);
								util.animation_slide_out_right(AbastecimentoActivity.this, view, new Runnable() { 
																								public void run() { 
																									carregaListaAbastecimentos();
																								}
																							});
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
		invalidateOptionsMenu();
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
				util.mostraMensagem("Campo Obrigatório", Messages.CAMPO_OBRIGATORIO, AbastecimentoActivity.this);
			}
			else if(util.convertStringParaDouble(litros) == 0 || util.convertStringParaDouble(odometro) == 0){
				util.mostraMensagem("Valor Inválido", Messages.CAMPO_NAO_PODE_SER_ZERO, AbastecimentoActivity.this);
			}
				else{
					banco_de_dados.gravarAbastecimentoQuery(AbastecimentoActivity.this, util.convertStringParaDouble(odometro), util.convertStringParaDouble(litros), obs, data, idCarro, idAbastecimento);
					chamaListaAbastecimentos();
				}
		}  catch (Exception e) {
			util.mostraMensagem("Erro", Messages.ERRO_GRAVAR_REGISTRO + e.getMessage(), AbastecimentoActivity.this);
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
     * Callback quando o usuário escolhe uma data no datepickerdialog
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                	atualizaValoresData(dia, mes, ano);
                    atualizaTvData();
                }
            };
	
	/**
	 * Chama tela de edição
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
			util.mostraMensagem("Erro", Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), AbastecimentoActivity.this);
		}
	}
	
	/**
	 * Carrega spinner com os carros cadastrados
	 */
	public void carregaSpinnerCarro(){
		cursorSpinnerCarro = banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO);
		List<String> nomes = new ArrayList<String>();
		cursorSpinnerCarro.moveToFirst();
		
		while(!cursorSpinnerCarro.isAfterLast()){
			nomes.add(cursorSpinnerCarro.getString(cursorSpinnerCarro.getColumnIndex("marca")));
			cursorSpinnerCarro.moveToNext();
		}
		
		//Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomes);
		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spCarros.setAdapter(spinnerArrayAdapter);
	
	}
	
	/**
	 * Método do Spinner para capturar o item selecionado 
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
			
			@SuppressWarnings("deprecation")
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				cursorSpinnerCarro.moveToPosition(position);
				idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
				cursor = banco_de_dados.filtraAbastecimentoPorCarroQuery(idCarro, Variaveis.CAMPOS_ABASTECIMENTO);
				dataSource = new SimpleCursorAdapter(AbastecimentoActivity.this, 
													R.layout.item_list_abastecimento, 
													cursor, 
													Variaveis.CAMPOS_ABASTECIMENTO, 
													new int[] { R.id.tvData, R.id.tvOdometro, R.id.tvLitros, R.id.tvMedia, R.id.tvObs});
				carregaResumo();
				listContentAbastecimentos.setAdapter(dataSource);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	/**
	 * Carrega resummo (Abastecimentos e Média Total)
	 */
	public void carregaResumo(){
		Double somaMedia = banco_de_dados.somaMediaAbastecimento(idCarro, AbastecimentoActivity.this);
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
	 * Atualiza variáveis referente a data
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
	 * Inicializa dados (botões, edit text, etc)
	 */
	public void inicializaDados() {
		
		// Botões
		btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
		
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
	}
	
	/**
	 * Criação do menu no action bar
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
     * Preparação do menu no action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
		menu_novo = menu.findItem(R.id.menu_novo); 
		menu_grava = menu.findItem(R.id.menu_grava); 
		
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
	 * Ações dos botões na action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
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
	 * Sobrescreve método do botão voltar do android
	 */
	@Override
    public void onBackPressed() {
		redirecionaVoltar();
    }
	
	/**
	 * Identifica tela atual para enviar para tela correta ao pressionar o botão 'voltar' do android
	 */
	public void redirecionaVoltar(){
		switch (pagina_atual) {
		case Pages.LISTAGEM_ABASTECIMENTOS:
			chamaTelaInicial();
			break;
		case Pages.FORM_ABASTECIMENTO:
			chamaListaAbastecimentos();
			break;
		default:
			banco_de_dados.fechaBancoDeDados(AbastecimentoActivity.this);
			finish();
			break;
		}
	}
}
