package mastrjimbo.itemsmith.gate;

/**
 * The region/protection requirement on an ability.
 *
 * @param region       name of a WorldGuard region the caster must be standing in
 *                     ({@code ""} = no region requirement). Fails closed when
 *                     WorldGuard is absent (can't verify → deny).
 * @param canBuild     when true, the caster must be allowed to build at their own
 *                     location (WorldGuard BUILD flag / GriefPrevention claim).
 *                     Fails open when no protection plugin is present.
 * @param respectClaims when true, the ability's block-modifying actions consult the
 *                     {@code ProtectionHook} before editing, so they can't grief
 *                     protected land. Opt-in; off preserves existing behavior.
 */
public record RegionSpec(String region, boolean canBuild, boolean respectClaims) {

    public boolean isNoOp() {
        return (region == null || region.isEmpty()) && !canBuild && !respectClaims;
    }
}
