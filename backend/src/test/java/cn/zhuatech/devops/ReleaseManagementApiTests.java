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
class ReleaseManagementApiTests {
    @Autowired MockMvc mvc;
    private static final String DIGEST="sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Test
    void releasePassesGateApprovalDeploymentAndControlledRollback() throws Exception {
        long id=create("REL-DOMAIN-001",99.5,0,DIGEST);
        mvc.perform(get("/api/devops/releases/{id}/gate",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("READY"));
        mvc.perform(post("/api/devops/releases/{id}/submit",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("PENDING_APPROVAL"));
        mvc.perform(post("/api/admin/devops/releases/{id}/approve",id).param("remark","越权")
                .with(httpBasic("operator","operator123")))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/devops/releases/{id}/approve",id).param("remark","CAB批准")
                .with(httpBasic("admin","admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("APPROVED"));
        mvc.perform(post("/api/devops/releases/{id}/deploy",id).with(httpBasic("operator","operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"artifactDigest\":\""+DIGEST+"\",\"strategy\":\"CANARY\",\"healthCheckPassed\":true}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("DEPLOYED"));
        mvc.perform(post("/api/devops/releases/{id}/rollback",id).with(httpBasic("operator","operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"targetVersion\":\"v1.9.0\",\"reason\":\"错误率超过阈值\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("ROLLED_BACK"));
    }

    @Test
    void qualityAndSecurityGateBlockUnsafeRelease() throws Exception {
        long id=create("REL-DOMAIN-BLOCKED",70,3,DIGEST);
        mvc.perform(get("/api/devops/releases/{id}/gate",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("BLOCKED"))
            .andExpect(jsonPath("$.data.blockers.length()").value(2));
        mvc.perform(post("/api/devops/releases/{id}/submit",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isConflict());
        mvc.perform(get("/api/devops/releases/dashboard").with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.blocked").isNumber());
    }

    @Test
    void deploymentRejectsArtifactDriftAndFailedHealthCheck() throws Exception {
        long id=create("REL-DOMAIN-DRIFT",100,0,DIGEST);
        mvc.perform(post("/api/devops/releases/{id}/submit",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk());
        mvc.perform(post("/api/admin/devops/releases/{id}/approve",id).param("remark","批准")
                .with(httpBasic("admin","admin123"))).andExpect(status().isOk());
        mvc.perform(post("/api/devops/releases/{id}/deploy",id).with(httpBasic("operator","operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"artifactDigest\":\"sha256:bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\",\"strategy\":\"ROLLING\",\"healthCheckPassed\":true}"))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/devops/releases/{id}/deploy",id).with(httpBasic("operator","operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"artifactDigest\":\""+DIGEST+"\",\"strategy\":\"ROLLING\",\"healthCheckPassed\":false}"))
            .andExpect(status().isConflict());
    }

    @Test
    void productionFreezeWindowRequiresEmergencyApproval() throws Exception {
        long blocked=createAt("REL-FREEZE-BLOCKED",100,0,DIGEST,"2026-08-29T10:00:00",false);
        mvc.perform(get("/api/devops/releases/{id}/gate",blocked).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("BLOCKED"))
            .andExpect(jsonPath("$.data.blockers[0]").value("计划时间处于周末冻结窗口且未取得紧急放行"));
        long emergency=createAt("REL-FREEZE-EMERGENCY",100,0,DIGEST,"2026-08-29T10:00:00",true);
        mvc.perform(get("/api/devops/releases/{id}/gate",emergency).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("READY"));
    }

    private long create(String no,double passRate,int vulnerabilities,String digest) throws Exception {
        return createAt(no,passRate,vulnerabilities,digest,"2026-08-28T10:00:00",false);
    }

    private long createAt(String no,double passRate,int vulnerabilities,String digest,String scheduledAt,
            boolean emergencyApproval) throws Exception {
        var result=mvc.perform(post("/api/devops/releases").with(httpBasic("operator","operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"releaseNo\":\""+no+"\",\"applicationCode\":\"ORDER-SERVICE\","
                    +"\"commitSha\":\"0123456789abcdef0123456789abcdef01234567\","
                    +"\"artifactDigest\":\""+digest+"\",\"environment\":\"PRODUCTION\","
                    +"\"testPassRate\":"+passRate+",\"criticalVulnerabilities\":"+vulnerabilities+","
                    +"\"rollbackVersion\":\"v1.9.0\",\"changeTicket\":\"CHG-2026-001\","
                    +"\"scheduledAt\":\""+scheduledAt+"\",\"emergencyApproval\":"+emergencyApproval+"}"))
            .andExpect(status().isOk()).andReturn();
        var matcher=Pattern.compile("\\\"id\\\":(\\d+)").matcher(result.getResponse().getContentAsString());
        Assertions.assertTrue(matcher.find());return Long.parseLong(matcher.group(1));
    }
}
