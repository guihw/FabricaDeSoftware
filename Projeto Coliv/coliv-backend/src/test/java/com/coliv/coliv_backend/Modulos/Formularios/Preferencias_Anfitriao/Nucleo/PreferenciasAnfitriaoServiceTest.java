package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciaAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.IUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenciasAnfitriaoServiceTest {

    @Mock
    private PreferenciasAnfitriaoRepository par;
    @Mock
    private IUsuario iUsuario;

    @InjectMocks
    private PreferenciasAnfitriaoService pas;

    @Test
    @DisplayName("Buscar Preferencia Anfitriao Retorno Positivo")
    public void buscarPreferenciaAnfitriaoRetornoPositivo() {
        Long id = 1L;
        PreferenciasAnfitriao pa = new PreferenciasAnfitriao(true, "10:00",
                "Não sujo", "Livre", "Comunicativo");
        pa.setId(id);

        when(par.findById(id)).thenReturn(Optional.of(pa));

        PreferenciasAnfitriao preferencia = pas.buscarPorId(id);

        assertThat(preferencia.getHorariosParaVisita()).isEqualTo("10:00");
        assertThat(preferencia.getPerfilColegaDesejado()).isEqualTo("Comunicativo");
    }

    @Test
    @DisplayName("Buscar Preferencia Anfitriao Retorno Negativo")
    public void buscarPreferenciaAnfitriaoRetornoNegativo() {
        Long id = -1L;
        PreferenciasAnfitriao pa = new PreferenciasAnfitriao(true, "10:00",
                "Não sujo", "Livre", "Comunicativo");
        pa.setId(id);

        when(par.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pas.buscarPorId(id)).isInstanceOf(PreferenciaAnfitriaoIDNaoEncontrado.class);

        verify(par, times(1)).findById(id);
    }
}