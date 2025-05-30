package com.ecosystem.controller;

import com.ecosystem.model.creatures.Creature;
import com.ecosystem.services.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    // Using a default world size for the service if not configured elsewhere
    private static final int DEFAULT_WORLD_WIDTH = 800;
    private static final int DEFAULT_WORLD_HEIGHT = 600;

    @Autowired
    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    // Constructor for when SimulationService might not be auto-injected by default (e.g. needs explicit setup)
    // Or if we want to initialize it here with specific parameters if not already a singleton bean.
    // SimulationService is now a @Service, so Spring will inject it.
    // The constructor public SimulationController() { ... } is no longer needed.

    @GetMapping("/state")
    public List<Creature> getSimulationState() {
        // The simulation now updates periodically in the background.
        // This endpoint just returns the current state.
        return simulationService.getCreatures();
    }

    // Endpoint to get all creatures without forcing an update
    // This is essentially the same as /state now.
    @GetMapping("/creatures")
    public List<Creature> getAllCreatures() {
        return simulationService.getCreatures();
    }

    // You could add more specific endpoints, e.g., to get a single creature by ID
    // @GetMapping("/creatures/{id}")
    // public Creature getCreatureById(@PathVariable int id) {
    //     return simulationService.getCreatures().stream()
    //             .filter(c -> c.getId() == id)
    //             .findFirst()
    //             .orElse(null); // Or throw an exception
    // }
}
