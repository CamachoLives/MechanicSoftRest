package com.mechanicsoft.Features.ConsultaPublica.service;

import com.mechanicsoft.Features.Clientes.entity.Cliente;
import com.mechanicsoft.Features.Clientes.entity.TipoDocumento;
import com.mechanicsoft.Features.ConsultaPublica.dto.ConsultaResponse;
import com.mechanicsoft.Features.OrdenesServicio.entity.OrdenServicio;
import com.mechanicsoft.Features.OrdenesServicio.repository.OrdenServicioRepository;
import com.mechanicsoft.Features.Vehiculos.entity.Vehiculo;
import com.mechanicsoft.Features.Vehiculos.repository.VehiculoRepository;
import com.mechanicsoft.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ConsultaPublicaServiceImpl implements ConsultaPublicaService {

    // Mensaje genérico para no revelar si la placa existe o si el documento está registrado.
    private static final String ERROR_NO_ENCONTRADO =
            "No se encontró ninguna moto asociada a los datos ingresados. Verifica la placa y tu documento.";

    private final VehiculoRepository vehiculoRepository;
    private final OrdenServicioRepository ordenRepository;

    public ConsultaPublicaServiceImpl(VehiculoRepository vehiculoRepository,
                                      OrdenServicioRepository ordenRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.ordenRepository = ordenRepository;
    }

    @Override
    public ConsultaResponse consultar(String placa, TipoDocumento tipoDocumento, String numeroDocumento) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException(ERROR_NO_ENCONTRADO));

        Cliente propietario = vehiculo.getCliente();
        boolean documentoCoincide = tipoDocumento.equals(propietario.getTipoDocumento())
                && numeroDocumento.trim().equals(propietario.getNumeroDocumento());

        if (!documentoCoincide) {
            throw new RecursoNoEncontradoException(ERROR_NO_ENCONTRADO);
        }

        List<ConsultaResponse.OrdenInfo> ordenes = ordenRepository
                .findByVehiculoId(vehiculo.getId())
                .stream()
                .sorted(Comparator.comparing(OrdenServicio::getFechaIngreso).reversed())
                .map(ConsultaResponse.OrdenInfo::desde)
                .toList();

        return new ConsultaResponse(ConsultaResponse.VehiculoInfo.desde(vehiculo), ordenes);
    }
}
