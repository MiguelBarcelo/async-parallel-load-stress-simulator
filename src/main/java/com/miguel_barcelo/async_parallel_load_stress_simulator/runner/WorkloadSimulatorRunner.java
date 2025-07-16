package com.miguel_barcelo.async_parallel_load_stress_simulator.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class WorkloadSimulatorRunner implements CommandLineRunner {
	
	private static final int THREADS = Runtime.getRuntime().availableProcessors();
	private static final int NUM_REQUESTS = 100;
	private static final int CANCELLED_STATUS = 404;
	private final WebClient webClient;
	
	public WorkloadSimulatorRunner(WebClient webClient) {
		this.webClient = webClient;
	}
	
	@Override
	public void run(String... args) {
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		Map<Integer, Integer> requests = new ConcurrentHashMap<>();
		List<Callable<Integer>> tasks = new ArrayList<>();
		
		for (int i = 0; i < NUM_REQUESTS; i++) {
			tasks.add(() -> getStatusCode());
		}
		
		try {
			List<Future<Integer>> futures = executor.invokeAll(tasks, 2, TimeUnit.SECONDS);
			
			for (Future<Integer> future: futures) {
				if (future.isCancelled()) {
					requests.put(CANCELLED_STATUS, requests.getOrDefault(CANCELLED_STATUS, 0) + 1);
				} else {
					try {
						int statusCode = future.get();
						requests.put(statusCode, requests.getOrDefault(statusCode, 0) + 1);
					} catch (ExecutionException e) {
						System.out.println("❌ Error in task: " + e.getCause());
					}					
				}
			}	
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("⚠️ Interrupted tasks");
		} finally {
			executor.shutdown();			
		}
		
		showResults(requests);
	}
	
	private int getStatusCode() {
		long start = System.currentTimeMillis();
		
		try {
			int status = webClient.get()
					.uri("/dummy")
					.exchangeToMono(response -> Mono.just(response.statusCode().value()))
					.block();
			long duration = System.currentTimeMillis() - start;
			//System.out.printf("🧵 Thread: %s | ⏱️ Time: %d ms | Status: %d\n", Thread.currentThread().getName(), duration, status);
			
			return status;
		} catch (Exception e) {
			long duration = System.currentTimeMillis() - start;
			//System.out.printf("🧵 Thread: %s | ⏱️ Time: %d ms | Error: %s\n", Thread.currentThread().getName(), duration, e.getMessage());
			
			return 500;
		}
	}
	
	private void showResults(Map<Integer, Integer> requests) {
		System.out.println("📊 Simulation results:");
		requests.forEach((status, count) -> System.out.printf("🔹 Status %d → %d times\n", status, count));
	}
}
