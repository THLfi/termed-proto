package fi.thl.termed.model;

import com.google.common.base.Objects;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Indexed
@MappedSuperclass
public class AuditedResource extends PropertyResource {

  @Field
  @Column(name = "created_by")
  private String createdBy;

  @Field
  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Field
  @Column(name = "created_date")
  private Date createdDate;

  @Field
  @Column(name = "last_modified_date")
  private Date lastModifiedDate;

  public AuditedResource() {
    super();
  }

  public AuditedResource(String id) {
    super(id);
  }

  public AuditedResource(AuditedResource schemeResource) {
    super(schemeResource);
  }

  @PrePersist
  public void resourceCreated() {
    this.createdBy = currentUser();
    this.createdDate = new Date();
    this.lastModifiedBy = currentUser();
    this.lastModifiedDate = new Date();
  }

  @PreUpdate
  public void resourceUpdated() {
    this.lastModifiedBy = currentUser();
    this.lastModifiedDate = new Date();
  }

  private String currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null ? auth.getName() : "";
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  @Override
  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("createdBy", createdBy)
        .add("lastModifiedBy", lastModifiedBy)
        .add("createdDate", createdDate)
        .add("lastModifiedDate", lastModifiedDate);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AuditedResource that = (AuditedResource) o;

    return super.equals(o) &&
           Objects.equal(createdBy, that.createdBy) &&
           Objects.equal(lastModifiedBy, that.lastModifiedBy) &&
           Objects.equal(createdDate, that.createdDate) &&
           Objects.equal(lastModifiedDate, that.lastModifiedDate);
  }

  @Override
  public int hashCode() {
    return Objects
        .hashCode(super.hashCode(), createdBy, lastModifiedBy, createdDate, lastModifiedDate);
  }

}