package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByBillingId(String billingId);
    Optional<Pagamento> findByUsuarioId(Long usuarioId);
}
