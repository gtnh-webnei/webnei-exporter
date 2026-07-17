package moe.takochan.webnei.exporter.rfb;

import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbPlugin;

public final class WebneiRfbPlugin implements RfbPlugin {

    @Override
    public RfbClassTransformer[] makeTransformers() {
        return new RfbClassTransformer[] { new LwjglxKeyboardTransformer() };
    }
}
