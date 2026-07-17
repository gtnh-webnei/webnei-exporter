package moe.takochan.webnei.exporter.domain.recipe.model;

/** Canonical recipe candidate presentation protocol constants. */
public final class RecipeCandidateProtocol {

    public static final String TARGET_DOMAIN_ITEM = "item";
    public static final String TARGET_DOMAIN_FLUID = "fluid";

    public static final String PRESENTATION_TYPE_ITEM_STACK = "itemStack";
    public static final String PRESENTATION_TYPE_GT_FLUID_DISPLAY = "gtFluidDisplay";
    public static final String PRESENTATION_TYPE_FLUID_SLOT = "fluidSlot";

    public static final String AMOUNT_UNIT_ITEM = "item";
    public static final String AMOUNT_UNIT_LITER = "L";
    public static final String AMOUNT_UNIT_MILLIBUCKET = "mB";
    public static final String AMOUNT_UNIT_MILLIBUCKET_PER_TICK = "mBPerTick";

    public static final double DEFAULT_PROBABILITY = 1.0d;

    private RecipeCandidateProtocol() {}
}
