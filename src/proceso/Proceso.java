/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

import modos.User;
import estructuras.ColaSimple;

/**
 *
 * @author 58412
 */
public class Proceso {

    private final int pid;
    private final String nombre;
    private final User owner;
    private ProcesoEstado estado;

    private final ColaSimple<IORequest> pendingRequests;

    public Proceso(int pid, String nombre, User owner) {
        this.pid = pid;
        this.nombre = nombre;
        this.owner = owner;
        this.estado = ProcesoEstado.NUEVO;
        this.pendingRequests = new ColaSimple<>();
    }

    public int getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public User getOwner() {
        return owner;
    }

    public ProcesoEstado getEstado() {
        return estado;
    }

    public void setEstado(ProcesoEstado estado) {
        this.estado = estado;
    }

    public ColaSimple<IORequest> getPendingRequests() {
        return pendingRequests;
    }


    public IORequest createIORequest(int blockIndex, long time) {
        return createIORequest(blockIndex, time, null);
    }


    public IORequest createIORequest(int blockIndex, long time, OperacionArchivo operation) {
        IORequest req = new IORequest(this, blockIndex, time, operation);
        pendingRequests.enqueue(req);
        estado = ProcesoEstado.ESPERANDO_IO;
        return req;
    }


    public void notifyIOCompleted() {
        pendingRequests.dequeue();
        if (pendingRequests.isEmpty()) {
            estado = ProcesoEstado.LISTO;
        }
    }

    @Override
    public String toString() {
        return "P" + pid + " (" + nombre + ") - " + estado +
                " [owner=" + owner.getUsername() + "]";
    }
}