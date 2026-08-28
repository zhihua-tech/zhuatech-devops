/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.devops.model;
import jakarta.persistence.*;
@Entity @Table(name="system_settings")
public class SystemSetting {
    @Id @Column(length=60) private String settingKey;
    @Column(nullable=false,length=300) private String settingValue;
    protected SystemSetting(){} public SystemSetting(String key,String value){settingKey=key;settingValue=value;}
    public void change(String value){settingValue=value;} public String getSettingKey(){return settingKey;} public String getSettingValue(){return settingValue;}
}
