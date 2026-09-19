/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.config;
import cn.zhuatech.devops.domain.DomainCatalog;
import cn.zhuatech.devops.model.*;
import cn.zhuatech.devops.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Configuration public class DataInitializer {
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Bean CommandLineRunner seed(BusinessRecordRepository records,SystemSettingRepository settings,DomainCatalog catalog) {
  return args -> { if(records.count()>0)return;
            settings.save(new SystemSetting("company", "上海如静知华信息科技有限公司"));
            settings.save(new SystemSetting("website", "https://www.zhuatech.cn/"));
            settings.save(new SystemSetting("recordNoRule", "DEVOPS-{YYYY}-{SEQ}"));
            settings.save(new SystemSetting("retentionPolicy", "按企业制度配置"));
            settings.save(new SystemSetting("integrationMode", "ADAPTER_RESERVED"));
   int sequence=1; for(var module:catalog.modules()) {
    String no="DEVOPS-DEMO-"+String.format("%03d",sequence);
    records.save(new BusinessRecord(no,module.code(),module.name()+"标准业务事项","上海总部",sequence%3==0?"内控经理":"业务专员",catalog.initialStatus(),BigDecimal.valueOf(sequence*12500L),sequence*2,LocalDate.now().plusDays(sequence*3L),sequence%4==0?"关注":"正常",module.description()+"；演示台账、状态流、权限和审计能力")); sequence++;
   }
  };
 }
}
