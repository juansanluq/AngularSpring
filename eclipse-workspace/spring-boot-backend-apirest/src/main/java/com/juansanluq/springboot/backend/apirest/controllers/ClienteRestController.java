package com.juansanluq.springboot.backend.apirest.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.juansanluq.springboot.backend.apirest.models.entity.Cliente;
import com.juansanluq.springboot.backend.apirest.models.services.IClienteService;

@CrossOrigin
//(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index()
	{
		return clienteService.findAll();
	}
	
	@GetMapping("clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id)
	{
		Cliente cliente = null;
		Map<String, Object> responseMap = new HashMap<>();
		
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			responseMap.put("mensaje","Error al realizar la consulta en la base de datos");
			responseMap.put("error",e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(cliente == null)
		{
			responseMap.put("mensaje","El cliente ID: " + id.toString() + " no existen en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result)
	{
		Cliente clienteNew = null;
		Map<String, Object> responseMap = new HashMap<>();
		
		if(result.hasErrors())
		{
			/*List<String> errors = new ArrayList<String>();

			for(FieldError error : result.getFieldErrors())
			{
				errors.add("El campo " + error.getField() + " " + error.getDefaultMessage());
			}*/
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> {
						return "El campo " + err.getField() + " " + err.getDefaultMessage();
					})
					.collect(Collectors.toList());
			responseMap.put("errors",errors);
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			responseMap.put("mensaje","Error al realizar el insert en la base de datos");
			responseMap.put("error",e.getMessage() + ": " + e.getCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("mensaje","El cliente ha sido creado con éxito!");
		responseMap.put("cliente",clienteNew);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.CREATED);
	}
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, @PathVariable Long id, BindingResult result)
	{
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdated = null;
		
		Map<String, Object> responseMap = new HashMap<>();
		
		if(result.hasErrors())
		{
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> {
						return "El campo " + err.getField() + " " + err.getDefaultMessage();
					})
					.collect(Collectors.toList());
			responseMap.put("errors",errors);
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		}
		
		if(clienteActual == null)
		{
			responseMap.put("mensaje","Error: no se puedo editar, el cliente ID: " + id.toString() + " no existen en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.NOT_FOUND);
		}
		
		try {
		if(cliente.getApellido().isEmpty() || cliente.getNombre().isEmpty() || cliente.getEmail().isEmpty())
		{
			throw new DataAccessException("No puede haber campos en blanco") {};
		}
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setEmail(cliente.getEmail());
		clienteActual.setCreateAt(cliente.getCreateAt());
		clienteUpdated = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			responseMap.put("mensaje","Error al actualizar el cliente en la base de datos");
			responseMap.put("error",e.getMessage());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		responseMap.put("mensaje","El cliente ha sido actualizado con éxito!");
		responseMap.put("cliente",clienteUpdated);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id)
	{
		Map<String, Object> responseMap = new HashMap<>();
		
		try {
		clienteService.delete(id);
		} catch (DataAccessException e) {
			responseMap.put("mensaje","Error al eliminar el cliente en la base de datos");
			responseMap.put("error",e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		responseMap.put("mensaje","El cliente ha sido eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}
	
}
