package moe.takochan.webnei.exporter.bundle.record.mapper;

import java.util.Arrays;
import java.util.List;

import moe.takochan.webnei.exporter.bundle.record.BundleRecordSet;
import moe.takochan.webnei.exporter.bundle.record.BundleRecordSetSpec;
import moe.takochan.webnei.exporter.bundle.record.IBundleRecordSetMapper;
import moe.takochan.webnei.exporter.domain.fluid.FluidExportModel;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidBlockRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidContainerRow;
import moe.takochan.webnei.exporter.domain.fluid.model.FluidRow;

/** fluid export model 的记录集映射。 */
public final class FluidRecordSetMapper implements IBundleRecordSetMapper<FluidExportModel> {

    private static final BundleRecordSetSpec<FluidRow> FLUID = BundleRecordSetSpec.<FluidRow>recordSet("fluid", 70)
        .field("dataset_id", FluidRow::getDatasetId)
        .field("fluid_id", FluidRow::getFluidId)
        .field("mod_id", FluidRow::getModId)
        .field("registry_name", FluidRow::getRegistryName)
        .field("unlocalized_name", FluidRow::getUnlocalizedName)
        .field("display_name", FluidRow::getDisplayName)
        .field("chemical_expression", FluidRow::getChemicalExpression)
        .field("luminosity", FluidRow::getLuminosity)
        .field("density", FluidRow::getDensity)
        .field("temperature", FluidRow::getTemperature)
        .field("viscosity", FluidRow::getViscosity)
        .field("gaseous", FluidRow::isGaseous);

    private static final BundleRecordSetSpec<FluidContainerRow> FLUID_CONTAINER = BundleRecordSetSpec
        .<FluidContainerRow>recordSet("fluid_container", 80)
        .field("dataset_id", FluidContainerRow::getDatasetId)
        .field("fluid_id", FluidContainerRow::getFluidId)
        .field("amount", FluidContainerRow::getAmount)
        .field("item_variant_id", FluidContainerRow::getItemVariantId);

    private static final BundleRecordSetSpec<FluidBlockRow> FLUID_BLOCK = BundleRecordSetSpec
        .<FluidBlockRow>recordSet("fluid_block", 90)
        .field("dataset_id", FluidBlockRow::getDatasetId)
        .field("fluid_id", FluidBlockRow::getFluidId)
        .field("item_variant_id", FluidBlockRow::getItemVariantId);

    @Override
    public Class<FluidExportModel> modelType() {
        return FluidExportModel.class;
    }

    @Override
    public List<BundleRecordSet> recordSets(FluidExportModel model) {
        return Arrays.asList(
            FLUID.records(model.getFluids()),
            FLUID_CONTAINER.records(model.getContainers()),
            FLUID_BLOCK.records(model.getBlocks()));
    }
}
