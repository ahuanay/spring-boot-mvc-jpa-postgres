package com.ahuanay.empleado.common.infrastructure.adapters.persistence;

import com.ahuanay.empleado.domain.exception.ParametroInvalidoException;
import org.springframework.data.domain.Sort;

import java.util.Set;
import java.util.stream.Collectors;

public final class JpaQueryUtils {

    private static final String CAMPO_POR_DEFECTO = "id";
    private static final Set<String> DIRECCIONES_VALIDAS = Set.of("ASC", "DESC");

    private JpaQueryUtils() {
    }

    public static Sort resolverSort(String sortBy, String direction, Set<String> camposPermitidos) {
        String campo = resolverCampo(sortBy, camposPermitidos);
        String direccion = direction != null ? direction.trim().toUpperCase() : "ASC";

        if (campo == null) {
            throw new ParametroInvalidoException(
                "El parámetro 'sortBy' con valor '" + sortBy + "' no es válido. Valores permitidos: "
                    + camposOrdenados(camposPermitidos) + "."
            );
        }

        if (!DIRECCIONES_VALIDAS.contains(direccion)) {
            throw new ParametroInvalidoException(
                "El parámetro 'direction' con valor '" + direction + "' no es válido. Valores permitidos: ASC, DESC."
            );
        }

        return Sort.by(Sort.Direction.valueOf(direccion), campo);
    }

    public static String escaparLike(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static String resolverCampo(String sortBy, Set<String> camposPermitidos) {
        String solicitado = (sortBy == null || sortBy.isBlank())
            ? CAMPO_POR_DEFECTO
            : sortBy.trim();

        return camposPermitidos.stream()
            .filter(campo -> campo.equalsIgnoreCase(solicitado))
            .findFirst()
            .orElse(null);
    }

    private static String camposOrdenados(Set<String> camposPermitidos) {
        return camposPermitidos.stream()
            .sorted()
            .collect(Collectors.joining(", "));
    }
}
