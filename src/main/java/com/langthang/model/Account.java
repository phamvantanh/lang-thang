package com.langthang.model;

import com.langthang.event.listener.AccountEntityListener;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "account")
@EntityListeners(AccountEntityListener.class)
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @OneToOne(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "role_on_id", referencedColumnName = "id")
    private Role role;

    @Builder.Default
    private boolean enabled = false;

    private String avatarLink;

    private String about;

    private String fbLink;

    private String instagramLink;

    @OneToOne(mappedBy = "account")
    private RegisterToken registerToken;

    @OneToOne(mappedBy = "account")
    private PasswordResetToken passwordResetToken;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @OrderBy("publishedDate DESC")
    private List<Post> posts;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    private Set<FollowingRelationship> following;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @OrderBy("notifyDate DESC ")
    private List<Notify> notifies;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @OrderBy("reportedDate DESC")
    private List<PostReport> postReports;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY)
    private Set<BookmarkedPost> bookmarkedPosts;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @JoinTable(
            name = "comment_like",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private Set<Comment> likedComments;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + enabled + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", avatarLink='" + avatarLink + '\'' +
                ", about='" + about + '\'' +
                '}';
    }
}
