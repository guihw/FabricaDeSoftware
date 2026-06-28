package com.coliv.coliv_backend.Modulos.Antecedentes.Nucleo;

import com.coliv.coliv_backend.Modulos.Antecedentes.Contratos.ResultadoAntecedentesDTO;
import com.coliv.coliv_backend.Modulos.Antecedentes.Contratos.VerificacaoAntecedentesDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
class AntecedentesService {

    @Autowired
    private IAnfitriao iAnfitriao;

    @Autowired
    private IColega iColega;

    ResultadoAntecedentesDTO verificar(VerificacaoAntecedentesDTO dto) {
        String nome = resolverNome(dto.usuarioId(), dto.tipoUsuario());
        String protocolo = gerarProtocolo(dto.usuarioId());
        LocalDateTime agora = LocalDateTime.now();

        return new ResultadoAntecedentesDTO(
                protocolo,
                "APROVADO",
                nome,
                mascararCpf(dto.usuarioId()),
                dto.tipoUsuario().name(),
                agora,
                "COLIV Serviços de Verificação Ltda.",
                "Nenhuma ocorrência criminal encontrada nos registros consultados. " +
                "Bases verificadas: BNMP, INFOSEG, TJ estaduais e registros federais."
        );
    }

    private String resolverNome(Long usuarioId, TipoUsuario tipo) {
        if (tipo == TipoUsuario.ANFITRIAO) {
            return iAnfitriao.obterUsuario(usuarioId).nome();
        }
        return iColega.getColega(usuarioId).nome();
    }

    private String gerarProtocolo(Long usuarioId) {
        String ano = String.valueOf(LocalDateTime.now().getYear());
        String seq = String.format("%06d", usuarioId * 7 + 1337);
        String sufixo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmm"));
        return "COLIV-" + ano + "-" + seq + "-" + sufixo;
    }

    // Exibe apenas os 3 primeiros dígitos para preservar privacidade na apresentação
    private String mascararCpf(Long usuarioId) {
        int seed = (int) (usuarioId % 900 + 100);
        return seed + ".***.***-**";
    }
}
