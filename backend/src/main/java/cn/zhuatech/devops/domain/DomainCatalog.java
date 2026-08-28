/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.domain;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class DomainCatalog {
    private final Map<String, WorkflowAction> actions = new LinkedHashMap<>();
    public DomainCatalog() {
        actions.put("SUBMIT", new WorkflowAction("SUBMIT", "提交发布评审", List.of("草稿"), "待审批", "OPERATOR"));
        actions.put("APPROVE", new WorkflowAction("APPROVE", "批准发布", List.of("待审批"), "待发布", "ADMIN"));
        actions.put("RELEASE", new WorkflowAction("RELEASE", "确认生产发布", List.of("待发布"), "已发布", "ADMIN"));
    }
    public String systemName() { return "知华科技企业研发效能与DevOps平台"; }
    public String scene() { return "代码仓库、持续集成、自动化测试、制品、环境、发布审批、安全门禁、部署与度量"; }
    public String initialStatus() { return "草稿"; }
    public String partyLabel() { return "产品/发布版本"; }
    public String amountLabel() { return "交付价值"; }
    public String quantityLabel() { return "变更数量"; }
    public String dueLabel() { return "发布时间"; }
    public List<ModuleDefinition> modules() { return List.of(
            new ModuleDefinition("REPOSITORY", "代码仓库", "登记仓库、分支策略、代码所有者和合并规则"),
            new ModuleDefinition("PIPELINE", "流水线", "编排构建、测试、扫描、制品和部署阶段"),
            new ModuleDefinition("ARTIFACT", "制品管理", "管理版本、哈希、来源、签名、保留和推广"),
            new ModuleDefinition("ENVIRONMENT", "环境管理", "维护开发、测试、预发和生产环境及配置差异"),
            new ModuleDefinition("TEST", "测试质量", "汇总单元、集成、端到端和回归测试结果"),
            new ModuleDefinition("SECURITY", "安全门禁", "执行依赖、镜像、代码和密钥扫描及例外审批"),
            new ModuleDefinition("RELEASE", "发布管理", "管理发布单、变更窗口、审批、版本说明和回滚"),
            new ModuleDefinition("DEPLOYMENT", "部署编排", "支持分批、蓝绿、金丝雀部署和自动回退"),
            new ModuleDefinition("OBSERVABILITY", "效能度量", "统计交付周期、部署频率、失败率和恢复时间")
        ); }
    public Map<String, WorkflowAction> actions() { return Collections.unmodifiableMap(actions); }
    public record ModuleDefinition(String code,String name,String description) {}
    public record WorkflowAction(String code,String label,List<String> from,String to,String requiredRole) {}
}
