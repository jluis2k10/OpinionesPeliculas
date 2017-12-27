package es.uned.entities;

/**
 *
 */
public enum AccountRoleType {
    USER("USER"),
    ADMIN("ADMIN");

    String accountRoleType;

    AccountRoleType(String accountRoleType) {
        this.accountRoleType = accountRoleType;
    }

    public String getAccountRoleType() {
        return this.accountRoleType;
    }

}
