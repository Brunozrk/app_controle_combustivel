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
import android.widget.CheckBox;
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
import com.br.uteis.Pages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public class LembreteActivity extends BaseActivity {

	// Date Dialog
	int dpAno;
	int dpMes;
	int dpDia;
	TextView tvData;
	ImageButton btnDatePicker;
	StringBuilder data;
	final int DATE_DIALOG_ID = 0;
	
	BancoDeDados banco_de_dados;

	EditText etDesc;
	CheckBox chNotifica;
	
	String dataFiltro = "";
	int idCarro = 0;
	int idLembrete = 0;
	int pagina_atual = 0;
	
	// Lembretes
	ListView listContentLembretes;

	// Spinners
 	Spinner spCarros;
 	Cursor cursorSpinnerCarro = null;
 	
 	Cursor cursor = null;
	CursorAdapter dataSource;
	
	Uteis util = new Uteis();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(LembreteActivity.this);
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		chamaListaLembretes();
		menuEsquerda();
	}
	
	/**
	 * Chama lista de lembrete
	 */
	public void chamaListaLembretes(){
		try {
			pagina_atual = Pages.LISTAGEM_LEMBRETES;
			supportInvalidateOptionsMenu();
			if (getCurrentFocus() != null){
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			}
			carregaListaLembretes();
			
		} catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_LISTAR + e.getMessage(), LembreteActivity.this);
		}

	}
	
	/**
	 * Carrega Lista de lembretes
	 */
	public void carregaListaLembretes(){
		setContentView(R.layout.listagem_lembretes);
		inicializaDados();
		carregaSpinnerCarro(true);
		idLembrete = 0;
		
		// Captura e filtra
		capturaItemSelecionadoSpinnerParaFiltrar();
		
		// Edição click
		listContentLembretes.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    		chamaEdicaoLembrete(position);
			    }
		});
		
		// Excluir click
		listContentLembretes.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long id) {
    			cursor.moveToPosition(position);
    			idLembrete = cursor.getInt(cursor.getColumnIndex("_id"));
				util.confirm(LembreteActivity.this, 
						 CONFIRMACAO,
						 CONFIRMA_EXCLUSAO, 
						 SIM, 
						 NAO,
						 new Runnable() {
							public void run() {
								Boolean sucesso = banco_de_dados.excluirLembreteQuery(LembreteActivity.this, idLembrete);
								if (sucesso){
									util.mostraToast(SUCESSO_EXCLUSAO, LembreteActivity.this);
									util.animation_slide_out_right(LembreteActivity.this, view, new Runnable() { 
																														public void run() { 
																															carregaListaLembretes();
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
	public void chamaCadastroLembrete() {
		pagina_atual = Pages.FORM_LEMBRETE;
		supportInvalidateOptionsMenu();
		setContentView(R.layout.form_lembrete);
		inicializaDados();
		carregaSpinnerCarro(false);
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
	 * Grava lembrete
	 */
	public void gravaLembrete(){
		try {
			String desc = etDesc.getText().toString().trim();
			if (desc.equals("")){
				util.mostraMensagem(CAMPO_OBRIGATORIO_TITULO, CAMPO_OBRIGATORIO, LembreteActivity.this);
			}
			else{
				int retorno = banco_de_dados.gravarLembreteQuery(LembreteActivity.this, desc, data, chNotifica.isChecked(), idCarro, idLembrete);
				if (retorno == 1){
					util.mostraToast(SUCESSO_CADASTRO, LembreteActivity.this);
				}else{
					util.mostraToast(SUCESSO_EDICAO, LembreteActivity.this);
				}
				chamaListaLembretes();
				habilitaDrawer();
				menuEsquerda();
			}
		}  catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_GRAVAR_REGISTRO + e.getMessage(), LembreteActivity.this);
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
	public void chamaEdicaoLembrete(int position) {

		try {
			cursor.moveToPosition(position);
			idLembrete = cursor.getInt(cursor.getColumnIndex("_id"));
			int postionCursorSpinnerCarro = cursorSpinnerCarro.getPosition();
			
			// Atualiza data para a data do registro a ser editado
			String dataColumn = cursor.getString(cursor.getColumnIndex("date"));
			atualizaValoresData(util.convertStringParaInt(dataColumn.split("/")[0]), util.convertStringParaInt(dataColumn.split("/")[1]) - 1, util.convertStringParaInt(dataColumn.split("/")[2]));
			Calendar c = Calendar.getInstance();
			c.set(dpAno, dpMes, dpDia);
			
			// CARREGA CADASTRO
			chamaCadastroLembrete();
			
			etDesc.setText(cursor.getString(cursor.getColumnIndex("desc")));
			spCarros.setSelection(postionCursorSpinnerCarro);
			
			chNotifica.setChecked(cursor.getInt(cursor.getColumnIndex("mostra")) == 1 ? true : false);
			
		} catch (Exception e) {
			util.mostraMensagem(ERRO, ERRO_CARREGAR_REGISTRO + e.getMessage(), LembreteActivity.this);
		}
	}
	
	/**
	 * Carrega spinner com os carros cadastrados
	 */
	public void carregaSpinnerCarro(Boolean adiciona_todos){
		cursorSpinnerCarro = banco_de_dados.buscaCarrosQuery(Variaveis.CAMPOS_CARRO);
		List<String> nomes = new ArrayList<String>();
		cursorSpinnerCarro.moveToFirst();
		if (adiciona_todos){
//			nomes.add("Todos");
//			faca nada ainda
		}

		idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
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
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//				position--;
//				if (position < 0){
//					idCarro = 0;
//				}else{
					cursorSpinnerCarro.moveToPosition(position);
					idCarro = cursorSpinnerCarro.getInt(cursorSpinnerCarro.getColumnIndex("_id"));
//				}
				filtraListaLembretes();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	public void filtraListaLembretes(){
		cursor = banco_de_dados.filtraLembretePorCarroQuery(idCarro, LembreteActivity.this);
		dataSource = new SimpleCursorAdapter(LembreteActivity.this, 
											R.layout.item_list_lembrete, 
											cursor, 
											Variaveis.CAMPOS_LEMBRETE, 
											new int[] { R.id.tvData, R.id.tvDesc, R.id.tvCarro});
		listContentLembretes.setAdapter(dataSource);
//		((SimpleCursorAdapter) dataSource).setViewBinder(binder);
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
		btnDatePicker = (ImageButton) findViewById(R.id.btnDatePicker);
		
		// Edit Text
		etDesc = (EditText) findViewById(R.id.etDesc);
		
		// Check Box
		chNotifica = (CheckBox) findViewById(R.id.chNotifica);

		// Text View
		tvData = (TextView) findViewById(R.id.tvData);
		
		// ListView
		listContentLembretes = (ListView) findViewById(R.id.listViewLembretes);
		
		spCarros = (Spinner) findViewById(R.id.spCarros);
	}
	

	/**********************************************************************************
	 * ACTION BAR - Aqui estão os métodos que fazem o controle da action bar
	 **********************************************************************************/
	
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
		menu_envia = menu.findItem(R.id.menu_envia); 
		
		menu_envia.setVisible(false);
		switch (pagina_atual) {
			case Pages.LISTAGEM_LEMBRETES:
				menu_novo.setVisible(true);
				menu_grava.setVisible(false);
				break;
			case Pages.FORM_LEMBRETE:
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
	    		if (pagina_atual == Pages.LISTAGEM_LEMBRETES){
	    			return(super.onOptionsItemSelected(item));
	    		}
	        	redirecionaVoltar();
	            return(true);
	        case R.id.menu_novo:
				Calendar c = Calendar.getInstance();
				atualizaValoresData(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
	            chamaCadastroLembrete();
	            return true;
		    case R.id.menu_grava:
		    	gravaLembrete();
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
			case Pages.LISTAGEM_LEMBRETES:
				chamaTelaInicial();
				break;
			case Pages.FORM_LEMBRETE:
				chamaListaLembretes();
				habilitaDrawer();
				menuEsquerda();
				break;
			default:
				banco_de_dados.fechaBancoDeDados(LembreteActivity.this);
				finish();
				break;
		}
	}
}
