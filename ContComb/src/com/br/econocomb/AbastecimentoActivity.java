package com.br.econocomb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.br.uteis.BancoDeDados;
import com.br.uteis.Messages;
import com.br.uteis.Pages;
import com.br.uteis.Uteis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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

public class AbastecimentoActivity extends Activity {

	// Date Dialog
	int dpAno;
	int dpMes;
	int dpDia;
	TextView tvData;
	Button btnDatePicker;
	StringBuilder data;
	final int DATE_DIALOG_ID = 0;
	
	BancoDeDados banco_de_dados;
	Button btnAbastecimentos, btnTelaInicial, btnFormAbastecimento, btnGravaAbastecimento;

	EditText etLitros, etOdometro, etObs;
	
	int idCarro = 0;
	int idAbastecimento = 0;
	int pagina_atual = 0;
	String campos_carro[] = {"marca", "_id"};
	
	// Abastecimentos
	ListView listContentAbastecimentos;
	String campos_abastecimento[] = {"strftime('%d/%m/%Y',date)", "odometro", "litros", "media", "obs", "_id"};

	// Spinner carros
 	Spinner spCarros;
 	Cursor cursorSpinnerCarro = null;
 	
 	Cursor cursor = null;
	CursorAdapter dataSource;
	Uteis util = new Uteis();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(AbastecimentoActivity.this);
		chamaListaAbastecimentos();
	}
	
	public void chamaListaAbastecimentos(){
		try {
			setContentView(R.layout.listagem_abastecimentos);
			pagina_atual = Pages.LISTAGEM_ABASTECIMENTOS;
			inicializaDados();
			carregaSpinnerCarro();
			criaBotaoTelaInicial();
			
			idAbastecimento = 0;
			
			btnFormAbastecimento.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Calendar c = Calendar.getInstance();
					atualizaValoresData(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
					chamaCadastroAbastecimento();
				}
			});
			
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
																										chamaListaAbastecimentos();
																									}
																								});
								}
							}, null);
	    
	                return true;
	            }
	        });
			
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO_LISTAR + e.getMessage(), AbastecimentoActivity.this);
		}

	}
	
	
	/**
	 *  Chama tela de cadastro
	 */
	public void chamaCadastroAbastecimento() {
		setContentView(R.layout.form_abastecimento);
		pagina_atual = Pages.FORM_ABASTECIMENTO;
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
		
		// BOTÃO GRAVAR
		btnGravaAbastecimento.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String litros = etLitros.getText().toString().trim();  
					String odometro = etOdometro.getText().toString().trim();  
					String obs = etObs.getText().toString().trim();
					if (litros.equals("") || odometro.equals("")){
						util.mostraMensagem(Messages.CAMPO_OBRIGATORIO, AbastecimentoActivity.this);
					}
					else if(Double.parseDouble(litros) == 0 || Double.parseDouble(odometro) == 0){
						util.mostraMensagem(Messages.CAMPO_NAO_PODE_SER_ZERO, AbastecimentoActivity.this);
					}
						else{
							banco_de_dados.gravarAbastecimentoQuery(AbastecimentoActivity.this, Double.parseDouble(odometro), Double.parseDouble(litros), obs, data, idCarro, idAbastecimento);
							chamaListaAbastecimentos();
						}
				}  catch (Exception e) {
					util.mostraMensagem(Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), AbastecimentoActivity.this);
					capturaItemSelecionadoSpinnerParaGravar();
				}
			}
		});
		
		criaBotaoAbastecimentos();
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
			atualizaValoresData(Integer.parseInt(dataColumn.split("/")[0]), Integer.parseInt(dataColumn.split("/")[1]) - 1, Integer.parseInt(dataColumn.split("/")[2]));
			Calendar c = Calendar.getInstance();
			c.set(dpAno, dpMes, dpDia);
			
			// CARREGA CADASTRO
			chamaCadastroAbastecimento();
			
			etOdometro.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("odometro"))));
			etLitros.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("litros"))));
			etObs.setText(cursor.getString(cursor.getColumnIndex("obs")));
			spCarros.setSelection(postionCursorSpinnerCarro);
			
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), AbastecimentoActivity.this);
		}
	}
	
	/**
	 * Cria botão que vai para a listagem de abastecimentos
	 */
	public void criaBotaoAbastecimentos(){
		btnAbastecimentos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (banco_de_dados.buscaCarrosQuery(campos_carro).getCount() == 0){
					util.mostraToast(Messages.NAO_HA_CARRO_CADASTRADO, AbastecimentoActivity.this);
				}else{
					chamaListaAbastecimentos();
				}
			
			}
		});
	}
	
	/**
	 * Carrega spinner com os carros cadastrados
	 */
	public void carregaSpinnerCarro(){
		cursorSpinnerCarro = banco_de_dados.buscaCarrosQuery(campos_carro);
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
				cursor = banco_de_dados.filtraAbastecimentoPorCarroQuery(idCarro, campos_abastecimento);
				dataSource = new SimpleCursorAdapter(AbastecimentoActivity.this, 
													R.layout.item_list_abastecimento, 
													cursor, 
													campos_abastecimento, 
													new int[] { R.id.tvData, R.id.tvOdometro, R.id.tvLitros, R.id.tvMedia, R.id.tvObs});
				
				listContentAbastecimentos.setAdapter(dataSource);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
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
	 * Cria botão que vai para a tela inicial
	 */
	public void criaBotaoTelaInicial() {
		btnTelaInicial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaTelaInicial();
			}
		});
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
		btnTelaInicial = (Button) findViewById(R.id.btnTelaInicial);
		btnAbastecimentos = (Button) findViewById(R.id.btnAbastecimetos);
		btnFormAbastecimento = (Button) findViewById(R.id.btnFormAbastecimento);
		btnGravaAbastecimento = (Button) findViewById(R.id.btnGravaAbastecimento);
		btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
		
		// Edit Text
		etLitros = (EditText) findViewById(R.id.etLitros);
		etOdometro = (EditText) findViewById(R.id.etOdometro);
		etObs = (EditText) findViewById(R.id.etObs);

		// Text View
        tvData = (TextView) findViewById(R.id.tvData);
		
		// ListView
		listContentAbastecimentos = (ListView) findViewById(R.id.listViewAbastecimentos);
		
		spCarros = (Spinner) findViewById(R.id.spCarros);
	}
	
	/**
	 * Identifica tela atual para enviar para tela correta ao pressionar o botão 'voltar' do android
	 */
	@Override
    public void onBackPressed() {
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
