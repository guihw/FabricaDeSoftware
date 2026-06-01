package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteAceito;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteAceitoDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteInfoDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.MembroInfoDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.NovoMembroDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro.Membro;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro.MembroService;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupoServiceTest {

    @Mock
    private GrupoRepository grupoRepository;
    @Mock
    private MembroService membroService;
    @Captor
    ArgumentCaptor<Grupo> captor;

    @InjectMocks
    private GrupoService grupoService;

    Long id = 1L, cid = 2L;
    TipoUsuario tipoA = TipoUsuario.ANFITRIAO;
    TipoUsuario tipoC = TipoUsuario.COLEGA;

    Grupo grupo = new Grupo.Builder().
            anfitriaoId(id).
            id(id).
            nomeGrupo("Grupo").
            membros(new ArrayList<>(){{
                Grupo g = new Grupo.Builder().id(id).build();

                add(new Membro.Builder().
                        id(id).
                        usuarioId(id).
                        tipoUsuario(tipoA).
                        grupo(g).
                        build());
                add(new Membro.Builder().
                        id(cid).
                        usuarioId(id).
                        tipoUsuario(tipoC).
                        grupo(g).
                        build());
            }}).
            build();
    NovoMembroDTO membroADTO = new NovoMembroDTO(id, tipoA);
    NovoMembroDTO membroCDTO = new NovoMembroDTO(cid, tipoC);
    Membro membroA = new Membro.Builder().
            id(id).
            usuarioId(id).
            tipoUsuario(tipoA).
            grupo(grupo).
            build();
    Membro membroC = new Membro.Builder().
            id(cid).
            usuarioId(cid).
            tipoUsuario(tipoC).
            grupo(grupo).
            build();
    ConviteAceito evento = new ConviteAceito(new ConviteAceitoDTO(id, cid, new ConviteInfoDTO(id, id,
            BigDecimal.valueOf(299.99), "Condicoes", LocalDateTime.now())));

    @Test
    @DisplayName("Buscar Por Usuario Anfitriao ID Retorno Positivo")
    public void buscarPorUsuarioAnfitriaoIDRetornoPositivo() {
        Long id = 1L;
        TipoUsuario tipo = TipoUsuario.ANFITRIAO;

        when(grupoRepository.findByAnfitriaoId(id)).thenReturn(Optional.of(grupo));

        GrupoResponseDTO retorno = grupoService.buscarPorUsuarioId(id, tipo);
        verify(grupoRepository, times(1)).findByAnfitriaoId(id);

        assertThat(retorno).isNotNull();
        assertThat(retorno.nomeGrupo()).isEqualTo("Grupo");
        assertThat(retorno.membroList().stream().toList()).isEqualTo(grupo.getMembros().stream().
                map(membro -> new MembroInfoDTO(membro.getUsuarioId(), membro.getTipoUsuario())).toList());
    }

    @Test
    @DisplayName("Buscar Por Usuario Anfitriao ID Retorno Negativo")
    public void buscarPorUsuarioAnfitriaoIDRetornoNegativo() {
        Long id = -1L;
        TipoUsuario tipo = TipoUsuario.ANFITRIAO;

        when(grupoRepository.findByAnfitriaoId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> grupoService.buscarPorUsuarioId(id, tipo)).isInstanceOf(
                GrupoNaoEncontradoUsandoReferencia.class);
        verify(grupoRepository, times(1)).findByAnfitriaoId(id);
    }

    @Test
    @DisplayName("Buscar Por Usuario Colega ID Retorno Positivo")
    public void buscarPorUsuarioColegaIDRetornoPositivo() {

        when(grupoRepository.findByColegaId(cid)).thenReturn(Optional.of(grupo));

        GrupoResponseDTO retorno = grupoService.buscarPorUsuarioId(cid, tipoC);
        verify(grupoRepository, times(1)).findByColegaId(cid);

        assertThat(retorno).isNotNull();
        assertThat(retorno.nomeGrupo()).isEqualTo("Grupo");
        assertThat(retorno.membroList().stream().toList()).isEqualTo(grupo.getMembros().stream().
                map(membro -> new MembroInfoDTO(membro.getUsuarioId(), membro.getTipoUsuario())).toList());
    }

    @Test
    @DisplayName("Buscar Por Usuario Colega ID Retorno Negativo")
    public void buscarPorUsuarioColegaIDRetornoNegativo() {
        Long id = 1L, cid = 2L;
        TipoUsuario tipo = TipoUsuario.COLEGA;

        when(grupoRepository.findByColegaId(cid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> grupoService.buscarPorUsuarioId(cid, tipo)).isInstanceOf(
                GrupoNaoEncontradoUsandoReferencia.class);
        verify(grupoRepository, times(1)).findByColegaId(cid);
    }

    @Test
    @DisplayName("Evento Convite Aceito Teste 1")
    public void eventoConviteAceitoTeste1() {
        Grupo grupo2 = new Grupo.Builder().
                id(id).
                anfitriaoId(id).
                nomeGrupo("Grupo").
                build();

        when(grupoRepository.findByAnfitriaoId(id)).thenReturn(Optional.empty());
        when(grupoRepository.save(any(Grupo.class))).
                thenReturn(grupo2).
                thenReturn(grupo);
        when(membroService.novoMembro(any(NovoMembroDTO.class), any(Grupo.class))).
                thenReturn(membroA).
                thenReturn(membroC);

        grupoService.conviteAceitoEvento(evento);
        verify(grupoRepository, times(1)).findByAnfitriaoId(id);
        verify(grupoRepository, times(2)).save(any(Grupo.class));
        verify(membroService, times(2)).novoMembro(any(NovoMembroDTO.class), any(Grupo.class));
    }

    @Test
    @DisplayName("Evento Convite Aceito Teste 2")
    public void eventoConviteAceitoTeste2() {
        Grupo grupo2 = new Grupo.Builder().
                anfitriaoId(id).
                id(id).
                nomeGrupo("Grupo").
                membros(new ArrayList<>(){{
                    Grupo g = new Grupo.Builder().id(id).build();

                    add(new Membro.Builder().
                            id(id).
                            usuarioId(id).
                            tipoUsuario(tipoA).
                            grupo(g).
                            build());
                    add(new Membro.Builder().
                            id(cid).
                            usuarioId(id).
                            tipoUsuario(tipoC).
                            grupo(g).
                            build());
                    add(new Membro.Builder().
                            id(cid + 1).
                            usuarioId(id + 1).
                            tipoUsuario(tipoC).
                            grupo(g).
                            build());
                }}).
                build();

        when(grupoRepository.findByAnfitriaoId(id)).thenReturn(Optional.of(grupo));
        when(grupoRepository.save(any(Grupo.class))).
                thenReturn(grupo2);

        grupoService.conviteAceitoEvento(evento);
        verify(grupoRepository, times(1)).findByAnfitriaoId(id);
        verify(grupoRepository, times(1)).save(any(Grupo.class));
        verify(membroService, times(1)).novoMembro(any(NovoMembroDTO.class), any(Grupo.class));
    }

    @Test
    @DisplayName("Evento Usuario Removido do Imovel")
    @Disabled
    void eventoUsuarioRemovidoDoImovel() {}
}