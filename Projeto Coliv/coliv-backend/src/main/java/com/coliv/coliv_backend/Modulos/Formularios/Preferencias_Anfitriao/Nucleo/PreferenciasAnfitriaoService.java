package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.IPreferenciasAnfitriao;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciaNaoEncontradaUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciasAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciaAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class PreferenciasAnfitriaoService implements IPreferenciasAnfitriao {

    @Autowired
    private PreferenciasAnfitriaoRepository par;
    @Autowired
    private ApplicationEventPublisher publisher;

    public List<PreferenciasAnfitriao> listar() {
        return par.findAll();
    }

    public PreferenciasAnfitriao buscarPorId(Long id) {
        return par.findById(id).orElseThrow(() -> new PreferenciaAnfitriaoIDNaoEncontrado(id));
    }

    public PreferenciasAnfitriao criarPreferencia(Long userId, PreferenciasAnfitriaoDTO preferenciasDTO) {
        PreferenciasAnfitriao preferencias = new PreferenciasAnfitriao(preferenciasDTO.presencaAnimais(),
                preferenciasDTO.horariosParaVisita(), preferenciasDTO.politicaDeLimpeza(),
                preferenciasDTO.regrasDaCasa(), preferenciasDTO.perfilColegaDesejado(), userId);

        preferencias = par.save(preferencias);

        return preferencias;
    }

    public PreferenciasAnfitriao editarPreferencias(Long id, PreferenciasAnfitriaoDTO preferenciasDTO) {
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
    public PreferenciasAnfitriaoDTO getPreferenciasAnfitriao(Long anfitriaoId) {
        PreferenciasAnfitriao pa = par.findByAnfitriaoId(anfitriaoId).orElseThrow(() -> new
                PreferenciaNaoEncontradaUsandoReferencia(anfitriaoId));

        return new PreferenciasAnfitriaoDTO(pa.getPresencaAnimais(),
                pa.getHorariosParaVisita(), pa.getPoliticaDeLimpeza(), pa.getRegrasDaCasa(), pa.getPerfilColegaDesejado());
    }

    @EventListener
    public void eventoAnfitriaoCriado(UsuarioAnfitriaoCriado evento) {
        PreferenciasAnfitriao preferencias = new PreferenciasAnfitriao();
        preferencias.setAnfitriaoId(evento.anfitriaoId());

        par.save(preferencias);
    }

    @EventListener
    public void eventoAnfitriaoExcluido(AnfitriaoExcluido evento) {
         PreferenciasAnfitriao preferencia =  par.findByAnfitriaoId(evento.anfitriaoId()).orElseThrow(() ->  new
                 PreferenciaNaoEncontradaUsandoReferencia(evento.anfitriaoId()));

         par.deleteById(preferencia.getId());
    }
}
