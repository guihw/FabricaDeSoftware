package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo.Grupo;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo.GrupoRepository;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemGrupoServiceTest {

    @Mock
    private MensagemGrupoRepository msgRepository;
    @Mock
    private GrupoRepository grupoRepository;

    @InjectMocks
    private MensagemGrupoService msgService;

    Long id = 1L;
    MensagemGrupo mensagem = new MensagemGrupo(id, "Texto", null, id, id, TipoUsuario.ANFITRIAO);
    MensagemResponseDTO responseDTO = new MensagemResponseDTO(id, mensagem.getTexto(), mensagem.getTipoUsuario(),
            mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId());
    MensagemRequestDTO requestDTO = new MensagemRequestDTO(mensagem.getTexto(), mensagem.getTipoUsuario(),
            mensagem.getArquivoId());

    @Test
    void buscarPorGrupoId() {
        when(msgRepository.findByGrupoId(mensagem.getGrupoId())).thenReturn(List.of(mensagem));

        List<MensagemResponseDTO> retorno = msgService.buscarPorGrupoId(mensagem.getGrupoId());
        verify(msgRepository, times(1)).findByGrupoId(mensagem.getGrupoId());

        assertThat(retorno).isNotEmpty();
    }

    @Test
    void buscarPorGrupoId2() {
        Long id = mensagem.getSequencialId() + 1;

        when(msgRepository.findByGrupoId(id)).thenReturn(List.of());

        List<MensagemResponseDTO> retorno = msgService.buscarPorGrupoId(id);
        verify(msgRepository, times(1)).findByGrupoId(id);

        assertThat(retorno).isEmpty();
    }

    @Test
    void buscarPorUsuarioId() {
        when(msgRepository.findByUsuarioIdAndTipoUsuario(mensagem.getUsuarioId(), mensagem.getTipoUsuario())).
                thenReturn(List.of(mensagem));

        List<MensagemResponseDTO> retorno = msgService.buscarPorUsuarioId(mensagem.getUsuarioId(),
                mensagem.getTipoUsuario());
        verify(msgRepository, times(1)).findByUsuarioIdAndTipoUsuario(mensagem.getUsuarioId(),
                mensagem.getTipoUsuario());

        assertThat(retorno).isNotEmpty();
    }

    @Test
    void buscarPorUsuarioId2() {
        Long id = mensagem.getSequencialId() + 1;

        when(msgRepository.findByUsuarioIdAndTipoUsuario(id, mensagem.getTipoUsuario())).
                thenReturn(List.of());

        List<MensagemResponseDTO> retorno = msgService.buscarPorUsuarioId(id, mensagem.getTipoUsuario());
        verify(msgRepository, times(1)).findByUsuarioIdAndTipoUsuario(id,
                mensagem.getTipoUsuario());

        assertThat(retorno).isEmpty();
    }

    @Test
    void buscarPorGrupoEUsuario() {
        when(msgRepository.findByGrupoIdAndUsuarioIdAndTipoUsuario(mensagem.getGrupoId(), mensagem.getUsuarioId(),
                mensagem.getTipoUsuario())).thenReturn(List.of(mensagem));

        List<MensagemResponseDTO> retorno = msgService.buscarPorGrupoEUsuario(mensagem.getGrupoId(),
                mensagem.getUsuarioId(), mensagem.getTipoUsuario());
        verify(msgRepository, times(1)).findByGrupoIdAndUsuarioIdAndTipoUsuario(
                mensagem.getGrupoId(), mensagem.getUsuarioId(), mensagem.getTipoUsuario());

        assertThat(retorno).isNotEmpty();
    }

    @Test
    void buscarPorGrupoEUsuario2() {
        Long id = mensagem.getSequencialId() + 1;

        when(msgRepository.findByGrupoIdAndUsuarioIdAndTipoUsuario(id, id, mensagem.getTipoUsuario())).
                thenReturn(List.of());

        List<MensagemResponseDTO> retorno = msgService.buscarPorGrupoEUsuario(id, id, mensagem.getTipoUsuario());
        verify(msgRepository, times(1)).findByGrupoIdAndUsuarioIdAndTipoUsuario(id, id,
                mensagem.getTipoUsuario());

        assertThat(retorno).isEmpty();
    }

    @Test
    void buscarPorChatETexto() {
        when(msgRepository.findByGrupoIdAndTextoContainingIgnoreCase(mensagem.getGrupoId(), mensagem.getTexto())).
                thenReturn(List.of(mensagem));

        List<MensagemResponseDTO> retorno = msgService.buscarPorChatETexto(mensagem.getGrupoId(), mensagem.getTexto());
        verify(msgRepository, times(1)).findByGrupoIdAndTextoContainingIgnoreCase(
                mensagem.getGrupoId(), mensagem.getTexto());

        assertThat(retorno).isNotEmpty();
    }

    @Test
    void buscarPorChatETexto2() {
        Long id = mensagem.getSequencialId() + 1;

        when(msgRepository.findByGrupoIdAndTextoContainingIgnoreCase(id, "Texto2")).
                thenReturn(List.of());

        List<MensagemResponseDTO> retorno = msgService.buscarPorChatETexto(id, "Texto2");
        verify(msgRepository, times(1)).findByGrupoIdAndTextoContainingIgnoreCase(id,
                "Texto2");

        assertThat(retorno).isEmpty();
    }

    @Test
    @Disabled
    void criarMensagem() {
        Grupo grupo = new Grupo.Builder().id(id).build();
        mensagem.setSequencialId(0L);
        MensagemGrupo mensagem2 = mensagem;
        mensagem2.setId(id);
        Grupo grupo2 = grupo;
        grupo2.setMensagensUsuario(new ArrayList<>(){{add(mensagem2);}});

        when(msgRepository.findMaxSequence(id)).thenReturn(id);
        when(msgRepository.save(any(MensagemGrupo.class))).thenReturn(mensagem2);
        when(grupoRepository.findById(id)).thenReturn(Optional.of(grupo));
        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupo2);

        MensagemResponseDTO retorno = msgService.criarMensagem(id, id, requestDTO);
        verify(msgRepository, times(1)).findMaxSequence(id);
        verify(msgRepository, times(1)).save(mensagem);
        verify(grupoRepository, times(1)).findById(id);
        verify(grupoRepository, times(1)).save(grupo);

        assertThat(retorno).isNotNull();
        assertThat(retorno.texto()).isEqualTo(requestDTO.texto());
    }
}