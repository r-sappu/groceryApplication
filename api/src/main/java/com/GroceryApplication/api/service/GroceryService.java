package com.GroceryApplication.api.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

//import com.GroceryApplication.api.controller.GroceryController;
import com.GroceryApplication.api.model.Grocery;
import com.GroceryApplication.api.model.GroceryDetails;
import com.GroceryApplication.api.model.Request;
import com.GroceryApplication.api.repository.GroceryRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@Service
public class GroceryService {

	private static final Logger logger = LoggerFactory.getLogger(GroceryService.class);
		
	
	@Autowired
	private GroceryRepo groceryRepo;

    @Autowired
    private MessageSource messageSource;

//    Locale locale = LocaleContextHolder.getLocale();
	//For getting all the items name
	public List<String> getItems(){
		List<Grocery> g=groceryRepo.findAll();
		List<String> strings=new ArrayList<>();
		for(Grocery a:g) {
			strings.add(a.getName());			
		}
		return strings;
	}
	
	//Adding a new Item to database
	public Map<String,String> addItems(Request req) {

		List<GroceryDetails> list=req.getGroceryDetails();
		Map<String,String> map=new HashMap<>();
		
		for(GroceryDetails gc:list) {
			Grocery g=groceryRepo.findByName(gc.getName());
			if(g == null) {
								
				Grocery gnew=new Grocery();
				gnew.setName(gc.getName());
				gnew.setPrice(gc.getPrice());
				gnew.setQuantity(gc.getQuantity());
				gnew.setInfo(gc.getInfo());
				gnew.setUnit(gc.getUnit());
				groceryRepo.save(gnew);
				
				logger.info(gc.getName()+" added to database.");
				map.put(gc.getName(), messageSource.getMessage("add.new.success", null, LocaleContextHolder.getLocale()));
			}else {
				
				logger.info(gc.getName()+" : Item found and units will be added");
				
				int newUnit = g.getUnit()+gc.getUnit();
				g.setUnit(newUnit);
				groceryRepo.save(g);

				map.put(gc.getName(), messageSource.getMessage("add.success", null, LocaleContextHolder.getLocale()));
			}
			
		}
		return map; 
	}
	
	
	//To fetch all the items with details
	public List<GroceryDetails> manageInventory(){
		List<Grocery> g=groceryRepo.findAll();
		List<GroceryDetails> strings=new ArrayList<>();
		for(Grocery a:g) {
			GroceryDetails gd=new GroceryDetails();
			gd.setInfo(a.getInfo());
			gd.setName(a.getName());
			gd.setPrice(a.getPrice());
			gd.setQuantity(a.getQuantity());
			gd.setUnit(a.getUnit());
			
			strings.add(gd);
		}		
		return strings;
	}
	
	//To remove Items from the database
	public String removeItems(String req){
		
		Grocery g=groceryRepo.findByName(req);
		if(g==null) {
			throw new RuntimeException("Item not present.");
		}	
		else {
			logger.info(g.getName()+" : Item found and will be deleted.");
			groceryRepo.delete(g);
			return messageSource.getMessage("remove.success", null, LocaleContextHolder.getLocale());
		}
	}
	
	//To update Items
	public Map<String,String> updateItems(Request req){
		
		List<GroceryDetails> list=req.getGroceryDetails();
		Map<String,String> map=new HashMap<>();
		
		for(GroceryDetails gc:list) {
			Grocery g=groceryRepo.findByName(gc.getName());
			if(g == null) {
				logger.info(gc.getName()+" : Item not found.");
				map.put(gc.getName(), "Item not Found");
			}else {
				if(g.getUnit()<gc.getUnit()) {
					logger.info(gc.getName()+" : Less item in Inventory.");
					map.put(gc.getName(), "Less item in Inventory");
				}
				else {
					logger.info(gc.getName()+" : Item found and will be updated.");
					int newUnit = g.getUnit()-gc.getUnit();
					g.setUnit(newUnit);
					groceryRepo.save(g);
					map.put(gc.getName(), "Item removed, stock left : "+newUnit);
				}				
			}
		}
		
		return map;
	}
		
	public Map<String,String> manageOrder(String req) throws JsonParseException{
		
		Map<String,String> map=new HashMap<>();
		try {
		JsonObject jO = JsonParser.parseString(req).getAsJsonObject();
		JsonElement jE =  jO.get("customer_id");		
		logger.info("Order received for customer : "+jE.getAsString());
		
		JsonArray jA = jO.get("items").getAsJsonArray();
		
		for(int i=0;i<jA.size();i++) {
			
			JsonObject item = jA.get(i).getAsJsonObject();
			String item_id = item.get("item_id").getAsString();
			int quantity = item.get("quantity").getAsInt();
			
			Grocery g=groceryRepo.findByName(item_id);
			if(g == null) {
				map.put(item_id, "Item not Available in Inventory");
			}else {
				if(g.getUnit()<quantity) {
					map.put(item_id, "Less item in Inventory");
				}
				else {
					int newUnit = g.getUnit()-quantity;
					g.setUnit(newUnit);
					groceryRepo.save(g);
					map.put(item_id, "Item added to order, stock left : "+newUnit);
				}
				
			}
			
		}
		}finally {
			
		}
		return map;
		
		
		
	}
		
	
}
