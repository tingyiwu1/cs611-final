package testclasses;

import java.util.*;

public class testCourse {
    private String course_id;
    private String course_number;
    private String instructor;
    private List<String> enrollment_ids = new ArrayList<>();
    private List<String> grader_ids = new ArrayList<>();
    private String name;
    private String description;
    private List<String> assignment_ids = new ArrayList<>();
    private String term;

    public testCourse(String course_id, String course_number, String name, String term) {
        this.course_id = course_id;
        this.course_number = course_number;
        this.name = name;
        this.term = term;
    }

    public String getCourse_id() {
        return course_id;
    }
    public String getCourse_number() {
        return course_number;
    }
}
