/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

/**
 *
 * @author 58412
 */
public class IORequest {

    private final Proceso proceso;
    private final int blockIndex;
    private final long arrivalTime;
    private final OperacionArchivo operation;

    public IORequest(Proceso proceso, int blockIndex, long arrivalTime) {
        this(proceso, blockIndex, arrivalTime, null);
    }

    public IORequest(Proceso proceso, int blockIndex, long arrivalTime, OperacionArchivo operation) {
        this.proceso = proceso;
        this.blockIndex = blockIndex;
        this.arrivalTime = arrivalTime;
        this.operation = operation;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public OperacionArchivo getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "IORequest{P" + proceso.getPid() +
                ", block=" + blockIndex +
                ", t=" + arrivalTime +
                ", op=" + (operation != null ? operation.getType() : "NONE") +
                "}";
    }
}