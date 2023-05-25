package ro.pub.cs.systems.eim.practicaltest02.model;

public class Alarm {
    private String time;
    private String status;

    public Alarm() {
        this.time = null;
        this.status = null;
    }

    public Alarm(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "time='" + time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
