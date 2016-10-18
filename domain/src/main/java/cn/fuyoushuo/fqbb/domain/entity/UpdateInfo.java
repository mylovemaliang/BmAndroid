package cn.fuyoushuo.fqbb.domain.entity;

/**
 * 封装从远程获取到的软件最新版本信息
 */
public class UpdateInfo {

    //主要是用于版本升级所用，是INT类型的(只能是整型)
    private Integer version;

    //我们常说明的版本号，如1.1.1   可以显示给用户看
    private String versionName;

    //最新版本的安装包下载地址
    private String url;

    //软件描述，如新增什么功能
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl()  {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
