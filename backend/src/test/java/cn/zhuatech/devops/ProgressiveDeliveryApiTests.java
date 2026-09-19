/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops;import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;import org.springframework.http.MediaType;import org.springframework.test.web.servlet.MockMvc;import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest @AutoConfigureMockMvc class ProgressiveDeliveryApiTests{@Autowired MockMvc mvc;static final String BODY="""
 {"releaseNo":"REL-1","currentTrafficPercent":10,"trafficStepPercent":20,"minimumSamples":1000,"maxErrorRateIncrease":0.5,"maxP95LatencyIncreaseMs":100,"minimumAvailability":99.9,"approvalPassed":true,"baseline":{"errorRate":0.2,"p95LatencyMs":200,"availability":99.99,"sampleCount":5000},"canary":{"errorRate":0.3,"p95LatencyMs":230,"availability":99.95,"sampleCount":1500}}
 """;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void promotesHealthyCanaryByOneBoundedStep()throws Exception{mvc.perform(post("/api/advanced/devops/progressive-delivery").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("PROMOTE_STEP")).andExpect(jsonPath("$.data.nextTrafficPercent").value(30));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Test void rollsBackOnErrorRegression()throws Exception{mvc.perform(post("/api/advanced/devops/progressive-delivery").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(BODY.replace("\"errorRate\":0.3","\"errorRate\":2.3"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("ROLLBACK")).andExpect(jsonPath("$.data.nextTrafficPercent").value(0));}}
