package com.ahuanay.empleado.common.infrastructure.adapters.persistence;

import com.ahuanay.empleado.domain.exception.ParametroInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JpaQueryUtilsTest {

    private static final Set<String> CAMPOS_PRUEBA = Set.of("id", "nombre", "salario");
    private static final Set<String> CAMPOS_CON_FECHA = Set.of("id", "nombre", "fechaIngreso");

    @Test
    @DisplayName("Debería retornar un objeto Sort válido cuando los parámetros de ordenamiento son correctos")
    void deberiaResolverSortExitosamente() {
        // Arrange & Act
        Sort sort = JpaQueryUtils.resolverSort("nombre", "DESC", CAMPOS_PRUEBA);

        // Assert
        assertNotNull(sort);
        assertTrue(sort.isSorted());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("nombre").getDirection());
        assertEquals("nombre", sort.getOrderFor("nombre").getProperty());
    }

    @Test
    @DisplayName("Debería usar valores por defecto (id, ASC) cuando sortBy y direction son nulos")
    void deberiaUsarValoresPorDefectoEnSortCuandoSonNulos() {
        // Arrange & Act
        Sort sort = JpaQueryUtils.resolverSort(null, null, CAMPOS_PRUEBA);

        // Assert
        assertNotNull(sort);
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("id").getDirection());
        assertEquals("id", sort.getOrderFor("id").getProperty());
    }

    @Test
    @DisplayName("Debería respetar el nombre camelCase de la propiedad al ordenar por fechaIngreso")
    void deberiaResolverSortConPropiedadCamelCase() {
        // Arrange & Act
        Sort sort = JpaQueryUtils.resolverSort("fechaIngreso", "ASC", CAMPOS_CON_FECHA);

        // Assert
        assertNotNull(sort.getOrderFor("fechaIngreso"));
        assertEquals("fechaIngreso", sort.getOrderFor("fechaIngreso").getProperty());
    }

    @Test
    @DisplayName("Debería aceptar el campo de ordenamiento sin distinguir mayúsculas y minúsculas")
    void deberiaResolverSortSinDistinguirMayusculas() {
        // Arrange & Act
        Sort sort = JpaQueryUtils.resolverSort("FECHAINGRESO", "desc", CAMPOS_CON_FECHA);

        // Assert
        assertNotNull(sort.getOrderFor("fechaIngreso"));
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("fechaIngreso").getDirection());
    }

    @Test
    @DisplayName("Debería lanzar ParametroInvalidoException cuando el campo sortBy no está permitido")
    void deberiaLanzarExcepcionCuandoSortByNoEsPermitido() {
        // Arrange
        String campoInvalido = "campoNoExistente";

        // Act & Assert
        ParametroInvalidoException exception = assertThrows(
            ParametroInvalidoException.class,
            () -> JpaQueryUtils.resolverSort(campoInvalido, "ASC", CAMPOS_PRUEBA)
        );

        assertTrue(exception.getMessage().contains("El parámetro 'sortBy' con valor 'campoNoExistente' no es válido"));
    }

    @Test
    @DisplayName("Debería lanzar ParametroInvalidoException cuando la dirección de ordenamiento no es válida")
    void deberiaLanzarExcepcionCuandoDirectionNoEsValida() {
        // Arrange
        String direccionInvalida = "DIAGONAL";

        // Act & Assert
        ParametroInvalidoException exception = assertThrows(
            ParametroInvalidoException.class,
            () -> JpaQueryUtils.resolverSort("nombre", direccionInvalida, CAMPOS_PRUEBA)
        );

        assertTrue(exception.getMessage().contains("El parámetro 'direction' con valor 'DIAGONAL' no es válido"));
    }

    @Test
    @DisplayName("Debería escapar correctamente los caracteres especiales para consultas LIKE")
    void deberiaEscaparCaracteresLikeCorrectamente() {
        // Arrange
        String textoConComodines = "Test_100%[back\\slash]";

        // Act
        String textoEscapado = JpaQueryUtils.escaparLike(textoConComodines);

        // Assert
        assertEquals("Test\\_100\\%[back\\\\slash]", textoEscapado);
    }

    @Test
    @DisplayName("Debería retornar cadena vacía al escapar un texto nulo en LIKE")
    void deberiaRetornarVacioCuandoTextoLikeEsNulo() {
        // Act
        String resultado = JpaQueryUtils.escaparLike(null);

        // Assert
        assertEquals("", resultado);
    }
}
