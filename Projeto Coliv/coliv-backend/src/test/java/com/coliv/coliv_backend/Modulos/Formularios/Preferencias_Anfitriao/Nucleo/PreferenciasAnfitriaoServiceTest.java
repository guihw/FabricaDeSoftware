package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciaNaoEncontradaUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciasAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciaAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenciasAnfitriaoServiceTest {

    @Mock
    private PreferenciasAnfitriaoRepository par;

    @InjectMocks
    private PreferenciasAnfitriaoService pas;

    @Captor
    ArgumentCaptor<PreferenciasAnfitriao> paCaptor;

    @Test
    @DisplayName("Buscar Preferencia Anfitriao Retorno Positivo")
    public void buscarPreferenciaAnfitriaoRetornoPositivo() {
        Long id = 1L;
        PreferenciasAnfitriao pa = new PreferenciasAnfitriao(true, LocalTime.of(10, 0),
                "Não sujo", "Livre", "Comunicativo");
        pa.setId(id);

        when(par.findById(id)).thenReturn(Optional.of(pa));

        PreferenciasAnfitriao preferencia = pas.buscarPorId(id);

        assertThat(preferencia.getHorariosParaVisita().toString()).isEqualTo("10:00");
        assertThat(preferencia.getPerfilColegaDesejado()).isEqualTo("Comunicativo");
    }

    @Test
    @DisplayName("Buscar Preferencia Anfitriao Retorno Negativo")
    public void buscarPreferenciaAnfitriaoRetornoNegativo() {
        Long id = -1L;
        PreferenciasAnfitriao pa = new PreferenciasAnfitriao();
        pa.setId(id);

        when(par.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pas.buscarPorId(id)).isInstanceOf(PreferenciaAnfitriaoIDNaoEncontrado.class);

        verify(par, times(1)).findById(id);
    }

    @Test
    @DisplayName("Criar Preferencia")
    public void criarPreferencia () {
        Long id = 1L, userId = 1L;

        PreferenciasAnfitriaoDTO dto = new PreferenciasAnfitriaoDTO(false, "20:30",
                "Limpeza", "Agendar visitas com antecedencia", "Silencioso");

        PreferenciasAnfitriao salvo = new PreferenciasAnfitriao(false, LocalTime.of(20, 30),
                "Limpeza", "Agendar visitas com antecedencia", "Silencioso");
        salvo.setId(id);

        when(par.save(any())).thenReturn(salvo);

        PreferenciasAnfitriaoDTO preferencias = pas.criarPreferencia(userId, dto);
        verify(par, times(1)).save(paCaptor.capture());
        PreferenciasAnfitriao capturado = paCaptor.getValue();

        assertThat(capturado).isNotNull();
        assertThat(capturado.getId()).isNull();
    }

    @Test
    @DisplayName("Editar Preferencia Anfitriao Teste com Retorno Positivo")
    public void editarPreferenciaAnfitriaoTesteRetornoPositivo () {
        Long id = 1L;
        PreferenciasAnfitriao preferencia = new PreferenciasAnfitriao(false, LocalTime.of(20, 30),
                "Limpeza", "Agendar visitas com antecedencia", "Silencioso");
        PreferenciasAnfitriao preferenciaEditada = new PreferenciasAnfitriao(false,
                LocalTime.of(20, 30), "Limpeza", "Agendar visitas é opcional",
                "Trabalhador");
        PreferenciasAnfitriaoDTO dto = new PreferenciasAnfitriaoDTO(false,
                "20:30", "Limpeza", "Agendar visitas é opcional",
                "Trabalhador");
        preferencia.setId(id);
        preferenciaEditada.setId(id);

        when(par.findById(id)).thenReturn(Optional.of(preferencia));
        when(par.save(any())).thenReturn(preferenciaEditada);

        PreferenciasAnfitriaoDTO update = pas.editarPreferencias(id, dto);
        verify(par, times(1)).findById(id);
        verify(par, times(1)).save(paCaptor.capture());
        PreferenciasAnfitriao capturado = paCaptor.getValue();

        assertThat(update).isNotNull();
        assertThat(capturado.getRegrasDaCasa()).isEqualTo(update.regrasDaCasa());
        assertThat(update.perfilColegaDesejado()).isEqualTo("Trabalhador");
    }

    @Test
    @DisplayName("Editar Preferencia Anfitriao Teste com Retorno Negativo")
    public void editarPreferenciaAnfitriaoTesteRetornoNegativo () {
        Long id = -1L;
        PreferenciasAnfitriaoDTO dto = new PreferenciasAnfitriaoDTO(false, "20:30",
                "Limpeza", "Agendar visitas com antecedencia", "Silencioso");

        when(par.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pas.editarPreferencias(id, dto)).isInstanceOf(PreferenciaAnfitriaoIDNaoEncontrado.class);
        verify(par, times(1)).findById(id);
        verify(par, never()).save(any());
    }

    @Test
    @DisplayName("Remover Preferencia Anfitriao Retorno Positivo")
    public void removerPreferenciaAnfitriaoRetornoPositivo() {
        Long id = 1L;
        PreferenciasAnfitriao pa = new PreferenciasAnfitriao();
        pa.setId(id);

        when(par.findById(id)).thenReturn(Optional.of(pa));
        pas.excluir(id);

        verify(par, times(1)).findById(id);
        verify(par, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Remover Preferencia Anfitriao Retorno Negativo")
    public void removerPreferenciaAnfitriaoRetornoNegativo() {
        Long id = 1L;

        when(par.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pas.excluir(id)).isInstanceOf(PreferenciaAnfitriaoIDNaoEncontrado.class);

        verify(par, times(1)).findById(id);
        verify(par, never()).deleteById(id);
    }

    @Test
    @DisplayName("Get Preferencia Anfitriao Interno Retorno Positivo")
    public void getPreferenciaAnfitriaoInternoRetornoPositivo() {
        Long id = 1L, aid = 2L;
        PreferenciasAnfitriao preferencias = new PreferenciasAnfitriao(false, LocalTime.of(20, 30),
                "Limpeza", "Agendar visitas com antecedência", "Silencioso");
        preferencias.setId(id);
        preferencias.setAnfitriaoId(aid);

        when(par.findByAnfitriaoId(aid)).thenReturn(Optional.of(preferencias));

        PreferenciasAnfitriaoDTO retornando = pas.getPreferenciasAnfitriao(aid);
        verify(par, times(1)).findByAnfitriaoId(aid);

        assertThat(retornando.presencaAnimais()).isEqualTo(false);
        assertThat(retornando.perfilColegaDesejado()).isEqualTo("Silencioso");
        assertThat(retornando.regrasDaCasa()).isEqualTo("Agendar visitas com antecedência");
    }

    @Test
    @DisplayName("Get Preferencia Anfitriao Interno Retorno Negativo")
    public void getPreferenciaAnfitriaoInternoRetornoNegativo() {
        Long aid = -2L;

        when(par.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pas.getPreferenciasAnfitriao(aid)).isInstanceOf(PreferenciaNaoEncontradaUsandoReferencia.class);
        verify(par, times(1)).findByAnfitriaoId(aid);
    }

    @Test
    @DisplayName("Anfitriao Criado Evento")
    public void anfitriaoCriadoEvento() {
        Long aid = 1L;
        PreferenciasAnfitriao preferencias = new PreferenciasAnfitriao();
        UsuarioAnfitriaoCriado evento = new UsuarioAnfitriaoCriado(aid);
        preferencias.setId(aid);
        preferencias.setAnfitriaoId(aid);

        when(par.save(any(PreferenciasAnfitriao.class))).thenReturn(preferencias);

        pas.eventoAnfitriaoCriado(evento);
        verify(par, times(1)).save(paCaptor.capture());
        PreferenciasAnfitriao captura = paCaptor.getValue();

        assertThat(captura.getId()).isNull();
        assertThat(captura.getAnfitriaoId()).isEqualTo(preferencias.getAnfitriaoId());
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento Retorno Positivo")
    public void anfitriaoExcluidoEventoRetornoPositivo() {
        Long aid = 1L;
        PreferenciasAnfitriao preferencias = new PreferenciasAnfitriao();
        AnfitriaoExcluido evento = new AnfitriaoExcluido(aid);
        preferencias.setId(aid);
        preferencias.setAnfitriaoId(aid);

        when(par.findByAnfitriaoId(aid)).thenReturn(Optional.of(preferencias));

        pas.eventoAnfitriaoExcluido(evento);
        verify(par, times(1)).findByAnfitriaoId(aid);
        verify(par, times(1)).deleteById(aid);
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento Retorno Negativo")
    public void anfitriaoExcluidoEventoRetornoNegativo() {
        Long aid = -1L;
        AnfitriaoExcluido evento = new AnfitriaoExcluido(aid);

        when(par.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pas.eventoAnfitriaoExcluido(evento)).isInstanceOf(PreferenciaNaoEncontradaUsandoReferencia.class);
        verify(par, times(1)).findByAnfitriaoId(aid);
        verify(par, never()).deleteById(aid);
    }
}