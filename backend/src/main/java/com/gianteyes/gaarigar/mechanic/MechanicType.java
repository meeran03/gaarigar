package com.gianteyes.gaarigar.mechanic;

public enum MechanicType {
    VULCANIZER("Vulcanizer"),
    MECHANIC("Mechanic"),
    ELECTRICIAN("Electrician"),
    BODY_REPAIRER("Body Repairer"),
    PAINTER("Painter"),
    TECHNICIAN("Technician");

    private final String val;

    MechanicType(String val) {
        this.val = val;
    }
}
