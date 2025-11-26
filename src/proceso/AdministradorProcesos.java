/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

import sistemadearchivos.SistemaArchivo;
import sistemadearchivos.NodoDirectorio; 
import sistemadearchivos.NodoFs; 
import modos.User; 
import estructuras.ListaSimple;
      
/**
 *
 * @author Usuario
 */
public class AdministradorProcesos {

    private final SistemaArchivo fileSystem;
    private final ListaSimple<Proceso> procesos;

    private final int maxBlockIndex;
    private DiskSchedulerType schedulerType;
    private Scheduler scheduler;

    private int nextPid;

    private int currentHeadPosition;

    private long totalHeadMovements;

    public AdministradorProcesos(SistemaArchivo fileSystem) {
        this.fileSystem = fileSystem;
        this.procesos = new ListaSimple<>();
        this.maxBlockIndex = fileSystem.getDisk().getNumBlocks() - 1;
        this.schedulerType = DiskSchedulerType.FIFO;
        this.scheduler = createScheduler(this.schedulerType);
        this.nextPid = 1;
        this.currentHeadPosition = 0;
        this.totalHeadMovements = 0;
    }

    public Proceso createProcess(String nombre, User owner) {
        int pid = nextPid++;
        Proceso p = new Proceso(pid, nombre, owner);
        p.setEstado(ProcesoEstado.LISTO);
        procesos.addLast(p);
        return p;
    }

    public int getProcessCount() {
        return procesos.size();
    }

    public Proceso getProcessAt(int index) {
        return procesos.get(index);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public DiskSchedulerType getSchedulerType() {
        return schedulerType;
    }

    public int getCurrentHeadPosition() {
        return currentHeadPosition;
    }

    public long getTotalHeadMovements() {
        return totalHeadMovements;
    }

    public void setSchedulerType(DiskSchedulerType newType) {
        if (newType == null || newType == this.schedulerType) {
            return;
        }

        ListaSimple<IORequest> oldRequests = scheduler.getRequests();

        Scheduler newScheduler = createScheduler(newType);

        int n = oldRequests.size();
        for (int i = 0; i < n; i++) {
            newScheduler.addRequest(oldRequests.get(i));
        }

        this.scheduler = newScheduler;
        this.schedulerType = newType;
    }

    private Scheduler createScheduler(DiskSchedulerType type) {
        switch (type) {
            case SSTF:
                return new SstfScheduler(maxBlockIndex);
            case SCAN:
                return new ScanScheduler(maxBlockIndex);
            case C_SCAN:
                return new CscanScheduler(maxBlockIndex);
            case FIFO:
            default:
                return new FifoScheduler(maxBlockIndex);
        }
    }


    public IORequest addIORequestToProcess(Proceso p, int blockIndex, long time) {
        IORequest req = p.createIORequest(blockIndex, time, null);
        scheduler.addRequest(req);
        return req;
    }

    private IORequest addIORequestToProcess(Proceso p, int blockIndex, long time, OperacionArchivo op) {
        IORequest req = p.createIORequest(blockIndex, time, op);
        scheduler.addRequest(req);
        return req;
    }


    public IORequest scheduleFileCreate(NodoDirectorio parent,
                                        String name,
                                        int sizeBlocks,
                                        boolean publico,
                                        User owner) {

        if (!fileSystem.getDisk().hasFreeBlocks(sizeBlocks)) {
            throw new IllegalStateException("No hay espacio suficiente en disco.");
        }

        Proceso proc = createProcess("Crear archivo " + name, owner);

        int targetBlock = Math.abs(name.hashCode());
        targetBlock = targetBlock % (maxBlockIndex + 1);

        long time = System.currentTimeMillis();

        OperacionArchivo op = OperacionArchivo.createCreateOperation(parent, name, sizeBlocks, publico, owner);

        return addIORequestToProcess(proc, targetBlock, time, op);
    }

    public IORequest scheduleNodeDelete(NodoFs node, User owner) {

        if (!owner.isAdmin()) {
            throw new SecurityException("Solo el administrador puede eliminar nodos.");
        }

        Proceso proc = createProcess("Eliminar " + node.getNombre(), owner);

        int targetBlock = Math.abs(node.getNombre().hashCode());
        targetBlock = targetBlock % (maxBlockIndex + 1);

        long time = System.currentTimeMillis();

        OperacionArchivo op = OperacionArchivo.createDeleteOperation(node, owner);

        return addIORequestToProcess(proc, targetBlock, time, op);
    }

    public IORequest dispatchNextRequest() {
        if (!scheduler.hasRequests()) {
            return null;
        }
        int oldHead = currentHeadPosition;

        IORequest req = scheduler.getNextRequest(currentHeadPosition);
        if (req != null) {
            currentHeadPosition = req.getBlockIndex();
            totalHeadMovements += Math.abs(currentHeadPosition - oldHead);

            Proceso p = req.getProceso();
            p.notifyIOCompleted();

            executeFileOperation(req);
        }
        return req;
    }

    private void executeFileOperation(IORequest req) {
        OperacionArchivo op = req.getOperation();
        if (op == null) {
            return;
        }

        try {
            switch (op.getType()) {
                case CREATE:
                    fileSystem.createFile(
                            op.getParent(),
                            op.getName(),
                            op.getSizeBlocks(),
                            op.getOwner(),
                            op.isPublico(),
                            req.getProceso().getPid()
                    );
                    break;

                case DELETE:
                    fileSystem.deleteNode(op.getTargetNode(), op.getOwner());
                    break;

                case READ:
                default:

                    break;
            }
        } catch (Exception ex) {

            System.err.println("Error al ejecutar operación de archivo: " + ex.getMessage());
        }
    }
}
