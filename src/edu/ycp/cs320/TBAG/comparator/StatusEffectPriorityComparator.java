package edu.ycp.cs320.TBAG.comparator;

import java.util.Comparator;
import edu.ycp.cs320.TBAG.model.StatusEffect;

public class StatusEffectPriorityComparator implements Comparator<StatusEffect> {
    @Override
    public int compare(StatusEffect e1, StatusEffect e2) {
        // Higher priority effects come first
        // Stun should be processed first, then damage effects
        if (e1.getType().equals("stun") && !e2.getType().equals("stun")) {
            return -1;
        } else if (!e1.getType().equals("stun") && e2.getType().equals("stun")) {
            return 1;
        }
        // Then sort by remaining duration (longest first)
        return Integer.compare(e2.getDuration(), e1.getDuration());
    }
}