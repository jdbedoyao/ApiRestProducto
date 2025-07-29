package com.jdbo.hm.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jdbo.hm.entity.Producto;
import com.jdbo.hm.jsonapi.JsonApiData;
import com.jdbo.hm.jsonapi.JsonApiDocument;
import com.jdbo.hm.jsonapi.JsonApiError;
import com.jdbo.hm.jsonapi.ProductoAttributes;
import com.jdbo.hm.repository.ProductoRepository;
import com.jdbo.hm.services.ProductoServices;

@RestController
@RequestMapping("/productos")
public class ProductoController {
	@Autowired
    public ProductoServices productosServCont;
	
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
	public ResponseEntity<JsonApiDocument<ProductoAttributes>> listaProductos() {
        System.out.println("Número de Porductos en la BD:" + productosServCont.contarProducto());

        List<Producto> listaProductos = productosServCont.obtenerTodos();
        List<JsonApiData<ProductoAttributes>> jsonApiDataList = new ArrayList<JsonApiData<ProductoAttributes>>();  
        
        for(int i=0; i< listaProductos.size(); i++) {
            JsonApiData<ProductoAttributes> producto = 	new JsonApiData<ProductoAttributes>(
                    String.valueOf(listaProductos.get(i).getId()), // ID del recurso
                    "productos", // Tipo del recurso (debe ser plural y consistente)
                    new ProductoAttributes(listaProductos.get(i).getNombre(), listaProductos.get(i).getPrecio(), 
                    	listaProductos.get(i).getDescripcion()) );
            jsonApiDataList.add(producto);
        }
        
         // Envuelve la lista de datos en un documento JSON:API
        JsonApiDocument<ProductoAttributes> document = new JsonApiDocument<>(jsonApiDataList);
        return new ResponseEntity<>(document, HttpStatus.OK);
	}
    
    
    @ResponseBody
    @GetMapping(value = "/{idProducto}"  , produces = "application/vnd.api+json")
	public ResponseEntity<JsonApiDocument<ProductoAttributes>> getProductoId(@PathVariable Long idProducto) {
        Optional<Producto> prodOptional = Optional.of(productosServCont.obtenerporId(idProducto));

        if (prodOptional.isPresent()) {
            Producto prod = prodOptional.get();
            // Mapea la entidad Producto a un objeto JsonApiData<PostAttributes>
            JsonApiData<ProductoAttributes> jsonApiData = new JsonApiData<ProductoAttributes>(
                    String.valueOf(prod.getId()),
                    "productos",
                    new ProductoAttributes(prod.getNombre(), prod.getPrecio(), prod.getDescripcion())
            );
            // Envuelve el dato en un documento JSON:API
            JsonApiDocument<ProductoAttributes> documento = new JsonApiDocument<>(jsonApiData);
            return new ResponseEntity<>(documento, HttpStatus.OK);
        } else {
            // Si el post no se encuentra, retorna un error JSON:API
            JsonApiError error = new JsonApiError();
            error.setStatus("404");
            error.setTitle("Not Found");
            error.setDetail("Post with ID " + idProducto + " not found.");
            return new ResponseEntity<>(new JsonApiDocument<>(List.of(error), ProductoAttributes.class), HttpStatus.NOT_FOUND);
        }
    }
    
    
    // Endpoint para crear un nuevo post
    // Responde a POST /api/posts
    // Espera un cuerpo de solicitud en formato JSON:API
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonApiDocument<ProductoAttributes>> createPost(@RequestBody JsonApiDocument<ProductoAttributes> requestDocument) {
        if (requestDocument == null || requestDocument.getData() == null || requestDocument.getData().isEmpty()) {
            JsonApiError error = new JsonApiError();
            error.setStatus("400");
            error.setTitle("Bad Request");
            error.setDetail("Request body must contain 'data' object.");
            // *** USO DEL NUEVO CONSTRUCTOR PARA ERRORES ***
            return new ResponseEntity<>(new JsonApiDocument<>(List.of(error), ProductoAttributes.class), HttpStatus.BAD_REQUEST);
        }

        JsonApiData<ProductoAttributes> requestData = requestDocument.getData().get(0);
        ProductoAttributes attributes = requestData.getAttributes();

        if (attributes == null) {
            JsonApiError error = new JsonApiError();
            error.setStatus("400");
            error.setTitle("Bad Request");
            error.setDetail("Resource 'attributes' are missing in request body.");
            // *** USO DEL NUEVO CONSTRUCTOR PARA ERRORES ***
            return new ResponseEntity<>(new JsonApiDocument<>(List.of(error), ProductoAttributes.class), HttpStatus.BAD_REQUEST);
        }

        // Crea una nueva entity Producto a partir de los atributos recibidos
        Producto producto = new Producto();
        producto.setNombre(attributes.getNombre());
        producto.setPrecio(attributes.getPrecio());
        producto.setDescripcion(attributes.getDescripcion());

        // Guarda el nuevo post en la base de datos
        Producto savedProd = productosServCont.crearProducto(producto);

        // Prepara la respuesta en formato JSON:API
        JsonApiData<ProductoAttributes> responseData = new JsonApiData<>(
                String.valueOf(savedProd.getId()),
                "productos",
                new ProductoAttributes(savedProd.getNombre(), savedProd.getPrecio(), savedProd.getDescripcion())
        );
        JsonApiDocument<ProductoAttributes> responseDocument = new JsonApiDocument<>(responseData);

        return new ResponseEntity<>(responseDocument, HttpStatus.CREATED);
    }    
    
//    @ResponseBody
//    @PostMapping(value = "/consultaClienteObj/")
//    //@PostMapping(value = "/consultaCliente/{idUsuario}") // , produces = MediaType.APPLICATION_JSON_VALUE
//	public ResponseEntity<Vector> ConsulCliente(@RequestBody Producto us) {
//    	Vector<Respuesta> vec = new Vector<>();
//    	Producto cliente = clienteServCont.obtenerporId(us.getId_cliente());
//    	    if(cliente!=null) {
//    	      Respuesta resp = new Respuesta();
//          	  resp.setId(cliente.getId_cliente());
//        	  resp.setSuccess("true");
//        	  resp.setMsg(cliente.getNombre_cliente());
//        	  vec.add(resp);
//    	    }
//    	return new ResponseEntity<Vector>(vec, HttpStatus.OK );
//    }
    
    
    
    
    	
}
