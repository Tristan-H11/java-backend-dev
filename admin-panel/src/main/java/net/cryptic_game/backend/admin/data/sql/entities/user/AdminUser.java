package net.cryptic_game.backend.admin.data.sql.entities.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.admin.authentication.Group;
import net.cryptic_game.backend.admin.data.sql.GroupsAttributeConverter;
import net.cryptic_game.backend.base.sql.models.TableModel;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin_user")
public class AdminUser extends TableModel {

    @Id
    @Column(name = "id", updatable = false, unique = true)
    private long id;

    @ElementCollection(targetClass = Group.class, fetch = FetchType.EAGER)
    @JoinTable(name = "admin_user_groups", joinColumns = @JoinColumn(name = "user_id", nullable = false))
    @Column(name = "group_id", nullable = false)
    @Convert(converter = GroupsAttributeConverter.class)
    @Cascade(CascadeType.DELETE)
    private Set<Group> groups;
}
