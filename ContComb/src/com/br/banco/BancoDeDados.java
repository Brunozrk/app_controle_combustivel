package com.br.banco;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.br.uteis.Messages;
import com.br.uteis.Uteis;
import com.br.uteis.Variaveis;

public class BancoDeDados{

	private final String NOME_DO_BANCO = "econocomb";
	private final String TABELA_CARRO = "tb_carro";
	private final String TABELA_ABASTECIMENTO = "tb_abastecimento";
	private final String TABELA_LEMBRETE = "tb_lembrete";
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

			String sql_tabela_lembrete = "CREATE TABLE IF NOT EXISTS " + TABELA_LEMBRETE +
	   				  "(" +
		   				  "_id INTEGER PRIMARY KEY, " +
		   				  "desc TEXT, " +
		   				  "date DATE, " +
 						  "mostra TINYINT(1) DEFAULT 1, " +
		   				  "carro_id INTEGER NOT NULL, " +
		   				  "FOREIGN KEY(carro_id) REFERENCES "+TABELA_CARRO+"(_id) " +
	   				  ");";
			
			bancoDeDados.execSQL(sql_tabela_carro);
			bancoDeDados.execSQL(sql_tabela_abastecimento);
			bancoDeDados.execSQL(sql_tabela_lembrete);
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_ABRIR_CRIAR + e.getMessage(), context);
		}
	}
	
	/**
	 * Fecha conex�o com o banco
	 * @param context
	 */
	public void fechaBancoDeDados(Context context) {
		try {
			bancoDeDados.close();
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_FECHAR + e.getMessage(), context);
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
	public int gravarCarroQuery(Context context, String marca, int idCarro) {
		try {

			String sql = "";
			int sucesso = 1;

			if (idCarro != 0) {
				sql = "UPDATE " + TABELA_CARRO  + " SET marca = '" + marca + "' WHERE _id = " + idCarro;
				sucesso = 2;

			} else {
				sql = "INSERT INTO " + TABELA_CARRO + " (marca) VALUES ("+ "'" + marca + "')";
				sucesso = 1;
			}
			
			
			bancoDeDados.execSQL(sql);

			return sucesso;

		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_SALVAR_EDITAR + e.getMessage(), context);
			return 0;
		}
	}	
	
	/**
	 * Excluir um carro do banco e todos abastecimentos relacionados
	 * @param context
	 * @param idCarro
	 */
	public boolean excluirCarroQuery(Context context, int idCarro) {
		try {

			String sql_carro = "DELETE FROM " + TABELA_CARRO + " WHERE _id = " + idCarro;
			String sql_abastecimento = "DELETE FROM " + TABELA_ABASTECIMENTO + " WHERE carro_id = " + idCarro;
			String sql_lembrete = "DELETE FROM " + TABELA_LEMBRETE + " WHERE carro_id = " + idCarro;
			bancoDeDados.execSQL(sql_carro);
			bancoDeDados.execSQL(sql_abastecimento);
			bancoDeDados.execSQL(sql_lembrete);
			
			return true;

		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_EXCLUIR + e.getMessage(), context);
			return false;
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
	 * Busca todos os carros
	 * @param campos_carro
	 * @return
	 */
	public Cursor buscaDatasAbastecimentoQuery(int idCarro){
		return  bancoDeDados.query(TABELA_ABASTECIMENTO, 
								   Variaveis.CAMPOS_DATAS_ABASTECIMENTO,
								   "carro_id = ?", 
								   new String[]{Integer.toString(idCarro)}, // selection,
								   Variaveis.CAMPOS_DATAS_ABASTECIMENTO[0], // groupBy,
								   null, // having,
								   "date DESC, _id Desc"); // orderBy);
		
	}
	
	/**
	 * Filtra a listagem de abastecimentos pelo id do carro
	 * @param idCarro
	 * @param campos_abastecimento
	 * @return
	 */
	public Cursor filtraAbastecimentoPorCarroDataQuery(int idCarro, String data, Context context){
		try {
			String select = "carro_id = ?";
			String[] where = new String[]{Integer.toString(idCarro)};
			
			if (!data.equals("")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
				String[] dataSplit = data.toString().split("/");
				
				Date objDateMin = new Date();
				objDateMin.setDate(1);
				objDateMin.setMonth(Integer.parseInt(dataSplit[0]) - 1);
				objDateMin.setYear(Integer.parseInt(dataSplit[1]) - 1900);
				String dateMin = sdf.format(objDateMin);
				
				Date objDateMax = new Date();
				objDateMax.setDate(1);
				if (dataSplit[0].equals("12")){
					objDateMax.setMonth(0);
					objDateMax.setYear((Integer.parseInt(dataSplit[1]) - 1900) + 1);
				}
				else{
					objDateMax.setMonth(Integer.parseInt(dataSplit[0]));
					objDateMax.setYear(Integer.parseInt(dataSplit[1]) - 1900);
				}
				
				String dateMax = sdf.format(objDateMax);
				
				select += " AND date >= ? AND date < ?";
				where = new String[]{Integer.toString(idCarro), dateMin, dateMax};
			}
			
			return bancoDeDados.query(TABELA_ABASTECIMENTO, 
					Variaveis.CAMPOS_ABASTECIMENTO, 
					select, 
					where, 
					null, 
					null, 
					"date DESC, _id Desc");
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), context);
			return null;
		}
	    
	}
	
	/**
	 * Retorna soma da m�dia dos abastecimento
	 * @param idCarro
	 * @param context
	 * @return
	 */
	public Double somaMediaAbastecimento(int idCarro, String data, Context context){
		Double media = 0.0;
		try {
			Cursor cursor = filtraAbastecimentoPorCarroDataQuery(idCarro, data, context);
			if (cursor.moveToFirst())
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					media += Double.parseDouble(cursor.getString(cursor.getColumnIndex("media")).replace(",", "."));
				}
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), context);
		}finally{
			return media;
		}
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
	public int gravarAbastecimentoQuery(Context context, Double odometro, Double litros, String obs, StringBuilder data, int idCarro,  int idAbastecimento) {
		try {

			
			String sql = "";
			int sucesso = 0;
			String media = util.tresCasasDecimais(odometro / litros);
			
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
				sucesso = 2;
			} else {
				sql = "INSERT INTO " + TABELA_ABASTECIMENTO + " (odometro, litros, media, obs, date, carro_id) " +
					  "VALUES ("+ "'" + odometro + "', " +
								  "'" + litros + "', " +
								  "'" + media + "', " +
								  "'" + obs + "', " +
								  "'" + date + "', " +
								  "'" + idCarro + "'" +
								  ")";
				
				sucesso = 1;
			}
			
			bancoDeDados.execSQL(sql);
			return sucesso;
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_SALVAR_EDITAR + e.getMessage(), context);
			return 0;
		}
	}

	/**
	 * Excluir um abastecimento do banco
	 * @param context
	 * @param idAbastecimento
	 */
	public boolean excluirAbastecimentoQuery(Context context, int idAbastecimento) {
		try {

			String sql = "DELETE FROM " + TABELA_ABASTECIMENTO + " WHERE _id = " + idAbastecimento;

			bancoDeDados.execSQL(sql);

			return true;

		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_EXCLUIR + e.getMessage(), context);
			return false;
		}
	}

	// ------------------------------------------------------------------------
	// -------------------------------- FIM - ABASTECIMENTOS ------------------
	// ------------------------------------------------------------------------
	
	
	// ------------------------------------------------------------------------
	// -------------------------------- LEMBRETES ------------------------
	// ------------------------------------------------------------------------	
	
	/**
	 * Filtra a listagem de lembretes pelo id do carro
	 * @param idCarro
	 * @return
	 */
	public Cursor filtraLembretePorCarroQuery(int idCarro, Context context){
		try {
			String query = "SELECT strftime('%d/%m/%Y', a.date) as date, a.desc, b.marca, a.mostra, a._id FROM "+ TABELA_LEMBRETE +" a INNER JOIN "+ TABELA_CARRO +" b ON a.carro_id=b._id";
			if (idCarro != 0){
				query += " WHERE a.carro_id=?";
				query += " ORDER BY date DESC";
				return bancoDeDados.rawQuery(query, new String[]{String.valueOf(idCarro)});
			}else{
				query += " ORDER BY date DESC";
				return bancoDeDados.rawQuery(query, null);
			}
			
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), context);
			return null;
		}
	    
	}

	/**
	 * Grava (insert ou update) um abastecimento no banco
	 * @param context
	 * @param desc
	 * @param data
	 * @param idCarro
	 * @param idLembrete
	 */
	public int gravarLembreteQuery(Context context, String desc, StringBuilder data, boolean notifica, int idCarro,  int idLembrete) {
		try {

			
			String sql = "";
			int sucesso = 0;
			int mostra = notifica ? 1 : 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			Date objDate = new Date();
			String[] dataSplit = data.toString().split("/");
			
			objDate.setDate(Integer.parseInt(dataSplit[0]));
			objDate.setMonth(Integer.parseInt(dataSplit[1]) - 1);
			objDate.setYear(Integer.parseInt(dataSplit[2]) - 1900);
			String date = sdf.format(objDate);
			
			if (idLembrete != 0) {
				sql = "UPDATE " + TABELA_LEMBRETE  +
						  			  " SET desc = '" + desc + "', " +
						  			  "date = '" + date + "', " +
						  			  "mostra = '" + mostra + "', " +
									  "carro_id = '" + idCarro + "'" +
									  " WHERE _id = " + idLembrete;
				sucesso = 2;
			} else {
				sql = "INSERT INTO " + TABELA_LEMBRETE+ " (desc, date, mostra, carro_id) " +
					  "VALUES ("+ "'" + desc + "', " +
								  "'" + date + "', " +
								  "'" + mostra + "', " +
								  "'" + idCarro + "'" +
								  ")";
				
				sucesso = 1;
			}
			
			bancoDeDados.execSQL(sql);
			return sucesso;
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_SALVAR_EDITAR + e.getMessage(), context);
			return 0;
		}
	}

	/**
	 * Excluir um abastecimento do banco
	 * @param context
	 * @param idLembrete
	 */
	public boolean excluirLembreteQuery(Context context, int idLembrete) {
		try {

			String sql = "DELETE FROM " + TABELA_LEMBRETE+ " WHERE _id = " + idLembrete;

			bancoDeDados.execSQL(sql);

			return true;

		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_EXCLUIR + e.getMessage(), context);
			return false;
		}
	}
	
	public Cursor filtraLembretesHoje(Context context){
		try {
			Date date = new Date();
			String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
			String query = "SELECT strftime('%d/%m/%Y', a.date) as date, a.desc, b.marca, a._id " +
						   "FROM "+ TABELA_LEMBRETE +" a INNER JOIN "+ TABELA_CARRO +" b ON a.carro_id=b._id " +
					   	   "WHERE a.date = ? AND mostra = 1";
			return bancoDeDados.rawQuery(query, new String[]{String.valueOf(currentDate)});
			
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.ERRO_CARREGAR_REGISTRO + e.getMessage(), context);
			return null;
		}
	}
	
	public void marcaParaNaoMostrar(Context context){
		try {
			Cursor cursorLembretesHoje = filtraLembretesHoje(context);
			String query = "";
			if (cursorLembretesHoje.moveToFirst()) {
			    do {
			    	query = "UPDATE " + TABELA_LEMBRETE + " " +
			    			"SET mostra = 0 WHERE _id = " + cursorLembretesHoje.getInt(cursorLembretesHoje.getColumnIndex("_id"));
			    	bancoDeDados.execSQL(query);
			    } while (cursorLembretesHoje.moveToNext());
			}
			
		} catch (Exception e) {
			util.mostraMensagem(Messages.ERRO, Messages.BANCO_ERRO_SALVAR_EDITAR + e.getMessage(), context);
		}
	}

	// ------------------------------------------------------------------------
	// -------------------------------- FIM - LEMBRETES      ------------------
	// ------------------------------------------------------------------------
	
}
