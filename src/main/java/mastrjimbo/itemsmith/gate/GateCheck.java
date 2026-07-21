package mastrjimbo.itemsmith.gate;

/**
 * The result of a pure {@link GateEvaluator#check} pass. Either {@link #PASS}
 * (the ability may run and its costs may be committed) or a denial carrying the
 * first-failing {@link Reason} plus a short human {@code detail} (e.g. the amount
 * needed or remaining cooldown) for the optional deny message.
 */
public record GateCheck(boolean passed, Reason reason, String detail) {

    public enum Reason {
        NONE, PERMISSION, REGION, COOLDOWN, CHARGES, MONEY, XP, HUNGER, ITEMS
    }

    public static final GateCheck PASS = new GateCheck(true, Reason.NONE, "");

    public static GateCheck deny(Reason reason, String detail) {
        return new GateCheck(false, reason, detail == null ? "" : detail);
    }
}
