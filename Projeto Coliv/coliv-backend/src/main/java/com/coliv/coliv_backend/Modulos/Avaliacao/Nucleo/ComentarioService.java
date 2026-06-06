package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
class ComentarioService implements IComentario {

    private final ComentarioRepository repository;

    ComentarioService(ComentarioRepository repository) {
        this.repository = repository;
    }

    public Comentario criar(ComentarioDTO dto) {

        Comentario comentario = new Comentario();

        comentario.setCardId(dto.cardId());
        comentario.setUsuarioId(dto.usuarioId());
        comentario.setParentId(dto.parentId());
        comentario.setTexto(dto.texto());
        comentario.setData(LocalDateTime.now());

        return repository.save(comentario);
    }

    public List<Comentario> listar() {
        return repository.findAll();
    }

    public Comentario buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ComentarioIdNaoEncontrado(id));
    }

    public Comentario editar(Long id,
                             ComentarioDTO dto) {

        Comentario comentario =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ComentarioIdNaoEncontrado(id));

        comentario.setCardId(dto.cardId());
        comentario.setUsuarioId(dto.usuarioId());
        comentario.setParentId(dto.parentId());
        comentario.setTexto(dto.texto());

        return repository.save(comentario);
    }

    public void excluir(Long id) {

        repository.findById(id)
                .orElseThrow(() ->
                        new ComentarioIdNaoEncontrado(id));

        repository.deleteById(id);
    }

    @Override
    public ComentarioResponse getComentario(Long id) {

        Comentario comentario =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ComentarioIdNaoEncontrado(id));

        return new ComentarioResponse(
                comentario.getId(),
                comentario.getCardId(),
                comentario.getUsuarioId(),
                comentario.getParentId(),
                comentario.getData(),
                comentario.getTexto()
        );
    }
}