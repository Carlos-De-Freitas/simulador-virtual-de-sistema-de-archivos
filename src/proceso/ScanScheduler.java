/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

/**
 *
 * @author Usuario
 */
public class ScanScheduler extends Scheduler {

    private boolean movingUp = true; 

    public ScanScheduler(int maxBlockIndex) {
        super(maxBlockIndex);
    }

    @Override
    public IORequest getNextRequest(int currentHead) {
        if (!hasRequests()) {
            return null;
        }

        
        IORequest next = findNextInDirection(currentHead, movingUp);

        if (next == null) {
            
            movingUp = !movingUp;
            next = findNextInDirection(currentHead, movingUp);
        }

        return next;
    }

    private IORequest findNextInDirection(int currentHead, boolean movingUpDirection) {
        int n = requests.size();
        int bestIndex = -1;
        IORequest bestReq = null;
        int bestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            IORequest r = requests.get(i);
            int block = r.getBlockIndex();
            int distance = Math.abs(block - currentHead);

            if (movingUpDirection) {
                if (block >= currentHead && distance < bestDistance) {
                    bestDistance = distance;
                    bestReq = r;
                    bestIndex = i;
                }
            } else {
                if (block <= currentHead && distance < bestDistance) {
                    bestDistance = distance;
                    bestReq = r;
                    bestIndex = i;
                }
            }
        }

        if (bestIndex == -1) {
            return null; 
        }

        requests.removeAt(bestIndex);
        return bestReq;
    }
}
