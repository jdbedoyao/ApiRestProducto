package com.jdbo.hm;

import com.jdbo.hm.entity.Producto;
import com.jdbo.hm.repository.ProductoRepository;
import com.jdbo.hm.services.impl.ProductoServicesImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Anotación para habilitar la extensión de Mockito para JUnit 5.
// Esto permite usar @Mock y @InjectMocks.
@ExtendWith(MockitoExtension.class)
public class ProductoServicesImplTest {

    // @Mock crea un mock (simulacro) de la interfaz ProductoRepository.
    // No se usa la implementación real, sino una versión controlada para las pruebas.
    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks inyecta los mocks creados (productoRepository) en la instancia de ProductoServicesImpl.
    // Esto significa que cuando se llama a un método en productoServices,
    // usará el mock de productoRepository en lugar de una instancia real.
    @InjectMocks
    private ProductoServicesImpl productoServices;

    // Método que se ejecuta antes de cada prueba.
    // Útil para inicializar objetos comunes o configurar el estado de los mocks.
    @BeforeEach
    void setUp() {
        // No se necesita inicialización compleja aquí, ya que @Mock y @InjectMocks
        // se encargan de la inyección.
    }

    @Test
    void testObtenerTodos() {
        // Configura el comportamiento del mock:
        // Cuando se llame a productoRepository.findAll(), devuelve una lista predefinida de productos.
        List<Producto> productos = Arrays.asList(
                new Producto(1L, "Laptop", 1200, "Portátil de alto rendimiento"),
                new Producto(2L, "Mouse", 25, "Mouse inalámbrico")
        );
        when(productoRepository.findAll()).thenReturn(productos);

        // Llama al método que estamos probando.
        List<Producto> result = productoServices.obtenerTodos();

        // Verifica que el método del mock fue llamado exactamente una vez.
        verify(productoRepository, times(1)).findAll();
        // Verifica que el resultado no es nulo y que tiene el tamaño esperado.
        assertNotNull(result);
        assertEquals(2, result.size());
        // Verifica que los productos devueltos son los mismos que los predefinidos.
        assertEquals("Laptop", result.get(0).getNombre());
        assertEquals("Mouse", result.get(1).getNombre());
    }

    @Test
    void testObtenerporId_ProductoExistente() {
        // Configura el comportamiento del mock para un producto existente.
        Long productId = 1L;
        Producto producto = new Producto(productId, "Teclado", 75, "Teclado mecánico");
        when(productoRepository.findById(productId)).thenReturn(Optional.of(producto));

        // Llama al método.
        Producto result = productoServices.obtenerporId(productId);

        // Verifica que el método del mock fue llamado.
        verify(productoRepository, times(1)).findById(productId);
        // Verifica que el resultado no es nulo y que es el producto esperado.
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Teclado", result.getNombre());
    }

    @Test
    void testObtenerporId_ProductoNoExistente() {
        // Configura el comportamiento del mock para un producto no existente.
        Long productId = 99L;
        when(productoRepository.findById(productId)).thenReturn(Optional.empty());

        // Llama al método.
        Producto result = productoServices.obtenerporId(productId);

        // Verifica que el método del mock fue llamado.
        verify(productoRepository, times(1)).findById(productId);
        // Verifica que el resultado es nulo, como se espera cuando no se encuentra.
        assertNull(result);
    }

    @Test
    void testCrearProducto() {
        // Crea un producto de entrada y un producto que se espera que sea guardado.
        Producto newProducto = new Producto(5L,"Monitor", 300, "Monitor 4K");
        Producto savedProducto = new Producto(5L, "Monitor", 300, "Monitor 4K");

        // Configura el comportamiento del mock:
        // Cuando se llame a productoRepository.save() con cualquier instancia de Producto,
        // devuelve el producto con un ID asignado.
        when(productoRepository.save(any(Producto.class))).thenReturn(savedProducto);

        // Llama al método.
        Producto result = productoServices.crearProducto(newProducto);

        // Verifica que el método save fue llamado con una instancia de Producto.
        verify(productoRepository, times(1)).save(any(Producto.class));
        // Verifica que el resultado no es nulo y que tiene el ID asignado.
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Monitor", result.getNombre());
    }

    @Test
    void testActualizarProducto_ProductoExistente() {
        // Prepara los datos para la actualización.
        Long productId = 1L;
        Producto existingProducto = new Producto(productId, "Antiguo Nombre", 100, "Antigua Descripción");
        Producto updatedProductoDetails = new Producto(null, "Nuevo Nombre", 150, "Nueva Descripción");
        Producto finalUpdatedProducto = new Producto(productId, "Nuevo Nombre", 150, "Nueva Descripción");

        // Configura el mock para encontrar el producto existente.
        when(productoRepository.findById(productId)).thenReturn(Optional.of(existingProducto));
        // Configura el mock para guardar el producto actualizado.
        when(productoRepository.save(any(Producto.class))).thenReturn(finalUpdatedProducto);

        // Llama al método.
        Producto result = productoServices.actualizarProducto(productId, updatedProductoDetails);

        // Verifica las llamadas a los mocks.
        verify(productoRepository, times(1)).findById(productId);
        verify(productoRepository, times(1)).save(any(Producto.class)); // save se llama con el objeto modificado

        // Verifica que el resultado no es nulo y que los campos se actualizaron correctamente.
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Nuevo Nombre", result.getNombre());
        assertEquals(150, result.getPrecio());
        assertEquals("Nueva Descripción", result.getDescripcion());
    }

    @Test
    void testActualizarProducto_ProductoNoExistente() {
        // Prepara los datos para la actualización de un producto que no existe.
        Long productId = 99L;
        Producto updatedProductoDetails = new Producto(null, "Producto Inexistente", 200, "Descripción");

        // Configura el mock para no encontrar el producto.
        when(productoRepository.findById(productId)).thenReturn(Optional.empty());

        // Llama al método.
        Producto result = productoServices.actualizarProducto(productId, updatedProductoDetails);

        // Verifica que findById fue llamado, pero save no.
        verify(productoRepository, times(1)).findById(productId);
        verify(productoRepository, never()).save(any(Producto.class)); // save nunca debe ser llamado

        // Verifica que el resultado es nulo.
        assertNull(result);
    }

    @Test
    void testContarProducto() {
        // Configura el comportamiento del mock para devolver un conteo.
        when(productoRepository.count()).thenReturn(5L);

        // Llama al método.
        long result = productoServices.contarProducto();

        // Verifica que el método del mock fue llamado.
        verify(productoRepository, times(1)).count();
        // Verifica que el conteo es el esperado.
        assertEquals(5L, result);
    }
}