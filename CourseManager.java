package KodlamaioInheritance;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {

    List<Course> cources;

    public CourseManager() {
        cources = new ArrayList<>();
    }

    public void addCourse(int id, String image, String title, String description, Instructor instructor, double price) {
        Course course = new Course(id, image, title, description, instructor, price);
        cources.add(course);
    }

    public void deleteCourse(int id) {

        for (int i = 0; i < cources.size(); i++) {
            if (cources.get(i).getId() == id) {
                cources.remove(i);
                return;
            }
        }

    }

    public List getListCources() {
        return cources;
    }

    public void setComplated(int id, int percend) {
        for (int i = 0; i < cources.size(); i++) {
            if (cources.get(i).getId() == id) {
                cources.get(i).setComplated(percend);
                return;
            }
        }
    }

    public Course getCourse(int id) {
        for (int i = 0; i < cources.size(); i++) {
            if (cources.get(i).getId() == id) {
                return cources.get(i);
            }
        }
        return null;
    }

    public void addUser(Course course, User user) {
        course.getUsers().add(user);
    }
    
    public List<User> listUser(int id){
        for (int i = 0; i < cources.size(); i++) {
            if (cources.get(i).getId()==id) {
                return cources.get(i).getUsers();
            }
        }
        return null;
    
    }

}
