package com.br.econocomb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.br.uteis.BancoDeDados;
import com.br.uteis.Messages;
import com.br.uteis.Pages;
import com.br.uteis.Uteis;

public class CarroActivity extends Activity {
	
	BancoDeDados banco_de_dados;
	Button btnCarros, btnTelaInicial, btnFormCarro, btnGravaCarro;

	EditText etMarca, etFiltro;
	
	ListView listContentCarros;
	int idCarro = 0;
	int pagina_atual = 0;

	String campos_carro[] = {"marca", "_id"};
	
 	Cursor cursor = null;
	CursorAdapter dataSource;
	Uteis util = new Uteis();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		banco_de_dados = new BancoDeDados(CarroActivity.this);
		chamaListaCarros();
	}
	
	/**
	 * Carrega a listagem de carros
	 */
	@SuppressWarnings("deprecation")
	public void chamaListaCarros(){
		try {
			pagina_atual = Pages.LISTAGEM_CARROS;
			setContentView(R.layout.listagem_carros);
			inicializaDados();
			criaBotaoTelaInicial();
			
			cursor = banco_de_dados.buscaCarrosQuery(campos_carro);
			dataSource = new SimpleCursorAdapter(CarroActivity.this, 
												 R.layout.item_list_carro, 
												 cursor, 
												 campos_carro, 
												 new int[] { R.id.tvMarca});
			
			idCarro = 0;
			
			listContentCarros.setAdapter(dataSource);
			
			btnFormCarro.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					chamaCadastroCarro();
				}
			});
			
			listContentCarros.setTextFilterEnabled(true);
			listContentCarros.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    	chamaEdicaoCarro(position);
 			    }
			});
			
			listContentCarros.setOnItemLongClickListener(new OnItemLongClickListener() {
	            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long id) {
	    			cursor.moveToPosition(position);
	    			idCarro = cursor.getInt(cursor.getColumnIndex("_id"));

					util.confirm(CarroActivity.this, 
							 "Confirmação",
							 Messages.CONFIRMA_EXCLUSAO + Messages.AVISO_ABASTECIMENTOS_DO_CARRO, 
							 "Sim", 
							 "Não",
							 new Runnable() {
								public void run() {
									banco_de_dados.excluirCarroQuery(CarroActivity.this, idCarro);
									util.animation_slide_out_right(CarroActivity.this, view, new Runnable() { 
																						public void run() { 
																							chamaListaCarros();
																						}
																				});
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
					cursor = banco_de_dados.filtraCarroQuery(filtro, campos_carro);
					return cursor;
				}
			});
			
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO_LISTAR + e.getMessage(), CarroActivity.this);
		}

	}
	
	/**
	 *  Chama tela de cadastro
	 */
	public void chamaCadastroCarro() {
		setContentView(R.layout.form_carro);
		pagina_atual = Pages.FORM_CARRO;
		inicializaDados();
			
		// BOTÃO GRAVAR
		btnGravaCarro.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String marca = etMarca.getText().toString().trim();
				if (marca.equals("")){
					util.mostraMensagem(Messages.CAMPO_OBRIGATORIO, CarroActivity.this);
				}
				else{
					banco_de_dados.gravarCarroQuery(CarroActivity.this, marca, idCarro);
					chamaListaCarros();
				}
			}
		});
		
		criaBotaoCarros();
	}
	
	/**
	 * Chama tela de edição
	 * @param position
	 */
	public void chamaEdicaoCarro(int position) {

		try {
			cursor.moveToPosition(position);
			idCarro = cursor.getInt(cursor.getColumnIndex("_id"));
			
			// CARREGA CADASTRO
			chamaCadastroCarro();

			etMarca.setText(cursor.getString(cursor.getColumnIndex("marca")));

		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), CarroActivity.this);
		}
	}
	
	
	/**
	 * Cria botão que vai para a listagem de carros
	 */
	public void criaBotaoCarros(){
		btnCarros.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaListaCarros();
			}
		});
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
	 * Carrega MainActivity
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
		btnCarros = (Button) findViewById(R.id.btnCarros);
		btnFormCarro = (Button) findViewById(R.id.btnFormCarro);
		btnGravaCarro = (Button) findViewById(R.id.btnGravaCarro);
		
		// Edit Text
		etFiltro= (EditText) findViewById(R.id.etFiltro);
		etMarca = (EditText) findViewById(R.id.etMarca);
		
		// ListView
		listContentCarros = (ListView) findViewById(R.id.listViewCarros);
	}
	
	/**
	 * Identifica tela atual para enviar para tela correta ao pressionar o botão 'voltar' do android
	 */
	@Override
    public void onBackPressed() {
		switch (pagina_atual) {
		case Pages.LISTAGEM_CARROS:
			chamaTelaInicial();
			break;
		case Pages.FORM_CARRO:
			chamaListaCarros();
			break;
		default:
			banco_de_dados.fechaBancoDeDados(CarroActivity.this);
			finish();
			break;
		}
    }
}
