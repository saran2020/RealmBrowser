package com.github.saran2020.realmbrowser2.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Saran on 24-Dec-17.
 */
public class Senator extends RealmObject {

    @PrimaryKey
    private int id = Integer.MIN_VALUE;

    private String caucus;
    private RealmList<Integer> congress_numbers;
    private boolean current = true;
    private String description;
    private String district;
    private String enddate;
    private Extra extra;
    private String leadership_title;
    private String party;
    private Person person;
    private String phone;
    private String role_type;
    private String role_type_label;
    private String senator_class;
    private String senator_class_label;
    private String senator_rank;
    private String senator_rank_label;
    private String startdate;
    private String state;
    private String title;
    private String title_long;
    private String website;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaucus() {
        return caucus;
    }

    public void setCaucus(String caucus) {
        this.caucus = caucus;
    }

    public RealmList<Integer> getCongress_numbers() {
        return congress_numbers;
    }

    public void setCongress_numbers(RealmList<Integer> congress_numbers) {
        this.congress_numbers = congress_numbers;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public String getLeadership_title() {
        return leadership_title;
    }

    public void setLeadership_title(String leadership_title) {
        this.leadership_title = leadership_title;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole_type() {
        return role_type;
    }

    public void setRole_type(String role_type) {
        this.role_type = role_type;
    }

    public String getRole_type_label() {
        return role_type_label;
    }

    public void setRole_type_label(String role_type_label) {
        this.role_type_label = role_type_label;
    }

    public String getSenator_class() {
        return senator_class;
    }

    public void setSenator_class(String senator_class) {
        this.senator_class = senator_class;
    }

    public String getSenator_class_label() {
        return senator_class_label;
    }

    public void setSenator_class_label(String senator_class_label) {
        this.senator_class_label = senator_class_label;
    }

    public String getSenator_rank() {
        return senator_rank;
    }

    public void setSenator_rank(String senator_rank) {
        this.senator_rank = senator_rank;
    }

    public String getSenator_rank_label() {
        return senator_rank_label;
    }

    public void setSenator_rank_label(String senator_rank_label) {
        this.senator_rank_label = senator_rank_label;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_long() {
        return title_long;
    }

    public void setTitle_long(String title_long) {
        this.title_long = title_long;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
