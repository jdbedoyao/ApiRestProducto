package com.jdbo.hm;

import com.jdbo.hm.controller.ProductoController;
import com.jdbo.hm.entity.Producto;
import com.jdbo.hm.jsonapi.JsonApiData;
import com.jdbo.hm.jsonapi.JsonApiDocument;
import com.jdbo.hm.jsonapi.ProductoAttributes;
import com.jdbo.hm.services.ProductoServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Anotación para habilitar la extensión de Mockito para JUnit 5.
@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    // @Mock crea un mock de la interfaz ProductoServices.
    @Mock
    private ProductoServices productoServices;

    // @InjectMocks inyecta el mock de ProductoServices en la instancia de ProductoController.
    @InjectMocks
    private ProductoController productoController;

    // Método que se ejecuta antes de cada prueba.
    @BeforeEach
    void setUp() {
        // No se necesita inicialización compleja aquí.
    }

    @Test
    void testListaProductos_ConProductos() {
        // Prepara una lista de productos para ser devuelta por el servicio mock.
        List<Producto> productos = Arrays.asList(
                new Producto(1L, "Laptop", 1200, "Portátil"),
                new Producto(2L, "Monitor", 300, "Monitor curvo")
        );
        when(productoServices.obtenerTodos()).thenReturn(productos);
        when(productoServices.contarProducto()).thenReturn(2L);

        // Llama al método del controlador.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.listaProductos();

        // Verifica el estado HTTP de la respuesta.
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Verifica el tipo de medio de la respuesta.
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        // Verifica el cuerpo de la respuesta.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getData());
        assertEquals(2, document.getData().size());

        // Verifica los datos del primer producto.
        JsonApiData<ProductoAttributes> firstProductData = document.getData().get(0);
        assertEquals("1", firstProductData.getId());
        assertEquals("productos", firstProductData.getType());
        assertEquals("Laptop", firstProductData.getAttributes().getNombre());
        assertEquals(1200, firstProductData.getAttributes().getPrecio());
    }

    @Test
    void testListaProductos_SinProductos() {
        // Prepara una lista vacía para ser devuelta por el servicio mock.
        when(productoServices.obtenerTodos()).thenReturn(new ArrayList<>());
        when(productoServices.contarProducto()).thenReturn(0L);

        // Llama al método del controlador.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.listaProductos();

        // Verifica el estado HTTP de la respuesta.
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Verifica el cuerpo de la respuesta.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getData());
        assertTrue(document.getData().isEmpty()); // La lista de datos debe estar vacía
    }

    @Test
    void testGetProductoId_ProductoExistente() {
        // Prepara un producto para ser devuelto por el servicio mock.
        Long productId = 1L;
        Producto producto = new Producto(productId, "Teclado", 75, "Teclado mecánico");
        when(productoServices.obtenerporId(productId)).thenReturn(producto);

        // Llama al método del controlador.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.getProductoId(productId);

        // Verifica el estado HTTP y el tipo de medio.
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.valueOf("application/vnd.api+json"), responseEntity.getHeaders().getContentType());

        // Verifica el cuerpo de la respuesta.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getData());
        assertEquals(1, document.getData().size());

        JsonApiData<ProductoAttributes> productData = document.getData().get(0);
        assertEquals(String.valueOf(productId), productData.getId());
        assertEquals("productos", productData.getType());
        assertEquals("Teclado", productData.getAttributes().getNombre());
    }

    @Test
    void testGetProductoId_ProductoNoExistente() {
        // Configura el servicio mock para devolver null (producto no encontrado).
        Long productId = 99L;
        when(productoServices.obtenerporId(productId)).thenReturn(null);

        // Llama al método del controlador.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.getProductoId(productId);

        // Verifica el estado HTTP (NOT_FOUND).
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(MediaType.valueOf("application/vnd.api+json"), responseEntity.getHeaders().getContentType());

        // Verifica que el cuerpo de la respuesta contenga errores.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getErrors());
        assertEquals(1, document.getErrors().size());
        assertEquals("404", document.getErrors().get(0).getStatus());
        assertEquals("Not Found", document.getErrors().get(0).getTitle());
        assertTrue(document.getErrors().get(0).getDetail().contains("Producto with ID " + productId + " not found."));
    }

    @Test
    void testCreatePost_Exito() {
        // Prepara los datos de entrada para la solicitud.
        ProductoAttributes attributes = new ProductoAttributes("Nuevo Producto", 50, "Descripción del nuevo producto");
        JsonApiData<ProductoAttributes> requestData = new JsonApiData<>(null, "productos", attributes);
        JsonApiDocument<ProductoAttributes> requestDocument = new JsonApiDocument<>(requestData);

        // Prepara el producto que se espera que el servicio guarde y devuelva.
        Producto savedProducto = new Producto(10L, "Nuevo Producto", 50, "Descripción del nuevo producto");
        when(productoServices.crearProducto(any(Producto.class))).thenReturn(savedProducto);

        // Llama al método del controlador.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.createPost(requestDocument);

        // Verifica el estado HTTP (CREATED).
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        // Verifica el cuerpo de la respuesta.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getData());
        assertEquals(1, document.getData().size());
        assertEquals("10", document.getData().get(0).getId());
        assertEquals("Nuevo Producto", document.getData().get(0).getAttributes().getNombre());
    }

    @Test
    void testCreatePost_RequestBodyNulo() {
        // Llama al método con un RequestBody nulo.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.createPost(null);

        // Verifica el estado HTTP (BAD_REQUEST).
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        // Verifica el cuerpo de la respuesta para errores.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getErrors());
        assertEquals(1, document.getErrors().size());
        assertEquals("400", document.getErrors().get(0).getStatus());
        assertEquals("Bad Request", document.getErrors().get(0).getTitle());
        assertTrue(document.getErrors().get(0).getDetail().contains("Request body must contain 'data' object."));
    }

    @Test
    void testCreatePost_DataVacia() {
        // Prepara un documento con la lista de datos vacía.
        JsonApiDocument<ProductoAttributes> requestDocument = new JsonApiDocument<>(Collections.emptyList());

        // Llama al método.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.createPost(requestDocument);

        // Verifica el estado HTTP (BAD_REQUEST).
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        // Verifica el cuerpo de la respuesta para errores.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getErrors());
        assertEquals(1, document.getErrors().size());
        assertEquals("400", document.getErrors().get(0).getStatus());
        assertEquals("Bad Request", document.getErrors().get(0).getTitle());
        assertTrue(document.getErrors().get(0).getDetail().contains("Request body must contain 'data' object."));
    }

    @Test
    void testCreatePost_AttributesNulos() {
        // Prepara un documento con atributos nulos.
        JsonApiData<ProductoAttributes> requestData = new JsonApiData<>(null, "productos", null);
        JsonApiDocument<ProductoAttributes> requestDocument = new JsonApiDocument<>(requestData);

        // Llama al método.
        ResponseEntity<JsonApiDocument<ProductoAttributes>> responseEntity = productoController.createPost(requestDocument);

        // Verifica el estado HTTP (BAD_REQUEST).
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        // Verifica el cuerpo de la respuesta para errores.
        JsonApiDocument<ProductoAttributes> document = responseEntity.getBody();
        assertNotNull(document);
        assertNotNull(document.getErrors());
        assertEquals(1, document.getErrors().size());
        assertEquals("400", document.getErrors().get(0).getStatus());
        assertEquals("Bad Request", document.getErrors().get(0).getTitle());
        assertTrue(document.getErrors().get(0).getDetail().contains("Resource 'attributes' are missing in request body."));
    }
}
