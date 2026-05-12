package org.example.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Job {
    @SerializedName("job_title")
    private String jobTitle;
    private String title;
    private String company;
    private List<String> city;
    private List<String> location;
    private List<String> county;
    private String remote;
    private String workmode;
    @SerializedName("job_link")
    private String jobLink;
    private String url;
    private String id;
    private List<String> salary;
    private List<String> tags;
    private String cif;
    private String date;
    @SerializedName("vdate")
    private String vDate;
    @SerializedName("expirationdate")
    private String expirationDate;
    private String status;

    public Job() {

    }

    public Job(String jobTitle, String title, String company, List<String> city, List<String> location, List<String> county, String remote, String workmode, String jobLink, String url, String id, List<String> salary, List<String> tags, String cif, String date, String vDate, String expirationDate, String status) {
        this.jobTitle = jobTitle;
        this.title = title;
        this.company = company;
        this.city = city;
        this.location = location;
        this.county = county;
        this.remote = remote;
        this.workmode = workmode;
        this.jobLink = jobLink;
        this.url = url;
        this.id = id;
        this.salary = salary;
        this.tags = tags;
        this.cif = cif;
        this.date = date;
        this.vDate = vDate;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(List<String> city) {
        this.city = city;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public List<String> getCounty() {
        return county;
    }

    public void setCounty(List<String> county) {
        this.county = county;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getWorkmode() {
        return workmode;
    }

    public void setWorkmode(String workmode) {
        this.workmode = workmode;
    }

    public String getJobLink() {
        return jobLink;
    }

    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSalary() {
        return salary;
    }

    public void setSalary(List<String> salary) {
        this.salary = salary;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getvDate() {
        return vDate;
    }

    public void setvDate(String vDate) {
        this.vDate = vDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobTitle='" + jobTitle + '\'' +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", city=" + city +
                ", location=" + location +
                ", county=" + county +
                ", remote='" + remote + '\'' +
                ", workmode='" + workmode + '\'' +
                ", jobLink='" + jobLink + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", salary=" + salary +
                ", tags=" + tags +
                ", cif='" + cif + '\'' +
                ", date='" + date + '\'' +
                ", vDate='" + vDate + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
