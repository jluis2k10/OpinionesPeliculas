package es.uned.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "User_name", length = 25, unique = true, nullable = false)
    private String userName;

    @Column(name = "Email", length = 100, nullable = false)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Accounts_to_Roles",
            joinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<AccountRole> accountRoles = new ArrayList<>();

    @Transient
    private String passwordConfirm;

    @Transient
    private String newPassword;

    public boolean isAdmin() {
        Iterator<AccountRole> it = accountRoles.iterator();
        while (it.hasNext()) {
            if (it.next().getRole().equals("ADMIN"))
                return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AccountRole> getAccountRoles() {
        return accountRoles;
    }

    public void setAccountRoles(List<AccountRole> accountRoles) {
        this.accountRoles = accountRoles;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
