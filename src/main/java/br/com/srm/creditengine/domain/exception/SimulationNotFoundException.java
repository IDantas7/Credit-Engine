package br.com.srm.creditengine.domain.exception;

public class SimulationNotFoundException extends RuntimeException {
    public SimulationNotFoundException(Long simulationId) {
        super("Simulation not found with id: " + simulationId);
    }
}
