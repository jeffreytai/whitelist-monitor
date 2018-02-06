package com.crypto.orm.entity;

import com.google.gson.annotations.SerializedName;
import org.jsoup.nodes.Element;
import sun.jvm.hotspot.memory.Generation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "PresaleCoin")
public class PresaleCoin implements Comparable<PresaleCoin> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @SerializedName("id")
    private Long id;

    @Column(name = "Name", nullable = false)
    @SerializedName("name")
    private String name;

    @Column(name = "PresaleInterest")
    @SerializedName("presale_interest")
    private String presaleInterest;

    @Column(name = "PresaleDate")
    @SerializedName("presale_date")
    private String presaleDate;

    @Column(name = "Bonus")
    @SerializedName("bonus")
    private String bonus;

    @Column(name = "MinRate")
    @SerializedName("min_rate")
    private String minRate;

    @Column(name = "Url")
    @SerializedName("url")
    private String url;

    @Column(name = "Created")
    @SerializedName("created")
    private Date created;

    /*****************
     * Constructor
     ****************/

    public PresaleCoin() {}

    public PresaleCoin(String name, String presaleInterest, String presaleDate, String bonus, String minRate, String url) {
        this.name = name;
        this.presaleInterest = presaleInterest;
        this.presaleDate = presaleDate;
        this.bonus = bonus;
        this.minRate = minRate;
        this.url = url;
        this.created = new Date();
    }

    public PresaleCoin(Element project) {
        Element projectInfo = project.getElementsByClass("white_info").first().getElementsByTag("a").first();
        Element interestInfo = project.getElementsByClass("presale_interest").first();
        Element dateInfo = project.getElementsByClass("presale_date").first();
        Element bonusInfo = project.getElementsByClass("bonus_presale").first();
        Element minRateInfo = project.getElementsByClass("min_rate_presale").first();

        this.name = projectInfo.text();
        this.presaleInterest = interestInfo.text();
        this.presaleDate = dateInfo.text();
        this.bonus = bonusInfo.text();
        this.minRate = minRateInfo.text();
        this.url = projectInfo.attr("href");
        this.created = new Date();
    }

    @Override
    public int compareTo(PresaleCoin comp) {
        return Comparator.comparing(PresaleCoin::getPresaleInterest)
                .thenComparing(PresaleCoin::getPresaleDate)
                .thenComparing(PresaleCoin::getBonus)
                .thenComparing(PresaleCoin::getMinRate)
                .compare(this, comp);
    }


    /*******************
     * Getters and setters
     ******************/

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

    public String getPresaleInterest() {
        return presaleInterest;
    }

    public void setPresaleInterest(String presaleInterest) {
        this.presaleInterest = presaleInterest;
    }

    public String getPresaleDate() {
        return presaleDate;
    }

    public void setPresaleDate(String presaleDate) {
        this.presaleDate = presaleDate;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getMinRate() {
        return minRate;
    }

    public void setMinRate(String minRate) {
        this.minRate = minRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
