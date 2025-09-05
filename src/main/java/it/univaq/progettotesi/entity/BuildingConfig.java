package it.univaq.progettotesi.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "building_config")
public class BuildingConfig implements org.springframework.data.domain.Persistable<Long> {

    @Id
    @Column(name = "building_id", nullable = false)
    @Getter
    private Long buildingId;   // PK = FK

    @Transient
    private boolean isNew = true;

    @PostLoad
    void markNotNew() { this.isNew = false; }

    @Override
    public Long getId() { return buildingId; }

    @Override
    public boolean isNew() { return isNew; }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", foreignKey = @ForeignKey(name = "fk_building_config_building"))
    @MapsId
    @Setter
    @Getter
    private Building building;

    @Column(name = "json", columnDefinition = "json", nullable = false)
    @Setter
    @Getter
    private String json;

    @Column(nullable = false)
    @Setter
    @Getter
    private Integer version = 0;

    @UpdateTimestamp
    @Setter
    @Getter
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp(6)")
    private Instant updatedAt;

    @CreationTimestamp
    @Setter
    @Getter
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp(6)")
    private Instant createdAt;

    @Version
    @Setter
    @Getter
    @Column(name = "row_version")
    private Long rowVersion;

    public BuildingConfig() {

    }

    public BuildingConfig(Long buildingId, Building building, Integer version) {
        this.buildingId = buildingId;
        this.building = building;
        this.version = version;
    }
}
