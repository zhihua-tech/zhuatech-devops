/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.service;

import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.repository.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class EnterpriseControlService {
    private static final int MAX_BATCH_SIZE = 100;
    private final EnterpriseControlRepository controls;
    private final ControlDocumentRepository documents;
    private final AuditLogRepository audits;
    private final SystemSettingRepository settings;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseControlService(EnterpriseControlRepository controls, ControlDocumentRepository documents,
            AuditLogRepository audits, SystemSettingRepository settings) {
        this.controls = controls;
        this.documents = documents;
        this.audits = audits;
        this.settings = settings;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<EnterpriseControl> list(String state, String organizationCode, String fiscalPeriod) {
        return filtered(state, organizationCode, fiscalPeriod);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Summary summary(String organizationCode, String fiscalPeriod) {
        var all = filtered(null, organizationCode, fiscalPeriod);
        Map<String, Long> states = new LinkedHashMap<>(), sync = new LinkedHashMap<>();
        all.forEach(item -> {
            states.merge(item.getState(), 1L, Long::sum);
            sync.merge(item.getSyncState(), 1L, Long::sum);
        });
        long overdue = all.stream().filter(item -> isOpen(item)
            && item.getDueDate().isBefore(LocalDate.now())).count();
        return new Summary(all.size(), overdue, states, sync);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Workbench workbench(String organizationCode, String fiscalPeriod) {
        LocalDate today = LocalDate.now();
        var all = filtered(null, organizationCode, fiscalPeriod);
        long open = all.stream().filter(this::isOpen).count();
        long overdue = all.stream().filter(item -> isOpen(item) && item.getDueDate().isBefore(today)).count();
        long dueSoon = all.stream().filter(item -> isOpen(item) && !item.getDueDate().isBefore(today)
            && !item.getDueDate().isAfter(today.plusDays(7))).count();
        long evidenceMissing = all.stream().filter(item -> Set.of("APPROVED", "COMPLETED").contains(item.getState())
            && item.getDocumentCount() == 0).count();
        long highRisk = all.stream().filter(item -> "高风险".equals(item.getRiskLevel())).count();
        long syncFailed = all.stream().filter(item -> "FAILED".equals(item.getSyncState())).count();
        long completed = all.stream().filter(item -> "COMPLETED".equals(item.getState())).count();
        long syncEligible = all.stream().filter(item -> Set.of("APPROVED", "COMPLETED").contains(item.getState())).count();
        long synced = all.stream().filter(item -> "SYNCED".equals(item.getSyncState())).count();
        Map<String, Long> byOrganization = new LinkedHashMap<>();
        all.forEach(item -> byOrganization.merge(item.getOrganizationCode(), 1L, Long::sum));
        return new Workbench(all.size(), open, overdue, dueSoon, evidenceMissing, highRisk, syncFailed,
            percent(completed, all.size()), percent(synced, syncEligible), byOrganization);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public EnterpriseControl create(CreateRequest request) {
        var existing = controls.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent()) return existing.get();
        ensurePeriodOpen(request.organizationCode(), request.fiscalPeriod());
        if (controls.findByControlNo(request.controlNo()).isPresent()) throw conflict("企业控制单号已存在");
        var item = controls.save(new EnterpriseControl(request.controlNo(), request.organizationCode(),
            request.fiscalPeriod(), request.controlType(), request.subjectNo(), request.subjectName(),
            request.assignee(), request.riskLevel(), request.dueDate(), request.externalSystem(),
            request.externalRef(), request.idempotencyKey()));
        audit("创建企业控制项", item, request.subjectName());
        return item;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public EnterpriseControl submit(Long id) {
        var item = get(id);
        ensurePeriodOpen(item);
        requireState(item, "DRAFT", "只有草稿可以提交");
        item.submit();
        audit("提交复核", item, "进入管理员复核");
        return item;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public BatchResult bulkSubmit(BatchRequest request) {
        var items = loadBatch(request.ids());
        items.forEach(item -> {
            ensurePeriodOpen(item);
            requireState(item, "DRAFT", "批量提交仅支持全部为草稿的控制项");
        });
        items.forEach(item -> {
            item.submit();
            audit("批量提交复核", item, Objects.toString(request.remark(), ""));
        });
        return result(items, "PENDING_REVIEW");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public EnterpriseControl review(Long id, ReviewRequest request) {
        var item = get(id);
        ensurePeriodOpen(item);
        requireState(item, "PENDING_REVIEW", "只有待复核事项可以审批");
        applyReview(item, request.decision());
        audit("企业复核", item, request.decision() + " · " + Objects.toString(request.remark(), ""));
        return item;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public BatchResult bulkReview(BulkReviewRequest request) {
        if (!Set.of("APPROVE", "REJECT").contains(request.decision())) {
            throw bad("批量复核决定仅支持 APPROVE 或 REJECT");
        }
        var items = loadBatch(request.ids());
        items.forEach(item -> {
            ensurePeriodOpen(item);
            requireState(item, "PENDING_REVIEW", "批量复核仅支持全部为待复核的控制项");
        });
        items.forEach(item -> {
            applyReview(item, request.decision());
            audit("批量企业复核", item, request.decision() + " · " + request.remark());
        });
        return result(items, "APPROVE".equals(request.decision()) ? "APPROVED" : "REJECTED");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public EnterpriseControl complete(Long id) {
        var item = get(id);
        ensurePeriodOpen(item);
        requireState(item, "APPROVED", "只有已批准事项可以办结");
        if (item.getDocumentCount() == 0) throw conflict("办结前必须登记至少一份凭证附件");
        item.complete();
        audit("业务办结", item, "凭证数量 " + item.getDocumentCount());
        return item;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public ControlDocument registerDocument(Long id, DocumentRequest request) {
        var item = get(id);
        ensurePeriodOpen(item);
        if (Set.of("COMPLETED", "REJECTED").contains(item.getState())) throw conflict("终态事项不能补充附件");
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var doc = documents.save(new ControlDocument(id, request.fileName(), request.mediaType(),
            request.sizeBytes(), request.sha256().toLowerCase(Locale.ROOT), request.storageKey(),
            auth == null ? "system" : auth.getName()));
        item.addDocument();
        audit("登记附件", item, request.fileName());
        return doc;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<ControlDocument> documents(Long id) {
        get(id);
        return documents.findByControlIdOrderByCreatedAtDesc(id);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public EnterpriseControl sync(Long id, SyncRequest request) {
        var item = get(id);
        if (!Set.of("APPROVED", "COMPLETED").contains(item.getState())) {
            throw conflict("仅批准或办结事项允许同步");
        }
        item.sync(request.success() ? "SYNCED" : "FAILED", request.externalRef());
        audit("外部同步", item, item.getSyncState() + " · " + Objects.toString(request.message(), ""));
        return item;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional
    public PeriodStatus setPeriodLock(PeriodLockRequest request) {
        String key = periodKey(request.organizationCode(), request.fiscalPeriod());
        String value = request.locked() ? "LOCKED" : "OPEN";
        var setting = settings.findById(key).orElseGet(() -> new SystemSetting(key, value));
        setting.change(value);
        settings.save(setting);
        audits.save(new AuditLog("ENTERPRISE", request.locked() ? "锁定账期" : "解锁账期",
            request.organizationCode() + "/" + request.fiscalPeriod(), operator(), Objects.toString(request.reason(), "")));
        return new PeriodStatus(request.organizationCode(), request.fiscalPeriod(), request.locked());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public PeriodStatus periodStatus(String organizationCode, String fiscalPeriod) {
        validatePeriod(organizationCode, fiscalPeriod);
        return new PeriodStatus(organizationCode, fiscalPeriod, isPeriodLocked(organizationCode, fiscalPeriod));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private List<EnterpriseControl> filtered(String state, String organizationCode, String fiscalPeriod) {
        return controls.findAllByOrderByUpdatedAtDesc().stream()
            .filter(item -> blank(state) || state.equals(item.getState()))
            .filter(item -> blank(organizationCode) || organizationCode.equals(item.getOrganizationCode()))
            .filter(item -> blank(fiscalPeriod) || fiscalPeriod.equals(item.getFiscalPeriod()))
            .toList();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private List<EnterpriseControl> loadBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty() || ids.size() > MAX_BATCH_SIZE) {
            throw bad("批量操作数量必须在 1 到 " + MAX_BATCH_SIZE + " 之间");
        }
        var unique = new LinkedHashSet<>(ids);
        if (unique.size() != ids.size()) throw bad("批量操作不能包含重复ID");
        var found = controls.findAllById(unique);
        if (found.size() != unique.size()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "批量操作包含不存在的控制项");
        Map<Long, EnterpriseControl> indexed = new HashMap<>();
        found.forEach(item -> indexed.put(item.getId(), item));
        return unique.stream().map(indexed::get).toList();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void applyReview(EnterpriseControl item, String decision) {
        if ("APPROVE".equals(decision)) item.approve();
        else if ("REJECT".equals(decision)) item.reject();
        else throw bad("复核决定仅支持 APPROVE 或 REJECT");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private BatchResult result(List<EnterpriseControl> items, String state) {
        return new BatchResult(items.stream().map(EnterpriseControl::getId).toList(), items.size(), state);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private EnterpriseControl get(Long id) {
        return controls.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "企业控制项不存在"));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void ensurePeriodOpen(EnterpriseControl item) {
        ensurePeriodOpen(item.getOrganizationCode(), item.getFiscalPeriod());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void ensurePeriodOpen(String organizationCode, String fiscalPeriod) {
        if (isPeriodLocked(organizationCode, fiscalPeriod)) throw conflict("当前组织账期已锁定，禁止业务变更");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private boolean isPeriodLocked(String organizationCode, String fiscalPeriod) {
        return settings.findById(periodKey(organizationCode, fiscalPeriod))
            .map(item -> "LOCKED".equals(item.getSettingValue())).orElse(false);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String periodKey(String organizationCode, String fiscalPeriod) {
        validatePeriod(organizationCode, fiscalPeriod);
        return "period.lock." + organizationCode + "." + fiscalPeriod;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void validatePeriod(String organizationCode, String fiscalPeriod) {
        if (blank(organizationCode) || fiscalPeriod == null
                || !fiscalPeriod.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw bad("组织代码或账期格式不正确");
        }
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private boolean isOpen(EnterpriseControl item) {
        return !Set.of("COMPLETED", "REJECTED").contains(item.getState());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private double percent(long numerator, long denominator) {
        return denominator == 0 ? 0 : Math.round(numerator * 10000d / denominator) / 100d;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void requireState(EnterpriseControl item, String state, String message) {
        if (!state.equals(item.getState())) throw conflict(message);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String operator() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "system" : auth.getName();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private void audit(String action, EnterpriseControl item, String detail) {
        audits.save(new AuditLog("ENTERPRISE", action, item.getControlNo(), operator(), detail));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record CreateRequest(
        @NotBlank @Size(max=40) String controlNo,
        @NotBlank @Size(max=40) String organizationCode,
        @NotBlank @Pattern(regexp="\\d{4}-(0[1-9]|1[0-2])") String fiscalPeriod,
        @NotBlank @Size(max=40) String controlType,
        @NotBlank @Size(max=60) String subjectNo,
        @NotBlank @Size(max=120) String subjectName,
        @NotBlank @Size(max=50) String assignee,
        @NotBlank @Size(max=20) String riskLevel,
        @NotNull LocalDate dueDate,
        @Size(max=40) String externalSystem,
        @Size(max=100) String externalRef,
        @NotBlank @Size(max=80) String idempotencyKey) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReviewRequest(@NotBlank String decision, @Size(max=300) String remark) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BatchRequest(@NotEmpty @Size(max=100) List<@Positive Long> ids, @Size(max=300) String remark) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BulkReviewRequest(@NotEmpty @Size(max=100) List<@Positive Long> ids,
        @NotBlank String decision, @NotBlank @Size(max=300) String remark) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PeriodLockRequest(@NotBlank @Size(max=40) String organizationCode,
        @NotBlank @Pattern(regexp="\\d{4}-(0[1-9]|1[0-2])") String fiscalPeriod,
        boolean locked, @NotBlank @Size(max=300) String reason) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record DocumentRequest(@NotBlank @Size(max=160) String fileName,
        @NotBlank @Size(max=100) String mediaType, @Positive long sizeBytes,
        @NotBlank @Pattern(regexp="(?i)[0-9a-f]{64}") String sha256,
        @NotBlank @Size(max=120) String storageKey) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record SyncRequest(boolean success, @Size(max=100) String externalRef,
        @Size(max=300) String message) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Summary(long total, long overdue, Map<String, Long> states,
        Map<String, Long> syncStates) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Workbench(long total, long open, long overdue, long dueSoon,
        long evidenceMissing, long highRisk, long syncFailed, double completionRate,
        double syncSuccessRate, Map<String, Long> workloadByOrganization) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BatchResult(List<Long> ids, int processed, String resultingState) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PeriodStatus(String organizationCode, String fiscalPeriod, boolean locked) {}
}
