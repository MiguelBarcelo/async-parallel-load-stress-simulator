package com.miguel_barcelo.async_parallel_load_stress_simulator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miguel_barcelo.async_parallel_load_stress_simulator.service.SimulatorService;

@RestController
@RequestMapping("/api/simulator/")
public class SimulatorController {

	private final SimulatorService service;
	
	public SimulatorController(SimulatorService service) {
		this.service = service;
	}
	
	@GetMapping("/dummy")
	public ResponseEntity<String> getDummy() {
		return ResponseEntity.ok(service.getDummy());
	}
}
