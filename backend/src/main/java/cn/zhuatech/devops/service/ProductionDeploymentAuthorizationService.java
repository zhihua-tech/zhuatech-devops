/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductionDeploymentAuthorizationService {
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.artifactSigned()) blockers.add("生产制品未完成签名验证");
        if (!request.testsPassed()) blockers.add("自动化测试未通过");
        if (!request.securityScanPassed()) blockers.add("安全扫描未通过");
        if (!request.changeApproved()) blockers.add("生产变更单尚未批准");
        if (request.freezeWindow() && !request.freezeExceptionApproved()) blockers.add("冻结窗口内缺少例外审批");
        if (!request.databaseMigrationReviewed()) blockers.add("数据库迁移方案未复核");
        if (!request.rollbackTested()) blockers.add("回滚方案尚未演练");
        if (!request.errorBudgetAvailable()) blockers.add("服务错误预算不足");
        if (!request.operatorSeparated()) blockers.add("部署人与审批人未职责分离");
        if (!request.auditReady()) blockers.add("审计证据链不完整");
        if (!request.canaryConfigured()) actions.add("配置灰度批次与自动终止阈值");
        if (!request.observabilityReady()) actions.add("补齐指标、日志、链路及告警看板");
        if (!request.stakeholderNoticeReady()) actions.add("准备业务方发布及回退通知");
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED : !actions.isEmpty() ? Decision.REVIEW : Decision.DEPLOY;
        return new Assessment(request.deploymentId(), decision, List.copyOf(blockers), List.copyOf(actions));
    }

    public record Request(@NotBlank String deploymentId, boolean artifactSigned, boolean testsPassed,
                          boolean securityScanPassed, boolean changeApproved, boolean freezeWindow,
                          boolean freezeExceptionApproved, boolean databaseMigrationReviewed,
                          boolean rollbackTested, boolean canaryConfigured, boolean observabilityReady,
                          boolean errorBudgetAvailable, boolean stakeholderNoticeReady,
                          boolean operatorSeparated, boolean auditReady) {}
    public record Assessment(String deploymentId, Decision decision, List<String> blockers, List<String> actions) {}
    public enum Decision { DEPLOY, REVIEW, BLOCKED }
}
