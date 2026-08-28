/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.util.regex.Pattern;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DevopsEnterpriseGovernanceApiTests {
    @Autowired MockMvc mvc;

    @Test
    void batchWorkflowIsAtomicAuditableAndFilterable() throws Exception {
        long first = create("GOV-DEVOPS-001", "gov-devops-001", "ZH-BJ", "2026-11");
        long second = create("GOV-DEVOPS-002", "gov-devops-002", "ZH-BJ", "2026-11");

        mvc.perform(post("/api/enterprise/controls/bulk-submit")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[" + first + "," + second + "],\"remark\":\"月度批量提交\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.processed").value(2))
            .andExpect(jsonPath("$.data.resultingState").value("PENDING_REVIEW"));

        mvc.perform(post("/api/admin/enterprise/controls/bulk-review")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[" + first + "," + second
                    + "],\"decision\":\"APPROVE\",\"remark\":\"财务与业务联合复核通过\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.processed").value(2))
            .andExpect(jsonPath("$.data.resultingState").value("APPROVED"));

        mvc.perform(get("/api/enterprise/controls")
                .param("organizationCode", "ZH-BJ").param("fiscalPeriod", "2026-11")
                .with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2));

        mvc.perform(get("/api/enterprise/workbench")
                .param("organizationCode", "ZH-BJ").param("fiscalPeriod", "2026-11")
                .with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(2))
            .andExpect(jsonPath("$.data.evidenceMissing").value(2))
            .andExpect(jsonPath("$.data.workloadByOrganization['ZH-BJ']").value(2));
    }

    @Test
    void fiscalPeriodLockPreventsBusinessMutationUntilAdminUnlocks() throws Exception {
        String lockBody = """
            {"organizationCode":"ZH-GZ","fiscalPeriod":"2026-12","locked":true,
             "reason":"月结已完成，冻结业务变更"}
            """;
        mvc.perform(put("/api/admin/enterprise/period-lock")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content(lockBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.locked").value(true));

        mvc.perform(post("/api/enterprise/controls")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("GOV-DEVOPS-LOCKED", "gov-devops-locked", "ZH-GZ", "2026-12")))
            .andExpect(status().isConflict());

        mvc.perform(get("/api/enterprise/period-status")
                .param("organizationCode", "ZH-GZ").param("fiscalPeriod", "2026-12")
                .with(httpBasic("operator", "operator123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.locked").value(true));

        mvc.perform(put("/api/admin/enterprise/period-lock")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(lockBody.replace("\"locked\":true", "\"locked\":false")
                    .replace("冻结业务变更", "经授权重新开放")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.locked").value(false));

        mvc.perform(post("/api/enterprise/controls")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("GOV-DEVOPS-UNLOCKED", "gov-devops-unlocked", "ZH-GZ", "2026-12")))
            .andExpect(status().isOk());
    }

    @Test
    void batchGovernanceRejectsInvalidOrUnauthorizedOperations() throws Exception {
        mvc.perform(post("/api/enterprise/controls/bulk-submit")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[],\"remark\":\"空批次\"}"))
            .andExpect(status().isBadRequest());

        mvc.perform(post("/api/admin/enterprise/controls/bulk-review")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[1],\"decision\":\"APPROVE\",\"remark\":\"越权审批\"}"))
            .andExpect(status().isForbidden());

        mvc.perform(put("/api/admin/enterprise/period-lock")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"organizationCode\":\"ZH-SH\",\"fiscalPeriod\":\"2026-11\","
                    + "\"locked\":true,\"reason\":\"越权锁账\"}"))
            .andExpect(status().isForbidden());
    }

    private long create(String no, String key, String organization, String period) throws Exception {
        return idOf(mvc.perform(post("/api/enterprise/controls")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(no, key, organization, period)))
            .andExpect(status().isOk()).andReturn());
    }

    private String body(String no, String key, String organization, String period) {
        return "{\"controlNo\":\"" + no + "\",\"organizationCode\":\"" + organization
            + "\",\"fiscalPeriod\":\"" + period + "\",\"controlType\":\"GOVERNANCE\","
            + "\"subjectNo\":\"SUB-" + no + "\",\"subjectName\":\"企业治理深化验收\","
            + "\"assignee\":\"治理专员\",\"riskLevel\":\"正常\",\"dueDate\":\"2027-01-15\","
            + "\"externalSystem\":\"ERP\",\"externalRef\":\"\",\"idempotencyKey\":\"" + key + "\"}";
    }

    private long idOf(MvcResult result) throws Exception {
        var matcher = Pattern.compile("\\\"id\\\":(\\d+)").matcher(
            result.getResponse().getContentAsString());
        Assertions.assertTrue(matcher.find());
        return Long.parseLong(matcher.group(1));
    }
}
