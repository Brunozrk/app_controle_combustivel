package com.br.uteis;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BancoDeDados{

	private final String NOME_DO_BANCO = "econocomb";
	private final String TABELA_CARRO = "tb_carro";
	private final String TABELA_ABASTECIMENTO = "tb_abastecimento";
	SQLiteDatabase bancoDeDados = null;
	Uteis util = new Uteis();
	
	/**
	 * Construtor que cria ou carrega o banco de dados
	 * @param context
	 */
	public BancoDeDados(Context context){
		try {
			// Cria ou abre o banco de dados.
			bancoDeDados = context.openOrCreateDatabase(NOME_DO_BANCO, context.MODE_WORLD_READABLE, null);

			String sql_tabela_carro = "CREATE TABLE IF NOT EXISTS " + TABELA_CARRO +
					   				  "(" +
						   				  "_id INTEGER PRIMARY KEY, " +
						   				  "marca TEXT" +
					   				  ");";

			String sql_tabela_abastecimento = "CREATE TABLE IF NOT EXISTS " + TABELA_ABASTECIMENTO +
							   				  "(" +
								   				  "_id INTEGER PRIMARY KEY, " +
								   				  "odometro REAL NOT NULL, " +
								   				  "litros REAL NOT NULL, " +
								   				  "media REAL NOT NULL, " +
								   				  "obs TEXT, " +
								   				  "date DATE, " +
								   				  "carro_id INTEGER NOT NULL, " +
								   				  "FOREIGN KEY(carro_id) REFERENCES "+TABELA_CARRO+"(_id) " +
							   				  ");";
			

			bancoDeDados.execSQL(sql_tabela_carro);
			bancoDeDados.execSQL(sql_tabela_abastecimento);
		} catch (Exception e) {
			util.mostraMensagem(Messages.BANCO_ERRO_ABRIR_CRIAR + e.getMessage(), context);
		}
	}
	
	/**
	 * Fecha conexão com o banco
	 * @param context
	 */
	public void fechaBancoDeDados(Context context) {
		try {
			bancoDeDados.close();
		} catch (Exception e) {
			util.mostraMensagem(Messages.BANCO_ERRO_FECHAR + e.getMessage(), context);
		}
	}

	// ------------------------------------------------------------------------
	// -------------------------------- CARROS --------------------------------
	// ------------------------------------------------------------------------

	/**
	 * Busca todos os carros
	 * @param campos_carro
	 * @return
	 */
	public Cursor buscaCarrosQuery(String [] campos_carro){
		return  bancoDeDados.query(TABELA_CARRO, 
								   campos_carro,
							       null, // selection,
								   null, // selectionArgs,
								   null, // groupBy,
								   null, // having,
								   null); // orderBy);
		
	}
	
	/**
	 * Grava (insert ou update) um carro no banco
	 * @param context
	 * @param marca
	 * @param idCarro
	 */
	public void gravarCarroQuery(Context context, String marca, int idCarro) {
		try {

			String sql = "";
			String sucesso = "";

			if (idCarro != 0) {
				sql = "UPDATE " + TABELA_CARRO  + " SET marca = '" + marca + "' WHERE _id = " + idCarro;
				sucesso = Messages.SUCESSO_EDICAO;

			} else {
				sql = "INSERT INTO " + TABELA_CARRO + " (marca) VALUES ("+ "'" + marca + "')";
				sucesso = Messages.SUCESSO_CADASTRO;
			}
			
			
			bancoDeDados.execSQL(sql);

			util.mostraToast(sucesso, context);

		} catch (Exception e) {
			util.mostraMensagem(Messages.BANCO_ERRO_SALVAR_EDITAR + e.getMessage(), context);
		}
	}	
	
	/**
	 * Excluir um carro do banco e todos abastecimentos relacionados
	 * @param context
	 * @param idCarro
	 */
	public void excluirCarroQuery(Context context, int idCarro) {
		try {

			String sql_carro = "DELETE FROM " + TABELA_CARRO + " WHERE _id = " + idCarro;
			String sql_abastecimento = "DELETE FROM " + TABELA_ABASTECIMENTO + " WHERE carro_id = " + idCarro;
			bancoDeDados.execSQL(sql_carro);
			bancoDeDados.execSQL(sql_abastecimento);

			util.mostraToast(Messages.SUCESSO_EXCLUSAO, context);

		} catch (Exception e) {
			util.mostraMensagem(Messages.BANCO_ERRO_EXCLUIR + e.getMessage(), context);
		}
	}
	
	/**
	 * Filtra a listagem de carros por marca
	 * @param filtro
	 * @param campos_carro
	 * @return
	 */
	public Cursor filtraCarroQuery(CharSequence filtro, String [] campos_carro){
		String value = "%"+filtro.toString()+"%";

	    return bancoDeDados.query(TABELA_CARRO, 
	    						  campos_carro, 
	    						  "marca like ?", 
	    						  new String[]{value}, 
	    						  null, 
	    						  null, 
	    						  null);
	    
	}
	
	// ------------------------------------------------------------------------
	// -------------------------------- FIM - CARROS --------------------------
	// ------------------------------------------------------------------------
	
	
	// ------------------------------------------------------------------------
	// -------------------------------- ABASTECIMENTOS ------------------------
	// ------------------------------------------------------------------------	
	
	/**
	 * Busca todos os abastecimentos
	 * @param campos_abastecimento
	 * @return
	 */
	public Cursor buscaAbastecimentosQuery(String [] campos_abastecimento){
		return  bancoDeDados.query(TABELA_ABASTECIMENTO, 
								   campos_abastecimento,
							       null, // selection,
								   null, // selectionArgs,
								   null, // groupBy,
								   null, // having,
								   null); // orderBy);
		
	}
	
	/**
	 * Filtra a listagem de abastecimentos pelo id do carro
	 * @param idCarro
	 * @param campos_abastecimento
	 * @return
	 */
	public Cursor filtraAbastecimentoPorCarroQuery(int idCarro, String [] campos_abastecimento){

	    return bancoDeDados.query(TABELA_ABASTECIMENTO, 
	    						  campos_abastecimento, 
	    						  "carro_id = ?", 
	    						  new String[]{Integer.toString(idCarro)}, 
	    						  null, 
	    						  null, 
	    						  "date DESC");
	    
	}
	
	/**
	 * Grava (insert ou update) um abastecimento no banco
	 * @param context
	 * @param odometro
	 * @param litros
	 * @param obs
	 * @param data
	 * @param idCarro
	 * @param idAbastecimento
	 */
	public void gravarAbastecimentoQuery(Context context, Double odometro, Double litros, String obs, StringBuilder data, int idCarro,  int idAbastecimento) {
		try {

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(3);
			
			String sql = "";
			String sucesso = "";
			String media = nf.format(odometro / litros);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			Date objDate = new Date();
			String[] dataSplit = data.toString().split("/");
			
			objDate.setDate(Integer.parseInt(dataSplit[0]));
			objDate.setMonth(Integer.parseInt(dataSplit[1]) - 1);
			objDate.setYear(Integer.parseInt(dataSplit[2]) - 1900);
			String date = sdf.format(objDate);
			
			if (idAbastecimento != 0) {
				sql = "UPDATE " + TABELA_ABASTECIMENTO  +
						  			  " SET odometro = '" + odometro + "', " +
									  "litros = '" + litros + "', " +
									  "media = '" + media + "', " +
									  "obs = '" + obs + "', " +
									  "date = '" + date + "', " +
									  "carro_id = '" + idCarro + "'" +
									  " WHERE _id = " + idAbastecimento;
				sucesso = Messages.SUCESSO_EDICAO;
			} else {
				sql = "INSERT INTO " + TABELA_ABASTECIMENTO + " (odometro, litros, media, obs, date, carro_id) " +
					  "VALUES ("+ "'" + odometro + "', " +
								  "'" + litros + "', " +
								  "'" + media + "', " +
								  "'" + obs + "', " +
								  "'" + date + "', " +
								  "'" + idCarro + "'" +
								  ")";
				
				sucesso = Messages.SUCESSO_CADASTRO;
			}
			
			bancoDeDados.execSQL(sql);

			util.mostraToast(sucesso, context);

		} catch (Exception e) {
			util.mostraMensagem(Messages.BANCO_ERRO_SALVAR_EDITAR + e.getMessage(), context);
		}
	}

	/**
	 * Excluir um abastecimento do banco
	 * @param context
	 * @param idAbastecimento
	 */
	public void excluirAbastecimentoQuery(Context context, int idAbastecimento) {
		try {

			String sql = "DELETE FROM " + TABELA_ABASTECIMENTO + " WHERE _id = " + idAbastecimento;

			bancoDeDados.execSQL(sql);

			util.mostraToast(Messages.SUCESSO_EXCLUSAO, context);

		} catch (Exception e) {
			util.mostraMensagem(Messages.BANCO_ERRO_EXCLUIR + e.getMessage(), context);
		}
	}

	// ------------------------------------------------------------------------
	// -------------------------------- FIM - ABASTECIMENTOS ------------------
	// ------------------------------------------------------------------------
	
}
