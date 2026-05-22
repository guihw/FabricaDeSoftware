package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos;

import java.util.List;

public interface IDadosImovel {

    public DadosImovelDTO getDadosImovel(Long anfitriaoId);

    public List<DadosImovelDTO> obterListaDeDados();
}
