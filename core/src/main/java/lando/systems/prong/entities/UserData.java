package lando.systems.prong.entities;

import java.util.Objects;

public class UserData {
    String name;
    Object object;
    Object data;

    public UserData() {}

    public UserData(String name, Object object, Object data) {
        this.name = name;
        this.object = object;
        this.data = data;
    }

    public static Builder builder(Object object) {
        return new Builder(object);
    }

    public static class Builder {
        private String name;
        private Object object;
        private Object data;

        public Builder(Object object) {
            this.name = Objects.toIdentityString(object);
            this.object = object;
            this.data = null;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public UserData build() {
            return new UserData(name, object, data);
        }
    }
}
