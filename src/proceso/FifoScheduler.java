/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

/**
 *
 * @author Usuario
 */
public class FifoScheduler extends Scheduler {

    public FifoScheduler(int maxBlockIndex) {
        super(maxBlockIndex);
    }

    @Override
    public IORequest getNextRequest(int currentHead) {
        return requests.removeFirst();
    }
}
