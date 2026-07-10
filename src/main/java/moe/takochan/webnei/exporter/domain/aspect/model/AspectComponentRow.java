package moe.takochan.webnei.exporter.domain.aspect.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** aspect_component 表行：compound aspect 的保序 parent 关系。 */
@Getter
@RequiredArgsConstructor
public final class AspectComponentRow {

    private final String datasetId;
    private final String aspectId;
    private final int componentIndex;
    private final String componentAspectId;
}
