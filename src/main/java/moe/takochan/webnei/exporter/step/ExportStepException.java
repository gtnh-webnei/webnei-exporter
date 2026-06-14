package moe.takochan.webnei.exporter.step;

/** step 执行失败时抛出，交给 workflow 统一转换成 BundleResult failure。 */
public final class ExportStepException extends Exception {

    public ExportStepException(String message) {
        super(message);
    }

    public ExportStepException(String message, Throwable cause) {
        super(message, cause);
    }
}
