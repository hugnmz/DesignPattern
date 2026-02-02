package com.hungnguyen.coffee.restapitjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass // để chia sẻ field vs các entity kế thừa khác, ko dc tạo  table trong db, ko dc query trực tiếp
    public class AbstractEntity<T extends Serializable> implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private T id;

    @CreatedBy
    @Column(name = "created_by")
    private T createdBy;

    @LastModifiedBy
    @Column(name = "update_by")
    private T updatedBy;

    @Column(name = "created_at")
    @CreationTimestamp // tự động insert thời gian vào db
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp // cập nhật time mỗi khi record dc update
    private Date updatedAt;
}
