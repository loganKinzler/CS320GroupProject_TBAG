package edu.ycp.cs320.TBAG.comparator;

import java.util.Comparator;
import edu.ycp.cs320.TBAG.model.StatusEffect;

public class StatusEffectByTypeComparator implements Comparator<StatusEffect> {
    @Override
    public int compare(StatusEffect e1, StatusEffect e2) {
        return e1.getType().compareTo(e2.getType());
    }
}