package com.br.uteis;

public interface Variaveis {

	String CAMPOS_CARRO[] = {"marca", "_id"};
	String CAMPOS_ABASTECIMENTO[] = {"strftime('%d/%m/%Y',date)", "odometro", "litros", "media", "obs", "_id"};
	String CAMPOS_DATAS_ABASTECIMENTO[] = {"strftime('%m/%Y',date)", "_id"};
	String CAMPOS_LEMBRETE[] = {"date", "desc", "marca", "_id"};
}
