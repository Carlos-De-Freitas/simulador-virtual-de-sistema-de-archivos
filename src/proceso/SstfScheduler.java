/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

/**
 *
 * @author Usuario
 */
public class SstfScheduler extends Scheduler {

    public SstfScheduler(int maxBlockIndex) {
        super(maxBlockIndex);
    }

    @Override
    public IORequest getNextRequest(int currentHead) {
        if (!hasRequests()) {
            return null;
        }

        int n = requests.size();
        int bestIndex = 0;
        IORequest bestReq = requests.get(0);
        int bestDistance = Math.abs(bestReq.getBlockIndex() - currentHead);

        for (int i = 1; i < n; i++) {
            IORequest r = requests.get(i);
            int d = Math.abs(r.getBlockIndex() - currentHead);
            if (d < bestDistance) {
                bestDistance = d;
                bestReq = r;
                bestIndex = i;
            }
        }

        requests.removeAt(bestIndex);
        return bestReq;
    }
}
