package com.GroceryApplication.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.GroceryApplication.api.model.Request;
import com.GroceryApplication.api.service.GroceryService;

import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;

@RestController
@Validated
public class GroceryController {

	private static final Logger logger = LoggerFactory.getLogger(GroceryController.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GroceryService grocceryService; 
	
	@GetMapping("/grocery/getItems")
	public ResponseEntity<?> getGroceryItems() {
		logger.info("Request received for fetch all items.");
		return new ResponseEntity<>(grocceryService.getItems().toString(),HttpStatus.ACCEPTED);
	}

	@GetMapping("/grocery/getInventoryDetails")
	public ResponseEntity<?> manage() {
		logger.info("Request received for fetch inventory details.");	
		return new ResponseEntity<>(grocceryService.manageInventory(),HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/grocery/addItems")
	public ResponseEntity<?> add(@Valid @RequestBody Request request) throws Exception{
		logger.info("Request received for adding items : "+objectMapper.writeValueAsString(request));;
		return new ResponseEntity<>(grocceryService.addItems(request),HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/grocery/updateItem")
	public ResponseEntity<?> update(@Valid @RequestBody Request req) throws Exception{
		logger.info("Request received for updating items : "+objectMapper.writeValueAsString(req));;
		return new ResponseEntity<>(grocceryService.updateItems(req),HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/grocery/removeItems/{item}")
	public ResponseEntity<?> remove(@PathVariable("item") String item) {
		logger.info("Request received for removing the item : "+item);
		return new ResponseEntity<>(grocceryService.removeItems(item),HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/grocery/order")
	public ResponseEntity<?> booking(@RequestBody String order) {
		logger.info("Order received : "+order.replaceAll("\n\s", "").replaceAll("\r", ""));
		return new ResponseEntity<>(grocceryService.manageOrder(order),HttpStatus.ACCEPTED);
	}
}
