package obj;

public class Course {
    private String code;
    private String name;
    private String semester;
    private int assignmentCount;

    public Course(String code, String name, String semester, int assignmentCount) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.assignmentCount = assignmentCount;
    }

    // Getter 方法
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }
    
    @Override
    public String toString() {
        return code + " - " + name;
    }
}
