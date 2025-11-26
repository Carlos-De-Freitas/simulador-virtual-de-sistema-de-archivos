/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

/**
 *
 * @author Usuario
 */
public class CscanScheduler extends Scheduler {

    public CscanScheduler(int maxBlockIndex) {
        super(maxBlockIndex);
    }

    @Override
    public IORequest getNextRequest(int currentHead) {
        if (!hasRequests()) {
            return null;
        }

        int n = requests.size();
        int bestIndex = -1;
        IORequest bestReq = null;
        int bestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            IORequest r = requests.get(i);
            int block = r.getBlockIndex();
            if (block >= currentHead) {
                int distance = block - currentHead; // siempre >= 0
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestReq = r;
                    bestIndex = i;
                }
            }
        }

        if (bestIndex == -1) {
            for (int i = 0; i < n; i++) {
                IORequest r = requests.get(i);
                int block = r.getBlockIndex();
                int distance = block; 
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestReq = r;
                    bestIndex = i;
                }
            }
        }

        requests.removeAt(bestIndex);
        return bestReq;
    }
}
