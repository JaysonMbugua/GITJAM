package com.conygre.spring.boot.rest;

import com.conygre.spring.boot.services.CompactDiscService;
import com.conygre.spring.boot.entities.CompactDisc;
import io.swagger.v3.oas.annotations.Operation;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/compactdiscs")
@CrossOrigin // allows requests from all domains
public class CompactDiscController {

	private static Logger logger = LogManager.getLogger(CompactDiscController.class);

	@Autowired
	private CompactDiscService service;

	@Operation(summary = "Get all compact discs", description = "Retrieve the complete catalog of compact discs")
	@GetMapping
	public Iterable<CompactDisc> findAll() {
		logger.info("managed to call a Get request for findAll");
		return service.getCatalog();
	}

	@Operation(summary = "Get compact disc by ID")
	@GetMapping("/{id}")
	public CompactDisc getCdById(@PathVariable("id") int id) {
		return service.getCompactDiscById(id);
	}

	@Operation(summary = "Get compact disc by ID with 404 handling")
	@GetMapping("/404/{id}")
	public ResponseEntity<CompactDisc> getByIdWith404(@PathVariable("id") int id) {
		CompactDisc disc = service.getCompactDiscById(id);
		if (disc == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity<>(disc, HttpStatus.OK);
		}
	}

	@Operation(summary = "Delete compact disc by ID")
	@DeleteMapping("/{id}")
	public void deleteCd(@PathVariable("id") int id) {
		service.deleteCompactDisc(id);
	}

	@Operation(summary = "Delete compact disc by object")
	@DeleteMapping
	public void deleteCd(@RequestBody CompactDisc disc) {
		service.deleteCompactDisc(disc);
	}

	@Operation(summary = "Add new compact disc")
	@PostMapping
	public void addCd(@RequestBody CompactDisc disc) {
		service.addNewCompactDisc(disc);
	}

}
