package interview.lld.interviewmain;

import java.util.Objects;

/*
shallow vs deep compare to check equality of object


 */
class UserObj {
    private int id;
    private String name;

    UserObj(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (!(obj instanceof UserObj user)) return false;
        return user.id == this.id && user.name == this.name;
    }

    @Override
    public int hashCode () { // hashCode() is used by hash-based collections when example use of HashMap, HashSet to decide which bucket to place the object in.
        return Objects.hashCode(id);
    }

}
public class EqualHashCodeInternals {
    static void main() {
        UserObj user = new UserObj(1, "Deep");
        UserObj user2 = new UserObj(1, "Raj");
        System.out.println(user.equals(user2));

    }
}
