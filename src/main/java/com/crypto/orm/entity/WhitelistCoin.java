package com.crypto.orm.entity;

import com.google.gson.annotations.SerializedName;
import org.jsoup.nodes.Element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "WhitelistCoin")
public class WhitelistCoin implements Comparable<WhitelistCoin> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @SerializedName("id")
    private Long id;

    @Column(name = "Name", nullable = false)
    @SerializedName("name")
    private String name;

    @Column(name = "CategoryName")
    @SerializedName("category_name")
    private String categoryName;

    @Column(name = "Meta")
    @SerializedName("meta")
    private String meta;

    @Column(name = "Status")
    @SerializedName("status")
    private String status;

    @Column(name = "Url")
    @SerializedName("url")
    private String url;

    @Column(name = "Created")
    @SerializedName("created")
    private Date created;

    /**************
     * Constructor
     *************/

    public WhitelistCoin() {}

    public WhitelistCoin(String name, String categoryName, String meta, String status, String url) {
        this.name = name;
        this.categoryName = categoryName;
        this.meta = meta;
        this.status = status;
        this.url = url;
        this.created = new Date();
    }

    public WhitelistCoin(Element project) {
        Element projectInfo = project.getElementsByClass("white_info").first().getElementsByTag("a").first();
        Element categoryInfo = project.getElementsByClass("white-ico-category-name").first();
        Element metaInfo = project.getElementsByClass("whitelist_meta_icon").first();
        Element statusInfo = project.getElementsByClass("whitelist_date").first();

        this.name = projectInfo.text();
        this.categoryName = categoryInfo.text();
        this.meta = metaInfo.text();
        this.status = statusInfo.text();
        this.url = projectInfo.attr("href");
        this.created = new Date();
    }

    @Override
    public int compareTo(WhitelistCoin comp) {
        return this.status.compareTo(comp.getStatus());
    }

    /*******************
     * Getters and setters
     *******************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
