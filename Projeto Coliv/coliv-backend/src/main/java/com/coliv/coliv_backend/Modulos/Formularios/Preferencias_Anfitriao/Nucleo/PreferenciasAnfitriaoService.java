package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.IPreferenciasAnfitriao;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PrefereciasAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciaAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class PreferenciasAnfitriaoService implements IPreferenciasAnfitriao {

    @Autowired
    private PreferenciasAnfitriaoRepository par;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private IUsuario iUsuario;

    public List<PreferenciasAnfitriao> listar() {
        return par.findAll();
    }

    public PreferenciasAnfitriao buscarPorId(Long id) {
        return par.findById(id).orElseThrow(() -> new PreferenciaAnfitriaoIDNaoEncontrado(id));
    }

    public PreferenciasAnfitriao criarPreferencia(Long userId, PrefereciasAnfitriaoDTO preferenciasDTO) {
        PreferenciasAnfitriao preferencias = new PreferenciasAnfitriao(preferenciasDTO.presencaAnimais(),
                preferenciasDTO.horariosParaVisita(), preferenciasDTO.politicaDeLimpeza(),
                preferenciasDTO.regrasDaCasa(), preferenciasDTO.perfilColegaDesejado());

        preferencias = par.save(preferencias);

        iUsuario.adicionarPreferenciaAnfitriao(userId ,preferencias.getId());

        return preferencias;
    }

    public PreferenciasAnfitriao editarPreferencias(Long id, PrefereciasAnfitriaoDTO preferenciasDTO) {
        PreferenciasAnfitriao original = par.findById(id).orElseThrow(() -> new PreferenciaAnfitriaoIDNaoEncontrado(id));

        original.setPresencaAnimais(preferenciasDTO.presencaAnimais());
        if (preferenciasDTO.horariosParaVisita() != null) {
            original.setHorariosParaVisita(preferenciasDTO.horariosParaVisita());
        }
        if (preferenciasDTO.politicaDeLimpeza() != null && !preferenciasDTO.politicaDeLimpeza().isBlank()) {
            original.setPoliticaDeLimpeza(preferenciasDTO.politicaDeLimpeza());
        }
        if (preferenciasDTO.regrasDaCasa() != null && !preferenciasDTO.regrasDaCasa().isBlank()) {
            original.setRegrasDaCasa(preferenciasDTO.regrasDaCasa());
        }
        if (preferenciasDTO.perfilColegaDesejado() != null && !preferenciasDTO.perfilColegaDesejado().isBlank()) {
            original.setPerfilColegaDesejado(preferenciasDTO.perfilColegaDesejado());
        }

        return par.save(original);
    }

    public void excluir(Long id) {
        par.findById(id).orElseThrow(() -> new PreferenciaAnfitriaoIDNaoEncontrado(id));
        par.deleteById(id);
    }

    @Override
    public PrefereciasAnfitriaoDTO getPreferenciasAnfitriao(Long id) {
        return par.findById(id).map(pa -> new PrefereciasAnfitriaoDTO(pa.getPresencaAnimais(),
                        pa.getHorariosParaVisita(), pa.getPoliticaDeLimpeza(), pa.getRegrasDaCasa(), pa.getPerfilColegaDesejado())).
                orElseThrow(() -> new PreferenciaAnfitriaoIDNaoEncontrado(id));
    }
}
