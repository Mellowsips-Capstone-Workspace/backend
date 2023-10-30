package com.capstone.workspace.entities.store;

import com.capstone.workspace.converters.PeriodListConverter;
import com.capstone.workspace.entities.partner.IPartnerEntity;
import com.capstone.workspace.entities.shared.BaseEntity;
import com.capstone.workspace.models.shared.Period;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Where;
import org.hibernate.dialect.PostgreSQLJsonPGObjectJsonbType;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "store", schema = "public")
@Where(clause = "is_deleted=false")
public class Store extends BaseEntity implements IPartnerEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(nullable = false)
    private String address;

    @Column
    private String profileImage;

    @Column
    private String coverImage;

    @Convert(attributeName = "categories")
    @Column
    private List<String> categories;

    @Column
    private Boolean isActive;

    @Convert(converter = PeriodListConverter.class)
    @Column
    @JdbcType(PostgreSQLJsonPGObjectJsonbType.class)
    private Map<DayOfWeek, List<Period<Time>>> operationalHours;

    @Column
    private String partnerId;
}
