package com.miguel_barcelo.async_parallel_load_stress_simulator.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.miguel_barcelo.async_parallel_load_stress_simulator.exception.SimulatorFailureException;

@Service
public class SimulatorService {
	Random random = new Random();

	public String getDummy() {
		try {
			long sleepTime = (long) (random.nextDouble() * 2000); // [0, 2)secs
			Thread.sleep(sleepTime);
			
			// 80% Success
			if (random.nextDouble() < 0.8)
				return "Éxito";
			
			throw new SimulatorFailureException("Fail");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SimulatorFailureException("Fail");
		}
	}
}
