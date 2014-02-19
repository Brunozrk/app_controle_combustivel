package com.br.uteis;

public interface Messages {

	String SUCESSO_CADASTRO = "Registro incluido com sucesso!";
	String SUCESSO_EDICAO = "Registro atualizado com sucesso!";
	String SUCESSO_EXCLUSAO = "Registro excluido com sucesso!";

	String CAMPO_OBRIGATORIO = "Existem campos obrigatórios que não foram preenchidos.";
	String CAMPO_NAO_PODE_SER_ZERO = "Os campos não podem ter o valor zero (0).";
	
	String CONFIRMA_EXCLUSAO = "Deseja realmente excluir o registro? ";
	String AVISO_ABASTECIMENTOS_DO_CARRO = "Os abastecimentos relacionados a esse carro também serão removidos.";
	
	String NAO_HA_CARRO_CADASTRADO = "Para acessar essa página você precisa ter pelo menos um carro cadastrado";
	
	String ERRO_LISTAR = "Erro ao carregar a lista: ";
	String ERRO_CARREGAR_REGISTRO = "Erro ao carregar o registro: ";
	
	String BANCO_ERRO_ABRIR_CRIAR = "Erro ao abrir/criar o Banco de Dados: ";
	String BANCO_ERRO_FECHAR = "Erro ao fechar o Banco de Dados: ";
	String BANCO_ERRO_SALVAR_EDITAR = "Ocorreu um erro ao salvar/atualizar o registro: ";
	String BANCO_ERRO_EXCLUIR = "Ocorreu um erro ao excluir o registro: ";
}
