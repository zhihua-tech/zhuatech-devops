/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import java.util.regex.Pattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class EnterpriseControlApiTests {
    @Autowired MockMvc mvc;
    private static final String BODY="""
        {"controlNo":"ENT-DEVOPS-001","organizationCode":"ZH-SH","fiscalPeriod":"2026-08",
         "controlType":"MONTHLY_CONTROL","subjectNo":"SUB-001","subjectName":"企业版控制闭环验收",
         "assignee":"王专员","riskLevel":"正常","dueDate":"2026-09-15","externalSystem":"ERP",
         "externalRef":"","idempotencyKey":"idem-devops-enterprise-001"}
        """;
    @Test void enterpriseControlSupportsIdempotentEndToEndWorkflow() throws Exception {
        var first=mvc.perform(post("/api/enterprise/controls").with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("DRAFT")).andReturn();
        long id=idOf(first);
        mvc.perform(post("/api/enterprise/controls").with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(id));
        mvc.perform(post("/api/enterprise/controls/{id}/submit",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("PENDING_REVIEW"));
        mvc.perform(post("/api/admin/enterprise/controls/{id}/review",id).with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"decision\":\"APPROVE\",\"remark\":\"无权限\"}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/enterprise/controls/{id}/review",id).with(httpBasic("admin","admin123"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"decision\":\"APPROVE\",\"remark\":\"复核通过\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("APPROVED"));
        mvc.perform(post("/api/enterprise/controls/{id}/complete",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/enterprise/controls/{id}/documents",id).with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"fileName":"凭证.pdf","mediaType":"application/pdf","sizeBytes":2048,
                 "sha256":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","storageKey":"controls/2026/receipt.pdf"}
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.data.uploadedBy").value("operator"));
        mvc.perform(post("/api/enterprise/controls/{id}/complete",id).with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value("COMPLETED"));
        mvc.perform(post("/api/admin/enterprise/controls/{id}/sync",id).with(httpBasic("admin","admin123"))
            .contentType(MediaType.APPLICATION_JSON).content("{\"success\":true,\"externalRef\":\"ERP-202608-001\",\"message\":\"适配器回执成功\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.syncState").value("SYNCED"))
            .andExpect(jsonPath("$.data.externalRef").value("ERP-202608-001"));
    }
    @Test void invalidPeriodAndChecksumAreRejected() throws Exception {
        mvc.perform(post("/api/enterprise/controls").with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content(BODY.replace("2026-08","2026-13").replace("ENT-DEVOPS-001","ENT-DEVOPS-BAD")))
            .andExpect(status().isBadRequest());
        var result=mvc.perform(post("/api/enterprise/controls").with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content(BODY.replace("001","002")))
            .andExpect(status().isOk()).andReturn();
        mvc.perform(post("/api/enterprise/controls/{id}/documents",idOf(result)).with(httpBasic("operator","operator123"))
            .contentType(MediaType.APPLICATION_JSON).content("""
                {"fileName":"bad.pdf","mediaType":"application/pdf","sizeBytes":10,"sha256":"1234","storageKey":"bad.pdf"}
                """)).andExpect(status().isBadRequest());
    }
    @Test void summaryAndFiltersAreAvailable() throws Exception {
        mvc.perform(get("/api/enterprise/summary").with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").isNumber())
            .andExpect(jsonPath("$.data.states").isMap());
        mvc.perform(get("/api/enterprise/controls?state=DRAFT").with(httpBasic("operator","operator123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray());
    }
    private long idOf(MvcResult result) throws Exception {
        var matcher=Pattern.compile("\\\"id\\\":(\\d+)").matcher(result.getResponse().getContentAsString());
        Assertions.assertTrue(matcher.find());return Long.parseLong(matcher.group(1));
    }
}
