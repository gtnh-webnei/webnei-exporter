package moe.takochan.webnei.exporter.engine.task;

/** task 执行失败时抛出，交给 plan executor 统一转换成 BundleResult failure。 */
public final class ExportTaskException extends Exception {

    public ExportTaskException(String message) {
        super(message);
    }

    public ExportTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
