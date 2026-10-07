package com.GroceryApplication.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.GroceryApplication.api.model.Request;
import com.GroceryApplication.api.service.GroceryService;

import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;

import java.util.Locale;

@RestController
@Validated
public class GroceryController {

	private static final Logger logger = LoggerFactory.getLogger(GroceryController.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GroceryService grocceryService;

    @Autowired
    private MessageSource messageSource;
	
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
	
	@PostMapping(value = "/grocery/addItems")
	public ResponseEntity<?> add(@Valid @RequestBody Request request, @RequestParam(value = "lang", required = false) Locale locale) throws Exception{
        Locale locale2 = LocaleContextHolder.getLocale();
        logger.info("Request received for adding items : "+objectMapper.writeValueAsString(request));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
		return new ResponseEntity<>(grocceryService.addItems(request), headers,HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/grocery/updateItem")
	public ResponseEntity<?> update(@Valid @RequestBody Request req) throws Exception{
		logger.info("Request received for updating items : "+objectMapper.writeValueAsString(req));;
		return new ResponseEntity<>(grocceryService.updateItems(req),HttpStatus.ACCEPTED);
	}
	
	@PostMapping(path = "/grocery/removeItems/{item}")
	public ResponseEntity<?> remove(@PathVariable("item") String item, @RequestParam(value = "lang", required = false) Locale locale) {
		logger.info("Request received for removing the item : "+item);
        String result;
//        Locale locale = LocaleContextHolder.getLocale();
        try {
            result = grocceryService.removeItems(item);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(messageSource.getMessage("item.not.present", null, locale), HttpStatus.BAD_REQUEST);
        }
		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/grocery/order")
	public ResponseEntity<?> booking(@RequestBody String order) {
		logger.info("Order received : "+order.replaceAll("\n\s", "").replaceAll("\r", ""));
		return new ResponseEntity<>(grocceryService.manageOrder(order),HttpStatus.ACCEPTED);
	}
}
