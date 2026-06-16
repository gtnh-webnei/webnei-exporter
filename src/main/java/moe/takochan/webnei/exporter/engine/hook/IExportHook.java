package moe.takochan.webnei.exporter.engine.hook;

/** 导出 hook 的基础契约。 */
public interface IExportHook {

    /**
     * 当前 hook 是否可用于本次导出。
     *
     * @return true 表示该 hook 会被注册并执行
     */
    boolean isAvailable();
}
