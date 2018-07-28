package es.uned.entities;

import javax.persistence.*;

/**
 * Entidad para roles de usuario.
 * Tabla ACCOUNT_ROLES en base de datos.
 */
@Entity
@Table(name = "Account_Roles")
public class AccountRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role", length = 15, unique = true, nullable = false)
    private String role = AccountRoleType.USER.getAccountRoleType();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
