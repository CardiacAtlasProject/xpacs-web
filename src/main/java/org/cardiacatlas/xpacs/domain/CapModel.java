package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A CapModel.
 */
@Entity
@Table(name = "cap_model")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "capmodel")
public class CapModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creation_date;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "xml_file")
    private String xml_file;

    @Column(name = "model_file")
    private String model_file;

    @Column(name = "uri")
    private String uri;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(optional = false)
    @NotNull
    private PatientInfo patientInfoFK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreation_date() {
        return creation_date;
    }

    public CapModel creation_date(LocalDate creation_date) {
        this.creation_date = creation_date;
        return this;
    }

    public void setCreation_date(LocalDate creation_date) {
        this.creation_date = creation_date;
    }

    public String getName() {
        return name;
    }

    public CapModel name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public CapModel type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXml_file() {
        return xml_file;
    }

    public CapModel xml_file(String xml_file) {
        this.xml_file = xml_file;
        return this;
    }

    public void setXml_file(String xml_file) {
        this.xml_file = xml_file;
    }

    public String getModel_file() {
        return model_file;
    }

    public CapModel model_file(String model_file) {
        this.model_file = model_file;
        return this;
    }

    public void setModel_file(String model_file) {
        this.model_file = model_file;
    }

    public String getUri() {
        return uri;
    }

    public CapModel uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getComment() {
        return comment;
    }

    public CapModel comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PatientInfo getPatientInfoFK() {
        return patientInfoFK;
    }

    public CapModel patientInfoFK(PatientInfo patientInfo) {
        this.patientInfoFK = patientInfo;
        return this;
    }

    public void setPatientInfoFK(PatientInfo patientInfo) {
        this.patientInfoFK = patientInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CapModel capModel = (CapModel) o;
        if (capModel.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, capModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CapModel{" +
            "id=" + id +
            ", creation_date='" + creation_date + "'" +
            ", name='" + name + "'" +
            ", type='" + type + "'" +
            ", xml_file='" + xml_file + "'" +
            ", model_file='" + model_file + "'" +
            ", uri='" + uri + "'" +
            ", comment='" + comment + "'" +
            '}';
    }
}
