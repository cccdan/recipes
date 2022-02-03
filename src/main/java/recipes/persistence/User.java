package recipes.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

@Entity
public class User {
    /**
     *
     */
    @Id
    @SequenceGenerator(name="seq2", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq2")
    private long id;

    /**
     *
     */
    @Column
    private String username;
    @Column
    private String password;
    @Column
    @JsonIgnore
    private String role;

    public User() { }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
