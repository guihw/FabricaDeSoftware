package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ColegaService implements IColega {

    private final ColegaRepository colegaRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao iAnfitriao;

    public ColegaService(
            ColegaRepository colegaRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher publisher
    ) {
        this.colegaRepository = colegaRepository;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
    }

    public List<ColegaResponse> listar() {

        return colegaRepository.findAll()
                .stream()
                .map(colega -> new ColegaResponse(
                        colega.getId(),
                        colega.getNome(),
                        colega.getEmail(),
                        colega.getDescricao(),
                        colega.getClassificacao(),
                        colega.preferenciaColegaId,
                        colega.getFotoPerfilId()
                ))
                .toList();
    }

    @Override
    public ColegaResponse getColega(Long id) {

        Colega colega = colegaRepository.findById(id)
                .orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        return new ColegaResponse(
                colega.getId(),
                colega.getNome(),
                colega.getEmail(),
                colega.getDescricao(),
                colega.getClassificacao(),
                colega.preferenciaColegaId,
                colega.getFotoPerfilId()
        );
    }

    @Override
    public List<ColegaResponse> getColegas(List<Long> ids) {

        return colegaRepository.findAllById(ids)
                .stream()
                .map(colega -> new ColegaResponse(
                        colega.getId(),
                        colega.getNome(),
                        colega.getEmail(),
                        colega.getDescricao(),
                        colega.getClassificacao(),
                        colega.preferenciaColegaId,
                        colega.getFotoPerfilId()
                ))
                .toList();
    }

    @Override
    public boolean verificarExistencia(Long id) {
        return colegaRepository.existsById(id);
    }

    @Override
    public Optional<ColegaCredenciaisDTO> buscarCredenciais(String email) {
        String emailLower = email != null ? email.toLowerCase() : "";
        return colegaRepository.findByEmail(emailLower)
                .map(a -> new ColegaCredenciaisDTO(a.getId(), a.getEmail(), a.getSenha()));

    }

    @Override
    public void ativarPlano(Long id) {
        Colega colega = colegaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Colega não encontrado. Id: " + id));

        colega.setPossuiPlano(true);
        colegaRepository.save(colega);
    }

    @Transactional
    public ColegaResponse createColega(CreateColegaRequest request) {

        if (request.nome() == null || request.nome().isBlank()) {
            throw new RuntimeException("Nome está faltando");
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new RuntimeException("Email está faltando");
        }

        if (request.password() == null || request.password().length() < 6) {
            throw new RuntimeException("Password precisa ter 6 caracteres");
        }

        if (request.cpf() == null || request.cpf().isBlank()) {
            throw new RuntimeException("CPF está faltando");
        }

        String emailLower = request.email() != null ? request.email().toLowerCase() : "";
        boolean colegaAlreadyExists =
                colegaRepository.existsByEmail(emailLower) || iAnfitriao.buscarCredenciais(emailLower).isPresent();

        if (colegaAlreadyExists) {
            throw new RuntimeException("Email já registrado");
        }

        String encryptedPassword =
                passwordEncoder.encode(request.password());

        Colega colega = new Colega();

        colega.setNome(request.nome());
        colega.setCpf(request.cpf());
        colega.setEmail(request.email().toLowerCase());
        colega.setSenha(encryptedPassword);

        Colega savedColega = colegaRepository.save(colega);

        publisher.publishEvent(
                new UsuarioColegaCriado(savedColega.getId())
        );

        return new ColegaResponse(
                savedColega.getId(),
                savedColega.getNome(),
                savedColega.getEmail(),
                savedColega.getDescricao(),
                savedColega.getClassificacao(),
                savedColega.preferenciaColegaId,
                savedColega.getFotoPerfilId()
        );
    }

    @Transactional
    public ColegaResponse editarColega(Long id, UpdateColegaRequest request) {

        Colega original = colegaRepository.findById(id)
                .orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        if (request.nome() != null && !request.nome().isBlank()) {
            original.setNome(request.nome());
        }

        if (request.cpf() != null && !request.cpf().isBlank()) {
            original.setCpf(request.cpf());
        }

        if (request.email() != null && !request.email().isBlank()) {
            original.setEmail(request.email());
        }

        if (request.password() != null && !request.password().isBlank()) {

            String encryptedPassword =
                    passwordEncoder.encode(request.password());

            original.setSenha(encryptedPassword);
        }

        if (request.descricao() != null && !request.descricao().isBlank()) {
            original.setDescricao(request.descricao());
        }
        if (request.fotoPerfilId() != null) {
            original.setFotoPerfilId(request.fotoPerfilId());
        }

        Colega updatedColega = colegaRepository.save(original);

        return new ColegaResponse(
                updatedColega.getId(),
                updatedColega.getNome(),
                updatedColega.getEmail(),
                updatedColega.getDescricao(),
                updatedColega.getClassificacao(),
                updatedColega.preferenciaColegaId,
                updatedColega.getFotoPerfilId()
        );
    }

    @Transactional
    public void excluir(Long id) {

        Colega colega = colegaRepository.findById(id)
                .orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        colegaRepository.delete(colega);

        publisher.publishEvent(
                new ColegaExcluido(id)
        );
    }
}