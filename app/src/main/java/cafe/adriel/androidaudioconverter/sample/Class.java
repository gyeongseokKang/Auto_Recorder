package cafe.adriel.androidaudioconverter.sample;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Class {

    public Class(String classname, String professorname, String starttime, String recordday) {
        this.classname = classname;
        this.professorname = professorname;
        this.starttime = starttime;
        this.recordday = recordday;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "class_name")
    private String classname;

    @ColumnInfo(name = "professor_name")
    private String professorname;

    @ColumnInfo(name = "start_time")
    private String starttime;

    @ColumnInfo(name = "record_day")
    private String recordday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRecordday() {
        return recordday;
    }

    public String getProfessorname() {
        return professorname;
    }

    public String getClassname() {
        return classname;
    }

    public String getStarttime() { return starttime; }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setProfessorname(String professorname) {
        this.professorname = professorname;
    }

    public void setRecordday(String recordday) {
        this.recordday = recordday;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
}
