package es.uned.entities;

/**
 * Tipos de Roles de usuario
 * <li>{@link #USER}</li>
 * <li>{@link #ADMIN}</li>
 */
public enum AccountRoleType {

    /**
     * Rol de usuario normal
     */
    USER("USER"),

    /**
     * Rol de usuario administrador
     */
    ADMIN("ADMIN");

    String accountRoleType;

    AccountRoleType(String accountRoleType) {
        this.accountRoleType = accountRoleType;
    }

    public String getAccountRoleType() {
        return this.accountRoleType;
    }

}
