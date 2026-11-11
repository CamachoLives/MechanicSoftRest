package com.mechanicsoft.Features.ConsultaPublica.service;

import com.mechanicsoft.Features.Clientes.entity.TipoDocumento;
import com.mechanicsoft.Features.ConsultaPublica.dto.ConsultaResponse;

public interface ConsultaPublicaService {
    ConsultaResponse consultar(String placa, TipoDocumento tipoDocumento, String numeroDocumento);
}
