/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

import estructuras.ListaSimple;
/**
 *
 * @author Usuario
 */
public abstract class Scheduler {

    protected final ListaSimple<IORequest> requests;
    protected final int maxBlockIndex; 

    public Scheduler(int maxBlockIndex) {
        this.requests = new ListaSimple<>();
        this.maxBlockIndex = maxBlockIndex;
    }

    public boolean hasRequests() {
        return !requests.isEmpty();
    }

    public int getPendingCount() {
        return requests.size();
    }

    public void addRequest(IORequest req) {
        requests.addLast(req);
    }

    public ListaSimple<IORequest> getRequests() {
        return requests;
    }

    public abstract IORequest getNextRequest(int currentHead);
}
