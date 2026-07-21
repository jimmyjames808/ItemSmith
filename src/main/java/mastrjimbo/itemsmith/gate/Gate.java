package mastrjimbo.itemsmith.gate;

/**
 * The full governance spec for one ability: who may use it (permission), where
 * (region), at what price (cost + charges), and how often (cooldown group). Parsed
 * once per ability by {@code ItemParser.parseGate} and evaluated by
 * {@link GateEvaluator} in {@code AbilityEngine.fire} before the ability's targets
 * are resolved.
 *
 * <p>{@link #NONE} is the shared empty gate; {@link #isNoOp()} lets the engine
 * fast-path the (vast majority of) abilities that declare no gate at all.
 */
public record Gate(
        String permission,
        RegionSpec region,
        CostSpec cost,
        int chargeCost,
        String cooldownGroup,
        double cooldownGroupSeconds,
        String denyMessage
) {
    public static final Gate NONE =
            new Gate("", null, CostSpec.NONE, 0, "", 0, "");

    public boolean isNoOp() {
        return (permission == null || permission.isEmpty())
                && (region == null || region.isNoOp())
                && (cost == null || cost.isNone())
                && chargeCost <= 0
                && (cooldownGroup == null || cooldownGroup.isEmpty());
    }
}
